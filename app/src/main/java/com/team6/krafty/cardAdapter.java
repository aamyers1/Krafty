package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class that generates cards that display an image with a caption to be used to
 * display products and their captions. Used to populate a RecyclerView with data.
 */
public class cardAdapter extends RecyclerView.Adapter<cardAdapter.ViewHolder>{

    private String[] captions;
    private  Bitmap[] images;
    private Listener listener;

    /**
     * Simple onClick interface for an individual card
     */
    interface Listener {
        void onClick(int position);
    }

    /**
     * Sets the listener
     * @param listener Listener object to be used with the onClick event
     */
    public void setListener(Listener listener){
        this.listener = listener;
    }

    /**
     * Constructor
     * @param images Array of bitmap images displayed on card
     * @param captions Array of String captions to display on card
     */
    public cardAdapter(Bitmap[] images, String[] captions){
        this.captions = captions;
        this.images = images;
    }

    /**
     * Gets the number of items in the adapter
     * @return int number of items
     */
    @Override
    public int getItemCount(){
        return captions.length;
    }

    /**
     * Sets the viewHolder, in this case cardview in captionedcard.xml
     * @param parent Containing object
     * @param viewType specifies integer representation of viewtype
     * @return
     */
    @Override
    public cardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.captionedcard,parent, false);
        return new ViewHolder(cv);
    }

    /**
     * When a ViewHolder object is bound, the card data is placed
     * @param holder CardView object
     * @param position The index of the current card
     */
    @Override
    public void onBindViewHolder(ViewHolder holder , final int position){
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imgCaption);
        if(images[position] != null){
            iv.setImageBitmap(images[position]);
        }
        else{
            iv.setBackgroundColor(Color.rgb(188,225,232));
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

    /**
     * Inner class that is the ViewHolder of the adapter. Extends
     */
    static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        /**
         * Constructs ViewHolder object from CardView v
         * @param v CardView to be set
         */
        ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    /**
     * Updates the adapter data when required by the caller
     * @param images Bitmap array of images to be placed in cards
     * @param captions String array of captions to label cards
     */
    public void updateData(Bitmap[] images, String[] captions){
        this.captions = captions;
        this.images = images;
    }

    //test only
    /**
     * Returns the caption of a given position
     * @param pos position from cardAdapter
     * @return the caption from captions array
     */
    public String getCaption(int pos){
        //Log.d("name", captions[pos].substring(0,captions[pos].indexOf(":")));
        return captions[pos];
    }
}
