package com.amina.campfootainment;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        int SPLASH_TIMEOUT = 2000;

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent LoginPageIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(LoginPageIntent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
