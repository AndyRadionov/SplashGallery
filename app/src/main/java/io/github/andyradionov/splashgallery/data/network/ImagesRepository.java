package io.github.andyradionov.splashgallery.data.network;

import android.support.annotation.NonNull;

import io.github.andyradionov.splashgallery.BuildConfig;
import io.github.andyradionov.splashgallery.data.entities.SearchResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class ImagesRepository {

    private ImagesApi mImagesApi;

    public ImagesRepository(ImagesApi imagesApi) {
        Timber.d("Constructor call");
        this.mImagesApi = imagesApi;
    }

    /**
     * Searching images and caching previous result
     *  @param query User search query input or default gallery
     * @param page Page that needs to be load
     */
    public final Observable<SearchResult> searchImages(@NonNull final String query, final int page) {

        Timber.d("searchImages(query = %s, page = %d)", query, page);

        return mImagesApi
                .searchImages(BuildConfig.PAGE_SIZE, query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
