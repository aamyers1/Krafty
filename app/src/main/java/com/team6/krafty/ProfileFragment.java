package com.team6.krafty;


import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button btn = view.findViewById(R.id.updateButton);
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
        User profile = SessionManager.getUser();
        if (profile == null){

            Toast.makeText(getContext(), "Failed to get user data.", Toast.LENGTH_SHORT).show();
            return; // TODO go to log out? retry user get?
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

}
