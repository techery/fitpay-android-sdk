package com.tabtale.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fitpay.android.FitPay;
import com.fitpay.android.api.FitPayService;
import com.fitpay.android.api.oauth.OAuthConfig;
import com.fitpay.android.models.User;
import com.fitpay.android.models.UsersCollection;
import com.fitpay.android.units.APIUnit;
import com.fitpay.android.utils.C;

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

    public void getUsers(){
        Call<UsersCollection> usersCall = fitPayAPI.getUsers(10, 0);
        usersCall.enqueue(new Callback<UsersCollection>() {
            @Override
            public void onResponse(Call<UsersCollection> call, Response<UsersCollection> response) {
                if(response.body().getResults().size() == 0){
                    createUser();
                }
            }

            @Override
            public void onFailure(Call<UsersCollection> call, Throwable t) {
                Log.i(C.FIT_PAY_ERROR_TAG, t.toString());
            }
        });
    }

    public void createUser(){
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate("1967-06-23");
        user.setEmail("john@doe.com");

    }
}
