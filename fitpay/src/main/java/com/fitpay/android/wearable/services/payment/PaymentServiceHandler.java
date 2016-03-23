package com.fitpay.android.wearable.services.payment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import com.fitpay.android.wearable.utils.Hex;
import com.fitpay.android.wearable.message.ApduResultMessage;
import com.fitpay.android.wearable.message.BleMessage;
import com.fitpay.android.wearable.message.ContinuationControlBeginMessage;
import com.fitpay.android.wearable.message.ContinuationControlEndMessage;
import com.fitpay.android.wearable.message.ContinuationPacketMessage;
import com.fitpay.android.wearable.message.SecurityStateMessage;
import com.fitpay.android.wearable.services.BluetoothDeviceNotifier;
import com.fitpay.android.wearable.services.ServiceHandler;
import com.fitpay.android.wearable.utils.BluetoothUuid;
import com.fitpay.android.wearable.utils.Crc32;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by ssteveli on 1/25/16.
 */
public class PaymentServiceHandler extends ServiceHandler {

    private final static String LOG_TAG = PaymentServiceHandler.class.getCanonicalName();

    public final static String PAYMENT_SERVICE_CHARACTERISTIC_CHANGE = "payment_service_characteristic_change";

    private final static byte[] SUCCESS = new byte[] { 0x00 };
    private final static byte[] FAILURE = null;

    private final static String[] READABLE_CHARACTERISTICS = {
            PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT,
            PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID,
            PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
            PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE
    };

    private UUID serviceUUID = PaymentServiceConstants.SERVICE_UUID;

    private final BluetoothDeviceNotifier mDeviceNotifier;
    private final ScheduledExecutorService executor;

    private ContinuationPayload continuationPayload = null;

    private BluetoothGattCharacteristic apduCharacteristic;
    private BluetoothGattCharacteristic continuationControlCharacteristic;
    private BluetoothGattCharacteristic continuationPacketCharacteristic;
    private BluetoothGattCharacteristic notificationCharacteristic;
    private BluetoothGattCharacteristic securityStateCharacteristic;

    private BluetoothGattService paymentService;
    private Map<String, String> characteristicNames = new HashMap<>();

    private int lastSequenceId = Integer.MIN_VALUE;

    public PaymentServiceHandler(Context context, BluetoothDeviceNotifier mDeviceNotifier) {
        super(context);
        this.mDeviceNotifier = mDeviceNotifier;
        this.executor = Executors.newScheduledThreadPool(1);
        this.secureElement = SecureElement.getInstance();
    }

    private final SecureElement secureElement;

    @Override
    public BluetoothGattService buildService() {
        Log.d(LOG_TAG, "Building Payment Service");
        paymentService = new BluetoothGattService(
                serviceUUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        paymentService.addCharacteristic(new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL),
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
        ));
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL, "APDU Control");

        apduCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT),
                BluetoothGattCharacteristic.PROPERTY_INDICATE,
                0 // no permissions on indicate
        );
        apduCharacteristic.addDescriptor(new BluetoothGattDescriptor(
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE)));
        paymentService.addCharacteristic(apduCharacteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT, "APDU Result");

        continuationControlCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL),
                //BluetoothGattCharacteristic.PROPERTY_INDICATE | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PROPERTY_INDICATE | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE // no permissions on indicate
        );
        continuationControlCharacteristic.addDescriptor(new BluetoothGattDescriptor(
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE)));
        paymentService.addCharacteristic(continuationControlCharacteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL, "Continuation Control");

        continuationPacketCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET),
                BluetoothGattCharacteristic.PROPERTY_INDICATE | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);

        continuationPacketCharacteristic.addDescriptor(new BluetoothGattDescriptor(
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE)));

        paymentService.addCharacteristic(continuationPacketCharacteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET, "Continuation Packet");

        notificationCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION),
                BluetoothGattCharacteristic.PROPERTY_INDICATE | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);

        notificationCharacteristic.addDescriptor(new BluetoothGattDescriptor(
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE)));

        paymentService.addCharacteristic(notificationCharacteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION, "Transaction Notification");

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        paymentService.addCharacteristic(characteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID, "eSE ID");
        characteristic.setValue(Hex.bytesToHexString(secureElement.getId().getBytes()));

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE),
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        paymentService.addCharacteristic(characteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE, "Security Write");

        securityStateCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE),
                BluetoothGattCharacteristic.PROPERTY_INDICATE | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);  // no permissions on indicate
        securityStateCharacteristic.addDescriptor(new BluetoothGattDescriptor(
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG,
                (BluetoothGattDescriptor.PERMISSION_READ| BluetoothGattDescriptor.PERMISSION_WRITE)));
        paymentService.addCharacteristic(securityStateCharacteristic);
        characteristicNames.put(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE, "Security State");
        securityStateCharacteristic.setValue(new byte[] {0x00, 0x00});

        return paymentService;
    }

    @Override
    public boolean canHandleCharacteristicRead(BluetoothGattCharacteristic characteristic) {

        for (String uuid: READABLE_CHARACTERISTICS) {
            if (UUID.fromString(uuid).equals(characteristic.getUuid())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canHandleCharacteristicWrite(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL)) ||
                characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION)) ||
                characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE)) ||
                characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL)) ||
                characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET))) {
            return true;
        }

        return false;
    }

    @Override
    public void handleCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "handleCharacteristicRead.  characteristic: " + characteristic.getUuid());
        if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID))) {
            Log.d(LOG_TAG, "secure element id read value is: " + Hex.bytesToHexString(secureElement.getId().getBytes()));
            characteristic.setValue(secureElement.getId().getBytes());
        } else if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE))) {
            // value is stored in characteristic
            Log.d(LOG_TAG, "security state read value is: " + Hex.bytesToHexString(characteristic.getValue()));
        }
    }

    @Override
    public byte[] handleCharacteristicWrite(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
        Log.d(LOG_TAG, "handleCharacteristicWrite.  characteristic: " + characteristic.getUuid()
                + ", offset: " + offset + ", value: " + Hex.bytesToHexString(value));

        broadcastCharacteristicChange(characteristic, value);

        if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL))) {
            Log.d(LOG_TAG, "continuation control write received [" + Hex.bytesToHexString(value) + "], length [" + value.length + "]");

            // start continuation packet
            if (value[0] == 0x00) {
                if (continuationPayload != null) {
                    Log.d(LOG_TAG, "continuation was previously started, resetting to blank");
                }

                ParcelUuid targetUuid = null;

                if (value.length == 17) {
                    byte[] uuidBytes = new byte[16];
                    System.arraycopy(value, 1, uuidBytes, 0, 16);
                    targetUuid = BluetoothUuid.parseUuidFrom(uuidBytes);
                    Log.d(LOG_TAG, "continuation target characteristic is: " + targetUuid.getUuid());
                } else {
                    return FAILURE;
                }

                continuationPayload = new ContinuationPayload(targetUuid);

                Log.d(LOG_TAG, "continuation start control received, ready to receive continuation data");
                return SUCCESS;
            } else if (value[0] == 0x01) {
                Log.d(LOG_TAG, "continuation finish control received.  process update to characteristic: " + continuationPayload.getTargetUuid());
                ParcelUuid targetUuid = continuationPayload.getTargetUuid();
                byte[] payloadValue = null;
                try {
                    payloadValue = continuationPayload.getValue();
                    continuationPayload = null;
                    Log.d(LOG_TAG, "complete continuation data [" + Hex.bytesToHexString(payloadValue) + "]");
                } catch (IOException e) {
                    continuationPayload = null;
                    Log.e(LOG_TAG, "error parsing continuation data", e);
                    return FAILURE;
                }

                if (value.length >= 5) { // does the write contain a checksum?
                    byte[] checkSum = Crc32.getCRC32Checksum(payloadValue);
                    byte[] continuationCS = new byte[4];
                    System.arraycopy(value, 1, continuationCS, 0, 4);
                    if (!Arrays.equals(checkSum, continuationCS)) {
                        Log.e(LOG_TAG, "Checksums not equal.  input data checksum: " + Hex.bytesToHexString(checkSum)
                                + ", expected value as provided on continuation end: " + Hex.bytesToHexString(continuationCS));
                        return FAILURE;
                    }
                } else {
                    Log.e(LOG_TAG, "no checksum provided");
                    return FAILURE;
                }

                if (PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL_PARCEL_UUID.equals(targetUuid)) {
                    Log.d(LOG_TAG, "continuation is for APDU Control");
                    doApduResponse(payloadValue);
                } else {
                    Log.w(LOG_TAG, "Code does not handle continuation for characteristic: " + targetUuid);
                    return FAILURE;
                }

                return SUCCESS;
            }

        } else if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET))) {
            Log.d(LOG_TAG, "continuation data packet received [" + Hex.bytesToHexString(value) + "]");

            if (continuationPayload == null) {
                Log.e(LOG_TAG, "invalid continuation, no start received on control characteristic");
                return FAILURE;
            }

            try {
                continuationPayload.processPacket(value);
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception handling continuation packet", e);
                return FAILURE;
            }

            return SUCCESS;
        } else if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL))) {
            Log.d(LOG_TAG, "apdu control received [" + Hex.bytesToHexString(value) + "]");
            doApduResponse(value);
            Log.d(LOG_TAG, "apdu control write success");
            return SUCCESS;
        } else if (characteristic.getUuid().equals(UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE))) {
            Log.d(LOG_TAG, "security write received [" + Hex.bytesToHexString(value) + "]");
            doSecurityWriteResponse(value);
            Log.d(LOG_TAG, "security write success");
            return SUCCESS;
         }

        Log.d(LOG_TAG, "unhandled characterisitic.  return FAILURE");
        return FAILURE;
    }

    private void broadcastCharacteristicChange(BluetoothGattCharacteristic characteristic, byte[] value) {
        final Intent intent = new Intent(PAYMENT_SERVICE_CHARACTERISTIC_CHANGE);
        intent.putExtra("uuid", characteristic.getUuid().toString());
        intent.putExtra("value", Hex.bytesToHexString(value));
        context.sendBroadcast(intent);
    }

    private void doSecurityWriteResponse(byte[] value) {
        Log.d(LOG_TAG, "getting response for securityWrite: " + Hex.bytesToHexString(value));
        byte[] currentValue = securityStateCharacteristic.getValue();
        SecurityStateMessage currentState = new SecurityStateMessage().withData(currentValue);
        SecurityStateMessage newState = new SecurityStateMessage().withNfcEnabled(!currentState.isNfcEnabled());
        securityStateCharacteristic.setValue(newState.getMessage());
        mDeviceNotifier.sendNotificationsToDevices(securityStateCharacteristic);
        broadcastCharacteristicChange(securityStateCharacteristic, newState.getMessage());
    }

    private void doApduResponse(byte[] value) {
        Log.d(LOG_TAG, "getting apduResponse for: " + Hex.bytesToHexString(value));
        byte[] apduResponse = null;
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
        try {
            in.readByte(); // skip the reserved byte
            int sequenceId = in.readUnsignedShort();
            final byte[] apduRequest = new byte[value.length - 3];
            in.read(apduRequest, 0, value.length - 3);

            if (sequenceId == lastSequenceId) {
                byte[] response = new ApduResultMessage()
                        .withResult(BleMessage.APDU_PROTOCOL_ERROR)
                        .withSequenceId(sequenceId)
                        .withData(BleMessage.PROTOCOL_ERROR_DUPLICATE_SEQUENCE_NUMBER)
                        .getMessage();
                Log.e(LOG_TAG, "received duplicate sequence id: " + sequenceId + "  return response: " + Hex.bytesToHexString(response));
                apduCharacteristic.setValue(response);
                mDeviceNotifier.sendNotificationsToDevices(apduCharacteristic);
                broadcastCharacteristicChange(apduCharacteristic, response);
                return;
            } else {
                lastSequenceId = sequenceId;
            }

            Log.d(LOG_TAG, "received sequenceId #" + sequenceId + ": apdu [" + Hex.bytesToHexString(apduRequest) + "]");

            // add some oddball latency
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // safely ignore
                    }

                    Log.d(LOG_TAG, "sending apdu indicate");
                        byte[] apduResponse = secureElement.process(apduRequest);
                        Log.d(LOG_TAG, "secure element apdu response: " + Hex.bytesToHexString(apduResponse));

                        if (apduResponse.length <= 17) {
                            boolean apduSuccessful = secureElement.wasSuccessful(apduResponse);
                            byte[] msg = new ApduResultMessage()
                                .withResult(apduSuccessful ? BleMessage.APDU_SUCCESS_NO_CONTINUATION : BleMessage.APDU_ERROR_NO_CONTINUATION)
                                .withSequenceId(lastSequenceId)
                                .withData(apduResponse)
                                .getMessage();

                            apduCharacteristic.setValue(msg);
                            mDeviceNotifier.sendNotificationsToDevices(apduCharacteristic);
                            broadcastCharacteristicChange(apduCharacteristic, msg);
                        } else {
                            sendApduResultContinuationResponse(apduResponse);
                        }
                }
            }).start();
        } catch (IOException e ) {
            Log.e(LOG_TAG, "error parsing APDU packet", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
        }

    }

    protected void sendApduResultContinuationResponse(byte[] apduResponse) {
        Log.d(LOG_TAG, "sending apdu result via continuation.  value: " + Hex.bytesToHexString(apduResponse));
        boolean apduSuccessful = secureElement.wasSuccessful(apduResponse);


        continuationControlCharacteristic.setValue(new ContinuationControlBeginMessage()
                .withUuid(PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT)
                .getMessage());
        mDeviceNotifier.sendNotificationsToDevices(continuationControlCharacteristic);
        broadcastCharacteristicChange(continuationControlCharacteristic, continuationControlCharacteristic.getValue());

        byte[] msg = new ApduResultMessage()
                .withEnforceLength(false)
                .withResult(apduSuccessful ? BleMessage.APDU_SUCCESS_CONTINUATION : BleMessage.APDU_ERROR_CONTINUATION)
                .withSequenceId(lastSequenceId)
                .withData(apduResponse)
                .getMessage();

        int currentPos = 0;
        int sortOrder = 0;
        byte[] dataToSend = null;
        while (currentPos < msg.length) {
            int len = Math.min(msg.length - currentPos, ContinuationPacketMessage.getMaxDataLength());
            dataToSend = new byte[len];
            System.arraycopy(msg, currentPos, dataToSend, 0, len);
            continuationPacketCharacteristic.setValue(new ContinuationPacketMessage()
                    .withSortOrder(sortOrder)
                    .withData(dataToSend)
                    .getMessage());
            mDeviceNotifier.sendNotificationsToDevices(continuationPacketCharacteristic);
            broadcastCharacteristicChange(continuationPacketCharacteristic, continuationPacketCharacteristic.getValue());

            currentPos+=len;
            sortOrder++;
        }

        continuationControlCharacteristic.setValue(new ContinuationControlEndMessage()
                .withPayload(msg)
                .getMessage());
        mDeviceNotifier.sendNotificationsToDevices(continuationControlCharacteristic);
        broadcastCharacteristicChange(continuationControlCharacteristic, continuationControlCharacteristic.getValue());

    }

    public BluetoothGattCharacteristic getNotificationCharacteristic() {
        return notificationCharacteristic;
    }

    @Override
    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return paymentService.getCharacteristics();
    }

    @Override
    public String getCharacteristicName(BluetoothGattCharacteristic characteristic) {
        return characteristicNames.get(characteristic.getUuid().toString());
    }

    @Override
    public UUID getServiceUUID() {
        return serviceUUID;
    }

    @Override
    public boolean isAdvertised() {
        return true;
    }



}
