package io.github.andyradionov.splashgallery.ui.details;

/**
 * @author Andrey Radionov
 */

public interface ImageSaveCallback {
    void showSuccess(String message);

    void showError(String message);
}
