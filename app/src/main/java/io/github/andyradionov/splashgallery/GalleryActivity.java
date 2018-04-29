package io.github.andyradionov.splashgallery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.model.Image;

/**
 * Main Application screen with Image gallery
 *
 * @author Andrey Radionov
 */
public class GalleryActivity extends AppCompatActivity implements
        GalleryAdapter.OnGalleryImageClickListener, GalleryView {

    private static final String CURRENT_QUERY_KEY = "current_query";
    private static final String CURRENT_PAGE_KEY = "current_page";

    private PagingScrollListener mScrollListener;
    private GalleryAdapter mGalleryAdapter;
    private ProgressBar mLoadingIndicator;
    private int mCurrentPage;
    private String mCurrentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mCurrentPage = 1;
        mCurrentRequest = App.MAIN_GALLERY;

        mLoadingIndicator = findViewById(R.id.pb_gallery_loading);
        RecyclerView galleryContainer = findViewById(R.id.rv_gallery_container);
        mGalleryAdapter = new GalleryAdapter(this);

        boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        int columns = isLandscape ? 3 : 2;

        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        galleryContainer.setAdapter(mGalleryAdapter);
        galleryContainer.setLayoutManager(layoutManager);

        mScrollListener = new PagingScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, @NonNull RecyclerView view) {
                mCurrentPage = page;
                App.getImagesNetworkStore().searchImages(GalleryActivity.this, mCurrentRequest, mCurrentPage);
            }
        };
        galleryContainer.addOnScrollListener(mScrollListener);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_QUERY_KEY, mCurrentRequest);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentRequest = savedInstanceState.getString(CURRENT_QUERY_KEY);
        mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getImagesNetworkStore().searchImages(this, mCurrentRequest, mCurrentPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchAction = menu.findItem(R.id.action_search);
        final MenuItem homeAction = menu.findItem(R.id.action_home);

        SearchView searchView = (SearchView) searchAction.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                homeAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

                restartSearch(query);

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchAction.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(v ->
                homeAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER));

        return true;
    }

    private void restartSearch(String query) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mScrollListener.resetState();
        mCurrentPage = 1;
        mCurrentRequest = query;
        mGalleryAdapter.clearData();
        App.getImagesNetworkStore().searchImages(GalleryActivity.this, query, mCurrentPage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                restartSearch(App.MAIN_GALLERY);

                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(@NonNull String imageUrl) {
        Intent showImageIntent = new Intent(this, ImageDetailsActivity.class);
        showImageIntent.putExtra(ImageDetailsActivity.IMAGE_URL_EXTRA, imageUrl);
        startActivity(showImageIntent);
    }

    @Override
    public void showImages(@NonNull final List<Image> images) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mGalleryAdapter.updateData(images);
    }

    @Override
    public void showError() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
    }

    @Override
    public void disableLoading() {
        mScrollListener.disableLoadingMore();
    }
}
