package com.team6.krafty;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScheduleCardAdapter extends RecyclerView.Adapter<ScheduleCardAdapter.ViewHolder>{

    private ArrayList<String> dates;
    private Listener listener;
    private SpecificScheduleCardAdapter ssca;
    Context thisContext;
    ScheduleFragment fragment;

    //create a generic listener interface for the adapter
    interface Listener {
        void onClick(int position);
    }

    //sets the listener for this adapter
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //constructor
    public ScheduleCardAdapter(Context context, ScheduleFragment fragment){
        this.fragment = fragment;
        thisContext = context;
        ArrayList<Schedulable> schedule = Schedule.getInstance().getSchedule();
        dates = new ArrayList<>();
        for(Schedulable i: schedule){
            if(!dates.contains(i.getDate().trim())){
                dates.add(i.getDate().trim());
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
        final ArrayList<Schedulable> scheduleDate = new ArrayList<>();
        for (Schedulable i : Schedule.getInstance().getSchedule()) {
            if (i.getDate().trim().equals(dates.get(position))) {
                scheduleDate.add(i);
            }
        }
        ssca = new SpecificScheduleCardAdapter(scheduleDate);
        rv.setAdapter(ssca);
        rv.setLayoutManager(new LinearLayoutManager(ll.getContext(), LinearLayoutManager.VERTICAL, false));
        ssca.setListener(new SpecificScheduleCardAdapter.Listener() {
            @Override
            public void onClick(int position) {
                final Schedulable item = scheduleDate.get(position);
                if (item.getType() == 1) {
                    int id = item.getID();
                    Intent intent = new Intent(thisContext, ViewSpecificEvent.class);
                    intent.putExtra("ID", id);
                    thisContext.startActivity(intent);
                }
                else{
                    new AlertDialog.Builder(thisContext)
                            .setTitle("Delete Confirmation")
                            .setMessage("Do you want to remove this task?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ScheduleController sc = ScheduleController.getInstance();
                                    int schedIDPos = Schedule.getInstance().getSchedIDPosByTask((Task)item);
                                    if (sc.unscheduleForEvent(Schedule.getInstance().getIDS().get(schedIDPos), thisContext, "p")) {
                                        Schedule.getInstance().getSchedule().remove(item);
                                        Schedule.getInstance().getIDS().remove(schedIDPos);
                                        updateData();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(thisContext, "Task not deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
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
        fragment.resetFragment();
    }

}
