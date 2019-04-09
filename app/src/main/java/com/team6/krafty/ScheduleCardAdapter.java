package com.team6.krafty;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleCardAdapter extends RecyclerView.Adapter<ScheduleCardAdapter.ViewHolder>{

    private ArrayList<String> dates;
    private Listener listener;
    Context thisContext;

    //create a generic listener interface for the adapter
    interface Listener {
        void onClick(int position);
    }

    //sets the listener for this adapter
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //constructor
    public ScheduleCardAdapter(Context context){
        thisContext = context;
        ArrayList<Schedulable> schedule = Schedule.getInstance().getSchedule();
        dates = new ArrayList<>();
        for(Schedulable i: schedule){
            if(!dates.contains(i.getDate())){
                dates.add(i.getDate());
            }
        }
    }

    //gets the number of items to be created
    @Override
    public int getItemCount(){
        return dates.size();
    }

    //sets the viewholder
    @Override
    public ScheduleCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType){
        LinearLayout cv = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_card, parent, false);
        return new ViewHolder(cv);
    }

    //when viewholder is bound
    @Override
    public void onBindViewHolder(ViewHolder holder , final int position) {
        LinearLayout ll = holder.ll;
        TextView tv = ll.findViewById(R.id.date);
        tv.setText(dates.get(position));


        //set the onClickListener for the cardview. The implementation of the RecyclerView will define a specific listener
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });

        RecyclerView rv = ll.findViewById(R.id.schedRecycler);
        ArrayList<Schedulable> scheduleDate = new ArrayList<>();
        for (Schedulable i : Schedule.getInstance().getSchedule()) {
            if (i.getDate().equals(dates.get(position))) {
                scheduleDate.add(i);
            }
        }
        SpecificScheduleCardAdapter ssca = new SpecificScheduleCardAdapter(scheduleDate);
        rv.setAdapter(ssca);
        rv.setLayoutManager(new LinearLayoutManager(ll.getContext(), LinearLayoutManager.VERTICAL, false));
        ssca.setListener(new SpecificScheduleCardAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Schedulable item = Schedule.getInstance().getSchedule().get(position);
                if (item.getType() == 1) {
                    int id = item.getID();
                    Intent intent = new Intent(thisContext, ViewSpecificEvent.class);
                    intent.putExtra("ID", id);
                    thisContext.startActivity(intent);
                }
            }
        });
    }

    //Viewholder of the adapter
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout ll;

        public ViewHolder(LinearLayout v){
            super(v);
            ll = v;
        }
    }

    public void updateData(){
        ArrayList<Schedulable> schedule = Schedule.getInstance().getSchedule();
        for(Schedulable i: schedule){
            if(!dates.contains(i.getDate())){
                dates.add(i.getDate());
            }
        }
    }

}
