package io.github.andyradionov.splashgallery.ui.details;

import com.arellomobile.mvp.MvpView;

/**
 * @author Andrey Radionov
 */

public interface ImageDetailsView extends MvpView {
    void showSaveSuccess();

    void showSaveError();
}
