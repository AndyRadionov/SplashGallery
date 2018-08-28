package io.github.andyradionov.splashgallery.presenter;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.github.andyradionov.splashgallery.model.dto.Image;
import io.github.andyradionov.splashgallery.model.network.ImagesCallback;
import io.github.andyradionov.splashgallery.model.network.ImagesRepository;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryView;

/**
 * @author Andrey Radionov
 */
@InjectViewState
public class GalleryPresenter extends MvpPresenter<GalleryView> implements ImagesCallback {

    private ImagesRepository imagesRepository;

    @Inject
    public GalleryPresenter(ImagesRepository imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    /**
     * Searching images and caching previous result
     *
     * @param query User search query input or default gallery
     * @param page Page that needs to be load
     */
    public final void searchImages(@NonNull final String query, final int page) {
        imagesRepository.searchImages(query, page, this);
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
