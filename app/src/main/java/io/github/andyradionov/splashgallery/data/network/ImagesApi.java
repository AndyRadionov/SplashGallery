package io.github.andyradionov.splashgallery.data.network;


import android.support.annotation.NonNull;

import io.github.andyradionov.splashgallery.data.entities.SearchResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit Api class
 *
 * @author Andrey Radionov
 */

public interface ImagesApi {

    @GET("search/photos")
    Observable<SearchResult> searchImages(@Query("per_page") int pageSize,
                                          @NonNull @Query("query") String query,
                                          @Query("page") int page);
}
