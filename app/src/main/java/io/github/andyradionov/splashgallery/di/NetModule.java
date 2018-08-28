package io.github.andyradionov.splashgallery.di;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.andyradionov.splashgallery.BuildConfig;
import io.github.andyradionov.splashgallery.model.network.ImagesApi;
import io.github.andyradionov.splashgallery.model.network.ImagesRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Andrey Radionov
 */
@Module
public class NetModule {
    private static final String TAG = NetModule.class.getSimpleName();

    @Provides
    @NonNull
    @Singleton
    public ImagesRepository provideImagesRepository(ImagesApi imagesApi) {
        Log.d(TAG, "provideImagesRepository");

        return new ImagesRepository(imagesApi);
    }

    @Provides
    @NonNull
    @Singleton
    public ImagesApi provideImagesApi(OkHttpClient httpClient) {
        Log.d(TAG, "provideImagesApi");

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
                .create(ImagesApi.class);
    }

    @Provides
    @NonNull
    @Singleton
    public OkHttpClient provideOkHttp() {
        Log.d(TAG, "provideOkHttp");
        String apiKey = "Client-ID " + BuildConfig.API_KEY;

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Accept-Version", "v1")
                            .header("Authorization", apiKey)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }).build();
    }

}
