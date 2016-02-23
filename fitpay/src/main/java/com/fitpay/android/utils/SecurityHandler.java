package com.fitpay.android.utils;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.callbacks.CallbackWrapper;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.ECCKeyPair;
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

    private static SecurityHandler sInstance;

    static {
        Security.addProvider(BouncyCastleProviderSingleton.getInstance());
    }

    public static SecurityHandler getInstance() {
        if (sInstance == null) {
            sInstance = new SecurityHandler();
        }

        return sInstance;
    }

    private ECCKeyPair mKeyPair;
    private SecretKey mSecretKey;

    private SecurityHandler() {
    }

    // Create the public and private keys
    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        keyGenerator.initialize(new ECGenParameterSpec(EC_CURVE), new SecureRandom());
        return keyGenerator.generateKeyPair();
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

    public ECCKeyPair createECCKeyPair() throws Exception {
        KeyPair keyPair = generateKeyPair();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        ECCKeyPair eccKeyPair = new ECCKeyPair();
        eccKeyPair.setKeyId(UUID.randomUUID().toString());
        eccKeyPair.setPrivateKey(Hex.toHexString(privateKey.getEncoded()));
        eccKeyPair.setPublicKey(Hex.toHexString(publicKey.getEncoded()));

        return eccKeyPair;
    }

    /**
     * update existing keyPair with a new one
     */
    public void updateECCKeyPair(final ApiCallback callback) {

        mSecretKey = null;

        try {
            mKeyPair = createECCKeyPair();

            ApiCallback<ECCKeyPair> apiCallback = new ApiCallback<ECCKeyPair>() {
                @Override
                public void onSuccess(ECCKeyPair result) {
                    mKeyPair.refreshWithNewData(result);
                    mSecretKey = getSecretKey();

                    if(callback != null){
                        callback.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                    Constants.printError(errorMessage);

                    if(callback != null){
                        callback.onFailure(errorCode, errorMessage);
                    }
                }
            };

            Call<ECCKeyPair> getKeyCall = ApiManager.getInstance().getClient().createEncryptionKey(mKeyPair);
            getKeyCall.enqueue(new CallbackWrapper<>(apiCallback));

        } catch (Exception e) {
            Constants.printError(e.toString());
        }
    }

    public String getKeyId(){
        if(mKeyPair != null){
            return mKeyPair.getKeyId();
        }

        return null;
    }

    //TODO: refresh secret if null
    public SecretKey getSecretKey() {
        if (mSecretKey == null && mKeyPair != null && mKeyPair.getServerPublicKey() != null) {
            mSecretKey = getSecretKey(mKeyPair.getPrivateKey(), mKeyPair.getServerPublicKey());
        }
        return mSecretKey;
    }

    public SecretKey getSecretKey(String privateKeyStr, String publicKeyStr) {

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

    public String getEncryptedString(String decryptedString) {

        JWEAlgorithm alg = JWEAlgorithm.A256GCMKW;
        EncryptionMethod enc = EncryptionMethod.A256GCM;

        JWEHeader.Builder jweHeaderBuilder = new JWEHeader.Builder(alg, enc)
                .contentType("application/json")
                .keyID(mKeyPair.getKeyId());

        JWEHeader header = jweHeaderBuilder.build();
        Payload payload = new Payload(decryptedString);
        JWEObject jweObject = new JWEObject(header, payload);
        JWEEncrypter encrypter = null;
        try {
            encrypter = new AESEncrypter(SecurityHandler.getInstance().getSecretKey());
            jweObject.encrypt(encrypter);
        } catch (JOSEException e) {
            Constants.printError(e.toString());
        }

        String strHeader = jweObject.getHeader().toJSONObject().toString();
        return jweObject.serialize();
    }

    public String getDecryptedString(String encryptedString) {

        JWEObject jweObject;
        try {
            jweObject = JWEObject.parse(encryptedString);
            jweObject.decrypt(new AESDecrypter(getSecretKey()));
            return jweObject.getPayload().toString();
        } catch (ParseException | JOSEException e) {
            Constants.printError(e.toString());
        }

        return null;
    }
}
