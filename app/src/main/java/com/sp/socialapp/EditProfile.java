package com.sp.socialapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.socialapp.dao.UserDao;
import com.sp.socialapp.model.User;
import com.sp.socialapp.ui.bottomFragments.profile.ProfileFragment;
import com.sp.socialapp.ui.dialog.ImageDialog;
import com.sp.socialapp.utils.SaveValue;
import com.sp.socialapp.utils.UploadToFirebase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    EditText displayName,profession,email,pNumber,url;
    ImageView profileImage;
    ProgressBar pBar;
    FloatingActionButton fab;
    private boolean edited = true;
    User user;
    UserDao userDao;
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            this::HandleDialog);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userDao = new UserDao();
        fab =findViewById(R.id.floatingActionButton);
        displayName = findViewById(R.id.displayName);
        profession = findViewById(R.id.editTextProfession);
        email = findViewById(R.id.editTextEmail);
        pNumber = findViewById(R.id.editTextPhone);
        url = findViewById(R.id.editTextUrl);
        profileImage = findViewById(R.id.profilePhoto);
        pBar = findViewById(R.id.progressBar2);
        userDao
                .getUserById(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    assert user != null;
                    setAll(user);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error:"+e.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ProfileFragment.class));
                });
        fab.setOnClickListener(v -> {
            if (edited){
                setEnabledAll(true);
                fab.setImageResource(R.drawable.ic_ok);
                edited = false;
            }
            else {
                edited = true;
                setEnabledAll(false);
                fab.setImageResource(R.drawable.ic_edit);
                if (!CheckIfEmpty(displayName,pNumber,profession,email,url)) handleInput();
            }
        });
        profileImage.setOnClickListener(v -> changeProfileImage());

    }

    private void handleInput() {
        pBar.setVisibility(View.VISIBLE);
        String email1,displayName1,pNo1,url1,profession1;
        email1 = email.getText().toString();
        displayName1 = displayName.getText().toString();
        pNo1 = pNumber.getText().toString();
        url1 = url.getText().toString();
        profession1 = profession.getText().toString();

        if (!email1.equals(user.getEmail())){
            user.setEmail(email1);
        }
        if (!displayName1.equals(user.getUserName())){
            user.setUserName(displayName1);
        }
        if (!pNo1.equals(user.getPhoneNumber())){
            user.setPhoneNumber(pNo1);
        }
        if (!url1.equals(user.getUrl())){
            user.setUrl(url1);
        }
        if (!profession1.equals(user.getProfession())){
            user.setProfession(profession1);
        }

        userDao.updateUser(user)
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "An Error occurred :" + e.toString(), Toast.LENGTH_SHORT).show();
                    pBar.setVisibility(View.GONE);
                })
                .addOnSuccessListener(aVoid -> {
                pBar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), UiActivity.class));
                SaveValue.SaveString(getApplicationContext(),getString(R.string.key_user_name),user.getUserName());
                finish();
                });

    }

    private boolean CheckIfEmpty(@NotNull TextView... views) {
        for (TextView t: views
             ) {
            if ( t.getText().toString().isEmpty() || t.getText() == null){
                pBar.setVisibility(View.GONE);
                t.setError("Enter a valid data");
                return true;
            }
        }
        return false;
    }

    private  void setAll(User user){
        String imageUri = SaveValue.getString(this,getString(R.string.key_image_url),user.getImageUri());
        Glide.with(profileImage.getContext()).load(imageUri).circleCrop().into(profileImage);
        displayName.setText(user.getUserName());
        email.setText(user.getEmail());
        profession.setText(user.getProfession());
        pNumber.setText(user.getPhoneNumber());
        url.setText(user.getUrl());

    }

    private void setEnabledAll(boolean isEnabled){
        displayName.setEnabled(isEnabled);
        profession.setEnabled(isEnabled);
        //email.setEnabled(isEnabled);
        pNumber.setEnabled(isEnabled);
        url.setEnabled(isEnabled);
        profileImage.setEnabled(isEnabled);
    }

    private void changeProfileImage(){
        pBar.setVisibility(View.VISIBLE);
        mGetContent.launch("image/*");
    }
    private void HandleDialog(Uri uri) {
        if (uri != null) {

                ImageDialog dialog = new ImageDialog(uri,
                        uri1 -> {
                            try {
                                upload(uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        },
                        () -> pBar.setVisibility(View.GONE));
                dialog.show(getSupportFragmentManager(), "Image Dialog");

        }
        else pBar.setVisibility(View.GONE);
    }

    public void upload(Uri uri) throws IOException {
        UploadToFirebase.uploadProfileImageToFirebase(this , uri , Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+".jpg",
                task -> {
                    if (!task.isSuccessful()){
                        pBar .setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Image Not uploaded", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    SaveValue.SaveString(EditProfile.this,getString(R.string.key_image_url),downloadUri.toString());

                    user.setImageUri(downloadUri.toString());
                    new UserDao().updateUser(user)
                            .addOnSuccessListener(aVoid -> Glide.with(profileImage.getContext()).load(downloadUri).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    pBar.setVisibility(View.GONE);
                                    Toast.makeText(EditProfile.this, "Image Loading Failed", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    pBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).circleCrop().into(profileImage))
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditProfile.this, "Cannot refresh the page", Toast.LENGTH_SHORT).show();
                                pBar.setVisibility(View.GONE);
                            });
                },progress -> {});

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),UiActivity.class));
        super.onBackPressed();
    }

}