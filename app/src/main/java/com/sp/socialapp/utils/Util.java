package com.sp.socialapp.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Util {
    @Nullable
    public static String getTimeAgo(long time) {
        long now= System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        long diff = now - time;
        long SECOND_MILLIS = 1000;
        long MINUTE_MILLIS = 60 * SECOND_MILLIS;
        long HOUR_MILLIS = 60 * MINUTE_MILLIS;
        if (diff < MINUTE_MILLIS) {
            return  "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return  "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return (diff / MINUTE_MILLIS) + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return  (diff / HOUR_MILLIS) + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return  "yesterday";
        } else if (diff < 30 * 24 * HOUR_MILLIS) {
            return  (diff / ( 24 * HOUR_MILLIS)) + " days ago";
        } else if (diff < 365 * 24 * HOUR_MILLIS) {
            return (diff / ( 30 * 24 * HOUR_MILLIS)) + " months ago";
        } else
            return (diff / ( 365 * 24 * HOUR_MILLIS)) + " years ago";
    }
    @Deprecated
    public static Bitmap compressImage(@NotNull Bitmap image) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Compression quality, here 100 means no compression, the storage of compressed data to outputStream
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        //problem set options=90
        int options = 99;

        while (outputStream.toByteArray().length / 1024 > 400) {
        //Loop if compressed picture is greater than 400kb, than to compression
            outputStream.reset();//Reset outputStream is empty outputStream

        // The compression options%, storing the compressed data to the outputStream
            image.compress(Bitmap.CompressFormat.JPEG, options, outputStream);

        //Every time reduced by 1,any problem set options-=10
            options -= 1;
        }
        //The storage of compressed data in the outputStream to ByteArrayInputStream
            ByteArrayInputStream isBm = new ByteArrayInputStream(outputStream.toByteArray());

        return BitmapFactory.decodeStream(isBm, null, null);
    }


    public static Bitmap getBitmap(@NotNull Activity activity , Uri imageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
    }
    @NotNull
    public static byte[] getDownsizedImageBytes(Bitmap fullBitmap, int scaleWidth, int scaleHeight) {

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true);

        // 2. Instantiate the downsized image content as a byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }
}
