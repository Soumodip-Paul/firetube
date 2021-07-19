package com.sp.socialapp.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

public class StorageDao {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    public static final String PROFILE_IMAGE_REFERENCE = "profile_images/";
    public static final String POST_IMAGE_REFERENCE = "posts/";

//    StorageMetadata metadata = new StorageMetadata.Builder()
//            .setContentType("image/jpg")
//            .build();

    public StorageReference addRef(String uploadFileReference){
        return storageRef.child(uploadFileReference);
    }
    public UploadTask uploadFile(@NotNull StorageReference reference, byte[] data){
        return reference
                .putBytes(data);
    }
    @Deprecated
    public Task<byte[]> downloadFileBytes(String uploadFileReference){
        StorageReference islandRef = storageRef.child(uploadFileReference);

        final long ONE_MEGABYTE = 1024 * 1024;
        return islandRef.getBytes(ONE_MEGABYTE);
    }

}
