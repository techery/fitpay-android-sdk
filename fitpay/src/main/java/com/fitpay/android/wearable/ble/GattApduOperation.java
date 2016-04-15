package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.TimestampUtils;
import com.fitpay.android.wearable.constants.ApduConstants;
import com.fitpay.android.wearable.constants.States;
import com.fitpay.android.wearable.enums.ApduExecutionError;
import com.fitpay.android.wearable.enums.Sync;
import com.fitpay.android.wearable.interfaces.IApduMessage;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Apdu operation. Can process commands with any size.
 */
class GattApduOperation extends GattOperation {

    private ApduExecutionResult mResult;

    private ApduNotificationListener mNotificationListener;
    private Map<Integer, String> mSequencesMap;

    private long validUntil;
    private long mStartTime;

    public GattApduOperation(ApduPackage apduPackage) {

        mResult = new ApduExecutionResult(apduPackage.getPackageId());
        validUntil = TimestampUtils.getDateForISO8601String(apduPackage.getValidUntil()).getTime();

        mSequencesMap = new HashMap<>();

        addNestedOperation(new GattOperation() {
            @Override
            public void execute(BluetoothGatt bluetoothGatt) {
                mStartTime = System.currentTimeMillis();

                mNotificationListener = new ApduNotificationListener();
                NotificationManager.getInstance().addListener(mNotificationListener);
            }

            @Override
            public boolean canRunNextOperation() {
                return true;
            }
        });

        for (ApduCommand command : apduPackage.getApduCommands()) {

            mSequencesMap.put(command.getSequence(), command.getCommandId());

            if (command.getCommand().length <= 17) {
                addNestedOperation(new GattApduBasicOperation(command));
            } else {
                addNestedOperation(new GattApduComplexOperation(command));
            }
        }
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        NotificationManager.getInstance().removeListener(mNotificationListener);

        long endTime = System.currentTimeMillis();
        int duration = (int) ((endTime - mStartTime) / 1000);

        @ResponseState.ApduState String state;

        if (mSequencesMap.size() != 0) {
            mSequencesMap.clear();
            state = ResponseState.ERROR;
        } else if (validUntil < endTime) {
            state = ResponseState.EXPIRED;
        } else {
            state = ResponseState.PROCESSED;

            resultsLoop:
            for (ApduCommandResult response : mResult.getResponses()) {
                int size = ApduConstants.SUCCESS_RESULTS.length;

                for (int i = 0; i < size; i++) {
                    if (!Arrays.equals(ApduConstants.SUCCESS_RESULTS[i], response.getResponseCode())) {
                        state = ResponseState.FAILED;
                        break resultsLoop;
                    }
                }
            }
        }

        mResult.setExecutedDuration(duration);
        mResult.setExecutedTsEpoch(endTime);
        mResult.setState(state);

        RxBus.getInstance().post(mResult);

        mSequencesMap.clear();
        clear();
    }

    @Override
    public boolean canRunNextOperation() {
        return true;
    }

    private class ApduNotificationListener extends Listener {
        public ApduNotificationListener() {
            super();

            mCommands.put(IApduMessage.class, data -> {

                IApduMessage apduMessage = (IApduMessage) data;

                int sId = apduMessage.getSequenceId();

                if (mSequencesMap.containsKey(sId)) {

                    RxBus.getInstance().post(new Sync(States.INC_PROGRESS));

                    ApduCommandResult result = new ApduCommandResult(mSequencesMap.get(sId), apduMessage);
                    mResult.addResponse(result);

                    mSequencesMap.remove(sId);

                    Logger.i(mSequencesMap.values().toString());
                }
            });

            mCommands.put(ApduExecutionError.class, data -> {
                execute(null);
            });
        }
    }
}
