package com.example.zhanghao.skylu.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhanghao.skylu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunListsFragment extends Fragment {


    public RunListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_lists, container, false);
    }

}
