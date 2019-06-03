package com.neelkanthjdabhi.fireblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.os.Build.VERSION_CODES.M;


public class SetupActivity extends AppCompatActivity {

    private Button submitBtn;

    private boolean isChanged=false;
    private EditText setup_name,setup_username,setup_bio;
    private FrameLayout frameLayout;
    private ProgressBar setup_progressbar;
    private Toolbar toolbar;
    private CircleImageView profileImage;
    private Uri mainImageURI=null;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private String url;
    private Uri downloadUrl;
    private  FirebaseFirestore firebaseFirestore;
    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        toolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        profileImage = findViewById(R.id.setup_profile_image);
        frameLayout = findViewById(R.id.framelayout);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        submitBtn = findViewById(R.id.setup_doneBtn);
        setup_progressbar = findViewById(R.id.setup_progressBar);
        setup_name = (EditText)findViewById(R.id.setup_name);
        setup_username = (EditText)findViewById(R.id.setup_username);
        setup_bio = (EditText)findViewById(R.id.setup_bio);
        userid = FirebaseAuth.getInstance().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();



        setup_progressbar.setVisibility(View.VISIBLE);
        submitBtn.setEnabled(false);
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
                        setup_name.setText(name);
                        setup_username.setText(username);
                        setup_bio.setText(userbio);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(new ColorDrawable(Color.GRAY));
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);
                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Firestore Retrieve Error: " + error, Toast.LENGTH_LONG).show();

                }

                setup_progressbar.setVisibility(View.INVISIBLE);
                submitBtn.setEnabled(true);

            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_progressbar.setVisibility(View.VISIBLE);
                String name = setup_name.getText().toString();
                String username = setup_username.getText().toString();
                String userbio = setup_bio.getText().toString();
                if(!TextUtils.isEmpty(name)&& mainImageURI!=null) {
                    if(isChanged){
                        setup_progressbar.setVisibility(View.VISIBLE);
                        userid = firebaseAuth.getCurrentUser().getUid();
                        StorageReference imagePath = storageReference.child("profile_images").child(userid + ".jpg");
                        imagePath.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        storeFireStore(uri,name, username, userbio);

                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                                "Profile Updated!",
                                                Snackbar.LENGTH_SHORT
                                        );
                                        SnackbarHelper.configSnackbar(SetupActivity.this, snack);
                                        snack.show();
                                        setup_progressbar.setVisibility(View.INVISIBLE);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String error = e.getMessage().toString();
                                        Toast.makeText(SetupActivity.this,"Fail Image upload " + error,Toast.LENGTH_SHORT).show();
                                        setup_progressbar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        });
                    }else {

                        storeFireStore(null ,name, username, userbio);

                    }
                }else {
                    if (TextUtils.isEmpty(name))
                    {
                        Snackbar snackbar = Snackbar.make(frameLayout, "Please enter your name.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else if(TextUtils.isEmpty(username))
                    {
                        Snackbar snackbar = Snackbar.make(frameLayout, "Please set User Name.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else if(TextUtils.isEmpty(userbio))
                    {
                        Snackbar snackbar = Snackbar.make(frameLayout, "Please add Bio.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else
                    {
                        Snackbar snackbar = Snackbar.make(frameLayout, "Please select a profile photo.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                }
            }

            private void storeFireStore(Uri uri, String name, String username, String userbio ) {

                if(uri!=null)
                {
                    downloadUrl = uri;
                }else {
                    downloadUrl = mainImageURI;
                }


                Map<String,String> userMap = new HashMap<>();
                userMap.put("name",name);
                userMap.put("username",username);
                userMap.put("userbio",userbio);
                userMap.put("image",downloadUrl.toString());
                firebaseFirestore.collection("Users").document(userid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar snackbar = Snackbar.make(frameLayout, "Settings Updated", Snackbar.LENGTH_LONG);
                            snackbar.show();

                            setup_progressbar.setVisibility(View.INVISIBLE);
                            Intent mainPage = new Intent(SetupActivity.this,FeedActivity.class);
                            startActivity(mainPage);
                            finish();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this,"Firestore Error: "+ error ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });




        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= M){
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},M);
                    }else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMaxCropResultSize(700,700)
                                .setMinCropResultSize(200,200)
                                .setActivityTitle("Select Profile Image")
                                .setAspectRatio(1,1)
                                .start(SetupActivity.this);
                    }
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();





                profileImage.setImageURI(mainImageURI);
                isChanged=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetupActivity.this,"Crop Error",Toast.LENGTH_SHORT).show();

            }
        }
    }

}