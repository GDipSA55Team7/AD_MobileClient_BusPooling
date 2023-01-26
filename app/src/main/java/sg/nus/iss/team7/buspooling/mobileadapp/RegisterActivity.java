package sg.nus.iss.team7.buspooling.mobileadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class RegisterActivity extends AppCompatActivity {

    EditText mName,mUserName,mPassword,mEmail,mPostalCode,mAddress;
    Button mRegister;
    String userName,password,email,postalCode;
    boolean nameIsValid ,usernameIsValid,passwordIsValid,emailIsValid,
            postalCodeIsValid,addressIsValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initElements();

        mRegister = findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if validated
                if(!allFieldsValid()){
                    Toast.makeText(getApplicationContext(),"hi" + nameIsValid + usernameIsValid +passwordIsValid + emailIsValid + postalCodeIsValid +addressIsValid,Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),"Pls ensure fields are completed or in the right format" + nameIsValid + usernameIsValid +passwordIsValid+emailIsValid+postalCodeIsValid+addressIsValid,Toast.LENGTH_SHORT).show();
                }
                else{
                    //store into shared Pref
                    SharedPreferences userDetailsSharedPref = getSharedPreferences("userDetails",MODE_PRIVATE);
                    SharedPreferences.Editor editor = userDetailsSharedPref.edit();
                    editor.putString("username",userName);
                    editor.putString("password",password);
                    editor.commit();

                    //register via api must start bg thread

                    //create retrofit object
                    Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                    ApiMethods api = retrofit.create(ApiMethods.class);

                    String address = mAddress.getText().toString().trim();
                    String postalCode = mPostalCode.getText().toString().trim();
                    String userName = mUserName.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    String email = mEmail.getText().toString().trim();
                    String name = mName.getText().toString().trim();
                    CustomerDTO customer = new CustomerDTO();
                    customer.setUsername(userName);
                    customer.setAddress(address);
                    customer.setPostalCode(postalCode);
                    customer.setPassword(password);
                    customer.setEmail(email);
                    customer.setName(name);

                    Call<CustomerDTO> registerNewCustomerCall = api.registerNewCustomer(customer);
                    //enqueue is async call
                    registerNewCustomerCall.enqueue(new Callback<CustomerDTO>() {
                        @Override
                        public void onResponse(Call<CustomerDTO> call, Response<CustomerDTO> response) {
                            if(response.isSuccessful()){
                                CustomerDTO newRegisteredCustomer = response.body();
                                Toast.makeText(getApplicationContext(),"Register successful" + newRegisteredCustomer,Toast.LENGTH_SHORT).show();
                            }
                            else{
                                int statusCode = response.code();
                                if(statusCode == 500){
                                    Toast.makeText(getApplicationContext(),"INTERNAL SERVER ERROR",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<CustomerDTO> call, Throwable t) {
                            if (t instanceof IOException) {
                                Toast.makeText(RegisterActivity.this, "Network Failure ", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "JSON Parsing Issue", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //Register success
//                    startActivity(new Intent(StartActivity.this, StartActivityPart2.class));
//                    Toast.makeText(StartActivity.this, "Your account is suspended.", Toast.LENGTH_SHORT).show();
//                    finish();
            }
        });

        Button clearFieldsBtn = findViewById(R.id.clearAllFields);
        clearFieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout =  findViewById(R.id.linearlayoutRegisterActivity);
                clearForm(linearLayout);
            }
        });


    }

    private void initElements(){
        mName = findViewById(R.id.name);
        mName.addTextChangedListener(nameTextWatcher);

        mEmail = findViewById(R.id.email);
        mEmail.addTextChangedListener(emailTextWatcher);

        mUserName= findViewById(R.id.username);
        mUserName.addTextChangedListener(userNameTextWatcher);

        mPassword = findViewById(R.id.password);
        mPassword.addTextChangedListener(passwordTextWatcher);

        mPostalCode = findViewById(R.id.postalCode);
        mPostalCode.addTextChangedListener(postalCodeTextWatcher);

        mAddress = findViewById(R.id.address);
        mAddress.addTextChangedListener(addressTextWatcher);
    }

    private TextWatcher nameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            nameIsValid = updateErrorTxt(mName,"Name",3,20);
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher userNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            usernameIsValid = updateErrorTxt(mUserName,"UserName",3,20);
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            passwordIsValid = updateErrorTxt(mPassword,"Password",3,20);
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher addressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            addressIsValid  = updateErrorTxt(mAddress,"Address",5,60);
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            email = mEmail.getText().toString().trim();
            if(!validateEmail(email).equals("")){
                mEmail.setError(validateEmail(email));
                if(emailIsValid){
                    emailIsValid = false;
                }
            }
            else{
                if(!emailIsValid){
                    emailIsValid = true;
                }
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher postalCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            postalCode = mPostalCode.getText().toString().trim();
            //only take in number so just validate length
            if(postalCode.isEmpty()){
                mPostalCode.setError("Postal Code must not be empty");
                if(postalCodeIsValid){
                    postalCodeIsValid = false;
                }
            }
            else if (postalCode.length() > 6){
                mPostalCode.setError("Postal Code should only be 6 digits long");
                if(postalCodeIsValid){
                    postalCodeIsValid = false;
                }
            }
            else{
                if(!postalCodeIsValid){
                    postalCodeIsValid = true;
                }
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private String validateEmail(String emailInput) {
        // for normal email  - String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern = "[a-zA-Z0-9._-]+@u.nus.edu";
        if (emailInput.isEmpty()){
            return "Email cannot be empty";
        }
        if(!emailInput.matches(emailPattern)){
            return "Please enter valid NUS email Format - ABC@u.nus.edu";
        }
        return "";
    }

    private boolean updateErrorTxt(EditText editTxt, String fieldName, int minChar, int maxChar){

        boolean fieldIsValid = true;
        String checkFieldStr = editTxt.getText().toString().trim();

        if(checkFieldStr.isEmpty()){
            editTxt.setError(fieldName +" must not be empty");
            if(fieldIsValid){
                fieldIsValid = false;
            }
        }
        else if (checkFieldStr.length() < minChar || checkFieldStr.length() > maxChar){
            editTxt.setError(fieldName + " must be between " +minChar + " and " + maxChar + " characters");
            if(fieldIsValid){
                fieldIsValid = false;
            }
        }
        return fieldIsValid;
    }

    private boolean allFieldsValid(){
        if(nameIsValid && usernameIsValid && passwordIsValid && emailIsValid && postalCodeIsValid && addressIsValid){
            return true;
        }
        else{
            return false;
        }
    }
    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

}