package com.neelkanthjdabhi.fireblog;

import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

public class BlogPostID {

    @Exclude
    public String BlogPostID;

    public <T extends BlogPostID> T withID (@NotNull final String id)
    {
        this.BlogPostID=id;
        return (T) this;
    }


}
