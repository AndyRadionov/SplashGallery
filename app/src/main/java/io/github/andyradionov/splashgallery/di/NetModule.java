package io.github.andyradionov.splashgallery.di;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.andyradionov.splashgallery.BuildConfig;
import io.github.andyradionov.splashgallery.app.TLSSocketFactory;
import io.github.andyradionov.splashgallery.data.network.ImagesApi;
import io.github.andyradionov.splashgallery.data.network.ImagesRepository;
import io.github.andyradionov.splashgallery.utils.NetworkUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */
@Module
public class NetModule {

    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    private static final String VARY_HEADER = "Vary";

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
    public OkHttpClient provideOkHttp(Application app, OkHttpClient.Builder clientBuilder) {
        Timber.d("provideOkHttp");

        return clientBuilder
                .addInterceptor(initApiKeyInterceptor())
                .addInterceptor(initOfflineCacheInterceptor(app))
                .addNetworkInterceptor(initCacheInterceptor())
                .cache(initCache(app))
                .build();
    }

    private Interceptor initApiKeyInterceptor() {
        Timber.d("provideOkHttp");
        String apiKey = "Client-ID " + BuildConfig.API_KEY;

        return chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Accept-Version", "v1")
                    .header("Authorization", apiKey)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }

    private Cache initCache(Application app) {
        Cache cache = null;
        try {
            cache = new Cache(new File(app.getCacheDir(), "http-cache"),
                    (10L * 1024L * 1024L)); // 10 MB
        } catch (Exception e) {
            Timber.e("Could not create Cache!");
        }

        return cache;
    }

    private Interceptor initCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            // re-write response header to force use of cache
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(10, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .removeHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER)
                    .removeHeader(VARY_HEADER)
                    .removeHeader(CACHE_CONTROL_HEADER)
                    .header(CACHE_CONTROL_HEADER, cacheControl.toString())
                    .build();
        };
    }

    private Interceptor initOfflineCacheInterceptor(Application app) {

        return chain -> {
            Request request = chain.request();

            if (!NetworkUtils.isInternetAvailable(app)) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }
}
