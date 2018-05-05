package io.github.andyradionov.splashgallery.utils;

/**
 * @author Andrey Radionov
 */

public class TestUtils {

    private static final String GOOGLE_LOGO_URL =
            "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";

    private TestUtils() {
    }

    public static String getCorrectImageUlr() {
        return GOOGLE_LOGO_URL;
    }

    public static String getCorrectSearchRequest() {
        return "nature";
    }

    public static String getWrongSearchRequest() {
        return "qwqwqwqwqwq";
    }

    public static void waitForSeconds(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
