package com.sp.socialapp.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.socialapp.model.User;

public class UserDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersCollection = db.collection("users");

    public void addUser(@NonNull User user) {
        usersCollection.document(user.getUid()).set(user);
    }
    public Task<DocumentSnapshot> getUserById(String uId) {
        return usersCollection.document(uId).get();
    }
    public Task<Void> updateUser(User user) {

       return usersCollection.document(user.getUid()).set(user);

    }
}