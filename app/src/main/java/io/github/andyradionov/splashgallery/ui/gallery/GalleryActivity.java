package io.github.andyradionov.splashgallery.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.model.Image;
import io.github.andyradionov.splashgallery.ui.about.AboutActivity;
import io.github.andyradionov.splashgallery.ui.base.BaseActivity;
import io.github.andyradionov.splashgallery.ui.details.ImageDetailsActivity;
import io.github.andyradionov.splashgallery.utils.NetworkUtils;

/**
 * Main Application screen with Image gallery
 *
 * @author Andrey Radionov
 */
public class GalleryActivity extends BaseActivity implements
        GalleryAdapter.OnGalleryImageClickListener, GalleryView {

    private static final String CURRENT_QUERY_KEY = "current_query";
    private static final String CURRENT_PAGE_KEY = "current_page";

    @InjectPresenter GalleryPresenter mGalleryPresenter;
    @BindView(R.id.pb_gallery_loading) ProgressBar mLoadingIndicator;
    @BindView(R.id.rv_gallery_container) RecyclerView mGalleryContainer;
    @BindView(R.id.iv_no_wifi) ImageView mNoInternetView;
    @BindView(R.id.tv_error) TextView mErrorView;

    private PagingScrollListener mScrollListener;
    private GalleryAdapter mGalleryAdapter;
    private int mCurrentPage;
    private String mCurrentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        setActionBar(getString(R.string.app_name));

        mCurrentPage = 1;
        mCurrentRequest = App.MAIN_GALLERY;

        setupRecycler();
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
        setActionBarTitle(mCurrentRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        restartSearch(mCurrentRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchAction = menu.findItem(R.id.action_search);
        final MenuItem homeAction = menu.findItem(R.id.action_home);

        SearchView searchView = (SearchView) searchAction.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setActionBarTitle(query);
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
                homeAction.setVisible(false);
                searchView.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                homeAction.setVisible(true);
                invalidateOptionsMenu();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                restartSearch(App.MAIN_GALLERY);
                setActionBarTitle(getString(R.string.app_name));
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
        setViewsVisibility(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);

        mGalleryAdapter.updateData(images);
    }

    @Override
    public void showError() {
        setViewsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
    }

    @Override
    public void disableLoading() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mScrollListener.disableLoadingMore();
    }

    private void setupRecycler() {
        mGalleryAdapter = new GalleryAdapter(this);

        int columns = getResources().getInteger(R.integer.gallery_cols_number);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        mGalleryContainer.setAdapter(mGalleryAdapter);
        mGalleryContainer.setLayoutManager(layoutManager);

        mScrollListener = new PagingScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, @NonNull RecyclerView view) {
                mCurrentPage = page;
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mGalleryPresenter.searchImages(mCurrentRequest, mCurrentPage);
            }
        };
        mGalleryContainer.addOnScrollListener(mScrollListener);
    }

    private void restartSearch(String query) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            setViewsVisibility(View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
        } else {
            setViewsVisibility(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
            mScrollListener.resetState();
            mCurrentPage = 1;
            mCurrentRequest = query;
            mGalleryAdapter.clearData();
            mGalleryPresenter.searchImages(query, mCurrentPage);
        }
    }

    private void setViewsVisibility(int container, int loader, int internet, int error) {
        mGalleryContainer.setVisibility(container);
        mLoadingIndicator.setVisibility(loader);
        mNoInternetView.setVisibility(internet);
        mErrorView.setVisibility(error);
    }
}
