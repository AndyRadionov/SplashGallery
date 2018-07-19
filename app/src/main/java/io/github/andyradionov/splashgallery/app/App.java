package io.github.andyradionov.splashgallery.app;

import android.app.Application;

import io.github.andyradionov.splashgallery.app.di.AppComponent;
import io.github.andyradionov.splashgallery.app.di.DaggerAppComponent;
import io.github.andyradionov.splashgallery.app.di.NetModule;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class App extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        sAppComponent = DaggerAppComponent
                .builder()
                .netModule(new NetModule())
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
