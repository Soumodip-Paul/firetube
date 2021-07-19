package com.sp.socialapp.dao;


import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.socialapp.R;
import com.sp.socialapp.model.Post;
import com.sp.socialapp.model.PostComments;
import com.sp.socialapp.model.User;

import java.util.Objects;


public class PostDao {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference postCollections = db.collection("posts");
    FirebaseAuth  auth = FirebaseAuth.getInstance();


    public void addPost( Post post) {
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        UserDao userDao =new UserDao();
        userDao.getUserById(currentUserId)
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    assert user != null;
                    postCollections.document().set(post);
                });

    }

    public Task<DocumentSnapshot> getPostById(String postId) {
        return postCollections.document(postId).get();
    }

    public void updateLikes(String postId) {

        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        getPostById(postId).addOnSuccessListener(documentSnapshot -> {
            Post post = documentSnapshot.toObject(Post.class);
            assert post != null;
            boolean isLiked = post.getLikedBy().contains(currentUserId);

            if(isLiked) {
                post.getLikedBy().remove(currentUserId);
            } else {
                post.getLikedBy().add(currentUserId);
            }
            postCollections.document(postId).set(post);

        });


    }
    public void updateLikes(String postId,Button button) {

        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        getPostById(postId).addOnSuccessListener(documentSnapshot -> {
            Post post = documentSnapshot.toObject(Post.class);
            assert post != null;
            boolean isLiked = post.getLikedBy().contains(currentUserId);

            if (isLiked) {
                post.getLikedBy().remove(currentUserId);
            } else {
                post.getLikedBy().add(currentUserId);
            }
            postCollections.document(postId).set(post).addOnSuccessListener(unused -> {
                if (!isLiked) {
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                }
                else {
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unliked, 0, 0, 0);
                }
                button.setText(String.valueOf((post.getLikedBy().size())));
            });

        });
    }

        public void addComment(String text,String postId){
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
            new PostDao().getPostById(postId).addOnSuccessListener(documentSnapshot1 -> {
                Post post = documentSnapshot1.toObject(Post.class);
                PostComments postComments = new PostComments(text,currentUserId,postId,System.currentTimeMillis());
                assert post != null;
                post.getCommentedBy().add(postComments);
                postCollections.document(postId).set(post);
            });
    }

}