package io.github.andyradionov.splashgallery.data.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * DTO class for search result. It wraps List of {@link Image} and total pages number
 *
 * @author Andrey Radionov
 */

public class SearchResult {
    private List<Image> results;
    @SerializedName("total_pages") private int totalPages;

    public List<Image> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "results=" + results +
                ", totalPages=" + totalPages +
                '}';
    }
}
