package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.ViewHolder>{

    private String[] captions;
    private String[] images;
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
    //Params: String array for captions for each card and String array of Base64 encoded images
    public cardAdapter(String[] captions, String[] imgs){
        this.captions = captions;
        this.images = imgs;
    }

    //gets the number of items to be created
    @Override
    public int getItemCount(){
        return captions.length;
    }

    //sets the viewholder, in this case the cardview in captionedcard.xml
    @Override
    public cardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.captionedcard,parent, false);
        return new ViewHolder(cv);
    }

    //when viewholder is bound
    @Override
    public void onBindViewHolder(ViewHolder holder , final int position){
        CardView cardView = holder.cardView;
        ImageView iv = (ImageView)cardView.findViewById(R.id.imgCaption);


        if(images[position] != "null" && images[position] != "no image") {
            try {
                byte[] encodeByte = Base64.decode(images[position].replace("<", "+"), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                iv.setImageBitmap(bitmap);
            } catch (Exception e) { //for file not found
                iv.setBackgroundColor(Color.rgb(188, 225,232));
                e.printStackTrace();
            }
        }else{
            iv.setBackgroundColor(Color.rgb(188, 225,232));
        }
        iv.setContentDescription(captions[position]);
        TextView tv = cardView.findViewById(R.id.txtCaption);
        tv.setText(captions[position]);

        //set the onClickListener for the cardview. The implementation of the RecyclerView will define a specific listener
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

    //Viewholder of the adapter
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }

    }
}
