package com.fitpay.android.utils;

import android.util.Log;

import com.fitpay.android.models.ECCKeyPair;
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
import java.util.UUID;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

/**
 * Created by Vlad on 16.02.2016.
 */
public class KeyHandler {
    private static final String ALGORITHM = "ECDH";
    private static final String EC_CURVE = "secp256r1";
    private static final String KEY_TYPE = "AES";

    static {
        Security.addProvider(BouncyCastleProviderSingleton.getInstance());
    }

    public KeyHandler() {
    }

    // Create the public and private keys
    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        keyGenerator.initialize(new ECGenParameterSpec(EC_CURVE), new SecureRandom());
        return keyGenerator.generateKeyPair();
    }

    public SecretKey generateSharedSecret(String privateKeyStr, String publicKeyStr) {
        try {
            PrivateKey privateKey = getPrivateKey(Hex.decode(privateKeyStr));
            PublicKey publicKey = getPublicKey(Hex.decode(publicKeyStr));

            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);

            return keyAgreement.generateSecret(KEY_TYPE);
        } catch (Exception e) {
            Log.e(C.FIT_PAY_ERROR_TAG, "error generating shared secret:" + e.toString());
            return null;
        }
    }

    public ECCKeyPair getKeyPair() throws Exception {
        KeyPair keyPair = generateKeyPair();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        ECCKeyPair eccKeyPair = new ECCKeyPair();
        eccKeyPair.setKeyId(UUID.randomUUID().toString());
        eccKeyPair.setPrivateKey(Hex.toHexString(privateKey.getEncoded()));
        eccKeyPair.setPublicKey(Hex.toHexString(publicKey.getEncoded()));

        return eccKeyPair;
    }

    // methods for ASN.1 encoded keys
    protected PrivateKey getPrivateKey(byte[] privateKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        return kf.generatePrivate(keySpec);
    }

    protected PublicKey getPublicKey(byte[] publicKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, BouncyCastleProviderSingleton.getInstance());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        return kf.generatePublic(keySpec);
    }
}
