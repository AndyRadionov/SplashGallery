package io.github.andyradionov.splashgallery.ui.details;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * @author Andrey Radionov
 */
@StateStrategyType(SkipStrategy.class)
public interface ImageDetailsView extends MvpView {
    void showSaveSuccess();

    void showSaveError();
}
