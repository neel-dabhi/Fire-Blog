package com.neelkanthjdabhi.fireblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class NewPostActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private ImageView newpost_image;
    private EditText newpost_description;
    private FloatingActionButton publish_btn;
    private ProgressBar newpost_progress;
    private Uri post_image_uri=null;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String curentUser_id;
    private  static final int MAX_LENGTH =100;
    private Bitmap compressedImageFile;
    AnimatedVectorDrawable uploading,uploadingComplete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        toolbar = findViewById(R.id.newpost_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        newpost_description = (EditText) findViewById(R.id.newpost_description);
        newpost_image = (ImageView) findViewById(R.id.newpost_image);
        publish_btn = findViewById(R.id.newpost_publish);
        newpost_progress = findViewById(R.id.newpost_progress);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }

        newpost_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMaxCropResultSize(1920,1080)
                        .setMinCropResultSize(426  ,240)
                        .setAspectRatio(16,9)
                        .start(NewPostActivity.this);
            }
        });


        publish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = newpost_description.getText().toString();

                if(!TextUtils.isEmpty(description)&& post_image_uri!=null)
                {
                    newpost_progress.setVisibility(View.VISIBLE);


                    uploading =
                            (AnimatedVectorDrawable) getDrawable(R.drawable.avd_uploading);
                    if (uploading != null) {
                        publish_btn.setImageDrawable(uploading);
                        uploading.start();
                    }

                    String randomName = UUID.randomUUID().toString();

                    StorageReference filepath = storageReference.child("post_image").child(randomName+".jpg");

                    filepath.putFile(post_image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String postimage_url = uri.toString();
                                    String downThumb;
                                    curentUser_id = mAuth.getCurrentUser().getUid();
                                    //compression
                                    File newImageFile = new File(post_image_uri.getPath());
                                    try {
                                        compressedImageFile = new Compressor(NewPostActivity.this)
                                                .setMaxWidth(200)
                                                .setMaxWidth(200)
                                                .setQuality(10)
                                                .compressToBitmap(newImageFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                                    byte[] thumb_data = baos.toByteArray();
                                    StorageReference uploadTask = storageReference.child("post_image/thumbs").child(randomName+".jpg");

                                    uploadTask.putBytes(thumb_data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            uploadTask.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String download_thumb = uri.toString();

                                                    Map<String,Object> postMap = new HashMap<>();
                                                    postMap.put("thumbnail",download_thumb);
                                                    postMap.put("image",postimage_url);
                                                    postMap.put("desc",description);
                                                    postMap.put("user_id",curentUser_id);
                                                    postMap.put("timestamp",FieldValue.serverTimestamp());

                                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            Toast.makeText(NewPostActivity.this,"Post Added",Toast.LENGTH_SHORT).show();

                                                            newpost_progress.setVisibility(View.INVISIBLE);

                                                            uploading.stop();

                                                            uploadingComplete =
                                                                    (AnimatedVectorDrawable) getDrawable(R.drawable.avd_upload_complete);
                                                            if (uploadingComplete != null) {
                                                                publish_btn.setImageDrawable(uploadingComplete);
                                                                uploadingComplete.start();
                                                            }

                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Intent mainPage = new Intent(NewPostActivity.this,FeedActivity.class);
                                                                    startActivity(mainPage);
                                                                    finish();

                                                                }
                                                            }, 1000);


                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            newpost_progress.setVisibility(View.INVISIBLE);
                        }
                    });


                }else {

                    if (TextUtils.isEmpty(description))
                    {
                        Snackbar snack = Snackbar.make(publish_btn,
                                "Description can not be empty.",
                                Snackbar.LENGTH_SHORT
                        );
                        SnackbarHelper.configSnackbar(NewPostActivity.this, snack);
                        snack.show();

                    }else {
                        Snackbar snack = Snackbar.make(publish_btn,
                                "Please select image for the post.",
                                Snackbar.LENGTH_SHORT
                        );
                        SnackbarHelper.configSnackbar(NewPostActivity.this, snack);
                        snack.show();

                    }
                }
            }
        });
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(NewPostActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(NewPostActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(NewPostActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(NewPostActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                post_image_uri = result.getUri();
                newpost_image.setImageURI(post_image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(NewPostActivity.this,"Crop Error",Toast.LENGTH_SHORT).show();

            }
        }
    }




    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


}
