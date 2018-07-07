package io.github.andyradionov.splashgallery.app.di;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.app.App;
import io.github.andyradionov.splashgallery.app.AppPreferences;
import io.github.andyradionov.splashgallery.model.network.ImagesApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Andrey Radionov
 */
@Module(includes = ContextModule.class)
public class NetModule {
    private static final String TAG = ContextModule.class.getSimpleName();

    @Provides
    @NonNull
    @Singleton
    public ImagesApi provideImagesApi(OkHttpClient httpClient) {
        Log.d(TAG, "provideImagesApi");

        return new Retrofit.Builder()
                .baseUrl(AppPreferences.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
                .create(ImagesApi.class);
    }

    @Provides
    @NonNull
    @Singleton
    public OkHttpClient provideOkHttp(Context context) {
        Log.d(TAG, "provideOkHttp");
        String apiKey = "Client-ID " + context.getString(R.string.client_id);

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
