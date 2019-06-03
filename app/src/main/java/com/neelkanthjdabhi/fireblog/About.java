package com.neelkanthjdabhi.fireblog;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.thefinestartist.finestwebview.FinestWebView;

public class About extends AppCompatActivity {

    private  Toolbar toolbar;
    private LinearLayout website,github, email, changelog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        website = findViewById(R.id.website);
        github = findViewById(R.id.github);
        email = findViewById(R.id.email);
        changelog = findViewById(R.id.changelog);


        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new FinestWebView.Builder(About.this).theme(R.style.FinestWebViewTheme)
                        .titleDefault("Neelkanth J. Dabhi")
                        .toolbarScrollFlags(0)
                        .statusBarColorRes(R.color.md_white_1000)
                        .toolbarColorRes(R.color.md_white_1000)
                        .titleColorRes(R.color.fontColor)
                        .urlColorRes(R.color.fontColor)
                        .iconDefaultColorRes(R.color.fontColor)
                        .theme(R.style.webview)
                        .progressBarColorRes(R.color.fontColor)
                        .swipeRefreshColorRes(R.color.blackPrimary)
                        .menuSelector(R.drawable.selector_light_theme)
                        .menuTextGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
                        .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                        .dividerHeight(0)
                        .gradientDivider(false)
                        .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold,
                                R.anim.slide_right_out)
                        .disableIconBack(true)
                        .disableIconClose(false)
                        .disableIconForward(true)
                        .disableIconMenu(false)
                        .show("https://neelkanthjdabhi.github.io/");


            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new FinestWebView.Builder(About.this).theme(R.style.FinestWebViewTheme)
                        .titleDefault("GithHub")
                        .toolbarScrollFlags(0)
                        .statusBarColorRes(R.color.md_white_1000)
                        .toolbarColorRes(R.color.md_white_1000)
                        .titleColorRes(R.color.fontColor)
                        .urlColorRes(R.color.fontColor)
                        .iconDefaultColorRes(R.color.fontColor)
                        .theme(R.style.webview)
                        .progressBarColorRes(R.color.fontColor)
                        .swipeRefreshColorRes(R.color.blackPrimary)
                        .menuSelector(R.drawable.selector_light_theme)
                        .menuTextGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
                        .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                        .dividerHeight(0)
                        .gradientDivider(false)
                        .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold,
                                R.anim.slide_right_out)
                        .disableIconBack(true)
                        .disableIconClose(false)
                        .disableIconForward(true)
                        .disableIconMenu(false)
                        .show("https://github.com/neelkanthjdabhi");

            }
        });


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(About.this,R.style.MyCustomAlert2);
                builder.setTitle("Alert!");
                builder.setMessage("This will take you to your email app. Are you sure you want to proceed?");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse("mailto:db.neelkanth@gmaill.com?subject=" + "Regarding Fire Blog" );
                        intent.setData(data);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        changelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(About.this,R.style.MyCustomAlert2);
                builder.setTitle("Changelog");
                builder.setMessage("This is initial build.");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });




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
