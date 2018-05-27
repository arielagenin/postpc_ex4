package com.arielagenin.postpc_ex4;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private String[] imageLinks;
    private RequestManager imageLoader;

    ImageAdapter(String[] imageLinks, RequestManager imageLoader) {
        this.imageLinks = imageLinks;
        this.imageLoader = imageLoader;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        imageLoader.load(imageLinks[position]).thumbnail(0.1f).into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return imageLinks.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageViewHolder(View itemView) {
            super(itemView);
        }

        private ImageView getImageView() {
            return (ImageView) itemView;
        }
    }
}
