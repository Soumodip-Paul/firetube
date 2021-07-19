package com.sp.socialapp.model;

import com.google.firebase.auth.FirebaseAuth;
import com.sp.socialapp.dao.PostDao;

import java.util.Objects;

public class PostComments {
    public String text;
    public String uid;
    public String postId;
    public long createdAt;

    @Deprecated
    public PostComments(){}

    public PostComments(String text, String uid, String postId, long createdAt) {
        this.text = text;
        this.uid = uid;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    /**
     * This method is used to add comment to a specific post
     *
     * @param comment the text of comment to be posted
     * @param postId the id of the given post
     *
     */
    public static void addComment(String comment, String postId){
        PostComments postComments = new PostComments(comment, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), postId,System.currentTimeMillis());
        PostDao postDao = new PostDao();
        postDao.getPostById(postId).addOnSuccessListener(documentSnapshot1 -> {
            Post post = Objects.requireNonNull(documentSnapshot1.toObject(Post.class));
            post.getCommentedBy().add(postComments);
            postDao.postCollections.document(postId).set(post);
        });
    }
}