package io.github.andyradionov.splashgallery.network;


import android.support.annotation.NonNull;

import io.github.andyradionov.splashgallery.app.App;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit Api class
 *
 * @author Andrey Radionov
 */

public interface ImagesApi {

    @GET("search/photos?per_page=" + App.PAGE_SIZE)
    Observable<SearchResultDto> searchImages(@NonNull @Query("query") String query,
                                             @Query("page") int page);
}
