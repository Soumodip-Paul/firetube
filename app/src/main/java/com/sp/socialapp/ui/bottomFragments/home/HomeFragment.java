package com.sp.socialapp.ui.bottomFragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.sp.socialapp.R;
import com.sp.socialapp.ViewPostActivity;
import com.sp.socialapp.adapters.PostAdapter;
import com.sp.socialapp.dao.PostDao;
import com.sp.socialapp.listeners.CommentAdapter;
import com.sp.socialapp.listeners.LikeAdapter;
import com.sp.socialapp.listeners.ViewPostListener;
import com.sp.socialapp.model.Post;
import com.sp.socialapp.utils.SaveValue;

public class HomeFragment extends Fragment implements LikeAdapter, CommentAdapter, ViewPostListener {

    RecyclerView recyclerView;
    PostDao postDao;
    PostAdapter adapter;
    EditText editText;
    ImageView cardImageView;
    ConstraintLayout constraintLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        cardImageView = root.findViewById(R.id.cardUserImage);
        editText = root.findViewById(R.id.cardPostEditText);
        constraintLayout = root.findViewById(R.id.constrain1);
        setUpRecyclerView();
        Glide.with(cardImageView.getContext())
                .load(SaveValue.getString(requireActivity(),getString(R.string.key_image_url),null))
                .circleCrop()
                .into(cardImageView);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(constraintLayout.getVisibility() == View.VISIBLE) constraintLayout.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(constraintLayout.getVisibility() == View.GONE) constraintLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(constraintLayout.getVisibility() == View.GONE) constraintLayout.setVisibility(View.VISIBLE);
            }
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
            constraintLayout.setVisibility(View.GONE);
        });
        return root;
    }
    private void setUpRecyclerView() {
        postDao =new PostDao();
        Log.d("TAG", "PostDao created ");
        CollectionReference postsCollections = postDao.postCollections;
        Query query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING);Log.d("TAG", "query executed ");
        FirestoreRecyclerOptions<Post> recyclerViewOptions =new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        Log.d("TAG", "options ");
        adapter =new PostAdapter(recyclerViewOptions, this,this,this);
        Log.d("TAG", "adapter");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        Log.d("TAG", "recyclerView set");
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

    @Override
    public void onCommentClick(Post post , String postId) {

    }

    @Override
    public void onLikeClicked(String postId) {
        postDao.updateLikes(postId);
    }

    @Override
    public void onPostAdapterClicked(Post post,String postId) {
        startActivity(new Intent(requireActivity(), ViewPostActivity.class)
                .putExtra("Post", post)
                .putExtra("postId",postId));
    }
}