package io.github.andyradionov.splashgallery.presenter;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.github.andyradionov.splashgallery.BuildConfig;
import io.github.andyradionov.splashgallery.data.network.ImagesRepository;
import io.github.andyradionov.splashgallery.ui.gallery.GalleryView;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */
@InjectViewState
public class GalleryPresenter extends MvpPresenter<GalleryView> {

    private ImagesRepository imagesRepository;
    private Disposable mSubscription;

    private int mMaxPage;

    @Inject
    public GalleryPresenter(ImagesRepository imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    /**
     * Searching images and caching previous result
     *
     * @param query User search query input or default gallery
     * @param page  Page that needs to be load
     */
    public final void searchImages(@NonNull final String query, final int page) {
        if (isMaxPage(page)) {
            getViewState().disableLoading();
            return;
        }

        unsubscribe();

        mSubscription = imagesRepository.searchImages(query, page)
                .doOnError(throwable -> {
                    Timber.d(throwable, "doOnError");
                    getViewState().showError();
                })
                .map(getSearchResultDto -> {
                    if (mMaxPage == 0) setMaxPage(getSearchResultDto.getTotalPages());
                    return getSearchResultDto.getResults();
                })
                .subscribe(images -> {
                    Timber.d("subscribe result: %s", images);
                    if (images.isEmpty()) {
                        getViewState().showError();
                    } else {
                        getViewState().showImages(images);
                    }
                }, throwable -> {
                    Timber.d(throwable, "subscribe failure");
                    getViewState().showError();
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    private void unsubscribe() {
        if (mSubscription != null && !mSubscription.isDisposed()) {
            mSubscription.dispose();
            mSubscription = null;
        }
    }

    private boolean isMaxPage(final int page) {
        return (mMaxPage != 0 && page > mMaxPage);
    }

    private void setMaxPage(final int totalPagesInRequest) {
        mMaxPage = totalPagesInRequest >= BuildConfig.MAX_PAGE_NUMBER ?
                BuildConfig.MAX_PAGE_NUMBER : totalPagesInRequest;
    }
}
