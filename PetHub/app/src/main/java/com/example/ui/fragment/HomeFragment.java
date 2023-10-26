package com.example.ui.fragment;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.R;
import com.example.adapter.HomePagerAdapter;
import com.example.utils.SystemUIUtil;


public class HomeFragment extends Fragment {

    private View mView;

    private TextView mTab1TextView;
    private TextView mTab2TextView;
    private TextView mTab3TextView;
    private View mIndicator;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        SystemUIUtil.setPaddingTop(mView.findViewById(R.id.homeRootFragment));

        initView();

        ViewPager2 viewPager2 = mView.findViewById(R.id.homeViewPager2);
        viewPager2.setAdapter(new HomePagerAdapter(this));

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                updateIndicatorPosition(position, positionOffset);

                if (position == 0) {
                    mTab1TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_selected));
                    mTab2TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_unselected));
                    mTab3TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_unselected));
                } else if (position == 1) {
                        mTab1TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_unselected));
                        mTab2TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_selected));
                        mTab3TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_unselected));
                } else if (position == 2) {
                    mTab1TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_unselected));
                    mTab2TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_unselected));
                    mTab3TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_button_selected));
                }
            }
        });


        return mView;
    }

    private void updateIndicatorPosition(int position, float positionOffset) {
        // 计算百分比位置
        float percentagePos = (position + positionOffset) / (3 - 1);

        // 增加基础偏移量和缩放因子
        float baseOffset = 0.1f;
        float scaleFactor = 0.8f;
        percentagePos = baseOffset + percentagePos * scaleFactor;

        if(mIndicator.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mIndicator.getLayoutParams();
            // 设置水平偏移
            params.horizontalBias = percentagePos;
            mIndicator.setLayoutParams(params);
        }

    }

    private void initView() {
        mTab1TextView = mView.findViewById(R.id.homeTab1TextView);
        mTab2TextView = mView.findViewById(R.id.homeTab2TextView);
        mTab3TextView = mView.findViewById(R.id.homeTab3TextView);
        mIndicator = mView.findViewById(R.id.homeIndicator);
    }
}