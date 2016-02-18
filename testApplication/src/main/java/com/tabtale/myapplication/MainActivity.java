package com.tabtale.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fitpay.android.FitPay;
import com.fitpay.android.api.FitPayService;
import com.fitpay.android.api.oauth.OAuthConfig;
import com.fitpay.android.models.ECCKeyPair;
import com.fitpay.android.models.ResultCollection;
import com.fitpay.android.models.User;
import com.fitpay.android.units.APIUnit;
import com.fitpay.android.utils.C;
import com.fitpay.android.utils.SecurityHandler;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;

import java.io.IOException;
import java.security.Security;
import java.text.ParseException;

import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: replace this application with unit test
public class MainActivity extends AppCompatActivity {

    private FitPayService fitPayAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Security.addProvider(BouncyCastleProviderSingleton.getInstance());

        testGetUsers();
//        testSecretKeys();
//        testServerSecretKey();
    }

    private void testGetUsers(){
        APIUnit api = new APIUnit(new OAuthConfig("e362a5cd-ab9d-4f9a-98ff-f91fcdd27936","s2CLUBKcbvQP6IqKx31XLclyqAd3nf6tyIPk74rL"));
        api.setAuthCallback(new APIUnit.IAuthCallback() {
            @Override
            public void onSuccess(FitPayService apiClient) {
                fitPayAPI = apiClient;
                getUsers();
            }

            @Override
            public void onError(String error) {

            }
        });

        FitPay fp = FitPay.init(this);
        fp.addUnit(api);
    }

    private void getUsers(){
        Call<ResultCollection<User>> usersCall = fitPayAPI.getUsers(10, 0);
        usersCall.enqueue(new Callback<ResultCollection<User>>() {
            @Override
            public void onResponse(Call<ResultCollection<User>> call, Response<ResultCollection<User>> response) {
                if (response.body().getResults().size() == 0) {
                    createUser();
                }
            }

            @Override
            public void onFailure(Call<ResultCollection<User>> call, Throwable t) {
                C.printError(t.toString());
            }
        });
    }

    private void createUser(){
        final User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate("1967-06-23");
        user.setEmail("john@doe.com");

        Call<User> createUserCall = fitPayAPI.createUser(user);
        createUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccess() && response.body() != null){
                    Log.i("SUCCESS", response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                C.printError(t.toString());
            }
        });
    }

    private void testSecretKeys(){
        SecurityHandler handler = SecurityHandler.getInstance();
        ECCKeyPair keyPairOne = null;
        ECCKeyPair keyPairTwo = null;

        try {
            keyPairOne = handler.createECCKeyPair();
            keyPairTwo = handler.createECCKeyPair();
        } catch (Exception e) {
        }

        if(keyPairOne != null && keyPairTwo != null){

            Log.i("KEY 1", "public: " + keyPairOne.getPublicKey());
            Log.i("KEY 1", "private: " + keyPairOne.getPrivateKey());

            Log.i("KEY 2", "public: " + keyPairTwo.getPublicKey());
            Log.i("KEY 2", "private: " + keyPairTwo.getPublicKey());

            SecretKey secretKey1 = handler.getSecretKey(keyPairOne.getPrivateKey(), keyPairTwo.getPublicKey());
            SecretKey secretKey2 = handler.getSecretKey(keyPairTwo.getPrivateKey(), keyPairOne.getPublicKey());

            String thisIsSparta = "This is SPARTAAAAA!!!";

            JWEAlgorithm alg = JWEAlgorithm.A256GCMKW;
            EncryptionMethod enc = EncryptionMethod.A256GCM;

            JWEHeader.Builder jweHeaderBuilder = new JWEHeader.Builder(alg, enc);
            JWEHeader header = jweHeaderBuilder.build();
            Payload payload = new Payload(thisIsSparta);
            JWEObject jweObject = new JWEObject(header, payload);
            JWEEncrypter encrypter = null;
            try {
                encrypter = new AESEncrypter(secretKey1);
                jweObject.encrypt(encrypter);
            } catch (KeyLengthException e) {
                e.printStackTrace();
            } catch (JOSEException e) {
                e.printStackTrace();
            }

            String jwe = jweObject.serialize();

            try {
                jweObject = JWEObject.parse(jwe);
                jweObject.decrypt(new AESDecrypter(secretKey2));
            } catch (JOSEException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String newString = jweObject.getPayload().toString();

            if(thisIsSparta.equals(newString)){
                Log.i("ENC_DEC", "SUCCESS");
            }
        }
    }

    public void testServerSecretKey(){

        APIUnit api = new APIUnit(new OAuthConfig("e362a5cd-ab9d-4f9a-98ff-f91fcdd27936","s2CLUBKcbvQP6IqKx31XLclyqAd3nf6tyIPk74rL"));
        FitPay fp = FitPay.init(this);
        fp.addUnit(api);

        SecurityHandler handler = SecurityHandler.getInstance();
        ECCKeyPair myKey = null;
        try {
            myKey = handler.createECCKeyPair();
        } catch (Exception e) {
        }

        ECCKeyPair encKeyPair = new ECCKeyPair();
        encKeyPair.setPublicKey(myKey.getPublicKey());

        Call<ECCKeyPair> getServerKey = api.getFitPayClient().createEncryptionKey(encKeyPair);
        getServerKey.enqueue(new Callback<ECCKeyPair>() {
            @Override
            public void onResponse(Call<ECCKeyPair> call, Response<ECCKeyPair> response) {
                if(response.isSuccess() && response.body() != null){
                    Log.i("KEY", response.body().getServerPublicKey());
                } else if (response.errorBody() != null){
                    try {
                        String errorMessage = response.errorBody().string();
                        C.printError(errorMessage);
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
    }
}
