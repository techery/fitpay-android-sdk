package com.fitpay.android.utils;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.callbacks.CallbackWrapper;
import com.fitpay.android.api.enums.ResultCode;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
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
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import retrofit2.Call;

/**
 * SecurityHandler is designed to create and manage @ECCKeyPair object.
 */
final class SecurityHandler {
    private static final String ALGORITHM = "ECDH";
    private static final String EC_CURVE = "secp256r1";
    private static final String KEY_TYPE = "AES";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Constants.KEY_API,
            Constants.KEY_RTM,
            Constants.KEY_WEB
    })
    public @interface KeyType {
    }

    static {
        Security.addProvider(BouncyCastleProviderSingleton.getInstance());
    }

    private static SecurityHandler sInstance;

    public static SecurityHandler getInstance() {
        if (sInstance == null) {
            sInstance = new SecurityHandler();
        }

        return sInstance;
    }

    private Map<Integer, ECCKeyPair> mKeysMap;

    private SecurityHandler() {
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

    private SecretKey getSecretKey(@KeyType int type) {

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

    private ECCKeyPair getPairForType(@KeyType int type) {
        return mKeysMap.get(type);
    }

    public void updateECCKey(final @KeyType int type, @NonNull final Runnable successRunnable, final ApiCallback callback) {

        try {
            ECCKeyPair keyPair = createECCKeyPair();
            mKeysMap.put(type, keyPair);

            ApiCallback<ECCKeyPair> apiCallback = new ApiCallback<ECCKeyPair>() {
                @Override
                public void onSuccess(ECCKeyPair result) {
                    result.setPrivateKey(mKeysMap.get(type).getPrivateKey());
                    mKeysMap.put(type, result);

                    successRunnable.run();
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

    public String getEncryptedString(@KeyType int type, String decryptedString) {

        JWEAlgorithm alg = JWEAlgorithm.A256GCMKW;
        EncryptionMethod enc = EncryptionMethod.A256GCM;

        ECCKeyPair keyPair = getPairForType(type);

        JWEHeader.Builder jweHeaderBuilder = new JWEHeader.Builder(alg, enc)
                .contentType("application/json")
                .keyID(keyPair.getKeyId());

        JWEHeader header = jweHeaderBuilder.build();
        Payload payload = new Payload(decryptedString);
        JWEObject jweObject = new JWEObject(header, payload);
        try {
            JWEEncrypter encrypter = new AESEncrypter(SecurityHandler.getInstance().getSecretKey(type));
            jweObject.encrypt(encrypter);
        } catch (JOSEException e) {
            Constants.printError(e.toString());
        }

        return jweObject.serialize();
    }

    public String getDecryptedString(@KeyType int type, String encryptedString) {

        JWEObject jweObject;
        try {
            jweObject = JWEObject.parse(encryptedString);
            jweObject.decrypt(new AESDecrypter(getSecretKey(type)));
            return jweObject.getPayload().toString();
        } catch (ParseException | JOSEException e) {
            Constants.printError(e.toString());
        }

        return null;
    }
}
