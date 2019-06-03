package com.neelkanthjdabhi.fireblog;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.View.INVISIBLE;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email;
    private EditText reg_pass;
    private EditText reg_confirmPass;
    private Button reg_btn;
    private TextView reg_loginBtn;
    private ProgressBar reg_progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_email = (EditText) findViewById(R.id.reg_email);
        reg_pass = (EditText) findViewById(R.id.reg_password);
        reg_confirmPass = (EditText) findViewById(R.id.reg_confirmPass);
        reg_btn = (Button) findViewById(R.id.reg_btn);
        reg_progressBar = (ProgressBar) findViewById(R.id.reg_progress);
        reg_loginBtn = (TextView) findViewById(R.id.signIn);
        mAuth = FirebaseAuth.getInstance();

        reg_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_email.getText().toString();
                String pass = reg_pass.getText().toString();
                String confirm_pass = reg_confirmPass.getText().toString();

                if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass))
                {
                    if (pass.equals(confirm_pass))
                    {
                        reg_progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                }else {
                                    String errorMessage = task.getException().getMessage();

                                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                            "Error: " + errorMessage,
                                            Snackbar.LENGTH_SHORT
                                    );
                                    SnackbarHelper.configSnackbar(RegisterActivity.this, snack);
                                    snack.show();
                                }
                                reg_progressBar.setVisibility(INVISIBLE);
                            }
                        });
                    }else {

                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                "Password do not match." ,
                                Snackbar.LENGTH_SHORT
                        );
                        SnackbarHelper.configSnackbar(RegisterActivity.this, snack);
                        snack.show();
                    }
                }else if(TextUtils.isEmpty(email)){

                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                            "Please enter Email ID." ,
                            Snackbar.LENGTH_SHORT
                    );
                    SnackbarHelper.configSnackbar(RegisterActivity.this, snack);
                    snack.show();

                }else {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                            "Please enter Password." ,
                            Snackbar.LENGTH_SHORT
                    );
                    SnackbarHelper.configSnackbar(RegisterActivity.this, snack);
                    snack.show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            sendToMainActivity();
        }
    }

    private void sendToMainActivity() {
        Intent mainIntent = new Intent(this, FeedActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
