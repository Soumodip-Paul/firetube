package com.sp.socialapp.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.sp.socialapp.dao.StorageDao;
import com.sp.socialapp.listeners.ProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class UploadToFirebase {
    private static void uploadImageToFirebase(@NotNull Bitmap fullBitmap, String referenceName, String imageName, OnCompleteListener<Uri> uriOnCompleteListener, ProgressListener listener){

        int scaleDivider = 4;

            // 1. Get the downsized image content as a byte[]
            int scaleWidth = fullBitmap.getWidth() / scaleDivider;
            int scaleHeight = fullBitmap.getHeight() / scaleDivider;
            byte[] downsizedImageBytes =
                    Util.getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight);

            // 2. Upload the byte[]; Eg, if you are using Firebase
            StorageDao dao = new StorageDao();
                    StorageReference reference = dao.addRef(referenceName + imageName);
                    dao.uploadFile(reference,downsizedImageBytes)
                            .addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                listener.onChangeProgress(progress);
                                Log.d("upload", "uploadImageToFirebase: "+progress);
                            })
                            .addOnPausedListener(taskSnapshot -> {

                            })

                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
                        return reference.getDownloadUrl();
                    })
                    .addOnCompleteListener(uriOnCompleteListener);

    }
    public static void uploadProfileImageToFirebase(Activity activity , Uri uri, String imageName, OnCompleteListener<Uri> uriOnCompleteListener, ProgressListener listener) throws IOException {

         Bitmap fullBitmap = Util.getBitmap(activity,uri);
        uploadImageToFirebase(fullBitmap, StorageDao.PROFILE_IMAGE_REFERENCE ,imageName, uriOnCompleteListener,listener);
    }

    public static void uploadPostImageToFirebase(Activity activity, Uri uri, String imageName, OnCompleteListener<Uri> uriOnCompleteListener ,ProgressListener listener) throws IOException {
        Bitmap fullBitmap = Util.getBitmap(activity,uri);
        uploadImageToFirebase(fullBitmap,
                StorageDao.POST_IMAGE_REFERENCE + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/"  ,
                imageName, uriOnCompleteListener,listener);
    }

    /*private static Bitmap decodeUriAsBitmap(Activity activity,Uri uri){
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }*/
}

