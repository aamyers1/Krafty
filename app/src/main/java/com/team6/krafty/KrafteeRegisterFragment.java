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
import android.widget.EditText;
import android.widget.ImageView;

import org.w3c.dom.Text;

import static android.app.Activity.RESULT_OK;

public class KrafteeRegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kraftee_register, container, false);
    }

    //DBruderick 11/8/208 for get from gallery
    //Need to define listeners when view has been created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Define the image view and set listener
        // getView() because is a fragment, then find imageView by id
         ImageView imgProfile = (ImageView)getView().findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //start activity passing pick media intent and PICK_IMAGE code (100)
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
        }
        });

    }



    //For results of requests
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();
            ImageView imgProfile = getView().findViewById(R.id.imgProfile);
            imgProfile.setImageURI(imageUri);
        }
    }
    //DBruderick 11/18/2018 end
}
