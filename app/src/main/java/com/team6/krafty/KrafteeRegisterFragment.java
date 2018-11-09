package com.team6.krafty;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

public class KrafteeRegisterFragment extends Fragment {
    //DBruderick 11/8/2018 for get from gallery
    ImageView imgProfile;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    //DBruderick 11/8/2018 end


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kraftee_register, container, false);
    }

    //DBruderick 11/8/208 for get from gallery
    //Need to define listeners when view has been created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //Define the image view and set listener
        //  getView() because is a fragment, then find imageView by id
        imgProfile = (ImageView)getView().findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //start activity passing pick media intent and PICK_IMAGE code (100)
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE);
        }
        });

    }

    //For results of requests
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }
    //DBruderick 11/18/2018 end
}
