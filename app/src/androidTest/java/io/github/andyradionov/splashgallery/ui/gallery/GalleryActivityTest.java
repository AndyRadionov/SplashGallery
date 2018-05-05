package io.github.andyradionov.splashgallery.ui.gallery;

import android.content.Context;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.andyradionov.splashgallery.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.github.andyradionov.splashgallery.utils.TestUtils.*;
import static io.github.andyradionov.splashgallery.utils.TestUtils.waitForSeconds;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class GalleryActivityTest {

    @Rule
    public ActivityTestRule<GalleryActivity> mActivityTestRule =
            new ActivityTestRule<>(GalleryActivity.class);

    @Test
    public void testClickRecyclerViewItem_OpenDetailsActivity() {
        waitForSeconds(1000);

        onView(withId(R.id.rv_gallery_container)).check(matches(isDisplayed()));

        int testIndex = 1;
        onView(withId(R.id.rv_gallery_container))
                .perform(RecyclerViewActions.actionOnItemAtPosition(testIndex, click()));

        onView(withId(R.id.iv_image_details)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickSearch_ContainerDisplayed() {
        onView(withId(R.id.action_search)).perform(click());

        onView(withResourceName("search_src_text"))
                .perform(typeText(getCorrectSearchRequest()), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.rv_gallery_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickSearch_ClickHome() {

        onView(withId(R.id.action_search)).perform(click());

        onView(withResourceName("search_src_text"))
                .perform(typeText(getCorrectSearchRequest()), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.action_home)).perform(click());

        onView(withId(R.id.rv_gallery_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testScrollContainer_LoadingMore() {
        int pageSize = 30;
        waitForSeconds(1000);

        onView(withId(R.id.rv_gallery_container)).check(matches(isDisplayed()));

        onView(withId(R.id.rv_gallery_container))
                .perform(RecyclerViewActions.scrollToPosition(pageSize));

        waitForSeconds(1000);

        onView(withId(R.id.rv_gallery_container))
                .perform(RecyclerViewActions.scrollToPosition(pageSize + 10));
    }

    @Test
    public void testClickAbout_OpenAboutActivity() {
        Context context = getInstrumentation().getTargetContext();
        String about = context.getString(R.string.about_title);

        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(about)).perform(click());

        onView(withId(R.id.toolbar_title))
                .check(matches(withText(about)));
    }
}