package com.sp.socialapp.listeners;

import com.sp.socialapp.model.Post;

public interface ViewPostListener {
    void onPostAdapterClicked(Post post,String postId);
}
