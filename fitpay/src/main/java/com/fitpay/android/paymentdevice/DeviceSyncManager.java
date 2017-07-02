package com.fitpay.android.paymentdevice;

import android.content.Context;

import com.fitpay.android.R;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.paymentdevice.callbacks.IListeners;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSkipped;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.EventCallback;
import com.fitpay.android.utils.FPLog;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.events.DeviceStatusMessage;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import static com.fitpay.android.utils.Constants.SYNC_DATA;

/**
 * Device sync manager can work with one device only, all new sync request will be put in a queue
 */
class DeviceSyncManager {
    private final static String TAG = DeviceSyncManager.class.getSimpleName();

    private final Context mContext;
    private final BlockingQueue<SyncRequest> requests;

    private SyncWorkerThread worker = null;

    /**
     * @deprecated no longer utilized, executor is not needed
     *
     * @param context
     * @param executor
     */
    public DeviceSyncManager(Context context, Executor executor) {
        this(context);
    }

    public DeviceSyncManager(Context context) {
        mContext = context;
        requests = new ArrayBlockingQueue<>(10);
    }

    public void onCreate() {
        worker = new SyncWorkerThread(mContext, requests);
        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    public void onDestroy() {
        if (requests != null) {
            requests.clear();
        }

        if (worker != null) {
            try {
                worker.shutdown();
                worker.join();
            } catch (InterruptedException e) {
                FPLog.e("error while waiting for sync worker to properly shutdown", e);
            }
        }
    }

    public void add(SyncRequest request) {
        if (request == null) {
            return;
        }

        try {
            FPLog.d("adding sync request to queue for processing, current queue size [" + requests.size() + "]:" + request);
            requests.put(request);
        } catch (InterruptedException e) {
            FPLog.w("interrupted exception while waiting to add sync request to processing queue");
        }
    }

    private static class SyncWorkerThread extends Thread {

        @Sync.State
        private Integer mSyncEventState;

        private final Context mContext;
        private final BlockingQueue<SyncRequest> requests;
        private volatile boolean running = true;
        private final SyncListener mSyncListener;

        private List<Commit> mCommits = null;

        private SyncRequest currentRequest = null;

        public SyncWorkerThread(Context mContext, BlockingQueue<SyncRequest> requests) {
            this.requests = requests;
            this.mContext = mContext;
            mSyncListener = new SyncListener();

            NotificationManager.getInstance().addListener(mSyncListener);
        }

        public void run() {
            while (running) {
                try {
                    currentRequest = requests.take();

                    FPLog.d("received sync requests, processing: " + currentRequest);
                    sync();
                } catch (InterruptedException e) {
                    FPLog.d("sync worker thread interruptted, shutting down");
                    running = false;
                }
            }
        }

        public void shutdown() {
            NotificationManager.getInstance().removeListener(mSyncListener);

            running = false;
            interrupt();
        }

        private void sync() {
            if (currentRequest == null) {
                FPLog.d("sync skipped, sync request was null");
                return;
            }

            RxBus.getInstance().post(Sync.builder()
                    .syncId(currentRequest.getSyncId())
                    .state(States.STARTED)
                    .build());

            if (currentRequest.getUser() == null) {
                FPLog.w(TAG, "No user");

                RxBus.getInstance().post(Sync.builder()
                        .syncId(currentRequest.getSyncId())
                        .state(States.SKIPPED)
                        .message("No user provided in current request: " + currentRequest)
                        .build());

                return;
            }

            if (currentRequest.getDevice() == null) {
                FPLog.w(TAG, "No payment device connector configured");

                RxBus.getInstance().post(Sync.builder()
                        .syncId(currentRequest.getSyncId())
                        .state(States.SKIPPED)
                        .message("No fitpay device provided in current request: " + currentRequest)
                        .build());

                return;
            }

            if (currentRequest.getConnector() == null) {
                FPLog.w(TAG, "No payment device connector configured");

                RxBus.getInstance().post(Sync.builder()
                        .syncId(currentRequest.getSyncId())
                        .state(States.SKIPPED)
                        .message("No payment device provided in current request: " + currentRequest)
                        .build());
                return;
            }

            if (currentRequest.getConnector().getState() != States.CONNECTED) {
                FPLog.w(TAG, "No payment device connection");

                RxBus.getInstance().post(Sync.builder()
                        .syncId(currentRequest.getSyncId())
                        .state(States.SKIPPED)
                        .message("Payment device is not currently connected (" + currentRequest.getConnector().getState() + ": " + currentRequest)
                        .build());

                return;
            }

            FPLog.d(TAG, "starting device sync.  device: " + currentRequest.getDevice().getDeviceIdentifier());
            FPLog.d(TAG, "sync initiated from thread: " + Thread.currentThread() + ", " + Thread.currentThread().getName());

            currentRequest.getConnector().setUser(currentRequest.getUser());

            syncDevice();
        }

        private void syncDevice() {
            String devId = currentRequest.getDevice().getDeviceIdentifier();

            RxBus.getInstance().post(new DeviceStatusMessage(mContext.getString(R.string.checking_wallet_updates), devId, DeviceStatusMessage.SUCCESS));

            if (currentRequest.getConnector().getState() == States.DISCONNECTED || currentRequest.getConnector().getState() == States.DISCONNECTING) {

                RxBus.getInstance().post(Sync.builder()
                        .syncId(currentRequest.getSyncId())
                        .state(States.FAILED)
                        .message(mContext.getString(R.string.disconnected))
                        .build());

                return;
            }

            FPLog.d(TAG, "sync running on thread: " + Thread.currentThread() + ", " + Thread.currentThread().getName());

            currentRequest.getConnector().syncInit();

        /*
         * In case of another account force update our wallet
         */
//        final AtomicBoolean forceWalletUpdate = new AtomicBoolean(false);
//        final String prevDeviceId = Prefs.with(mContext).getString(SYNC_PROPERTY_DEVICE_ID, null);
//        if (StringUtils.isEmpty(prevDeviceId) || !prevDeviceId.equals(devId)) {
//            Prefs.with(mContext).save(SYNC_PROPERTY_DEVICE_ID, devId);
//            forceWalletUpdate.set(true);
//        }

            DevicePreferenceData deviceData = DevicePreferenceData.load(mContext, devId);

            currentRequest.getDevice().getAllCommits(deviceData.getLastCommitId())
                    .compose(RxBus.applySchedulersExecutorThread())
                    .subscribe(
                            commitsCollection -> {
                                mCommits = commitsCollection.getResults();

                                int commitsSize = mCommits != null ? mCommits.size() : 0;

                                FPLog.i(SYNC_DATA, "\\CommitsReceived\\: " + commitsSize);

                                if (commitsSize > 0) {
                                    RxBus.getInstance().post(new DeviceStatusMessage(mContext.getString(R.string.updates_available), devId, DeviceStatusMessage.SUCCESS));
                                    RxBus.getInstance().post(new DeviceStatusMessage(mContext.getString(R.string.sync_started), devId, DeviceStatusMessage.PROGRESS));
                                    processNextCommit();
                                } else {
                                    RxBus.getInstance().post(new DeviceStatusMessage(mContext.getString(R.string.no_pending_updates), devId, DeviceStatusMessage.SUCCESS));

                                    RxBus.getInstance().post(Sync.builder()
                                            .syncId(currentRequest.getSyncId())
                                            .state(States.COMPLETED_NO_UPDATES)
                                            .build());
                                }
                            },
                            throwable -> {
                                if (throwable instanceof DeviceOperationException) {
                                    DeviceOperationException doe = (DeviceOperationException) throwable;
                                    FPLog.e(TAG, "get commits failed.  reasonCode: " + doe.getErrorCode() + ",  " + doe.getMessage());
                                } else {
                                    FPLog.e(TAG, "get commits failed. " + throwable.getMessage());
                                }

                                RxBus.getInstance().post(Sync.builder()
                                        .syncId(currentRequest.getSyncId())
                                        .state(States.FAILED)
                                        .message(throwable.getMessage())
                                        .build());
                            });
        }

        /**
         * process next commit
         */
        private void processNextCommit() {
            if (mCommits != null && mCommits.size() > 0) {

                RxBus.getInstance().post(Sync.builder()
                        .syncId(currentRequest.getSyncId())
                        .value(mCommits.size())
                        .build());

                Commit commit = mCommits.get(0);

                FPLog.i(SYNC_DATA, "\\ProcessNextCommit\\: " + commit);

                currentRequest.getConnector().processCommit(commit);
                // expose the commit out to others who may want to take action
                RxBus.getInstance().post(commit);
            } else {
                RxBus.getInstance().post(Sync.builder()
                    .syncId(currentRequest.getSyncId())
                    .state(States.COMPLETED)
                    .build());
            }
        }

        /**
         * Listen to Apdu and Sync callbacks
         */
        private class SyncListener extends Listener implements IListeners.SyncListener {

            private SyncListener() {
                super();
                mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
                mCommands.put(CommitSuccess.class, data -> onCommitSuccess((CommitSuccess) data));
                mCommands.put(CommitFailed.class, data -> onCommitFailed((CommitFailed) data));
                mCommands.put(CommitSkipped.class, data -> onCommitSkipped((CommitSkipped) data));
//            mCommands.put(AppMessage.class, data -> {
//                try {
//                    syncData(user, device);
//                } catch (Exception e) {
//                    //don't remove try/catch. syncData can throw an exception when it busy.
//                }
//            });
            }

            @Override
            public void onSyncStateChanged(Sync syncEvent) {
                mSyncEventState = syncEvent.getState();

                if (mSyncEventState == States.COMPLETED || mSyncEventState == States.FAILED || mSyncEventState == States.COMPLETED_NO_UPDATES) {
                    if (null == currentRequest) {
                        FPLog.i(TAG, "No current sync requests on sync event: " + syncEvent.getMessage() + " with state: " + mSyncEventState);
                    } else {
                        FPLog.i(SYNC_DATA, "\\EndSync\\: " + (mSyncEventState != States.FAILED ? "Success" : "Failed"));

                        if (currentRequest != null && currentRequest.getConnector() != null) {
                            currentRequest.getConnector().syncComplete();
                        }
                    }
                }

                if (mSyncEventState == States.COMMIT_COMPLETED) {
                    processNextCommit();
                }
            }

            @Override
            public void onCommitFailed(CommitFailed commitFailed) {
                FPLog.w(SYNC_DATA, "\\CommitProcessed\\: " + commitFailed);

                mCommits.clear();

                // TODO: research why two FAILEDs are posted, that doesn't make much sense... why not just one?
                RxBus.getInstance().post(Sync.builder()
                                .syncId(currentRequest.getSyncId())
                                .state(States.FAILED)
                                .build());

                RxBus.getInstance().post(Sync.builder()
                    .syncId(currentRequest.getSyncId())
                    .state(States.FAILED)
                    .message(commitFailed.getErrorMessage())
                    .build());

                EventCallback eventCallback = new EventCallback.Builder()
                        .setCommand(EventCallback.getCommandForCommit(commitFailed.getCommit()))
                        .setReason(commitFailed.getErrorMessage())
                        .setStatus(EventCallback.STATUS_FAILED)
                        .setTimestamp(commitFailed.getCreatedTs())
                        .build();
                eventCallback.send();
            }

            @Override
            public void onCommitSuccess(CommitSuccess commitSuccess) {
                FPLog.i(SYNC_DATA, "\\CommitProcessed\\: " + commitSuccess);

                DevicePreferenceData deviceData = DevicePreferenceData.load(mContext, currentRequest.getDevice().getDeviceIdentifier());
                deviceData.setLastCommitId(commitSuccess.getCommitId());
                DevicePreferenceData.store(mContext, deviceData);

                EventCallback eventCallback = new EventCallback.Builder()
                        .setCommand(EventCallback.getCommandForCommit(commitSuccess.getCommit()))
                        .setStatus(EventCallback.STATUS_OK)
                        .setTimestamp(commitSuccess.getCreatedTs())
                        .build();
                eventCallback.send();

                //TODO: sometimes I caught IndexOfBoundException. Need to find why this happens
                if (mCommits.size() > 0) {
                    mCommits.remove(0);
                }
                processNextCommit();
            }

            @Override
            public void onCommitSkipped(CommitSkipped commitSkipped) {
                FPLog.i(SYNC_DATA, "\\CommitProcessedWithErrorsCommitProcessed\\: " + commitSkipped);

                DevicePreferenceData deviceData = DevicePreferenceData.load(mContext, currentRequest.getDevice().getDeviceIdentifier());
                deviceData.setLastCommitId(commitSkipped.getCommitId());
                DevicePreferenceData.store(mContext, deviceData);

                EventCallback eventCallback = new EventCallback.Builder()
                        .setCommand(EventCallback.getCommandForCommit(commitSkipped.getCommit()))
                        .setStatus(EventCallback.STATUS_OK)
                        .setTimestamp(commitSkipped.getCreatedTs())
                        .build();
                eventCallback.send();

                if (mCommits.size() > 0) {
                    mCommits.remove(0);
                }
                processNextCommit();
            }
        }
    }
  }
