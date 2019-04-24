package com.team6.krafty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Fragment used to display current user's profile information.
 */
public class KrafterProfileActivity extends AppCompatActivity {

    private cardAdapter ca;
    private HashMap<String, String> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        String un = getIntent().getStringExtra("username");
        User profile = SessionManager.getExternalUser(this, un);
        setContentView(R.layout.activity_krafter_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (profile == null){
            Toast.makeText(this, "Failed to get user data.", Toast.LENGTH_SHORT).show();
            return;
        }

        FrameLayout fl = findViewById(R.id.krafterFrame);
        View v = getLayoutInflater().inflate(R.layout.krafterprofile, fl, true);
        TextView t = v.findViewById(R.id.Fields);
        StringBuilder krafterInfo = new StringBuilder();
        if(profile.getBusinessName()!= null && !profile.getBusinessName().equals("")){
            krafterInfo.append("Business Name: " + profile.getBusinessName() + "\n");
        }
        if(profile.getWebsite()!=null && !profile.getWebsite().equals("")){
            krafterInfo.append("Website: " + profile.getWebsite() + "\n");
        }
        if(profile.getEtsy()!=null && !profile.getEtsy().equals("")){
            krafterInfo.append("Etsy: " + profile.getEtsy() + "\n");
        }
        if(profile.getFacebook()!=null && !profile.getFacebook().equals("")){
            krafterInfo.append("Facebook: " + profile.getFacebook() + "\n");
        }
        if(profile.getInstagram()!=null && !profile.getInstagram().equals("")){
            krafterInfo.append("Instagram: " + profile.getInstagram() + "\n");
        }
        String total = krafterInfo.toString();
        if(!total.equals("")){
            total = total.substring(0, total.lastIndexOf("\n"));
        }
        t.setText(total);

            ProductController pc = new ProductController();
            products = pc.getKrafterProducts(un,this);
            RecyclerView rv = findViewById(R.id.recProducts);
            try {
                ca = new cardAdapter(getbmps(), getProdNames());
                rv.setAdapter(ca);
                rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
            catch(Exception e){
                Log.d("CA ERROR", e.getMessage());
            }

            /*
            // Listener to launch product details
            ca.setListener(new cardAdapter.Listener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(getApplicationContext(), ViewProductActivity.class);
                }
            });*/

        TextView tv = findViewById(R.id.username);
        tv.setText(profile.getUsername());
        tv = findViewById(R.id.joinDate);
        tv.setText(profile.getDateJoined());
        tv = findViewById(R.id.name);
        String temp = "Name: "  + profile.getFirst() + " " + profile.getLast();
        tv.setText(temp);
        tv = findViewById(R.id.location);
        temp = "Location: " + profile.getCity() + ", " + profile.getState();
        tv.setText(temp);
        tv = findViewById(R.id.description);
        tv.setText("About: " + profile.getBio());
        ImageView imgProfile = findViewById(R.id.imgProfile);
        if (profile.getBmp() != null) {
            imgProfile.setImageBitmap(profile.getBmp());
        } else {
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));
        }
    }

    /**
     * method to get bitmap images of products of a Krafter
     * @return a Bitmap array of images
     */
    private  Bitmap[] getbmps(){
        Bitmap[] bmp = new Bitmap[products.size()];
        String [] names  = getProdNames();
        for(int i = 0; i < products.size(); i++){
            bmp[i] = getBmp(products.get(names[i]));
        }
        return bmp;
    }

    /**
     * method to get names of products of a Krafter
     * @return a String array of names
     */
    private  String[] getProdNames(){
        int k = 0;
        String[] names = new String[products.size()];
        for(String i: products.keySet()){
            names[k] = i;
            k++;
        }
        return names;

    }

    private Bitmap getBmp(String image){
        Bitmap bmp;
        if(!(image == null) && !image.equals("no image") && !image.equals("") ) {
            try{
                byte[] encodeByte = Base64.decode(image.replace("<", "+"), Base64.DEFAULT);
                bmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            } catch (IllegalArgumentException e){

                Log.d("MATERIAL IMAGE ERROR", "bad image base-64");
                bmp = null;
            }
        }
        else{
            bmp = null;
        }
        return bmp;
    }

}
