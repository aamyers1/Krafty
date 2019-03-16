package com.team6.krafty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InventoryFragment extends Fragment {
    private static cardAdapter ca;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materials, container, false);

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Create a floating action button to add materials, set listener for click

    }




}
