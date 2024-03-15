package com.apptmyz.assessment.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apptmyz.assessment.R;

public class LearnFragment extends Fragment {

    RelativeLayout noMaterial_rl;
    int materials_count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_learn, container, false);

        noMaterial_rl = view.findViewById(R.id.noMaterial_rl);
        if(materials_count == 0){
            noMaterial_rl.setVisibility(View.VISIBLE);
        }

        return  view;
    }
}