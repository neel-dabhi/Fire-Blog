package com.neelkanthjdabhi.fireblog;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;


public class FragmentHome extends Fragment {

    SwipeRefreshLayout swipeLayout;
    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private BlogRecyclerAdepter blogRecyclerAdepter;
    private FirebaseAuth mAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad =true;
    private String UserID;
    private TextView textView;
    private AppBarLayout appBarLayout;
    private NestedScrollView nsv;


    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);

        nsv = view.findViewById(R.id.nsv);
        textView = ((FeedActivity) Objects.requireNonNull(getActivity())).getTextView();
        textView.setText("Create Post");

        appBarLayout = ((FeedActivity) getActivity()).getElevations();

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Snackbar snack = Snackbar.make(view,
                                "Your feed is up to date!",
                                Snackbar.LENGTH_SHORT
                        );
                        SnackbarHelper.configSnackbar(getActivity(), snack);
                        snack.show();
                        swipeLayout.setRefreshing(false);

                    }
                }, 1500);
            }
        });


        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);
        blogRecyclerAdepter = new BlogRecyclerAdepter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogRecyclerAdepter);
        blog_list_view.setItemViewCacheSize(20);
        blog_list_view.setDrawingCacheEnabled(true);
        blog_list_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mAuth = FirebaseAuth.getInstance();
        handleFab();

        if(mAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(4);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if(! (queryDocumentSnapshots.size()-1 <=0))
                    {
                        if(isFirstPageFirstLoad)
                        {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        }
                    }

                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                    {
                        if(doc.getType() == DocumentChange.Type.ADDED){

                            String blogPostID = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withID(blogPostID);
                            if (isFirstPageFirstLoad)
                            {
                                blog_list.add(blogPost);
                            }else {

                                blog_list.add(0,blogPost);
                            }
                            blogRecyclerAdepter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            });
        }
        return view;
    }



    private void handleFab() {
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    textView.setVisibility(View.GONE);
                    appBarLayout.setElevation(10.0f);

                } else if (scrollX == scrollY) {
                    textView.setVisibility(View.VISIBLE);
                    appBarLayout.setElevation(0.0f);

                } else {
                    textView.setVisibility(View.VISIBLE);

                }
            }
        });
    }




    public void loadMorePost()
    {
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(4);
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty())
                {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                    {
                        if(doc.getType() == DocumentChange.Type.ADDED){
                            String blogPostID = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withID(blogPostID);
                            blog_list.add(blogPost);
                            blogRecyclerAdepter.notifyDataSetChanged();
                        }
                    }
                }

            }
        });
    }
}
