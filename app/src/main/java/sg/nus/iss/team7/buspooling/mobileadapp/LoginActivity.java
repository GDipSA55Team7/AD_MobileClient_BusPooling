package sg.nus.iss.team7.buspooling.mobileadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sg.nus.iss.team7.buspooling.mobileadapp.model.CustomerDTO;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.ApiMethods;
import sg.nus.iss.team7.buspooling.mobileadapp.retrofit.ApiUtility;

public class LoginActivity extends AppCompatActivity {

    EditText mUsername,mPassword;
    Button loginBtn,registerBtn;
    ApiMethods apiMethod;
    CustomerDTO customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide the title bar and enable full screen mode in an Android app.
        //https://www.javatpoint.com/android-hide-title-bar-example
        //  hides the title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getSupportActionBar() method to hide the title bar.
        getSupportActionBar().hide();
        // set flags for full screen mode , with the FLAG_FULLSCREEN flag being set for the WindowManager.LayoutParams.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

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

                    // validate username,password with bg thread via rest api
                    //create retrofit object and define base url
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiUtility.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    apiMethod = ApiUtility.getMethodCallAPI();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CustomerDTO customerCheckValidLogin = new CustomerDTO();
                            customerCheckValidLogin.setUsername(username);
                            customerCheckValidLogin.setPassword(password);
                            Call<CustomerDTO> loginCustomerCall = apiMethod.loginCustomer(customerCheckValidLogin);
                            loginCustomerCall.enqueue(new Callback<CustomerDTO>() {
                                @Override
                                public void onResponse(Call<CustomerDTO> call, Response<CustomerDTO> response) {
                                    customer = response.body();
                                    if(customer != null){
                                        Toast.makeText(getApplicationContext(),"No such profile in our system",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<CustomerDTO> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"Error with login call",Toast.LENGTH_SHORT).show();
                                    Log.i("ERROR: ", t.getMessage());
                                }
                            });
                        }
                    }).start();

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
        finish();
    }


}