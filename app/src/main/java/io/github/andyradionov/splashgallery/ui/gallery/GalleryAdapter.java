package io.github.andyradionov.splashgallery.ui.gallery;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.data.entities.Image;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private final List<Image> mImages;
    private final OnGalleryImageClickListener mClickListener;

    public interface OnGalleryImageClickListener {
        void onClick(@NonNull String imageUrl, @NonNull String imageId, @NonNull ImageView imageView);
    }

    public GalleryAdapter(@NonNull final OnGalleryImageClickListener clickListener) {
        mClickListener = clickListener;
        mImages = new ArrayList<>();
    }

    public void clearData() {
        mImages.clear();
        notifyDataSetChanged();
    }

    public void updateData(@NonNull final List<Image> images) {
        mImages.addAll(images);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_card, parent, false);
        return new GalleryViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return mImages.isEmpty() ? 0 : mImages.size();
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final CardView mGalleryCard;
        private final ImageView mGalleryImage;

        private GalleryViewHolder(CardView itemView) {
            super(itemView);
            mGalleryCard = itemView;
            mGalleryImage = mGalleryCard.findViewById(R.id.iv_gallery_image);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            final Image image = mImages.get(position);


            Picasso.get()
                    .load(image.getSmallImage())
                    .placeholder(R.drawable.loading_indicator)
                    .fit()
                    .centerCrop()
                    .into(mGalleryImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Timber.d(e.getLocalizedMessage());
                            Picasso.get().load(R.drawable.error_placeholder)
                                    .fit()
                                    .centerCrop()
                                    .into(mGalleryImage);
                        }
                    });
            ViewCompat.setTransitionName(mGalleryImage, image.getId());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final Image image = mImages.get(position);
            mClickListener.onClick(image.getMediumImage(), image.getId(), mGalleryImage);
        }
    }
}
