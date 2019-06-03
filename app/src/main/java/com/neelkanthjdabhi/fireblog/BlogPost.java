package com.neelkanthjdabhi.fireblog;

import java.sql.Timestamp;
import java.util.Date;

public class BlogPost extends BlogPostID {

    public String user_id;
    public String image;
    public String desc;
    public String thumbnail;
    public Date timestamp;

    public BlogPost(){}

    public BlogPost(String user_id, String image, String desc, String thumbnail, Timestamp timestamp) {
        this.user_id = user_id;
        this.image = image;
        this.desc = desc;
        this.thumbnail = thumbnail;
        this.timestamp = timestamp;

    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getthumbnail() {
        return thumbnail;
    }

    public void setthumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}