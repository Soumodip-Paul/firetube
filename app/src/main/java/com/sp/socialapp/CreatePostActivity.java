package com.sp.socialapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.socialapp.dao.PostDao;
import com.sp.socialapp.listeners.ProgressListener;
import com.sp.socialapp.model.Post;
import com.sp.socialapp.utils.SaveValue;
import com.sp.socialapp.utils.UploadToFirebase;
import com.sp.socialapp.utils.Util;

import java.io.IOException;
import java.util.Objects;

public class CreatePostActivity extends AppCompatActivity implements ProgressListener {
    EditText editText;
    Button postButton , cancelButton;
    ImageView uploadImage , userImage;
    Uri dataUri;
    ProgressBar loader;
    String captionText , imageUrl;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            this::HandleUri);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        editText = findViewById(R.id.create_post_text);
        postButton = findViewById(R.id.create_post_upload);
        cancelButton = findViewById(R.id.create_post_cancel);
        uploadImage = findViewById(R.id.create_post_upload_image);
        userImage = findViewById(R.id.create_post_user_image);
        loader = findViewById(R.id.create_post_upload_loader);

        Glide.with(userImage.getContext())
                .load(SaveValue.getString(this,getString(R.string.key_image_url),null))
                .circleCrop()
                .into(userImage);
        cancelButton.setOnClickListener(v -> finish());
        uploadImage.setOnClickListener( view -> mGetContent.launch("image/*"));
        postButton.setOnClickListener(this::upLoad);


    }
    private void HandleUri(Uri uri) {
        if(uri != null) {
            try {
                dataUri = uri;
                Bitmap bitmap = Util.getBitmap(this,uri);
                uploadImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Internal Server Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    private void upLoad(View view){
        if (editText.getText() == null || editText.getText().toString().isEmpty()){
            editText.setError("Please Enter Your Caption");
        }
        else {
            view.setEnabled(false);
            captionText = editText.getText().toString();
            try {
                if(dataUri != null)
                    UploadToFirebase.uploadPostImageToFirebase(this,
                        dataUri, System.currentTimeMillis() + ".jpg",
                        this::getDownLoadUri,this);
                else Toast.makeText(this, "Please Select an image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    void getDownLoadUri(@NonNull Task<Uri> task){
        if (!task.isSuccessful()||task.getResult() == null){
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            return;
        }

            task.addOnCompleteListener(
                    downloadImageUri-> {
                        imageUrl = task.getResult().toString();
                        Post post = new Post(editText.getText().toString(), Objects.requireNonNull(mAuth.getCurrentUser()).getUid(),
                                imageUrl ,System.currentTimeMillis());
                        new PostDao().addPost(post);
                        finish();
                    }
            );

    }

    @Override
    public void onChangeProgress(double progress) {
        int p = (int)progress;
        System.out.println(progress);
            if (p > 0 && p != 100) {
                loader.setVisibility(View.VISIBLE);
                loader.setProgress(p);
            } else if (progress == 100) {
                loader.setProgress(0);
                loader.setVisibility(View.GONE);
            }

    }
}