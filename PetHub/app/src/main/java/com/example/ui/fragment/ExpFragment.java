package com.example.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.R;


public class ExpFragment extends Fragment {

    private View mView;
    public ExpFragment() {
        // Required empty public constructor
    }

    public static ExpFragment newInstance(String param1, String param2) {
        ExpFragment fragment = new ExpFragment();

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

        mView = inflater.inflate(R.layout.fragment_exp, container, false);
        return mView;
    }
}