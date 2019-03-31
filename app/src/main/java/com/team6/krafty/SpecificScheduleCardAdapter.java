package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SpecificScheduleCardAdapter extends RecyclerView.Adapter<SpecificScheduleCardAdapter .ViewHolder>{

    String[] titles;
    String[] times;
    int[] ids;
    int[] type;
    private Listener listener;

    //create a generic listener interface for the adapter
    interface Listener {
        void onClick(int position);
    }

    //sets the listener for this adapter
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //constructor
    public SpecificScheduleCardAdapter(ArrayList<Schedulable> thisSched){
        int k = thisSched.size();
        times = new String[k];
        titles = new String[k];
        ids = new int[k];
        type = new int[k];
        for(int i =0 ; i < k; i ++){
            times[i] = thisSched.get(i).getTime();
            ids[i] = thisSched.get(i).getID();
            type[i] = thisSched.get(i).getType();
            titles[i] = thisSched.get(i).getTitle();
        }
    }

    //gets the number of items to be created
    @Override
    public int getItemCount(){
        return titles.length;
    }

    //sets the viewholder
    @Override
    public SpecificScheduleCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_schedule_card, parent, false);
        return new ViewHolder(cv);
    }

    //when viewholder is bound
    @Override
    public void onBindViewHolder(ViewHolder holder , final int position){
        CardView cardView = holder.cardView;
        //set the onClickListener for the cardview. The implementation of the RecyclerView will define a specific listener
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });

        TextView tv = cardView.findViewById(R.id.title);
        tv.setText(titles[position]);
        tv.setTextColor(Color.rgb(255,255,255));
        tv = cardView.findViewById(R.id.time);
        tv.setText(times[position]);
        tv.setTextColor(Color.rgb(255,255,255));
        if(type[position] == 1){
            cardView.setBackgroundColor(Color.rgb(188,225,232));
        }
        else{
            cardView.setBackgroundColor(Color.rgb(166, 131, 191));
        }
    }

    //Viewholder of the adapter
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public void updateData(){

    }

}
