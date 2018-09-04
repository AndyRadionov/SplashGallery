package io.github.andyradionov.splashgallery.ui.details;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import de.mateware.snacky.Snacky;
import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.presenter.ImageDetailsPresenter;
import io.github.andyradionov.splashgallery.ui.common.BaseActivity;
import timber.log.Timber;

/**
 * Image details Screen Provides share and save buttons
 *
 * @author Andrey Radionov
 */
public class ImageDetailsActivity extends BaseActivity implements ImageDetailsView {

    private static final int REQUEST_STORAGE_PERMISSION = 42;
    private static final String IS_IMAGE_LOADED_KEY = "is_image_loaded";
    public static final String IMAGE_URL_EXTRA = "image_url";
    public static final String IMAGE_ID_EXTRA = "image_id";

    @Inject
    @InjectPresenter
    ImageDetailsPresenter mImageDetailsPresenter;

    @ProvidePresenter
    ImageDetailsPresenter providePresenter() {
        return mImageDetailsPresenter;
    }

    @BindView(R.id.iv_image_details)
    ImageView mImageDetailsView;
    @BindView(R.id.pb_image_loading)
    ProgressBar mImageLoadingIndicator;
    @BindView(R.id.btn_set_as_wallpaper)
    TextView mSetWallpaperButton;
    private AlertDialog mSetWallpaperDialog;
    private boolean mIsImageLoaded;
    private String mImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ButterKnife.bind(this);

        setActionBar(getString(R.string.app_name));

        if (savedInstanceState != null) {
            mIsImageLoaded = savedInstanceState.getBoolean(IS_IMAGE_LOADED_KEY);
        }

        final Bundle extras = getIntent().getExtras();
        mImageUrl = extras.getString(IMAGE_URL_EXTRA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(IMAGE_ID_EXTRA);
            mImageDetailsView.setTransitionName(imageTransitionName);
        }

        Picasso.get().load(mImageUrl).into(mImageDetailsView, new Callback() {
            @Override
            public void onSuccess() {
                mIsImageLoaded = true;
                mImageLoadingIndicator.setVisibility(View.INVISIBLE);
                mSetWallpaperButton.setEnabled(true);
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
            case android.R.id.home:
                onBackPressed();
                return true;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mImageDetailsPresenter.saveImage(mImageUrl);
            } else {
                Snacky.builder().setText(R.string.request_permission_text).setActivity(this).warning().show();
            }
        }
    }

    @OnClick(R.id.btn_set_as_wallpaper)
    public void onSetAsWallpaperClick(View view) {
        mImageDetailsPresenter.showSetWallpaperDialog();
    }

    @Override
    public void showSaveSuccess(String message) {
        Snacky.builder().setText(message).setActivity(this).success().show();
    }

    @Override
    public void showSaveError(String message) {
        Snacky.builder().setText(message).setActivity(this).error().show();
    }

    @Override
    public void showSetWallpaperDialog() {
        mSetWallpaperDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.set_wallpaper_message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        (dialog, id) -> {
                            dialog.cancel();
                            mImageDetailsPresenter.setWallpaper(mImageUrl);
                        })
                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> mImageDetailsPresenter.hideSetWallpaperDialog())
                .show();
    }

    @Override
    public void hideSetWallpaperDialog() {
        if (mSetWallpaperDialog != null) {
            mSetWallpaperDialog.dismiss();
        }
    }

    private void showLoadError() {
        Snacky.builder().setText(R.string.error_image_load).setActivity(this).error().show();
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
