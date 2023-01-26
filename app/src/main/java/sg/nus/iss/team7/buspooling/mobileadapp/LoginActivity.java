package sg.nus.iss.team7.buspooling.mobileadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sg.nus.iss.team7.buspooling.mobileadapp.model.CustomerDTO;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.ApiMethods;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.ApiUtility;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.RetroFitClient;

public class LoginActivity extends AppCompatActivity {

    EditText mUsername,mPassword;
    Button loginBtn,registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);

        //check if already logged in with SharedPref
        loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                //check if fields are empty
                if(username.isEmpty()){
                    mUsername.setError("Username cannot be empty");
                }
                if(password.isEmpty()){
                    mPassword.setError("Password cannot be empty");
                }

                if(!username.isEmpty() && !password.isEmpty()){
//                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(ApiUtility.BASE_URL)
//                            .client(client)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();

                    Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                    ApiMethods api = retrofit.create(ApiMethods.class);

                    CustomerDTO customerCheckValidLogin = new CustomerDTO();
                    customerCheckValidLogin.setUsername(username);
                    customerCheckValidLogin.setPassword(password);
                    Call<CustomerDTO> loginCustomerCall = api.loginCustomer(customerCheckValidLogin);
                    loginCustomerCall.enqueue(new Callback<CustomerDTO>() {
                        @Override
                        public void onResponse(Call<CustomerDTO> call, Response<CustomerDTO> response) {
                            if(response.isSuccessful()){
                                CustomerDTO newRegisteredCustomer = response.body();
                                Toast.makeText(getApplicationContext(),"Login successful" + newRegisteredCustomer,Toast.LENGTH_SHORT).show();
                            }
                            else {
                                int statusCode = response.code();
                                if (statusCode == 500) {
                                    Toast.makeText(getApplicationContext(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                                }
                                else if (statusCode == 404){
                                    Toast.makeText(getApplicationContext(), "No Such Registered User", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<CustomerDTO> call, Throwable t) {
                            if (t instanceof IOException) {
                                Toast.makeText(LoginActivity.this, "Network Failure ", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "JSON Parsing Issue", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterActivity();
            }
        });
    }

    public void launchRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }



}