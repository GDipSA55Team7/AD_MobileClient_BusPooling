package sg.nus.iss.team7.buspooling.mobileadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.buspooling.mobileadapp.model.CustomerDTO;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.ApiMethods;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.RetroFitClient;
import sg.nus.iss.team7.buspooling.mobileadapp.utility.UtilityConstant;

public class LoginActivity extends AppCompatActivity {

    EditText mUsername,mPassword;
    Button loginBtn,registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(isLoggedIn()) {
            //go to selectGroupActivity
            launchSelectGroupActivity();
        }

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
                                CustomerDTO currentCustomer = response.body();
                                Toast.makeText(getApplicationContext(),"Login successful, welcome " + currentCustomer.getName(),Toast.LENGTH_SHORT).show();
                                storeUserDetailsInSharedPref(currentCustomer);
                                launchSelectGroupActivity();


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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle("Leaving Application")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void launchRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    private void launchSelectGroupActivity(){
        Intent intent = new Intent(LoginActivity.this,SelectGroupActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean isLoggedIn(){
        SharedPreferences userDetailsSharedPref = getSharedPreferences("userDetails",MODE_PRIVATE);
        if(userDetailsSharedPref.contains("username") && userDetailsSharedPref.contains("password")){
            return true;
        }
        return false;
    }
    private void storeUserDetailsInSharedPref(CustomerDTO customerDTO){
        Gson gson = new Gson();
        String json = gson.toJson(customerDTO);
        SharedPreferences sharedPreferences = getSharedPreferences(UtilityConstant.CUSTOMER_CREDENTIALS, MODE_PRIVATE);
        sharedPreferences.edit().putString(UtilityConstant.CUSTOMER, json).apply();
    }

}