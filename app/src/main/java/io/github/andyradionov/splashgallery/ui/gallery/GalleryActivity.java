package io.github.andyradionov.splashgallery.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.model.Image;
import io.github.andyradionov.splashgallery.ui.about.AboutActivity;
import io.github.andyradionov.splashgallery.ui.details.ImageDetailsActivity;

/**
 * Main Application screen with Image gallery
 *
 * @author Andrey Radionov
 */
public class GalleryActivity extends MvpAppCompatActivity implements
        GalleryAdapter.OnGalleryImageClickListener, GalleryView {

    private static final String CURRENT_QUERY_KEY = "current_query";
    private static final String CURRENT_PAGE_KEY = "current_page";

    @InjectPresenter GalleryPresenter mGalleryPresenter;
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
                mGalleryPresenter.searchImages(mCurrentRequest, mCurrentPage);
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
        mGalleryPresenter.searchImages(mCurrentRequest, mCurrentPage);
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

        searchAction.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                homeAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                homeAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                return true;

            }
        });

        return true;
    }

    private void restartSearch(String query) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mScrollListener.resetState();
        mCurrentPage = 1;
        mCurrentRequest = query;
        mGalleryAdapter.clearData();
        mGalleryPresenter.searchImages(query, mCurrentPage);
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
