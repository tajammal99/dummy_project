package com.amina.campfootainment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextView sup;

    private TextView LoginButon;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink,ForgetPasswordLink;
    private String currentUserID;
    private DatabaseReference databaseReference;

    private ProgressDialog loadingBar;
    private DatabaseReference UsersRef;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private static final Pattern sEmail_PATTERN =
            Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Participants");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        InitializedFields();

        sup = findViewById(R.id.sup);

        sup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(LoginActivity.this, SignUp.class);
                startActivity(it);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        LoginButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });
    }

    public void gotoSignup(View view)
    {
        Intent it = new Intent(LoginActivity.this, SignUp.class);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (currentUser != null)
        {

            loadingBar.setTitle("Loading");
            loadingBar.setMessage("Please wait while we are checking User type");
            loadingBar.show();

            sendToUserHome();
        }
    }

    private void AllowUserToLogin()
    {
        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString().trim();


        if (email.isEmpty())
        {
            Toast.makeText(this, "Kindly enter your email", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty())
        {
            Toast.makeText(this, "Kindly enter password", Toast.LENGTH_SHORT).show();
        }
        else
        {

            if (confirmInput())
            {
                loadingBar.setTitle("Logging in");
                loadingBar.setMessage("Please wait.......");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult)
                                        {
                                            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                            String deviceToken = instanceIdResult.getToken();


                                            UsersRef.child(currentUserID).child("device_token")
                                                    .setValue(deviceToken)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                checkEmailVerification();
                                                                loadingBar.dismiss();
                                                            }
                                                        }
                                                    });

                                        }
                                    });

                                }
                                else
                                {
                                    String message = Objects.requireNonNull(task.getException()).toString();
                                    Toast.makeText(LoginActivity.this,"Error : "+ message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }




        }
    }


    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailFlag = Objects.requireNonNull(firebaseUser).isEmailVerified();

        if (emailFlag)
        {
            sendToUserHome();
        }
        else
        {
            Toast.makeText(this, "please verify your email", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

    private void InitializedFields()
    {
        LoginButon = findViewById(R.id.btn_login);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.signup_btn_link);
        ForgetPasswordLink = findViewById(R.id.forgot_password_link);

        loadingBar = new ProgressDialog(this);
    }


    private void SendUserToMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this,HomeP.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
    public boolean confirmInput() {
        boolean result = true;
        if (!validateEmail())
        {
            result = false;
        }

        return result;
    }

    public void onBackPressed() {
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?");
            builder.setCancelable(false);
            builder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            finish();
                        }
                    });
            builder.setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }

                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void gotForgoTPassword(View view)
    {
        Intent it = new Intent(LoginActivity.this, ForgotPassword.class);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToUserHome()
    {
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Participants").child(currentUserID);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String type = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();

                if (type.equals("admin"))
                {
                    loadingBar.dismiss();
                    startActivity(new Intent(LoginActivity.this,AdminHome.class));
                    finish();
                }
                else
                {
                    loadingBar.dismiss();
                    SendUserToMainActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
