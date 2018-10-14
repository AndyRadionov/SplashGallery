package io.github.andyradionov.splashgallery.ui.details;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * @author Andrey Radionov
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface ImageDetailsView extends MvpView {

    @StateStrategyType(SkipStrategy.class)
    void showSaveSuccess(String message);

    @StateStrategyType(SkipStrategy.class)
    void showSaveError(String message);

    void showSetWallpaperDialog();

    void hideSetWallpaperDialog();
}
