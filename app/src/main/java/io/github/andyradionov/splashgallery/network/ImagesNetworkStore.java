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

    private Disposable mSubscription;

    public ImagesNetworkStore() {
        Timber.d("Constructor call");
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

        mSubscription = App.getImagesApi()
                .searchImages(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Timber.d("doOnError: %s", throwable);
                    galleryView.showError();
                })
                .map(getSearchResultDto -> getSearchResultDto.getResults())
                .subscribe(images -> {
                    Timber.d("subscribe result: %s", images);
                    if (images.isEmpty()) {
                        galleryView.showError();
                    } else {
                        galleryView.showImages(images);
                    }
                }, throwable -> {
                    Timber.d("subscribe failure: %s", throwable);
                    galleryView.showError();
                });
    }
}
