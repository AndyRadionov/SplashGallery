package io.github.andyradionov.splashgallery.model.network;

import java.util.List;

import io.github.andyradionov.splashgallery.model.dto.Image;

/**
 * @author Andrey Radionov
 */

public interface ImagesCallback {

    void showError();

    void disableLoading();

    void showImages(List<Image> images);
}
