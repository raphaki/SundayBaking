package com.example.rapha.sundaybaking.ui;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.example.rapha.sundaybaking.util.GlideApp;

public class BindingsAdapters {
    @BindingAdapter(value = {"imageUrl", "placeholder"})
    public static void loadImage(ImageView view, String imageUrl, Drawable placeholder) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .placeholder(placeholder)
                .into(view);
    }

    @BindingAdapter(value = {"dividersDrawable"})
    public static void addDivider(RecyclerView view, Drawable dividersDrawable) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), ((LinearLayoutManager) view.getLayoutManager()).getOrientation());
        dividerItemDecoration.setDrawable(dividersDrawable);
        view.addItemDecoration(dividerItemDecoration);
    }
}