package io.github.andyradionov.splashgallery.app;

import android.app.Application;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.github.andyradionov.splashgallery.di.AppComponent;
import io.github.andyradionov.splashgallery.di.DaggerAppComponent;
import io.github.andyradionov.splashgallery.di.NetModule;
import okhttp3.OkHttpClient;
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

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(sAppComponent.getOkHttpClientBuilder().build()))
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
