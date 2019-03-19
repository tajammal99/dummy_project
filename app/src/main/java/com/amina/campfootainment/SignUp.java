package com.amina.campfootainment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private TextView sin;
    private EditText UserEmail,UserPassword,UserConfirmPassword,UserName;
    private ProgressDialog loadingBar;
    private TextView CreateAccountButton;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private String email,password,confirmPassword,userName;



    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    private static final Pattern sEmail_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@" + "lgu.edu.pk");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        InitializedFields();

        sin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(SignUp.this, LoginActivity.class);
                startActivity(it);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                email = UserEmail.getText().toString().trim();
                password = UserPassword.getText().toString().trim();
                confirmPassword = UserConfirmPassword.getText().toString().trim();
                userName = UserName.getText().toString().trim();

                if (userName.isEmpty())
                {
                    UserName.setError("Field can't be empty");
                }
                else if (email.isEmpty())
                {
                    UserEmail.setError("Field can't be empty");
                }
                else if (password.isEmpty())
                {
                    UserPassword.setError("Field can't be empty");
                }
                else if (confirmPassword.isEmpty())
                {
                    UserConfirmPassword.setError("Field can't be empty");
                }
                else
                {
                    if (confirmInput())
                    {
                        CreateNewAccount();
                    }
                }
            }
        });
    }


    private void CreateNewAccount()
    {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait while we are creating your account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,confirmPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                RootRef.child("Participants").child(currentUserID).child("userName").setValue(userName);
                                RootRef.child("Participants").child(currentUserID).child("email").setValue(email);
                                RootRef.child("Participants").child(currentUserID).child("type").setValue("user");
                                sendEmailVerification();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(SignUp.this,"Error : "+ message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
    }

    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(SignUp.this, "Successfully Registered, Verification email sent", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUp.this,LoginActivity.class));

                    }
                    else
                    {
                        Toast.makeText(SignUp.this, "verification email has't been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateEmail()
    {
        String emailInput = UserEmail.getText().toString().trim();

        if (emailInput.isEmpty())
        {
            UserEmail.setError("Field can't be empty");
            return false;
        }
        else if (!sEmail_PATTERN.matcher(emailInput).matches())
        {
            UserEmail.setError("Please enter a valid email address");
            return false;
        }
        else
        {
            UserEmail.setError(null);
            return true;
        }

    }

    private boolean validatePassword()
    {
        String passwordInput = UserConfirmPassword.getText().toString().trim();

        if (passwordInput.isEmpty())
        {
            UserPassword.setError("Field can't be empty");
            return false;
        }
        else if (!password.equals(confirmPassword))
        {
            UserPassword.setError("Mismatch Password");
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            UserConfirmPassword.setError("Password too weak");
            return false;
        } else {
            UserConfirmPassword.setError(null);
            return true;
        }
    }

    public boolean confirmInput() {
        boolean result = true;
        if (!validateEmail() || !validatePassword())
        {
            result = false;
        }

        return result;
    }

    private void InitializedFields()
    {
        UserName = findViewById(R.id.reg_username);
        UserEmail = findViewById(R.id.reg_email);
        UserPassword = findViewById(R.id.reg_password);
        UserConfirmPassword = findViewById(R.id.reg_confirm_password);
        sin = findViewById(R.id.sin);
        CreateAccountButton = findViewById(R.id.btn_create_account);

        loadingBar = new ProgressDialog(this);
    }
}
