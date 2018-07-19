package io.github.andyradionov.splashgallery.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.app.AppPreferences;
import io.github.andyradionov.splashgallery.model.dto.Image;
import io.github.andyradionov.splashgallery.model.network.ImagesApi;
import io.github.andyradionov.splashgallery.model.network.ImagesCallback;
import io.github.andyradionov.splashgallery.model.network.ImagesStore;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */
@InjectViewState
public class GalleryPresenter extends MvpPresenter<GalleryView> implements ImagesCallback {

    @Inject ImagesStore imagesStore;

    public GalleryPresenter() {
        App.getAppComponent().inject(this);
    }

    /**
     * Searching images and caching previous result
     *
     * @param query User search query input or default gallery
     * @param page Page that needs to be load
     */
    public final void searchImages(@NonNull final String query, final int page) {
        imagesStore.searchImages(query, page, this);
    }

    @Override
    public void onErrorLoading() {
        getViewState().showError();
    }

    @Override
    public void onSuccessLoading(List<Image> images) {
        getViewState().showImages(images);
    }

    @Override
    public void disableLoading() {
        getViewState().disableLoading();
    }
}
