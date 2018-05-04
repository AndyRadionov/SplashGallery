package io.github.andyradionov.splashgallery.presenter;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.andyradionov.splashgallery.ui.details.ImageDetailsView;
import io.github.andyradionov.splashgallery.ui.details.ImageDetailsView$$State;
import io.github.andyradionov.splashgallery.utils.TestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class ImageDetailsPresenterTest {

    @Mock
    ImageDetailsView imageDetailsView;

    @Mock
    ImageDetailsView$$State imageDetailsViewState;

    private ImageDetailsPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ImageDetailsPresenter();
        presenter.attachView(imageDetailsView);
        presenter.setViewState(imageDetailsViewState);
    }

    @Test
    public void testSaveImage() throws Exception {
        presenter.saveImage(TestUtils.getCorrectImageUlr());
        TestUtils.waitForSeconds(1000);
        verify(imageDetailsViewState, times(1)).showSaveSuccess();
    }

    @Test
    public void testSaveImageFail() throws Exception {
        presenter.saveImage(TestUtils.getWrongImageUlr());
        TestUtils.waitForSeconds(1000);
        verify(imageDetailsViewState, times(1)).showSaveError();
    }

}