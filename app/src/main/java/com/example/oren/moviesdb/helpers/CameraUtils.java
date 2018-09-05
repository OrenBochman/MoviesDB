package com.example.oren.moviesdb.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtils {

    public static final String TAG = CameraUtils.class.getName();
    public  static  Uri photoURI ;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_TAKE_PHOTO = 1;

    /**
     *
     * Captures a camera image using an available photo app.
     *
     * creates a unique file name which is converted to a URI
     *
     *
     * @param context
     */
    public static void dispatchTakePictureIntent(Activity context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException e) {
                // Error occurred while creating the File
                Log.e(TAG, "dispatchTakePictureIntent:Error occurred while creating the File ", e);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                CameraUtils.photoURI = FileProvider.getUriForFile(context,
                        "com.example.oren.moviesdb",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, CameraUtils.photoURI);
                context.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public static String mCurrentPhotoPath;

    /**
     * Create a collision resistant name for the photo based on a timestamp.
     *
     * @param context - calling context
     * @return File for the image being created
     * @throws IOException
     */
    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /** Invoke the media scanner after new image is added
     *
      * @param context - calling context
     */

    public static void galleryAddPic(Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    /**
     * Decode a scaled image.
     * <p>
     * Managing multiple full-sized images can be tricky with limited memory.
     * If you find your application running out of memory after displaying just a few images,
     * you can dramatically reduce the amount of dynamic heap used by expanding the JPEG into a
     * memory array that's already scaled to match the size of the destination view.
     * The following example method demonstrates this technique.
     *
     * @param targetW get via ImageView.getWidth();
     * @param targetH get via mImageView.getHeight();
     * @return the scaled bitmap - use with   mImageView.setImageBitmap(resizePic(height,width,bitmapPath));

     */
    public static Bitmap resizePic(int targetW, int targetH, String mCurrentPhotoPath) {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

    }
}
