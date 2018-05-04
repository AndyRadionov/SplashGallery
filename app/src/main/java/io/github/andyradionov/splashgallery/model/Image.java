package io.github.andyradionov.splashgallery.model;

/**
 * Model class contains Image id, background color and different image size urls
 *
 * @author Andrey Radionov
 */

public class Image {
    private String id;
    private Urls urls;

    private static class Urls {
        private String small;
        private String regular;
    }

    public String getId() {
        return id;
    }

    public String getSmallImage() {
        return urls != null ? urls.small : "";
    }

    public String getMediumImage() {
        return urls != null ? urls.regular : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        return id.equals(image.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
