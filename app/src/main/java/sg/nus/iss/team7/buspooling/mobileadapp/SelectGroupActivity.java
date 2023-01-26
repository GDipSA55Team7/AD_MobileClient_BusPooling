package sg.nus.iss.team7.buspooling.mobileadapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.nus.iss.team7.buspooling.mobileadapp.adapter.GroupAdapter;
import sg.nus.iss.team7.buspooling.mobileadapp.model.CustomerDTO;
import sg.nus.iss.team7.buspooling.mobileadapp.utility.UtilityConstant;

public class SelectGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        //pull all groups and display
        CustomerDTO customerDTO = readFromSharedPref();

        List<String> data = Arrays.asList("Semester1", "Semester2");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GroupAdapter adapter = new GroupAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                        SelectGroupActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private CustomerDTO readFromSharedPref(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(UtilityConstant.CUSTOMER_CREDENTIALS, MODE_PRIVATE);
        String json = sharedPreferences.getString(UtilityConstant.CUSTOMER, null);
        CustomerDTO customerDTO = gson.fromJson(json, CustomerDTO.class);
        return customerDTO;
    }
}