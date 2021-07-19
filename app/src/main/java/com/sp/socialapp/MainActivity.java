package com.sp.socialapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sp.socialapp.dao.UserDao;
import com.sp.socialapp.model.User;
import com.sp.socialapp.utils.SaveValue;

import java.util.Objects;

public class
MainActivity extends AppCompatActivity {
//    private static final int RC_SIGN_IN = 1;
    ActivityResultLauncher<Intent> mStartForResult;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton button;
    TextView textView;
    EditText emailText,PassText,ConfirmPass;
    Button LogIn,Register,Ok;
    ProgressBar progressBar;

    final
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.signInButton);
        emailText = findViewById(R.id.email_login);
        PassText = findViewById(R.id.password_login);
        ConfirmPass = findViewById(R.id.confirm_password_login);
        LogIn = findViewById(R.id.LogIn);
        Register = findViewById(R.id.Register);
        Ok = findViewById(R.id.confirm);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);

        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            //Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                            assert account != null;
                            PassText.setVisibility(View.VISIBLE);
                            ConfirmPass.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Ok.setVisibility(View.VISIBLE);
                            Ok.setOnClickListener(v -> {
                                String cP = checkPassword();
                                if (cP != null) {
                                    Ok.setVisibility(View.GONE);
                                    changeVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    firebaseAuthWithGoogle(account.getIdToken(), cP);
                                }
                                else PassText.setText("");
                            });

                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.w("TAG", "Google sign in failed", e);
                            updateUI(null);
                        }
                    }
                });

        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            changeVisibility(View.GONE);
            signIn();

        });
        LogIn.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String pass = PassText.getText().toString();
            if (!email.isEmpty()&&!pass.isEmpty()) {
                LoginEmail(email,pass);
            }
            else {
                Toast.makeText(this, "Enter a valid email or password", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mStartForResult.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken, String pass) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null)
                        user.updatePassword(pass).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                updateUI(user);
                            }
                            else{
                                signOut();
                            }
                        });

                        else signOut();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        progressBar.setVisibility(View.GONE);
        if (user != null) {
            String uId = user.getUid();
            String Name = user.getDisplayName();
            String email = user.getEmail();
            String imageUri = Objects.requireNonNull(user.getPhotoUrl()).toString();
            String pNumber = user.getPhoneNumber();
            User registerUser = new User(Name,uId,imageUri,pNumber,email);
            new UserDao().addUser(registerUser);
            startActivity(new Intent(this,LoaderActivity.class));
            finish();
        }
        else{
            Toast.makeText(this, "NO user", Toast.LENGTH_SHORT).show();
            changeVisibility(View.VISIBLE);
            PassText.setText("");
            ConfirmPass.setText("");
        }
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }
    private void changeVisibility(int Visibility){

        button.setVisibility(Visibility);
        textView.setVisibility(Visibility);
        emailText.setVisibility(Visibility);
        PassText.setVisibility(Visibility);
        ConfirmPass.setVisibility(Visibility);
        LogIn.setVisibility(Visibility);
        Register.setVisibility(Visibility);

    }
    private String checkPassword(){
        String pass = PassText.getText().toString();
        String pass2 = ConfirmPass.getText().toString();
        if( pass.equals(pass2)) return  pass;
        else {
            PassText.setError("Check now");
            return  null;
        }
    }
    private void LoginEmail(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user!= null){
                            startActivity(new Intent(this,LoaderActivity.class));
                            finish();
                        }
                        else {
                            updateUI(null);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });

    }

}
