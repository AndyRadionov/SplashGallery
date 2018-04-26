package io.github.andyradionov.splashgallery.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.andyradionov.splashgallery.GalleryView;
import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.model.Image;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Class for working with {@link ImagesApi}
 * Provides images search and caching previous search result
 *
 * @author Andrey Radionov
 */

public class ImagesNetworkStore {
    private final List<Image> mCachedImages;
    private int mCurrentPage;
    private int mMaxPage;
    private String mCurrentSearchRequest;
    private Disposable mSubscription;

    public ImagesNetworkStore() {
        Timber.d("Constructor call");
        mCachedImages = new ArrayList<>(App.PAGE_SIZE);
    }

    /**
     * Searching images and caching previous result
     *
     * @param galleryView GalleryView that provides callBack methods for showing result
     * @param query User search query input or default gallery
     * @param page Page that needs to be load
     */
    public final void searchImages(@NonNull final GalleryView galleryView,
                                   @NonNull final String query,
                                   final int page) {

        Timber.d("searchImages(query = %s, page = %d)", query, page);

        if(mSubscription != null && !mSubscription.isDisposed()){
            mSubscription.dispose();
            mSubscription = null;
        }

        if (isRequestCached(query, page) || isMaxPage(page)) {
            galleryView.showImages(mCachedImages);
            return;
        }

        if (isMaxPage(page)) {
            galleryView.disableLoading();
            return;
        }
        if (isNewRequest(query)) clearCache(query);

        mSubscription = App.getImagesApi()
                .searchImages(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Timber.d("doOnError: %s", throwable);
                    galleryView.showError();
                })
                .map(getSearchResultDto -> {
                    if (mMaxPage == 0) setMaxPage(getSearchResultDto.getTotalPages());
                    return getSearchResultDto.getResults();
                })
                .subscribe(images -> {
                    Timber.d("subscribe result: %s", images);
                    if (images.isEmpty()) {
                        galleryView.showError();
                    } else {
                        cachePage(images, page);
                        galleryView.showImages(images);
                    }
                }, throwable -> {
                    Timber.d("subscribe failure: %s", throwable);
                    galleryView.showError();
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
