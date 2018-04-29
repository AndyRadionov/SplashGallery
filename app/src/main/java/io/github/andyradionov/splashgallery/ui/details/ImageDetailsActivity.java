package io.github.andyradionov.splashgallery.ui.details;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.mateware.snacky.Snacky;
import io.github.andyradionov.splashgallery.R;
import timber.log.Timber;

/**
 * Image details Screen Provides share and save buttons
 *
 * @author Andrey Radionov
 */
public class ImageDetailsActivity extends MvpAppCompatActivity implements ImageDetailsView {

    private static final int REQUEST_STORAGE_PERMISSION = 42;
    private static final String IS_IMAGE_LOADED_KEY = "is_image_loaded";
    public static final String IMAGE_URL_EXTRA = "image_url";

    @InjectPresenter ImageDetailsPresenter mImageDetailsPresenter;
    private boolean mIsImageLoaded;
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
                mIsImageLoaded = true;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_IMAGE_LOADED_KEY, mIsImageLoaded);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsImageLoaded = savedInstanceState.getBoolean(IS_IMAGE_LOADED_KEY);
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

        if (!mIsImageLoaded)
            resIcon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        item.setEnabled(mIsImageLoaded);
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
                mImageDetailsPresenter.saveImage(mImageUrl);
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
            mImageDetailsPresenter.saveImage(mImageUrl);
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
