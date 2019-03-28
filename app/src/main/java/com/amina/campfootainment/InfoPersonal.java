package com.amina.campfootainment;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class InfoPersonal extends AppCompatActivity
{

    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_personel);


        viewPager = findViewById(R.id.myViewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),1000,2000);

    }

    public class MyTimerTask extends TimerTask
    {

        @Override
        public void run() {
            InfoPersonal.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0)
                    {
                        viewPager.setCurrentItem(1);
                    }
                    else if (viewPager.getCurrentItem() == 1)
                    {
                        viewPager.setCurrentItem(2);
                    }
                    else
                    {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

}
