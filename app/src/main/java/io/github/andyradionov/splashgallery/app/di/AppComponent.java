package io.github.andyradionov.splashgallery.app.di;

import javax.inject.Singleton;

import dagger.Component;
import io.github.andyradionov.splashgallery.model.network.ImagesStore;
import io.github.andyradionov.splashgallery.presenter.GalleryPresenter;

/**
 * @author Andrey Radionov
 */

@Singleton
@Component(modules = {ContextModule.class, NetModule.class})
public interface AppComponent {

    void inject(GalleryPresenter presenter);

    void inject(ImagesStore imagesStore);
}
