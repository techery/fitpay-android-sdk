package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResponse;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.TimestampUtils;
import com.fitpay.android.wearable.ble.utils.OperationQueue;
import com.fitpay.android.wearable.constants.ApduConstants;
import com.fitpay.android.wearable.constants.States;
import com.fitpay.android.wearable.enums.Sync;
import com.fitpay.android.wearable.interfaces.IApduMessage;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;

public class GattApduOperation extends GattOperation {

    private ApduExecutionResult mResult;

    private Subscription mApduSubscription;
    private Map<Integer, String> mSequencesMap;

    private long validUntil;
    private long mStartTime;

    public GattApduOperation(ApduPackage apduPackage) {

        mResult = new ApduExecutionResult(apduPackage.getPackageId());
        validUntil = TimestampUtils.getDateForISO8601String(apduPackage.getValidUntil()).getTime();

        mSequencesMap = new HashMap<>();
        mNestedQueue = new OperationQueue();

        mNestedQueue.add(new GattOperation() {
            @Override
            public void execute(BluetoothGatt bluetoothGatt) {
                mStartTime = System.currentTimeMillis();

                mApduSubscription = RxBus.getInstance().register(IApduMessage.class, new Action1<IApduMessage>() {
                    @Override
                    public void call(IApduMessage apduMessage) {

                        int sId = apduMessage.getSequenceId();

                        if (mSequencesMap.containsKey(sId)) {

                            RxBus.getInstance().post(new Sync(States.INC_PROGRESS));

                            ApduCommandResponse result = new ApduCommandResponse(mSequencesMap.get(sId), apduMessage);
                            mResult.addResponse(result);

                            mSequencesMap.remove(sId);

                            Logger.i(mSequencesMap.values().toString());
                        }
                    }
                });
            }

            @Override
            public boolean canRunNextOperation() {
                return true;
            }
        });

        for (ApduCommand command : apduPackage.getApduCommands()) {

            mSequencesMap.put(command.getSequence(), command.getCommandId());

            if (command.getCommand().length <= 17) {
                mNestedQueue.add(new GattApduBasicOperation(command));
            } else {
                mNestedQueue.add(new GattApduComplexOperation(command));
            }
        }
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        mApduSubscription.unsubscribe();

        long endTime = System.currentTimeMillis();
        int duration = (int) ((endTime - mStartTime) / 1000);

        @ResponseState.ApduState String state;

        if (mSequencesMap.size() != 0){
            mSequencesMap.clear();
            state = ResponseState.ERROR;
        } else if (validUntil < endTime) {
            state = ResponseState.EXPIRED;
        } else {
            state = ResponseState.PROCESSED;

            resultsLoop:
            for (ApduCommandResponse response : mResult.getResponses()) {
                int size = ApduConstants.SUCCESS_RESULTS.length;

                for(int i = 0; i < size; i++){
                    if(!Arrays.equals(ApduConstants.SUCCESS_RESULTS[i], response.getResponseCode())){
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
        mNestedQueue.clear();
    }

    @Override
    public boolean canRunNextOperation() {
        return true;
    }
}
