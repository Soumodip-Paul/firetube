package com.sp.socialapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sp.socialapp.dao.PostDao;
import com.sp.socialapp.dao.UserDao;
import com.sp.socialapp.model.Post;
import com.sp.socialapp.model.User;
import com.sp.socialapp.utils.Util;

import java.util.Objects;

public class ViewPostActivity extends AppCompatActivity {
    Post post;
    TextView userName,postText,createdAt;
    ImageView imageView;
    Button likeButton,commentButton;
    ProgressBar progressBar;
    String postId;
    boolean likeImage;
    String uid =Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        post = (Post) getIntent().getSerializableExtra("Post");
        postId = getIntent().getStringExtra("postId");
        findViewById(R.id.viewPostLikeButton).setOnClickListener(this::likePost);
        imageView = findViewById(R.id.viewPostUserImage);
        postText = findViewById(R.id.viewPostText);
        userName = findViewById(R.id.viewPostUserName);
        progressBar = findViewById(R.id.viewPostProgressBar);
        createdAt = findViewById(R.id.viewPostCreatedAt);
        likeButton = findViewById(R.id.viewPostLikeButton);
        commentButton = findViewById(R.id.viewPostCommentButton);
        createdAt.setText(Util.getTimeAgo(post.getCreatedAt()));
        postText.setText(post.getText());
         new UserDao().getUserById(post.getUserId()).addOnSuccessListener(this::loadUserImage);
        Glide.with(this).load(post.getImageUrl()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                postText.setCompoundDrawablesWithIntrinsicBounds(null,null,null,resource);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                postText.setCompoundDrawablesWithIntrinsicBounds(null,null,null,placeholder);
                progressBar.setVisibility(View.GONE);
            }
        });
        likeImage = post.getLikedBy().contains(uid);
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
        likeButton.setText(String.valueOf(post.getLikedBy().size()));
        if (likeImage) likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked,0,0,0);
        else likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unliked,0,0,0);
        likeButton.setOnClickListener(this::onClick);

    }

    private void loadUserImage(DocumentSnapshot documentSnapshot) {
        User user = documentSnapshot.toObject(User.class);
        if (user != null) {
            Glide.with(this).load(user.getImageUri()).circleCrop().into(imageView);
            userName.setText(user.getUserName());
        }

    }

    public void likePost(View view){

    }
    private void onClick(View v){
        new PostDao().updateLikes(postId,likeButton);
    }
}