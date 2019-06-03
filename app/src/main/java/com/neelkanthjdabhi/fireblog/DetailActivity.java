package com.neelkanthjdabhi.fireblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DetailActivity extends DragDismissActivity {

    ImageView imgPost,imgUserPost,likeBtn,viewsBtn,shareBtn,deleteBtn;
    TextView txtPostDesc,txtPostDateName,blogLikeCount,blogViewCount;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    String blogPostID;
    int count;


    @Override
    public View onCreateContent(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_detail, parent, false);

        getWindow().getDecorView().setSystemUiVisibility(   v.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        blogLikeCount = v.findViewById(R.id.blogLikeCount);
        deleteBtn = v.findViewById(R.id.deleteBtn);
        shareBtn = v.findViewById(R.id.shareBtn);
        likeBtn = v.findViewById(R.id.likeBtn);
        viewsBtn = v.findViewById(R.id.viewsBtn);
        imgPost = v.findViewById(R.id.post_detail_img);
        imgUserPost = v.findViewById(R.id.post_detail_user_img);
        txtPostDesc = v.findViewById(R.id.post_detail_desc);
        txtPostDateName = v.findViewById(R.id.post_detail_date_name);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        int position = getIntent().getExtras().getInt("pos");
        String postDescription = getIntent().getExtras().getString("description");
        blogPostID = getIntent().getExtras().getString("blogPostID");
        String postImage = getIntent().getExtras().getString("postImage") ;
        String postThumb = getIntent().getExtras().getString("postThumb") ;
        //String userpostImage = getIntent().getExtras().getString("postUserImage");
        //String postUserName = getIntent().getExtras().getString("postUserName");
        String postUserID = getIntent().getExtras().getString("postUserID");
        String currentUserID = getIntent().getExtras().getString("currentUserID");
        String postTimeStamp = getIntent().getExtras().getString("postTimeStamp");



        //Likes Feature
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animatable animatable = (Animatable) likeBtn.getDrawable();
                animatable.start();
                firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserID).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserID).delete();

                        }

                    }
                });
            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    likeBtn.setColorFilter(getResources().getColor(R.color.md_red_600));

                } else {


                    likeBtn.setColorFilter(getResources().getColor(R.color.fontColor));

                }

            }
        });

        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    updateLikesCount(count);

                } else {

                    updateLikesCount(0);

                }

            }
        });

        if(postUserID.equals(currentUserID))
        {
            deleteBtn.setVisibility(View.VISIBLE);
        }


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animatable animatable = (Animatable) shareBtn.getDrawable();
                animatable.start();

                shareItem(postImage,postDescription);
            }
        });


        viewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animatable animatable = (Animatable) viewsBtn.getDrawable();
                animatable.start();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this,R.style.MyCustomAlert2);
                builder.setTitle("Delete");
                builder.setNegativeButton("No",null);
                builder.setMessage("Are you sure you want to Delete this post?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar snackbar = Snackbar.make(v, "Post Deleted.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        deletePost(position);
                        finish();
                    }
                });
                builder.show();
            }
        });







        //SET POST IMAGE
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(new ColorDrawable(Color.GRAY));

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(postImage).thumbnail(
                Glide.with(this).load(postThumb)
        ).into(imgPost);


        //SET POST DESCRIPTION
        txtPostDesc.setText(postDescription);

        //SET IMAGE OF POST AUTHOR
        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(postUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    Glide.with(v.getContext()).load(userImage).into(imgUserPost);
                    txtPostDateName.setText(userName);

                } else { }
            }
        });

        return v;
    }

    public void updateLikesCount(int count){
        blogLikeCount = findViewById(R.id.blogLikeCount);
        blogLikeCount.setText(count + " Likes");
    }


    private void deletePost(int position) {
        firebaseFirestore.collection("Posts").document(blogPostID).delete();
    }


    public void shareItem(String url,String desc) {

        StringBuilder shareText = new StringBuilder();

        shareText.append(desc);
        shareText.append(System.getProperty("line.separator"));
        shareText.append(System.getProperty("line.separator"));
        shareText.append("Check out Fire Blog App");
        shareText.append(System.getProperty("line.separator"));
        shareText.append("http://Play.google.com/store/apps/details?id=com.neelkanthjdabhi.fireblog");

        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("*/*");
                        i.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                        startActivity(Intent.createChooser(i, "Share Image"));
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }



}
