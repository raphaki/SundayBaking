package com.example.rapha.sundaybaking.ui.details.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rapha.sundaybaking.R;
import com.example.rapha.sundaybaking.data.models.InstructionStep;
import com.example.rapha.sundaybaking.databinding.ItemStepDescriptionBinding;

import java.util.List;

public class InstructionsPagerAdapter extends PagerAdapter {

    private List<InstructionStep> steps;

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ItemStepDescriptionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.item_step_description, container, false);
        binding.setStep(steps.get(position));
        binding.executePendingBindings();
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setStepList(List<InstructionStep> newSteps) {
        steps = newSteps;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return steps == null ? 0 : steps.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
