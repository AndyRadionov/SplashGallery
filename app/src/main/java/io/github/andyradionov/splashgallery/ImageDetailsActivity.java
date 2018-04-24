package io.github.andyradionov.splashgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.mateware.snacky.Snacky;
import io.github.andyradionov.splashgallery.utils.ImageSaverUtils;
import timber.log.Timber;

/**
 * Image details Screen Provides share and save buttons
 *
 * @author Andrey Radionov
 */
public class ImageDetailsActivity extends AppCompatActivity implements ImageSaveCallback {
    private static final int REQUEST_STORAGE_PERMISSION = 42;

    public static final String IMAGE_URL_EXTRA = "image_url";
    private boolean isImageLoaded;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        final Intent startIntent = getIntent();
        mImageUrl = startIntent.getStringExtra(IMAGE_URL_EXTRA);

        final ImageView imageDetailsView = findViewById(R.id.iv_image_details);
        final ProgressBar imageLoadingIndicator = findViewById(R.id.pb_image_loading);

        Picasso.get().load(mImageUrl).into(imageDetailsView, new Callback() {
            @Override
            public void onSuccess() {
                isImageLoaded = true;
                imageLoadingIndicator.setVisibility(View.INVISIBLE);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onError(Exception e) {
                //todo add Broken Image instead of loaded
                Timber.d("+++=====================+++ " + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        Drawable resIcon = getResources().getDrawable(android.R.drawable.ic_menu_save);

        if (!isImageLoaded)
            resIcon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        item.setEnabled(isImageLoaded);
        item.setIcon(resIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                final Intent shareIntent = createImageShareIntent(mImageUrl);
                startActivity(shareIntent);
                return true;
            case R.id.action_save:
                checkPermissions();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createImageShareIntent(final String url) {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Look at this Image")
                .setText(url)
                .getIntent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImageSaverUtils.saveImage(this, mImageUrl);
            } else {
                Toast.makeText(this, "PERMISSIONS!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            ImageSaverUtils.saveImage(this, mImageUrl);
        }
    }

    @Override
    public void showSaveSuccess() {
        Snacky.builder().setText("IMAGE SAVED").setActivity(this).success().show();
    }

    @Override
    public void showSaveError() {
        Snacky.builder().setText("ERROR IMAGE SAVING").setActivity(this).error().show();
    }
}
