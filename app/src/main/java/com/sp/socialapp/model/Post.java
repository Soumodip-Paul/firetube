package com.sp.socialapp.model;


import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * The {@code Post} class is used to structure data from {@link com.google.firebase.firestore.FirebaseFirestore}
 */

public class Post implements Serializable {
    private String text;
    private String userId;
    private String imageUrl;
    private long createdAt;
    private ArrayList<String> likedBy = new ArrayList<String>(){};
    private ArrayList<PostComments> commentedBy =new ArrayList<>();

    public    String getImageUrl() {
        return imageUrl;
    }

    @Deprecated
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    @Deprecated
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    @Deprecated
    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public ArrayList<PostComments> getCommentedBy() {
        return commentedBy;
    }

    @Deprecated
    public void setCommentedBy(ArrayList<PostComments> commentedBy) {
        this.commentedBy = commentedBy;
    }

    @Deprecated
    public  Post(){}

    public Post(String text, String userId, @Nullable String imageUrl, long createdAt){
        this.text=text;
        this.userId = userId;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;

    }

}