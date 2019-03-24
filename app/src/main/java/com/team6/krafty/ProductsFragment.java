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


public class ProductsFragment extends Fragment {
    private static cardAdapter ca;
    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materials, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Inventory inv = Inventory.getInstance();
        ca = new cardAdapter(inv.getProductImages(), inv.getProductCaptions());
        FloatingActionButton fab = view.findViewById(R.id.addInventory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateProductActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView rv = view.findViewById(R.id.matRecycler);
        rv.setAdapter(ca);
        rv.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        ca.setListener(new cardAdapter.Listener() {
            @Override
            public void onClick(int position) {
                //Start a new Modify Material Activity, pass the position for processing
                Intent intent = new Intent(getView().getContext(),ViewProductActivity.class );
                intent.putExtra("EXTRA_ID", position);
                startActivity(intent);
            }
        });
    }

    public static void nullifyAdapter(){
        Inventory inv = Inventory.getInstance();
        ca.updateData(inv.getProductImages(), inv.getProductCaptions());
        ca.notifyDataSetChanged();
    }




}
