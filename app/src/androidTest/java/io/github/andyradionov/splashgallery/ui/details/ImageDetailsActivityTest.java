package io.github.andyradionov.splashgallery.ui.details;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.presenter.ImageDetailsPresenter;
import io.github.andyradionov.splashgallery.utils.ImageSaverUtils;
import io.github.andyradionov.splashgallery.utils.TestUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class ImageDetailsActivityTest {

    @Rule
    public final IntentsTestRule<ImageDetailsActivity> mActivityTestRule =
            new IntentsTestRule<ImageDetailsActivity>(ImageDetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent(InstrumentationRegistry.getTargetContext(),
                            ImageDetailsActivity.class);
                    intent.putExtra(ImageDetailsActivity.IMAGE_URL_EXTRA,
                            TestUtils.getCorrectImageUlr());
                    return intent;
                }
            };

    @Test
    public void testClickShare_OpenChooser() {
        onView(withId(R.id.action_share)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_SEND),
                hasExtra(Intent.EXTRA_TEXT, TestUtils.getCorrectImageUlr())));
    }

    @Test
    public void testClickSave_Success() {

        MockedImageDetailsPresenter presenter = new MockedImageDetailsPresenter(
                new ImageSaverUtils(InstrumentationRegistry.getContext()));
        mActivityTestRule.getActivity().mImageDetailsPresenter = presenter;

        onView(withId(R.id.action_save)).perform(click());
        assertTrue(presenter.success);
    }

    private static class MockedImageDetailsPresenter extends ImageDetailsPresenter {
        private boolean success;

        public MockedImageDetailsPresenter(ImageSaverUtils imageSaverUtils) {
            super(imageSaverUtils);
        }

        @Override
        public void showSuccess(String message) {
            success = true;
        }
    }
}