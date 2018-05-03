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

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mateware.snacky.Snacky;
import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.ui.base.BaseActivity;
import timber.log.Timber;

/**
 * Image details Screen Provides share and save buttons
 *
 * @author Andrey Radionov
 */
public class ImageDetailsActivity extends BaseActivity implements ImageDetailsView {

    private static final int REQUEST_STORAGE_PERMISSION = 42;
    private static final String IS_IMAGE_LOADED_KEY = "is_image_loaded";
    private static final String IS_SNACK_SHOWED_KEY = "is_snack_showed";
    public static final String IMAGE_URL_EXTRA = "image_url";

    @InjectPresenter ImageDetailsPresenter mImageDetailsPresenter;
    @BindView(R.id.iv_image_details) ImageView mImageDetailsView;
    @BindView(R.id.pb_image_loading) ProgressBar mImageLoadingIndicator;
    private boolean mIsImageLoaded;
    private boolean mIsSnackShowed;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ButterKnife.bind(this);

        setActionBar(getString(R.string.app_name));

        if (savedInstanceState != null) {
            mIsImageLoaded = savedInstanceState.getBoolean(IS_IMAGE_LOADED_KEY);
            mIsSnackShowed = savedInstanceState.getBoolean(IS_SNACK_SHOWED_KEY);
        }

        final Intent startIntent = getIntent();
        mImageUrl = startIntent.getStringExtra(IMAGE_URL_EXTRA);

        Picasso.get().load(mImageUrl).into(mImageDetailsView, new Callback() {
            @Override
            public void onSuccess() {
                mIsImageLoaded = true;
                mImageLoadingIndicator.setVisibility(View.INVISIBLE);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onError(Exception e) {
                Timber.d(e.getLocalizedMessage());
                mImageLoadingIndicator.setVisibility(View.INVISIBLE);
                Picasso.get().load(R.drawable.error_placeholder)
                        .into(mImageDetailsView);
                showLoadError();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_IMAGE_LOADED_KEY, mIsImageLoaded);
        outState.putBoolean(IS_SNACK_SHOWED_KEY, mIsSnackShowed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        Drawable resIcon = getResources().getDrawable(R.drawable.ic_action_save);

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
                mIsSnackShowed = false;
                checkPermissions();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mImageDetailsPresenter.saveImage(mImageUrl);
            } else {
                if (!mIsSnackShowed) {
                    mIsSnackShowed = true;
                    Snacky.builder().setText(R.string.request_permission_text).setActivity(this).warning().show();
                }
            }
        }
    }

    @Override
    public void showSaveSuccess() {
        if (!mIsSnackShowed) {
            mIsSnackShowed = true;
            Snacky.builder().setText(R.string.image_saved_msg).setActivity(this).success().show();
        }
    }

    @Override
    public void showSaveError() {
        if (!mIsSnackShowed) {
            mIsSnackShowed = true;
            Snacky.builder().setText(R.string.error_image_save).setActivity(this).error().show();
        }
    }

    public void showLoadError() {
        if (!mIsSnackShowed) {
            mIsSnackShowed = true;
            Snacky.builder().setText(R.string.error_image_load).setActivity(this).error().show();
        }
    }

    private Intent createImageShareIntent(final String url) {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(R.string.share_message)
                .setText(url)
                .getIntent();
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
}
