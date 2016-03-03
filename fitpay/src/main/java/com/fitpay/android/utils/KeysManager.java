package com.fitpay.android.utils;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;

import org.bouncycastle.util.encoders.Hex;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import retrofit2.Call;

/**
 * KeysManager is designed to create and manage @ECCKeyPair object.
 */
final class KeysManager {
    static final int KEY_API = 0;
    static final int KEY_RTM = KEY_API + 1;
    static final int KEY_WEB = KEY_RTM + 1;

    private static final String ALGORITHM = "ECDH";
    private static final String EC_CURVE = "secp256r1";
    private static final String KEY_TYPE = "AES";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            KeysManager.KEY_API,
            KeysManager.KEY_RTM,
            KeysManager.KEY_WEB
    })
    public @interface KeyType {
    }

    static {
        Security.addProvider(BouncyCastleProviderSingleton.getInstance());
    }

    static KeysManager sInstance;

    public static KeysManager getInstance() {
        if (sInstance == null) {
            sInstance = new KeysManager();
        }

        return sInstance;
    }

    private Map<Integer, ECCKeyPair> mKeysMap;

    private KeysManager() {
        mKeysMap = new HashMap<>();
    }

    // Create the public and private keys
    private ECCKeyPair createECCKeyPair() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        keyGenerator.initialize(new ECGenParameterSpec(EC_CURVE), new SecureRandom());

        KeyPair keyPair = keyGenerator.generateKeyPair();

        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        ECCKeyPair eccKeyPair = new ECCKeyPair();
        eccKeyPair.setKeyId(UUID.randomUUID().toString());
        eccKeyPair.setPrivateKey(Hex.toHexString(privateKey.getEncoded()));
        eccKeyPair.setPublicKey(Hex.toHexString(publicKey.getEncoded()));

        return eccKeyPair;
    }

    // methods for ASN.1 encoded keys
    private PrivateKey getPrivateKey(byte[] privateKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        return kf.generatePrivate(keySpec);
    }

    private PublicKey getPublicKey(byte[] publicKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        return kf.generatePublic(keySpec);
    }

    public SecretKey getSecretKey(@KeyType int type) {

        ECCKeyPair keyPair = getPairForType(type);
        SecretKey secretKey = keyPair.getSecretKey();

        if(secretKey == null) {
            secretKey = createSecretKey(keyPair.getPrivateKey(), keyPair.getServerPublicKey());
            keyPair.setSecretKey(secretKey);
        }

        return secretKey;
    }

    private SecretKey createSecretKey(String privateKeyStr, String publicKeyStr) {

        try {
            PrivateKey privateKey = getPrivateKey(Hex.decode(privateKeyStr));
            PublicKey publicKey = getPublicKey(Hex.decode(publicKeyStr));

            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);

            return keyAgreement.generateSecret(KEY_TYPE);
        } catch (Exception e) {
            Constants.printError("error generating shared secret:" + e.toString());
            return null;
        }
    }

    public ECCKeyPair getPairForType(@KeyType int type) {
        return mKeysMap.get(type);
    }

    public ECCKeyPair createPairForType(@KeyType int type) throws Exception {
        ECCKeyPair keyPair = createECCKeyPair();
        mKeysMap.put(type, keyPair);
        return keyPair;
    }

    public void updateECCKey(final @KeyType int type, @NonNull final Runnable successRunnable, final ApiCallback callback) {

        try {

            ECCKeyPair keyPair = createPairForType(type);

            ApiCallback<ECCKeyPair> apiCallback = new ApiCallback<ECCKeyPair>() {
                @Override
                public void onSuccess(ECCKeyPair result) {
                    result.setPrivateKey(mKeysMap.get(type).getPrivateKey());
                    mKeysMap.put(type, result);

                    if(successRunnable != null) {
                        successRunnable.run();
                    }
                }

                @Override
                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }
                }
            };

            Call<ECCKeyPair> getKeyCall = ApiManager.getInstance().getClient().createEncryptionKey(keyPair);
            getKeyCall.enqueue(new CallbackWrapper<>(apiCallback));

        } catch (Exception e) {
            callback.onFailure(ResultCode.REQUEST_FAILED, e.toString());
        }
    }

    public String getKeyId(@KeyType int type) {
        ECCKeyPair keyPair = getPairForType(type);

        if (keyPair != null) {
            return keyPair.getKeyId();
        }

        return null;
    }
}
