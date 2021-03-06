package com.example.rapha.sundaybaking.ui.common;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Generic view holder for RecyclerView adapters
 */
public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final T binding;

    public BindingViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
