package io.github.andyradionov.splashgallery.presenter;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.github.andyradionov.splashgallery.ui.details.ImageDetailsView;
import io.github.andyradionov.splashgallery.ui.details.ImageSaveCallback;
import io.github.andyradionov.splashgallery.utils.ImageSaverUtils;

/**
 * @author Andrey Radionov
 */
@InjectViewState
public class ImageDetailsPresenter extends MvpPresenter<ImageDetailsView>
        implements ImageSaveCallback {

    private ImageSaverUtils mImageSaverUtils;

    @Inject
    public ImageDetailsPresenter(ImageSaverUtils imageSaverUtils) {
        this.mImageSaverUtils = imageSaverUtils;
    }

    public void saveImage(@NonNull String imageUrl) {
        mImageSaverUtils.saveImage(this, imageUrl);
    }

    public void setWallpaper(@NonNull String imageUrl) {
        mImageSaverUtils.setWallpaper(this, imageUrl);
    }

    @Override
    public void showSuccess(String message) {
        getViewState().showSaveSuccess(message);
    }

    @Override
    public void showError(String message) {
        getViewState().showSaveError(message);
    }
}
