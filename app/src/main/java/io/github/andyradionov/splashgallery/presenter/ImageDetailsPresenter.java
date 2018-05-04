package io.github.andyradionov.splashgallery.presenter;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import io.github.andyradionov.splashgallery.ui.details.ImageDetailsView;
import io.github.andyradionov.splashgallery.ui.details.ImageSaveCallback;
import io.github.andyradionov.splashgallery.utils.ImageSaverUtils;

/**
 * @author Andrey Radionov
 */
@InjectViewState
public class ImageDetailsPresenter extends MvpPresenter<ImageDetailsView>
        implements ImageSaveCallback {

    public void saveImage(@NonNull String imageUrl) {
        ImageSaverUtils.saveImage(this, imageUrl);
    }

    @Override
    public void showSuccess() {
        getViewState().showSaveSuccess();
    }

    @Override
    public void showError() {
        getViewState().showSaveError();
    }
}
