package com.team6.krafty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        Schedule.getInstance().clearSchedule();
        ScheduleController.getSchedule(view.getContext());
        RecyclerView rv = view.findViewById(R.id.schedRecycler);
        ScheduleCardAdapter sca = new ScheduleCardAdapter(view.getContext());
        rv.setAdapter(sca);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
    }


}
