package com.amina.campfootainment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class InfoPersonal extends AppCompatActivity
{

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_personel);

        mAuth = FirebaseAuth.getInstance();
    }

    public void logout(View view)
    {
        mAuth.signOut();
        startActivity(new Intent(InfoPersonal.this,LoginActivity.class));
        finish();
    }
}
