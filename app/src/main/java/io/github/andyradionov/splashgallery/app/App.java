package io.github.andyradionov.splashgallery.app;

import android.app.Application;

import com.google.gson.Gson;

import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.app.di.AppComponent;
import io.github.andyradionov.splashgallery.app.di.ContextModule;
import io.github.andyradionov.splashgallery.app.di.DaggerAppComponent;
import io.github.andyradionov.splashgallery.model.network.ImagesApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
                .contextModule(new ContextModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
