package io.github.andyradionov.splashgallery.app.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrey Radionov
 */


@Module
public class ContextModule {
    private static final String TAG = ContextModule.class.getSimpleName();
    private Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return mContext;
    }

}

