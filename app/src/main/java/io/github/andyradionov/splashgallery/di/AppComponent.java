package io.github.andyradionov.splashgallery.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryActivity;
import okhttp3.OkHttpClient;

/**
 * @author Andrey Radionov
 */

@Singleton
@Component(modules = {ActivityBuilder.class, NetModule.class})
public interface AppComponent {

    OkHttpClient.Builder getOkHttpClientBuilder();

    void inject(App app);

    void inject(GalleryActivity galleryActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application app);

        AppComponent build();
    }
}
