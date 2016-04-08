package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResponse;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.apdu.ApduPackageResponse;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.TimestampUtils;
import com.fitpay.android.wearable.ble.utils.OperationQueue;
import com.fitpay.android.wearable.constants.ApduConstants;
import com.fitpay.android.wearable.interfaces.IApduMessage;
import com.fitpay.android.wearable.utils.ApduPair;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;

public class GattApduOperation extends GattOperation {

    private ApduPackage mPackage;
    private ApduPackageResponse mResult;

    private Subscription mApduSubscription;
    private Map<Integer, String> mSequencesMap;

    private long mStartTime;

    public GattApduOperation(ApduPackage apduPackage) {

        this.mPackage = apduPackage;
        this.mResult = new ApduPackageResponse(apduPackage.getPackageId());

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

        @ResponseState.ApduState String state = ResponseState.PROCESSED;

        if (mSequencesMap.size() != 0){
            state = ResponseState.ERROR;
        } else if (TimestampUtils.getDateForISO8601String(mPackage.getValidUntil()).getTime() < endTime) {
            state = ResponseState.EXPIRED;
        } else {
            for (ApduCommandResponse response : mResult.getResponses()) {
                if(!Arrays.equals(ApduConstants.SUCCESS, response.getResponseCode())){
                    state = ResponseState.FAILED;
                    break;
                }
            }
        }

        mResult.setExecutedDuration(duration);
        mResult.setExecutedTsEpoch(endTime);
        mResult.setState(state);

        RxBus.getInstance().post(new ApduPair(mPackage, mResult));

        mSequencesMap.clear();
        mNestedQueue.clear();
    }

    @Override
    public boolean canRunNextOperation() {
        return true;
    }
}
