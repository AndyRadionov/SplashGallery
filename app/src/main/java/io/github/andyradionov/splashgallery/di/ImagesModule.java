package io.github.andyradionov.splashgallery.di;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.andyradionov.splashgallery.utils.ImageSaverUtils;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */
@Module
public class ImagesModule {

    @Provides
    @NonNull
    @Singleton
    public ImageSaverUtils provideImagesRepository(Application app) {
        Timber.d("provideImagesRepository");

        return new ImageSaverUtils(app);
    }
}
