package com.neelkanthjdabhi.fireblog;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Pair;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.klinker.android.drag_dismiss.DragDismissIntentBuilder;


public class BlogRecyclerAdepter extends RecyclerView.Adapter<BlogRecyclerAdepter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    StringBuilder shareText = new StringBuilder();
    DocumentChange doc;
    String userImage,userName,dateString;

    public BlogRecyclerAdepter(List<BlogPost> blog_list){

        this.blog_list = blog_list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        String blogPostID = blog_list.get(position).BlogPostID;
        holder.setIsRecyclable(false);


        //final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDesc();
        shareText.append(desc_data);
        shareText.append(System.getProperty("line.separator"));
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage();
        String thumbUri = blog_list.get(position).getthumbnail();
        holder.setBlogImage(image_url, thumbUri);
        String post_user_id = blog_list.get(position).getUser_id();


        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(post_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    userName = task.getResult().getString("name");
                    userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);
                } else { }
            }
        });

        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        /*




        holder.deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getRootView().getContext())
                        .setMessage("Are you sure you want to Delete this post?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Snackbar snackbar = Snackbar.make(v, "Post Deleted.", Snackbar.LENGTH_SHORT);
                                snackbar.show();



                                deletePost(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });



        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).delete();

                        }

                    }
                });
            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){



                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.like_red));

                } else {


                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.like_greay));

                }

            }
        });

        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });


        if(user_id.equals(currentUserId))
        {
            holder.deleteButtonVisible(true);
        }

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareItem(image_url,desc_data);
            }
        });
        */


        holder.post_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent postDetailActivity = new Intent(context, DetailActivity.class);

                new DragDismissIntentBuilder(context)
                        .setTheme(DragDismissIntentBuilder.Theme.LIGHT)	// LIGHT (default), DARK, BLACK, DAY_NIGHT
                        .setTheme(DragDismissIntentBuilder.Theme.LIGHT)// defaults to a semi-transparent black
                        .setToolbarTitle(null)		// defaults to null
                        .setShowToolbar(true)				// defaults to true
                        .setShouldScrollToolbar(false)       // defaults to true
                        .setFullscreenOnTablets(false)      // defaults to false, tablets will have padding on each side
                        .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)  // Larger elasticities will make it easier to dismiss.
                        .setDrawUnderStatusBar(true)       // defaults to false. Change to true if you don't want me to handle the content margin for the Activity. Does not apply to the RecyclerView Activities
                        .build(postDetailActivity);

                        // do anything else that you want to set up the Intent
                        // dragDismissActivity.putBoolean("test_bool", true);

                        postDetailActivity.putExtra("description", blog_list.get(position).getDesc());
                        postDetailActivity.putExtra("postImage",blog_list.get(position).getImage());
                        postDetailActivity.putExtra("postThumb",blog_list.get(position).getthumbnail());
                        postDetailActivity.putExtra("pos",position);
                        postDetailActivity.putExtra("blogPostID",blog_list.get(position).BlogPostID);
                        postDetailActivity.putExtra("postUserImage",userImage);
                        postDetailActivity.putExtra("postUserName",userName);
                        postDetailActivity.putExtra("postUserID",post_user_id);
                        postDetailActivity.putExtra("currentUserID",currentUserId);
                        postDetailActivity.putExtra("postTimeStamp",dateString);


                        context.startActivity(postDetailActivity);


            }
        });



    }

    private void deletePost(int position) {
        firebaseFirestore.collection("Posts").document(blog_list.get(position).BlogPostID).delete();
    }

    public void shareItem(String url,String desc) {

        StringBuilder shareText = new StringBuilder();

        shareText.append(desc);
        shareText.append(System.getProperty("line.separator"));
        shareText.append(System.getProperty("line.separator"));
        shareText.append("Check out Fire Blog App");
        shareText.append(System.getProperty("line.separator"));
        shareText.append("http://Play.google.com/store/apps/details?id=com.neelkanthjdabhi.fireblog");

        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("*/*");
                        i.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                        context.startActivity(Intent.createChooser(i, "Share Image"));
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
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private CardView post_card;
        private TextView blogDate;
        private TextView blogUserName;
        private CircleImageView blogUserImage;
        private ImageView blogLikeBtn;
        private ImageView deletePostBtn;
        private TextView blogLikeCount;
        private ImageView shareButton;
        RequestOptions requestOptions,placeholderOption;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            //blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            //deletePostBtn = mView.findViewById(R.id.deletepost);
            //shareButton = mView.findViewById(R.id.shareBtn);
            post_card = mView.findViewById(R.id.post_card);
        }

        public void setDescText(String descText){

            //descView = mView.findViewById(R.id.post_description);
            //descView.setText(descText);

        }

        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.post_image);

            requestOptions = new RequestOptions();
            requestOptions.placeholder(new ColorDrawable(Color.GRAY));

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

        public void setTime(String date) {
            //blogDate = mView.findViewById(R.id.post_date);
            //blogDate.setText(date);
        }

        public void setUserData(String name, String image){
            blogUserImage = mView.findViewById(R.id.user_image);
            blogUserName = mView.findViewById(R.id.user_name);
            blogUserName.setText(name);
            placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_profile);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);
        }

        public void updateLikesCount(int count){
            //blogLikeCount = mView.findViewById(R.id.blog_like_count);
            //blogLikeCount.setText(count + " Likes");
        }

        public void deleteButtonVisible(Boolean value)
        {
            if(value)
            {
                //deletePostBtn = mView.findViewById(R.id.deletepost);
                //deletePostBtn.setVisibility(View.VISIBLE);

            }

        }

    }

}
