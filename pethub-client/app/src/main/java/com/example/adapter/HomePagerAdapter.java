package com.example.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ui.fragment.ExpFragment;
import com.example.ui.fragment.MissingFragment;
import com.example.ui.fragment.PostsFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;
    public HomePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ExpFragment();
            case 1:
                return new PostsFragment();
            case 2:
                return new MissingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
