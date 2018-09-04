package io.github.andyradionov.splashgallery.app;

import android.app.Activity;
import android.app.Application;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.github.andyradionov.splashgallery.di.AppComponent;
import io.github.andyradionov.splashgallery.di.DaggerAppComponent;
import io.github.andyradionov.splashgallery.di.NetModule;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class App extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        AppComponent appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build();
        appComponent.inject(this);

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(appComponent.getOkHttpClientBuilder().build()))
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}
