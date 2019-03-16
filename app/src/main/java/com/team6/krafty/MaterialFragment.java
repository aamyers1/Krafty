package com.team6.krafty;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MaterialFragment extends Fragment {
    private static cardAdapter ca;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materials, container, false);

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Create a floating action button to add materials, set listener for click
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addInventory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
        RecyclerView rv = getView().findViewById(R.id.matRecycler);
        //create card adapter
        Inventory inv = Inventory.getInstance();
        ca = new cardAdapter(inv.getMaterialImages(), inv.getMaterialCaptions());
        //set recycler view to use the cardAdapter in a Grid Layout
        rv.setAdapter(ca);
        rv.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        //create click listener for each RecyclerView object
        ca.setListener(new cardAdapter.Listener() {
            @Override
            public void onClick(int position) {
                //Start a new Modify Material Activity, pass the position for processing
                Intent intent = new Intent(getView().getContext(),ModifyMaterialActivity.class );
                intent.putExtra("EXTRA_ID", position);
                startActivity(intent);
            }
        });

    }

    //The floating action button listener method. Starts Create Material Activity
    public void onButtonClick(){
        Intent intent = new Intent(getActivity(), CreateMaterialActivity.class);
        startActivity(intent);
    }

    public static void nullifyAdapter(){
        Inventory inv = Inventory.getInstance();
        ca.updateData(inv.getMaterialImages(), inv.getMaterialCaptions());
        ca.notifyDataSetChanged();
    }




}
