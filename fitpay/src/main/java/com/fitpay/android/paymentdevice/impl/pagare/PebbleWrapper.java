package com.fitpay.android.paymentdevice.impl.pagare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import com.fitpay.android.paymentdevice.DeviceOperationException;
import com.fitpay.android.paymentdevice.enums.DeviceOperationError;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.getpebble.android.kit.util.PebbleTuple;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tgs on 5/24/16.
 */
public class PebbleWrapper {

    private static final String TAG = PebbleWrapper.class.getSimpleName();

    private Context context;
    private int numberRetries = 3;
    private int timeout = 2000;  // milliseconds
    private UUID pebbleAppUuid;
    private String reason;
    private int reasonCode;
    private int appSelectionDelay = 100;

    public PebbleWrapper(Context context, UUID pebbleAppUuid) {
        Log.d(TAG, "creating PebbleWrapper on thread: " + Thread.currentThread());
        this.context = context;
        this.pebbleAppUuid = pebbleAppUuid;
    }

    public PebbleWrapper(Context context, UUID pebbleAppUuid, int numberRetries, int timeout) {
        this(context, pebbleAppUuid);
        this.numberRetries = numberRetries;
        this.timeout = timeout;
    }

    public void setNumberRetries(int numberRetries) {
        this.numberRetries = numberRetries;
    }

    public void writeData(PebbleDictionary dict, int transactionId) throws DeviceOperationException {
        reason = null;
        reasonCode = DeviceOperationError.NONE;
        selectPebbleApp();
        Boolean result = null;
        if (null != dict && dict.size() > 0) {
            ResultProvider resultProvider = new ResultProvider();
            int attemptNumber = 0;
            try {
                while ((null == result || false == result.booleanValue()) && attemptNumber < numberRetries) {
                    final CountDownLatch latch = new CountDownLatch(1);
                    resultProvider.setLatch(latch);
                    Log.d(TAG, "sending data to pebble with transactionId: " + transactionId + ", number of elements: " + dict.size());
                    Log.d(TAG, "sending Pebble dictionary contents: " + getPebbleDictionaryContents(dict));
                    PebbleKit.sendDataToPebbleWithTransactionId(this.context, this.pebbleAppUuid, dict, transactionId);

                    try {
                        latch.await(this.timeout, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        // party on;
                    }
                    result = resultProvider.getResult();
                    if (result == null) {
                        reason = "No device response";
                        reasonCode = DeviceOperationError.DEVICE_FAILED_TO_RESPOND;
                    } else {
                        reason = null;
                        reasonCode = DeviceOperationError.NONE;
                    }
                    if (null == result || result.booleanValue() == false) {
                        //TODO what actions should be taken to increase chances of success?  open app
                        resetPebble();
                    }
                    attemptNumber++;
                }
            } finally {
                resultProvider.tearDown();
            }

        } else {
            return;
        }
        if (null == result) {
            throw new DeviceOperationException("device did not respond", DeviceOperationError.DEVICE_FAILED_TO_RESPOND);
        } else if (false == result.booleanValue()) {
            throw new DeviceOperationException("device denied the request", DeviceOperationError.DEVICE_DENIED_THE_REQUEST);
        }
    }

    public void selectPebbleApp() {
        PebbleKit.startAppOnPebble(this.context, this.pebbleAppUuid);
        delay(appSelectionDelay);
    }

    public void resetPebble() {
        PebbleKit.closeAppOnPebble(this.context, this.pebbleAppUuid);
        delay(appSelectionDelay);
        selectPebbleApp();
    }

    public void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // party on
        }
    }

    public PebbleDictionary readData(PebbleDictionary dict, int transactionId) throws DeviceOperationException {
        Log.d(TAG, "read data being called");
        boolean result = false;
        final PebbleDictionary[] response = new PebbleDictionary[1];
        if (null != dict && dict.size() > 0) {
            PebbleKit.PebbleDataReceiver receiver = null;
            try {
                Log.d(TAG, "registering data receiver");
                final CountDownLatch latch = new CountDownLatch(1);
                receiver = new PebbleKit.PebbleDataReceiver(this.pebbleAppUuid) {

                    @Override
                    public void receiveData(Context context, int id, PebbleDictionary data) {
                        Log.d(TAG, "receiveData for id: " + id + ", size: " + data.size());

                        // Always ACKnowledge the last message to prevent timeouts
                        PebbleKit.sendAckToPebble(context, id);
                        latch.countDown();

                        // Get action and display
                        response[0] = data;
                    }

                };
                PebbleKit.registerReceivedDataHandler(PebbleWrapper.this.context, receiver);
                //TODO writeData is a blocking call - we really do not want to block
                Runnable writeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            writeData(dict, transactionId);
                        } catch (DeviceOperationException e) {
                            // will address in read result;
                        }
                    }
                };
                Thread writeThread = new Thread(writeRunnable);
                writeThread.start();

                try {
                    latch.await((long) (this.numberRetries * this.timeout * 1.05), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // party on;
                }
                if (null == response[0] && null == reason) {
                    reason = "Device acknowledged the request but did not provide data";
                    reasonCode = DeviceOperationError.DEVICE_FAILED_TO_PROVIDE_REQUESTED_DATA;
                } else if (null != response[0]) {
                    reason = null;
                    reasonCode = DeviceOperationError.NONE;
                }
            } finally {
                if (null != receiver) {
                    context.unregisterReceiver(receiver);
                }
            }

        }
        if (null == response[0]) {
            throw new DeviceOperationException(reason, reasonCode);
        }
        return response[0];
    }


    public void writeData(PebbleDictionary dict) throws DeviceOperationException {
        writeData(dict, 0);
    }

    public String getReason() {
        return reason;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    private class ResultProvider {

        Boolean result = null;

        private BroadcastReceiver ackReceiver;
        private BroadcastReceiver nackReceiver;
        private CountDownLatch latch;

        protected ResultProvider() {
            ackReceiver = registerForAck();
            nackReceiver = registerForNack();
        }

        protected ResultProvider(CountDownLatch latch) {
            this();
            this.latch = latch;
        }

        protected void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        public Boolean getResult() {
            return result;
        }

        private BroadcastReceiver registerForAck() {
            Log.d(TAG, "registering ack handler");
            BroadcastReceiver receiver = PebbleKit.registerReceivedAckHandler(PebbleWrapper.this.context, new PebbleKit.PebbleAckReceiver(PebbleWrapper.this.pebbleAppUuid) {
                @Override
                public void receiveAck(Context context, int transactionId) {
                    Log.d(TAG, "received pebble ack: " + transactionId);
                    result = true;
                    if (null != latch) {
                        latch.countDown();
                    }
                }
            });
            return receiver;
        }

        private BroadcastReceiver registerForNack() {
            Log.d(TAG, "registering nack handler");

            BroadcastReceiver receiver = PebbleKit.registerReceivedNackHandler(PebbleWrapper.this.context, new PebbleKit.PebbleNackReceiver(PebbleWrapper.this.pebbleAppUuid) {
                @Override
                public void receiveNack(Context context, int transactionId) {
                    Log.d(TAG, "received pebble nack: " + transactionId);
                    result = false;
                    if (null != latch) {
                        latch.countDown();
                    }
                }
            });
            return receiver;
        }

        protected void tearDown() {
            if (null != ackReceiver) {
                context.unregisterReceiver(ackReceiver);
            }
            if (null != nackReceiver) {
                context.unregisterReceiver(nackReceiver);
            }

        }

    }

    private String getPebbleDictionaryContents(PebbleDictionary dict) {
        StringBuilder sb = new StringBuilder();
        sb.append("PebbleDictionary={");
        Iterator<PebbleTuple> iterator = dict.iterator();
        while (iterator.hasNext()) {
            PebbleTuple tuple = iterator.next();
            sb.append("(key=" + tuple.key);
            sb.append(", value=" + tuple.value);
            sb.append(", type=" + tuple.type);
            sb.append(", width=" + tuple.width);
            sb.append(", length=" + tuple.length);
            sb.append(")");
        }
        sb.append("}");
        return sb.toString();
    }


}
