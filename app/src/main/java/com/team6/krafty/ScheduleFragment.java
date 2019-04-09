package com.team6.krafty;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        ScheduleController.getSchedule(view.getContext());
        RecyclerView rv = view.findViewById(R.id.schedRecycler);
        ScheduleCardAdapter sca = new ScheduleCardAdapter(view.getContext(), this);
        rv.setAdapter(sca);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    //The floating action button listener method. Starts Create Task Activity
    public void onButtonClick(){
        Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
        startActivity(intent);
    }


}
