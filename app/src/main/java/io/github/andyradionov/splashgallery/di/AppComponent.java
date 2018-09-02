package io.github.andyradionov.splashgallery.di;

import javax.inject.Singleton;

import dagger.Component;
import io.github.andyradionov.splashgallery.model.network.ImagesRepository;
import io.github.andyradionov.splashgallery.presenter.GalleryPresenter;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryActivity;
import okhttp3.OkHttpClient;

/**
 * @author Andrey Radionov
 */

@Singleton
@Component(modules = {NetModule.class})
public interface AppComponent {

    OkHttpClient.Builder getOkHttpClientBuilder();

    void inject(GalleryActivity galleryActivity);
}
