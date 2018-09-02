package io.github.andyradionov.splashgallery.di;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.andyradionov.splashgallery.BuildConfig;
import io.github.andyradionov.splashgallery.app.TLSSocketFactory;
import io.github.andyradionov.splashgallery.model.network.ImagesApi;
import io.github.andyradionov.splashgallery.model.network.ImagesRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */
@Module
public class NetModule {

    @Provides
    @NonNull
    @Singleton
    public ImagesRepository provideImagesRepository(ImagesApi imagesApi) {
        Timber.d("provideImagesRepository");

        return new ImagesRepository(imagesApi);
    }

    @Provides
    @NonNull
    @Singleton
    public ImagesApi provideImagesApi(OkHttpClient httpClient) {
        Timber.d("provideImagesApi");

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
    public OkHttpClient.Builder provideOkHttpSSLBuilder() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        try {
            clientBuilder = new OkHttpClient.Builder()
                    .sslSocketFactory(new TLSSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Timber.d("provideOkHttp: %s", e.toString());
        }
        return clientBuilder;
    }

    @Provides
    @NonNull
    @Singleton
    public OkHttpClient provideOkHttp(OkHttpClient.Builder clientBuilder) {
        Timber.d("provideOkHttp");
        String apiKey = "Client-ID " + BuildConfig.API_KEY;

        return clientBuilder
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
