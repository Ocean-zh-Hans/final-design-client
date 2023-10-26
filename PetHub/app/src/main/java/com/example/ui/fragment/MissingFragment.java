package com.example.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.R;


public class MissingFragment extends Fragment {

    private View mView;

    public MissingFragment() {
        // Required empty public constructor
    }

    public static MissingFragment newInstance(String param1, String param2) {
        MissingFragment fragment = new MissingFragment();
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

        mView = inflater.inflate(R.layout.fragment_missing, container, false);
        return mView;
    }
}