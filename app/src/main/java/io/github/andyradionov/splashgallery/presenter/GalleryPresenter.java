package io.github.andyradionov.splashgallery.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.model.dto.Image;
import io.github.andyradionov.splashgallery.model.network.ImagesApi;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */
@InjectViewState
public class GalleryPresenter extends MvpPresenter<GalleryView> {

    private final List<Image> mCachedImages;
    private int mCurrentPage;
    private int mMaxPage;
    private String mCurrentSearchRequest;
    private Disposable mSubscription;
    @Inject ImagesApi mImagesApi;

    public GalleryPresenter() {
        Timber.d("Constructor call");
        mCachedImages = new ArrayList<>(App.PAGE_SIZE);
        App.getAppComponent().inject(this);
    }

    /**
     * Searching images and caching previous result
     *
     * @param query User search query input or default gallery
     * @param page Page that needs to be load
     */
    public final void searchImages(@NonNull final String query, final int page) {

        Timber.d("searchImages(query = %s, page = %d)", query, page);

        if(mSubscription != null && !mSubscription.isDisposed()){
            mSubscription.dispose();
            mSubscription = null;
        }

        if (isRequestCached(query, page)) {
            getViewState().showImages(mCachedImages);
            return;
        }

        if (isMaxPage(page)) {
            getViewState().disableLoading();
            return;
        }
        if (isNewRequest(query)) clearCache(query);

        mSubscription = mImagesApi
                .searchImages(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Timber.d("doOnError: %s", throwable);
                    getViewState().showError();
                })
                .map(getSearchResultDto -> {
                    if (mMaxPage == 0) setMaxPage(getSearchResultDto.getTotalPages());
                    return getSearchResultDto.getResults();
                })
                .subscribe(images -> {
                    Timber.d("subscribe result: %s", images);
                    if (images.isEmpty()) {
                        getViewState().showError();
                    } else {
                        cachePage(images, page);
                        getViewState().showImages(images);
                    }
                }, throwable -> {
                    Timber.d("subscribe failure: %s", throwable);
                    getViewState().showError();
                });
    }

    private boolean isRequestCached(@NonNull final String query, final int page) {
        return page == mCurrentPage
                && query.equals(mCurrentSearchRequest)
                && !mCachedImages.isEmpty();
    }

    private boolean isNewRequest(@NonNull final String query) {
        return TextUtils.isEmpty(mCurrentSearchRequest)
                || !mCurrentSearchRequest.equals(query);
    }

    private void clearCache(@NonNull final String query) {
        mCachedImages.clear();
        mCurrentSearchRequest = query;
        mCurrentPage = 1;
        mMaxPage = 0;
    }

    private void cachePage(@NonNull final List<Image> images, final int page) {
        mCachedImages.addAll(images);
        mCurrentPage = page;
    }

    private boolean isMaxPage(final int page) {
        return (mMaxPage != 0 && page > mMaxPage);
    }

    private void setMaxPage(final int totalPagesInRequest) {
        mMaxPage = totalPagesInRequest >= App.MAX_PAGE_NUMBER ? App.MAX_PAGE_NUMBER : totalPagesInRequest;
    }
}
