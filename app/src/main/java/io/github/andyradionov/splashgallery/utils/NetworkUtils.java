package io.github.andyradionov.splashgallery.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * Utility class for Network operations
 *
 * @author Andrey Radionov
 */

public class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * Check if Internet is Available on device
     *
     * @param context of application
     * @return internet status
     */
    public static boolean isInternetAvailable(@NonNull final Context context) {
        Timber.d("isInternetAvailable");
        final ConnectivityManager mConMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return mConMgr != null
                && mConMgr.getActiveNetworkInfo() != null
                && mConMgr.getActiveNetworkInfo().isAvailable()
                && mConMgr.getActiveNetworkInfo().isConnected();
    }
}
