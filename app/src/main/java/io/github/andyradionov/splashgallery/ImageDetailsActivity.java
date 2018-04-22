package io.github.andyradionov.splashgallery;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Image details Screen Provides share and save buttons
 *
 * @author Andrey Radionov
 */
public class ImageDetailsActivity extends AppCompatActivity {

    public static final String IMAGE_URL_EXTRA = "image_url";
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                final Intent shareIntent = createImageShareIntent(mImageUrl);
                startActivity(shareIntent);
                return true;
            case R.id.action_save:
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
}
