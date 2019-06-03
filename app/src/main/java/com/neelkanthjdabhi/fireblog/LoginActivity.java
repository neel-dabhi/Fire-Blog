package com.neelkanthjdabhi.fireblog;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText loginEmailText,loginPassText;
    private Button loginButton;
    private TextView signup_btn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            loginEmailText = (TextInputEditText) findViewById(R.id.reg_email);
            loginPassText = (TextInputEditText) findViewById(R.id.reg_password);
            loginButton = (Button) findViewById(R.id.setup_doneBtn);
            signup_btn =(TextView) findViewById(R.id.signup_btn);
            progressBar = (ProgressBar) findViewById(R.id.login_progress);
            mAuth = FirebaseAuth.getInstance();



            signup_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent regIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(regIntent);
                }
            });


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String login_email = loginEmailText.getText().toString();
                    String login_password = loginPassText.getText().toString();

                    if (!TextUtils.isEmpty(login_email) && !TextUtils.isEmpty(login_password))
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(login_email,login_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()){
                                 sendToMainActivity();

                             }else {

                                 String errorMessage = task.getException().getMessage();

                                 Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                         "ERROR : " + errorMessage,
                                         Snackbar.LENGTH_SHORT
                                 );
                                 SnackbarHelper.configSnackbar(LoginActivity.this, snack);
                                 snack.show();



                             }

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else if(TextUtils.isEmpty(login_email))
                    {
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                "Please enter Email ID.",
                                Snackbar.LENGTH_SHORT
                        );
                        SnackbarHelper.configSnackbar(LoginActivity.this, snack);
                        snack.show();

                    }else if(TextUtils.isEmpty(login_password))
                    {
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                "Please enter Password.",
                                Snackbar.LENGTH_SHORT
                        );
                        SnackbarHelper.configSnackbar(LoginActivity.this, snack);
                        snack.show();

                    }
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //checks if user is login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            sendToMainActivity();

        }
    }

    private void sendToMainActivity() {

        Intent mainIntent = new Intent(this, FeedActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
