package com.neelkanthjdabhi.fireblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private TextView title;
    private TextView profile_name,profile_username,profile_userbio;
    private LinearLayout logout,editprofile;
    private  ProgressBar profile_progressbar;
    private CircleImageView profileImage;
    private Uri mainImageURI=null;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_progressbar = (ProgressBar) findViewById(R.id.progressBar);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_username = (TextView)findViewById(R.id.profile_username);
        profile_userbio = (TextView)findViewById(R.id.profile_userbio);
        profileImage = findViewById(R.id.profileImage);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userid = FirebaseAuth.getInstance().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        editprofile = (LinearLayout)findViewById(R.id.editprofile);
        logout = (LinearLayout)findViewById(R.id.logout);


        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setupIntent = new Intent(Profile.this, SetupActivity.class);
                startActivity(setupIntent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        profile_progressbar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String username = task.getResult().getString("username");
                        String userbio = task.getResult().getString("userbio");
                        String image = task.getResult().getString("image");
                        mainImageURI = Uri.parse(image);
                        profile_name.setText(name);
                        profile_username.setText("@"+username);
                        profile_userbio.setText(userbio);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_profile);
                        Glide.with(Profile.this)
                                .load(image)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(new ColorDrawable(Color.GRAY))
                                .into(profileImage);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(Profile.this, "Firestore Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                }

                profile_progressbar.setVisibility(View.INVISIBLE);

            }
        });
    }


    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this,R.style.MyCustomAlert2);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                sendToLoginPage();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
