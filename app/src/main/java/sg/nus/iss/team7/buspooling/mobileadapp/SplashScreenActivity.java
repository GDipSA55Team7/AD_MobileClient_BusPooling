package sg.nus.iss.team7.buspooling.mobileadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.window.SplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

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

        setContentView(R.layout.splashscreen_activity);

        // Use a thread to delay the splash screen for 2.5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                // close splash activity
                finish();
            }
        }, 2500); // 2.5 seconds delay
    }
}