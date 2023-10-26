package com.example.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.R;
import com.example.utils.SystemUIUtil;

public class MeFragment extends Fragment {

    private View mView;

    public MeFragment() {
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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }

        mView = inflater.inflate(R.layout.fragment_me, container, false);
        SystemUIUtil.setPaddingTop(mView.findViewById(R.id.meRootFrameLayout));


        return mView;
    }
}