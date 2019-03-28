package com.amina.campfootainment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {

    private EditText passwordEmail;
    private TextView resetPassword;
    private FirebaseAuth firebaseAuth;
    private static final Pattern sEmail_PATTERN =
            Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        passwordEmail = findViewById(R.id.forgot_email);
        resetPassword = findViewById(R.id.btn_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = passwordEmail.getText().toString().trim();



                    if (confirmInput())
                    {

                        progressDialog.setMessage("Loading.....");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();


                        mAuth.fetchProvidersForEmail(passwordEmail.getText().toString())
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(ForgotPassword.this, "User does't exists", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<ProviderQueryResult> task)
                                    {
                                        firebaseAuth.sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ForgotPassword.this, "password reset email sent", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
                                                }
                                                else
                                                {
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });
                    }
            }
        });
    }
    private boolean validateEmail()
    {
        String emailInput = passwordEmail.getText().toString().trim();

        if (emailInput.isEmpty())
        {
            passwordEmail.setError("Field can't be empty");
            return false;
        }
        else if (!sEmail_PATTERN.matcher(emailInput).matches())
        {
            passwordEmail.setError("Please enter a valid email address");
            return false;
        }
        else
        {
            passwordEmail.setError(null);
            return true;
        }

    }
    public boolean confirmInput() {
        boolean result = true;
        if (!validateEmail())
        {
            result = false;
        }

        return result;
    }
}
