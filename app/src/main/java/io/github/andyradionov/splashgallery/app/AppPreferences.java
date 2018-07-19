package io.github.andyradionov.splashgallery.app;

import io.github.andyradionov.splashgallery.BuildConfig;

/**
 * @author Andrey Radionov
 */

public class AppPreferences {
    public static final String API_KEY = BuildConfig.ApiKey;;
    public static final String BASE_URL = "https://api.unsplash.com/";
    public static final String MAIN_GALLERY = "curated";
    public static final String IMG_FOLDER_NAME = "SplashGallery";
    public static final int PAGE_SIZE = 30;
    public static final int MAX_PAGE_NUMBER = 5;

    private AppPreferences() {
    }
}
