package io.github.andyradionov.splashgallery;

import android.support.annotation.NonNull;

import java.util.List;

import io.github.andyradionov.splashgallery.model.Image;

/**
 * @author Andrey Radionov
 */

public interface GalleryView {

    void showImages(@NonNull List<Image> images);

    void showError();

    void disableLoading();
}
