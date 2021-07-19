package com.sp.socialapp.ui.bottomFragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.sp.socialapp.R;
import com.sp.socialapp.ViewPostActivity;
import com.sp.socialapp.adapters.PostProfileAdapter;
import com.sp.socialapp.dao.PostDao;
import com.sp.socialapp.dao.UserDao;
import com.sp.socialapp.listeners.CommentAdapter;
import com.sp.socialapp.listeners.LikeAdapter;
import com.sp.socialapp.listeners.ViewPostListener;
import com.sp.socialapp.model.Post;
import com.sp.socialapp.model.User;

import java.util.Objects;

public class ProfileFragment extends Fragment implements LikeAdapter, CommentAdapter, ViewPostListener {

    RecyclerView recyclerView;
    PostDao postDao;
    PostProfileAdapter adapter;
    ImageView userImage;
    TextView userName , userEmail , userPhone , userProfession , userUrl ;
    User user;
    String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        root.findViewById(R.id.cardView3).setOnClickListener(v -> Toast.makeText(requireActivity(), "Hii", Toast.LENGTH_SHORT).show());

        recyclerView = root.findViewById(R.id.profileRecyclerView);
        userImage = root.findViewById(R.id.profileUserImage);
        userName = root.findViewById(R.id.profileUserName);
        userEmail = root.findViewById(R.id.profileEmail);
        userPhone = root.findViewById(R.id.profilePhone);
        userProfession = root.findViewById(R.id.profileProfession);
        userUrl = root.findViewById(R.id.profileUrl);

        recyclerView.setNestedScrollingEnabled(false);

        setUpRecyclerView();
        new UserDao().getUserById(uid).addOnSuccessListener(
                documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        userName.setText(user.getUserName());
                        userUrl.setText(user.getUrl());
                        userPhone.setText(user.getPhoneNumber());
                        userEmail.setText(user.getEmail());
                        userProfession.setText(user.getProfession());
                        Glide.with(requireActivity()).load(user.getImageUri()).circleCrop().into(userImage);
                    }
                }
        );
        return root;
    }

    private void setUpRecyclerView() {
        postDao =new PostDao();
        Log.d("TAG", "PostDao created ");
        CollectionReference postsCollections = postDao.postCollections;
        Query query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING);
        Query queryParams = query.whereEqualTo("userId",uid);
        Log.d("TAG", "query executed ");
        FirestoreRecyclerOptions<Post> recyclerViewOptions =new FirestoreRecyclerOptions.Builder<Post>().setQuery(queryParams, Post.class).build();
        Log.d("TAG", "options ");
        adapter =new PostProfileAdapter(recyclerViewOptions, this,this,this);
        Log.d("TAG", "adapter");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        Log.d("TAG", "recyclerView set");
    }

    @Override
    public void onCommentClick(Post post , String postId) {

    }

    @Override
    public void onLikeClicked(String postId) {
        postDao.updateLikes(postId);
    }

    @Override
    public void onPostAdapterClicked(Post post, String postId) {
        startActivity(new Intent(requireActivity(), ViewPostActivity.class)
                .putExtra("Post", post)
                .putExtra("postId",postId));
    }
    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();
        Log.d("TAG", "adapter.startListening ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}