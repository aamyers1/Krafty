package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Fragment used to display current user's profile information.
 */
public class ProfileFragment extends Fragment {

    private cardAdapter ca;
    private HashMap<String, Product> products;

    /**
     * Method called on object instantiation. Inflates XML file fragment_profile.xml
     * @param inflater LayoutInflater to inflate the xml file
     * @param container Containing object for the inflated xml
     * @param savedInstanceState - Bundle containing state information that can be used on fragment
     *                             relaunch or resume to restore state
     * @return Inflated xml object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    /**
     * Method run once XML is inflated. If user type is Krafter or Admin, add additional fields.
     * Fill fields with appropriate information from the database.
     * @param view Total view displayed to user
     * @param savedInstanceState Bundle containing state information that can be used on fragment
     *                           relaunch or resume to restore state
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        User profile;
        DBManager dbManager = DBManager.getInstance();
        String username;
        Bundle arguments = getArguments();
        if (arguments == null) {
            profile = SessionManager.getUser();
            Button btn = view.findViewById(R.id.updateButton);
            //links to the update profile activity
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //switch to update fragment
                    Fragment fragment = new UpdateProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            });
        }
        else {
            username = getArguments().getString("username");
            String token = SessionManager.getToken(getView().getContext());
            profile = dbManager.getUser(token, username);

            products = dbManager.getKrafterProducts(username,token);
            RecyclerView rv = view.findViewById(R.id.recProducts);
            try {
                ca = new cardAdapter(getbmps(), getProdNames());
                rv.setAdapter(ca);
                rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            }
            catch(Exception e){
                Log.d("CA ERROR", e.getMessage());
            }
        }
        if (profile == null){
            Toast.makeText(getContext(), "Failed to get user data.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(profile.getUserType() != 2){
            FrameLayout fl = view.findViewById(R.id.krafterFrame);
            View v = getLayoutInflater().inflate(R.layout.krafterprofile, fl, true);
            TextView t = view.findViewById(R.id.Fields);
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
            if(profile.getFacebook()!=
                    null && !profile.getFacebook().equals("")){
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
        }
        TextView tv = view.findViewById(R.id.username);
        tv.setText(profile.getUsername());
        tv = view.findViewById(R.id.joinDate);
        tv.setText(profile.getDateJoined());
        tv = view.findViewById(R.id.name);
        String temp = "Name: "  + profile.getFirst() + " " + profile.getLast();
        tv.setText(temp);
        tv = view.findViewById(R.id.location);
        temp = "Location: " + profile.getCity() + ", " + profile.getState();
        tv.setText(temp);
        tv = view.findViewById(R.id.description);
        tv.setText("About: " + profile.getBio());
        ImageView imgProfile = view.findViewById(R.id.imgProfile);
        if (profile.getBmp() != null) {
            imgProfile.setImageBitmap(profile.getBmp());
        } else {
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));
        }
    }

    private  Bitmap[] getbmps(){
        Bitmap[] bmp = new Bitmap[products.size()];
        String [] names  = getProdNames();
        for(int i = 0; i < products.size(); i++){
            bmp[i] = products.get(names[i]).getBmp();
        }
        return bmp;
    }

    private  String[] getProdNames(){
        int k = 0;
        String[] names = new String[products.size()];
        for(String i: products.keySet()){
                names[k] = i;
                k++;
            }
            return names;

    }

}
