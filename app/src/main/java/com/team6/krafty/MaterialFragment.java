package com.team6.krafty;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaterialFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_material, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addInventory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
        Material[] allMaterials = MaterialController.getMaterials(this.getContext());
        ListView lv = getView().findViewById(R.id.materialsList);
        ArrayAdapter<Material> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, allMaterials);
        lv.setAdapter(adapter);
    }

    public void onButtonClick(){
        Intent intent = new Intent(getActivity(), CreateMaterialActivity.class);
        startActivity(intent);
    }


}
