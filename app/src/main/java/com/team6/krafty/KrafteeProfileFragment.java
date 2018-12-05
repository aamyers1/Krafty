package com.team6.krafty;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class KrafteeProfileFragment extends Fragment {

    private User profile;
    private String encodedImage = "";

    public KrafteeProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kraftee_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final DBManager dbManager = new DBManager();
        final String token = SessionManager.getToken(getActivity());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                profile = new User();
                profile.parseJson(dbManager.getUser(token, ""));
            }
        });
        t.start();
        try {
            t.join();

        //Define the image view and set listener
        //  getView() because is a fragment, then find imageView by id
        //TODO check if all blank/valid
        TextView txtUsername = getView().findViewById(R.id.txtUsername);
        txtUsername.setText(profile.getUsername());

        ImageView imgProfile = getView().findViewById(R.id.imgProfile);
        if (profile.getBmp() != null) {
            imgProfile.setImageBitmap(profile.getBmp());
        } else {
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));
        }

        TextView tvDateJoined = getView().findViewById(R.id.txtDateJoined);
        if (profile.getDateJoined() != null) {
            tvDateJoined.setText("Since " + profile.getDateJoined());
        } else {
            tvDateJoined.setText("Since ");
        }

        EditText etFirst = getView().findViewById(R.id.etFirst);
        if (profile.getFirst() != null) {
            etFirst.setText(profile.getFirst());
        } else {
            etFirst.setText("");
        }

        EditText etLast = getView().findViewById(R.id.etLast);
        if (profile.getLast() != null) {
            etLast.setText(profile.getLast());
        } else {
            etLast.setText("");
        }

        EditText etCity = getView().findViewById(R.id.etCity);
        if (profile.getCity() != null) {
            etCity.setText(profile.getCity());
        } else {
            etCity.setText("");
        }

        EditText etState = getView().findViewById(R.id.etState);
        if(profile.getState() != null){
            etState.setText(profile.getState());
        } else {
            etState.setText("");
        }

        EditText etBio = getView().findViewById(R.id.etBio);
        if(profile.getBio() != null){
            etBio.setText(profile.getBio());
        } else {
            etBio.setText("");
        }
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }
}
