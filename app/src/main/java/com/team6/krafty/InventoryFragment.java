package com.team6.krafty;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class InventoryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addInventory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick(view);
            }
        });
        MaterialController mc = new MaterialController();
        mc.getMaterials(getContext());
        Object[] userMats = Material.allMats.toArray();
        ListView lv = getView().findViewById(R.id.materialsList);
        ArrayAdapter<Material> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, Material.allMats);
        lv.setAdapter(adapter);
    }

    public void onButtonClick(View view){
        Intent intent = new Intent(getActivity(), CreateMaterialActivity.class);
        startActivity(intent);
    }

}
