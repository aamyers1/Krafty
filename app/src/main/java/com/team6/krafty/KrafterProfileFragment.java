package com.team6.krafty;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class KrafterProfileFragment extends Fragment {

    private User profile;
    private String encodedImage = "";

    public KrafterProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_krafter_profile, container, false);
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
        try{
            t.join();
        }
        catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }


        //Define the image view and set listener
        //  getView() because is a fragment, then find imageView by id
        TextView txtUsername = getView().findViewById(R.id.txtUsername);
        txtUsername.setText(profile.getUsername());

        ImageView imgProfile = getView().findViewById(R.id.imgProfile);
        if(profile.getBmp()!= null){
            imgProfile.setImageBitmap(profile.getBmp());
        }
        else{
            imgProfile.setBackgroundColor(Color.rgb(188,225,232));
        }

        TextView tvDateJoined = getView().findViewById(R.id.txtDateJoined);
        tvDateJoined.setText("Since" + profile.getDateJoined());

        EditText etBusiness = getView().findViewById(R.id.etBusinessName);
        etBusiness.setText(profile.getBusinessName());

        EditText etFirst = getView().findViewById(R.id.etFirst);
        etFirst.setText(profile.getFirst());

        EditText etLast = getView().findViewById(R.id.etLast);
        etLast.setText(profile.getLast());

        EditText etCity = getView().findViewById(R.id.etCity);
        etCity.setText(profile.getCity());

        EditText etState = getView().findViewById(R.id.etState);
        etState.setText(profile.getState());

        EditText etBio = getView().findViewById(R.id.etBio);
        etBio.setText(profile.getBio());

        EditText etWebsite = getView().findViewById(R.id.etWebsite);
        etWebsite.setText(profile.getWebsite());

        EditText etFacebook = getView().findViewById(R.id.etFacebook);
        etFacebook.setText(profile.getFacebook());

        EditText etEtsy = getView().findViewById(R.id.etEtsy);
        etEtsy.setText(profile.getEtsy());

        EditText etInstagram = getView().findViewById(R.id.etInstagram);
        etInstagram.setText(profile.getInstagram());
    }
}
