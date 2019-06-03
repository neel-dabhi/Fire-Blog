package com.neelkanthjdabhi.fireblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class FeedActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private boolean backAgain = false;
    private CoordinatorLayout linearLayout;
    private FloatingActionButton floatingActionButton;
    private LinearLayout fab_layout;
    private TextView textView;
    private View view;
    private TextView tool_name;
    private DrawerLayout drawerLayout;
    private SmoothActionBarToggle toggle;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    private AppBarLayout appBarLayout;
    private static final String TAG = "MainActivity";
    private static final String URL = "https://play.google.com/store/apps/details?id=com.projects.sharath.materialvision";
    private FragmentManager manager;




    ///////////////////////
    CircleImageView currentUserImageCV;
    private FirebaseAuth mAuth;
    final String PREFS_NAME = "MyPrefsFile";
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private FragmentHome fragmentHome;
    private FirebaseUser user;
    Dialog myDialog;
    private String userName,name;
    private String userid;
    String currentUserImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        drawerLayout = findViewById(R.id.drawer_main);
        frameLayout = findViewById(R.id.container_main);
        appBarLayout = findViewById(R.id.appbar1);
        tool_name = findViewById(R.id.tool_name);
        currentUserImageCV = findViewById(R.id.currentUserImageCV);
        myDialog = new Dialog(this);


        ///////////////////////////////
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        //initNavigationMenu();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(user!=null)
        {
            userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            userName = task.getResult().getString("username");
                            name = task.getResult().getString("name");
                            currentUserImage = task.getResult().getString("image");
                            navigationView = (NavigationView) findViewById(R.id.nav_main);
                            View header = navigationView.getHeaderView(0);
                            TextView Name = (TextView) header.findViewById(R.id.name);
                            TextView uName = (TextView) header.findViewById(R.id.username);
                            ImageView profile = (ImageView) header.findViewById(R.id.imageView) ;
                            Name.setText(name);
                            uName.setText("@"+userName);


                            Glide.with(FeedActivity.this)
                                    .load(currentUserImage)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop()
                                    .placeholder(R.drawable.default_profile)
                                    .into(profile);

                            Glide.with(FeedActivity.this)
                                    .load(currentUserImage)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop()
                                    .placeholder(R.drawable.default_profile)
                                    .into(currentUserImageCV);



                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(FeedActivity.this, "Firestore Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        fragmentHome = new FragmentHome();
        initializeFragment();
        currentUserImageCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(v);
            }
        });



        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            // first time task
            Intent introPage = new Intent(FeedActivity.this,OnboardingActivity.class);
            startActivity(introPage);
            finish();

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }

        /////////////////////////////

        floatingActionButton = findViewById(R.id.download_fab);
        fab_layout = findViewById(R.id.fab_fn);
        fab_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newpost = new Intent(FeedActivity.this,NewPostActivity.class);
                startActivity(newpost);

            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newpost = new Intent(FeedActivity.this,NewPostActivity.class);
                startActivity(newpost);

            }
        });

        textView = findViewById(R.id.download_fab_text);
        textView.setText("Create Post");

        setUpToolbar();
        setUpView();
        setUpDrawerLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        } else {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        navigationView = findViewById(R.id.nav_main);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationClickListeners();
        if (savedInstanceState == null) {
        }


        CustomNavigationWidth customNavigationWidth = new CustomNavigationWidth(navigationView, this);
        customNavigationWidth.setWidth();
    }



    private void navigationClickListeners() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.home:
                        fab_layout.setVisibility(View.VISIBLE);
                        break;

                    case R.id.profile:
                        Intent profileIntent = new Intent(FeedActivity.this, Profile.class);
                        startActivity(profileIntent);

                        break;

                    case R.id.newpost:
                        Intent createnewpostIntent = new Intent(FeedActivity.this, NewPostActivity.class);
                        startActivity(createnewpostIntent);

                        break;

                    case R.id.signout:
                        logout();
                        break;

                    case R.id.aboutdev:
                        Intent aboutIntent = new Intent(FeedActivity.this, About.class);
                        startActivity(aboutIntent);

                        break;

                    case R.id.nav_feedback:
                        Intent feedbackIntent = new Intent(FeedActivity.this, FeedbackActivity.class);
                        startActivity(feedbackIntent);

                        break;

                    case R.id.notifications:
                        Intent notificationPage = new Intent(FeedActivity.this,NotificationActivity.class);
                        startActivity(notificationPage);

                        break;

                    case R.id.nav_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out Fire Blog App at: https://play.google.com/store/apps/details?id=com.neelkanthjdabhi.fireblog");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        break;


                    default:
                        Log.d(TAG, "onNavigationItemSelected: No class found");
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }






    // 1
    private void setUpToolbar() {
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    //2
    private void setUpView() {
        linearLayout = findViewById(R.id.cd_main);
        view = linearLayout;
    }

    //3
    private void setUpDrawerLayout() {
        toggle = new SmoothActionBarToggle(this,
                drawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.startLayoutAnimation();
        toggle.syncState();
    }

    //4
    private void handleFrame(final Fragment fragment, final String name) {
        toggle.runWhenIdle(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.container_main, fragment);
                fragmentTransaction.commit();
                tool_name.setText(name);
            }
        });

    }

    public void ShowPopup(View v) {
        TextView userName;
        CircleImageView manageAcc_userImage;
        Button manageAccountBtn,signoutBtn;
        myDialog.setContentView(R.layout.manage_acc_popup);
        manageAcc_userImage = myDialog.findViewById(R.id.manageAcc_userImage);
        userName =(TextView) myDialog.findViewById(R.id.manageAcc_userEmail);
        userName.setText(name);
        manageAccountBtn = (Button) myDialog.findViewById(R.id.button);
        signoutBtn = (Button) myDialog.findViewById(R.id.signoutBtn);

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                logout();
            }
        });

        Glide.with(FeedActivity.this)
                .load(currentUserImage)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(manageAcc_userImage);

        manageAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(FeedActivity.this, Profile.class);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCancelable(true);
        myDialog.show();
    }


    public TextView getTextView() {
        return textView;
    }

    public AppBarLayout getElevations() {
        return appBarLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(FeedActivity.this);

        if (user == null) {
            sendToLoginPage();
        } else {

            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        if(!task.getResult().exists())
                        {
                            Toast.makeText(FeedActivity.this,"Please complete setup before using the app.",Toast.LENGTH_LONG).show();
                            Intent setupPage = new Intent(FeedActivity.this,SetupActivity.class);
                            startActivity(setupPage);
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }

                    }else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(FeedActivity.this,"Error: " + errorMessage,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this,R.style.MyCustomAlert2);
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
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void initializeFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container_main, fragmentHome);
        fragmentTransaction.commit();

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("text", tool_name.getText().toString());
        if (fab_layout.getVisibility() == View.VISIBLE) {
            outState.putBoolean("fabVisible", true);
        } else {
            outState.putBoolean("fabVisible", false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            tool_name.setText(savedInstanceState.getString("text"));
            boolean visible = savedInstanceState.getBoolean("fabVisible");
            if (visible) {
                fab_layout.setVisibility(View.VISIBLE);
            } else {
                fab_layout.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (backAgain) {
                super.onBackPressed();
                return;
            }
            this.backAgain = true;
            Snackbar snackbar = Snackbar.make(view, "Please click back again to exit", LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(Color.parseColor("#d32f2f"));
            snackbar.getView().setAlpha(0.75f);
            snackbar.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backAgain = false;
                }
            }, 3000);
        }
    }




    public class SmoothActionBarToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                runnable.run();
                runnable = null;
            }
        }

        public void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }
}
