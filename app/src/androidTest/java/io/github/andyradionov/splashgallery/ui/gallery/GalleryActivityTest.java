package io.github.andyradionov.splashgallery.ui.gallery;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.TextView;

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
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

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

        onView(ViewMatchers.withResourceName("search_src_text"))
                .perform(typeText("test"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.rv_gallery_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickSearch_ClickHome() {

        onView(withId(R.id.action_search)).perform(click());

        onView(ViewMatchers.withResourceName("search_src_text"))
                .perform(typeText("test"), pressKey(KeyEvent.KEYCODE_ENTER));

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
    public void testClockAbout_OpenAboutActivity() {
        //todo change to Title of About text
        String about = "About";

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(about)).perform(click());

        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(about)));
    }

    private void waitForSeconds(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}