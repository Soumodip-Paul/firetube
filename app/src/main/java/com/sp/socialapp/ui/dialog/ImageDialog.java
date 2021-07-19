package com.sp.socialapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sp.socialapp.R;

import java.io.IOException;

public class ImageDialog extends DialogFragment {

    private final Uri uri;
    private final PostListener postListener;
    private final CancelListener cancelListener;


    public ImageDialog(Uri uri, PostListener postListener,CancelListener cancelListener) {
        this.uri = uri;
        this.postListener = postListener;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.alert_image_view, null));

        Dialog dialog = builder.create();
        dialog.setOnShowListener(d ->
        {
            ImageView imageView = dialog.findViewById(R.id.alert_image);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),uri);
                //imageView.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


            dialog.findViewById(R.id.alert_cancel).setOnClickListener(
                    v -> {
                        dialog.dismiss();
                        cancelListener.onCancel();
                    }
            );
            dialog.findViewById(R.id.alert_post).setOnClickListener(
                    v -> {
                        postListener.onPost(uri);
                        dialog.dismiss();
                    }
            );

        });


        return dialog;
    }

    public interface PostListener{
        void onPost(Uri uri);
    }
    public interface CancelListener{
        void onCancel();
    }
}
