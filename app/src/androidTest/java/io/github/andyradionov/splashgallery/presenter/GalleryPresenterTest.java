package io.github.andyradionov.splashgallery.presenter;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.andyradionov.splashgallery.ui.gallery.GalleryView;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryView$$State;
import io.github.andyradionov.splashgallery.utils.TestUtils;

import static io.github.andyradionov.splashgallery.utils.TestUtils.getWrongSearchRequest;
import static io.github.andyradionov.splashgallery.utils.TestUtils.waitForSeconds;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class GalleryPresenterTest {

    @Mock
    GalleryView galleryView;

    @Mock
    GalleryView$$State galleryViewState;

    private GalleryPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new GalleryPresenter();
        presenter.attachView(galleryView);
        presenter.setViewState(galleryViewState);
    }

    @Test
    public void testSearchImages() throws Exception {
        presenter.searchImages(TestUtils.getCorrectSearchRequest(), 1);
        waitForSeconds(2000);
        verify(galleryViewState, times(1)).showImages(anyList());
    }

    @Test
    public void testSearchImagesFail() throws Exception {
        presenter.searchImages(getWrongSearchRequest(), 1);
        waitForSeconds(2000);
        verify(galleryViewState, times(1)).showError();
    }
}