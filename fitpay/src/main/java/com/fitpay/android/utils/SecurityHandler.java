package com.fitpay.android.utils;

import com.fitpay.android.FitPay;
import com.fitpay.android.models.ECCKeyPair;
import com.fitpay.android.units.APIUnit;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;

import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
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
import java.util.UUID;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SecurityHandler is designed to create and manager @ECCKeyPair object.
 */
public class SecurityHandler {
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
    public void updateECCKeyPair() {

        mSecretKey = null;

        try {
            mKeyPair = createECCKeyPair();

            Call<ECCKeyPair> getServerKeyCall = FitPay.getInstance()
                    .getUnit(APIUnit.class).getFitPayClient().createEncryptionKey(mKeyPair);
            getServerKeyCall.enqueue(new Callback<ECCKeyPair>() {
                @Override
                public void onResponse(Call<ECCKeyPair> call, Response<ECCKeyPair> response) {
                    if (response.isSuccess() && response.body() != null) {

                        mKeyPair.refreshWithNewData(response.body());
                        mSecretKey = getSecretKey();

                    } else {
                        try {
                            C.printError(response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ECCKeyPair> call, Throwable t) {
                    C.printError(t.toString());
                }
            });
        } catch (Exception e) {
            C.printError(e.toString());
        }
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
            C.printError("error generating shared secret:" + e.toString());
            return null;
        }
    }

    public String getEncryptedString(String decryptedString) {

        JWEAlgorithm alg = JWEAlgorithm.A256GCMKW;
        EncryptionMethod enc = EncryptionMethod.A256GCM;

        JWEHeader.Builder jweHeaderBuilder = new JWEHeader.Builder(alg, enc);
        JWEHeader header = jweHeaderBuilder.build();
        Payload payload = new Payload(decryptedString);
        JWEObject jweObject = new JWEObject(header, payload);
        JWEEncrypter encrypter = null;
        try {
            encrypter = new AESEncrypter(SecurityHandler.getInstance().getSecretKey());
            jweObject.encrypt(encrypter);
        } catch (JOSEException e) {
            C.printError(e.toString());
        }

        String strHeader = jweObject.getHeader().toJSONObject().toString();
        return jweObject.serialize();
    }

    public String getDecryptedString(String encryptedString) {
        String decryptedString = encryptedString;
        return decryptedString;
    }
}
