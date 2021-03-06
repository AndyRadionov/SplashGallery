package io.github.andyradionov.splashgallery.ui.gallery;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import io.github.andyradionov.splashgallery.data.entities.Image;

/**
 * @author Andrey Radionov
 */

public interface GalleryView extends MvpView {

    void showImages(@NonNull List<Image> images);

    void showError();

    void disableLoading();
}
