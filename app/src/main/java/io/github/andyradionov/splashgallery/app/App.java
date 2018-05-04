package io.github.andyradionov.splashgallery.app;

import android.app.Application;

import com.google.gson.Gson;

import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.network.ImagesApi;
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

    private static final String BASE_URL = "https://api.unsplash.com/";
    public static final String MAIN_GALLERY = "curated";
    public static final String IMG_FOLDER_NAME = "/SplashGallery";
    public static final int PAGE_SIZE = 30;
    public static final int MAX_PAGE_NUMBER = 5;

    private static String sApiKey;
    private static ImagesApi sImagesApi;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        sApiKey = "Client-ID " + getString(R.string.client_id);
        sImagesApi = createApi();
    }

    /**
     * Provides Retrofit Api for searching images
     * @return ImagesApi
     */
    public static ImagesApi getImagesApi() {
        return sImagesApi;
    }

    private static ImagesApi createApi() {
        Timber.d("createApi");

        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Accept-Version", "v1")
                            .header("Authorization", sApiKey)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }).build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
                .create(ImagesApi.class);
    }
}
