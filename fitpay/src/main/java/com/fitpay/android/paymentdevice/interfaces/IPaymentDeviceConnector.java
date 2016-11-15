package com.fitpay.android.paymentdevice.interfaces;

import android.content.Context;

import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.card.TopOfWallet;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;

import java.util.List;
import java.util.Properties;

/**
 * abstract interface of wearable payment device
 */
public interface IPaymentDeviceConnector extends CommitHandler {

    /**
     * Provide an Android context to the PaymentDeviceConnector so tha it can access
     * application and environment reseources as needed.
     *
     * @param context Android context.   In most case this will be the DeviceService context.
     */
    void setContext(Context context);

    /**
     * Configuration properties for the PaymentDeviceConnector.
     * Property content is specific to the PaymentDeviceConnector implementation.
     *
     * @param props configuration properties
     */
    void init(Properties props);

    void reset();

    void connect();

    void disconnect();

    void reconnect();

    void close();

    //TODO remove ?  since some devices do not have MacAddress and / or it is not of interest in getting connection
    String getMacAddress();

    void readDeviceInfo();

    void readNFCState();

    void setNFCState(@NFC.Action byte state);

    void sendNotification(byte[] data);

    void setSecureElementState(@SecureElement.Action byte state);

    /**
     * Do any pre-sync preparation.
     * Typically this will be used to make sure the device is in the proper state
     * and to register event listeners used in the sync process
     */
    void syncInit();

    /**
     * Do any post-sync operations
     * Typically used for device finalization or to unregister sync specific listeners
     */
    void syncComplete();

    void executeApduPackage(ApduPackage apduPackage);

    void executeApduCommand(long apduPkgNumber, ApduCommand apduCommand);

    void executeTopOfWallet(List<TopOfWallet> towPackages);

    void addCommitHandler(String commitType, CommitHandler handler);

    void removeCommitHandler(String commitType);

    //TODO review - should this have a getState method?
    @Connection.State
    int getState();

    void setState(@Connection.State int state);

    /**
     * Add user
     *
     * @param user current user
     */
    void setUser(User user);
}
