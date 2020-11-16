package com.example.recycle.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.recycle.visual.Tab1;
import com.example.recycle.visual.Tab2;
import com.example.recycle.visual.Tab3;

public class MiPagerAdapter extends FragmentStateAdapter {
    public MiPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Tab1();
            case 1:
                return new Tab2();
            case 2:
                return new Tab3();
        }
        return null;
    }
}