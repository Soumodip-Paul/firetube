package com.sp.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sp.socialapp.utils.SaveValue;

import java.util.Objects;

public class UiActivity extends AppCompatActivity {

    FirebaseUser user;
    ImageView imageView;
    TextView title;
    String userTitle,userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        user = FirebaseAuth.getInstance().getCurrentUser();
        imageView = findViewById(R.id.profileImage);
        title = findViewById(R.id.title);
        findViewById(R.id.fab2).setOnClickListener(this::goToActivity);


        onLoad();

        BottomNavigationView navView = findViewById(R.id.nav_view);
//         Passing each menu ID as a set of Ids because each
//         menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_profile)
//                .build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        imageView.setOnClickListener(v -> startActivity(new Intent(this,EditProfile.class)));

    }

    private void goToActivity(View view) {
        startActivity(new Intent(this,CreatePostActivity.class));
    }


    protected void onLoad() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        userTitle = SaveValue.getString(this,getString(R.string.key_user_name),user.getDisplayName());
        userImage = SaveValue.getString(this,getString(R.string.key_image_url), Objects.requireNonNull(user.getPhotoUrl()).toString());
        Glide.with(imageView.getContext()).load(userImage).circleCrop().into(imageView);
        title.setText(userTitle);
    }
}