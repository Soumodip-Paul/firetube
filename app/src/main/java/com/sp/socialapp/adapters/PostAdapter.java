package com.sp.socialapp.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.socialapp.R;
import com.sp.socialapp.dao.UserDao;
import com.sp.socialapp.listeners.CommentAdapter;
import com.sp.socialapp.listeners.LikeAdapter;
import com.sp.socialapp.listeners.ViewPostListener;
import com.sp.socialapp.model.Post;
import com.sp.socialapp.model.User;
import com.sp.socialapp.utils.Util;

import java.util.Objects;

public class PostAdapter extends FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder> {
    CommentAdapter listener;
    LikeAdapter listener2;
    ViewPostListener onPostItemSelectedListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     */
    public PostAdapter(@NonNull FirestoreRecyclerOptions<Post> options, CommentAdapter listener, LikeAdapter listener2, @Nullable ViewPostListener onPostItemSelectedListener) {
        super(options);
        this.listener = listener;
        this.listener2 = listener2;
        this.onPostItemSelectedListener = onPostItemSelectedListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
        String id;
        if(model.getUserId() == null) {
             id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            Toast.makeText(holder.userImage.getContext(), getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId(), Toast.LENGTH_SHORT).show();
        }
        else { id = model.getUserId(); }
        new UserDao().getUserById(id)
            .addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
                assert user != null;
                holder.UserText.setText(user.getUserName());
            Glide.with(holder.userImage.getContext()).load(user.getImageUri()).circleCrop().into(holder.userImage);
        }).addOnFailureListener(e -> {
            holder.UserText.setText(R.string.anonymous);
            holder.userImage.setImageResource(R.drawable.ic_mystery_user);
        });
        holder.postText.setText(model.getText());
        holder.likeButton.setText(String.valueOf(model.getLikedBy().size()));
        holder.createdAt.setText( Util.getTimeAgo(model.getCreatedAt()));
        holder.comments.setText(String.valueOf(model.getCommentedBy().size()));
        if( model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(holder.postText.getContext()).load(model.getImageUrl()).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                    holder.postText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, resource);
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                    holder.postText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, placeholder);
                }
            });
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        boolean isLiked = model.getLikedBy().contains(currentUserId);
        holder.postText.setOnClickListener(v -> onPostItemSelectedListener.onPostAdapterClicked(model,getSnapshots().getSnapshot(position).getId()));
        holder.comments.setOnClickListener(v -> listener.onCommentClick(model,getSnapshots().getSnapshot(position).getId()));
        if(isLiked) {
            holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0 , 0, 0);
        } else {
            holder.likeButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_unliked , 0, 0 , 0);
        }

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostViewHolder viewHolder = new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
//        viewHolder.comments.setOnClickListener(v -> listener.onCommentClick(getSnapshots().getSnapshot(viewHolder.getAbsoluteAdapterPosition()).getId()));
        viewHolder.likeButton.setOnClickListener(v -> listener2.onLikeClicked(getSnapshots().getSnapshot(viewHolder.getAbsoluteAdapterPosition()).getId()));
//        viewHolder.postText.setOnClickListener( v -> onPostItemSelectedListener.onPostAdapterClicked(getSnapshots().getSnapshot(viewHolder.getAbsoluteAdapterPosition()).getId()));
        return viewHolder;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView postText,UserText,createdAt;
        Button comments,likeButton;
        public ImageView userImage;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postText =itemView.findViewById(R.id.postTitle);
            UserText =itemView.findViewById(R.id.viewPostUserName);
            createdAt =itemView.findViewById(R.id.createdAt);
            userImage =itemView.findViewById(R.id.userImage);
            likeButton =itemView.findViewById(R.id.likeButton);
            comments = itemView.findViewById(R.id.comments);

        }
    }
    }

