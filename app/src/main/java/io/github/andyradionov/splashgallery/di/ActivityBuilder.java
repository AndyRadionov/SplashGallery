package io.github.andyradionov.splashgallery.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryActivity;

/**
 * @author Andrey Radionov
 */
@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = NetModule.class)
    abstract GalleryActivity contributeGalleryActivityInjector();
}
