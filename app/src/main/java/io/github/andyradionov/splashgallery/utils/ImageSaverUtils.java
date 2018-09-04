package io.github.andyradionov.splashgallery.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.andyradionov.splashgallery.BuildConfig;
import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.ui.details.ImageSaveCallback;
import timber.log.Timber;

/**
 * Utility class for saving Image to External storage
 *
 * @author Andrey Radionov
 */

public class ImageSaverUtils {

    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ROOT);
    private static final String FILE_NAME_FORMAT = "IMG_%s.jpg";

    private Context context;
    public ImageSaverUtils(Context context) {
        this.context = context;
    }

    /**
     * Saving image to External Storage inside Gallery directory
     * Saving built with Picasso library
     *
     * @param callback Callback that shows user result of image saving
     * @param imageUrl Url of Image that needs to be saved
     */
    public synchronized void saveImage(@NonNull final ImageSaveCallback callback, final String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .into(picassoImageTarget(callback));
    }

    public synchronized void setWallpaper(@NonNull final ImageSaveCallback callback, final String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .into(picassoWallpaperTarget(callback));
    }

    private synchronized Target picassoWallpaperTarget(@NonNull final ImageSaveCallback callback) {
        Timber.d("picassoImageTarget");
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    WallpaperManager.getInstance(context).setBitmap(bitmap);
                    callback.showSuccess(context.getString(R.string.wallpaper_set_msg));
                } catch (RuntimeException e) {
                    Timber.d(e, "WallpaperManager Exception");
                    callback.showSuccess(context.getString(R.string.wallpaper_set_msg));
                } catch (IOException e) {
                    callback.showError(context.getString(R.string.error_wallpaper_set));
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                callback.showError(context.getString(R.string.error_wallpaper_set));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

    private synchronized Target picassoImageTarget(@NonNull final ImageSaveCallback callback) {
        Timber.d("picassoImageTarget");
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                if (saveImage(bitmap)) {
                    callback.showSuccess(context.getString(R.string.image_saved_msg));
                } else {
                    callback.showError(context.getString(R.string.error_image_save));
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                callback.showError(context.getString(R.string.error_image_save));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

    private static synchronized boolean saveImage(@NonNull final Bitmap image) {
        String savedImagePath;

        final String imageFileName = String.format(FILE_NAME_FORMAT, DATE_FORMAT.format(new Date()));
        final File storageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/" + BuildConfig.IMG_FOLDER_NAME);

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (!success) {
            return false;
        }
        final File imageFile = new File(storageDir, imageFileName);
        savedImagePath = imageFile.getAbsolutePath();
        try {
            OutputStream fOut = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.close();
            galleryAddPic(savedImagePath);
            return true;
        } catch (Exception e) {
            Timber.d("Error saving image");
            return false;
        }
    }

    private static synchronized void galleryAddPic(@NonNull final String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
    }
}
