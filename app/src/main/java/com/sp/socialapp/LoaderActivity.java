package com.sp.socialapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sp.socialapp.dao.UserDao;
import com.sp.socialapp.model.User;
import com.sp.socialapp.utils.SaveValue;

public class LoaderActivity extends Activity {
    TextView AppName,Text;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        AppName = findViewById(R.id.AppName);
        Text = findViewById(R.id.welcomeText);
        imageView = findViewById(R.id.imageView);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"TranslationY",0f,100f,-100f,50f,-50f,20f).setDuration(5000);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Path path = new Path();
            path.arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true);
            animator = ObjectAnimator.ofFloat(AppName, View.X, View.Y, path);
            animator.setDuration(2000);
            animator.start();
        } else {
            // Create animator without using curved path

        }*/
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"TranslationY",100f);
//        objectAnimator.setDuration(2000);
//        ObjectAnimator animatorL = ObjectAnimator.ofFloat(Text,"TranslationY",100f).setDuration(2000);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator);
        set.start();

        new Thread() {
            public void run() {
                try {

                    // Thread will sleep for 5 seconds
                    sleep(5000);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user == null)
                    // After 5 seconds redirect to another intent
                    {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }
                    else {

                        new UserDao().getUserById(user.getUid()).addOnSuccessListener(
                                documentSnapshot -> {
                                    User user1 = documentSnapshot.toObject(User.class);
                                    SaveValue.SaveString(LoaderActivity.this,getString(R.string.key_user_name),user1.getUserName());
                                    SaveValue.SaveString(LoaderActivity.this,getString(R.string.key_image_url),user1.getImageUri());
                                    startActivity(new Intent(getBaseContext(),UiActivity.class));
                                    finish();
                                }
                        ).addOnFailureListener(
                                e -> {
                                    startActivity(new Intent(getBaseContext(),UiActivity.class));
                                    finish();
                                }
                        );


                    }


                    //Remove activity

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } .start();

    }
}