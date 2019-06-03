package com.neelkanthjdabhi.fireblog;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

public class CustomNavigationWidth {
    private Activity activity;
    private int orientation;
    private NavigationView navigationView;
    private Resources resources;
    private DisplayMetrics displayMetrics = new DisplayMetrics();

    public CustomNavigationWidth(NavigationView navigationView, Activity activity) {
        this.navigationView = navigationView;
        this.activity = activity;
        resources = activity.getResources();
        orientation = activity.getResources().getConfiguration().orientation;
    }

    public void setWidth() {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            float screenWidth = height / resources.getDisplayMetrics().density;
            float navWidth = (screenWidth - 56);
            navWidth = Math.min(navWidth, 320);
            int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, resources.getDisplayMetrics());
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
            params.width = newWidth;
            navigationView.setLayoutParams(params);
        } else {
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            float screenWidth = width / resources.getDisplayMetrics().density;
            float navWidth = (screenWidth - 56);
            navWidth = Math.min(navWidth, 320);
            int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, resources.getDisplayMetrics());
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
            params.width = newWidth;
            navigationView.setLayoutParams(params);
        }


    }

}

