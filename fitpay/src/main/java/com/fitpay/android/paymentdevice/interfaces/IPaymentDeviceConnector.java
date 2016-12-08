package com.fitpay.android.paymentdevice.interfaces;

import android.content.Context;

import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.card.TopOfWallet;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.enums.Connection;

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

    /**
     * Read payment device info
     */
    void readDeviceInfo();

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

    /**
     * do what you need before executing apdu package
     */
    void onPreExecuteApdu();

    /**
     * do what you need after executiong apdu package
     */
    void onPostExecuteApdu();

    /**
     * process apdu package
     *
     * @param apduPackage apdu package commit
     */
    void executeApduPackage(ApduPackage apduPackage);

    /**
     * process single apdu command
     *
     * @param apduPkgNumber package number
     * @param apduCommand   apdu command
     */
    void executeApduCommand(long apduPkgNumber, ApduCommand apduCommand);

    /**
     * send apdu execution result to the server
     *
     * @param apduExecutionResult apdu execution result
     */
    void sendApduExecutionResult(ApduExecutionResult apduExecutionResult);

    /**
     * send top of wallet to the payment device
     *
     * @param towPackages top of wallet package
     */
    void executeTopOfWallet(List<TopOfWallet> towPackages);

    /**
     * Add commit type handler
     *
     * @param commitType type of commit
     * @param handler    handler to process that commit
     */
    void addCommitHandler(String commitType, CommitHandler handler);

    /**
     * Remove commit type handler
     *
     * @param commitType type of commit
     */
    void removeCommitHandler(String commitType);

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
