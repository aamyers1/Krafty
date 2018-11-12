package com.team6.krafty;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class KrafteeRegisterFragment extends Fragment {

    String encodedImage="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kraftee_register, container, false);
    }

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
        Button registerButton = getView().findViewById(R.id.btnSubmit);
        //register listener for the button click event
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });

    }

    public void onButtonClick(){
        String username, firstName, lastName, email, bio,city, state, password;
        EditText et = getView().findViewById(R.id.username);
        username = et.getText().toString();
        et = getView().findViewById(R.id.password);
        password = et.getText().toString();
        et = getView().findViewById(R.id.confirmPassword);
        if(!et.getText().toString().equals(password)){
            Toast.makeText(getContext(), "Password and Confirm Password do not match. Try again", Toast.LENGTH_SHORT).show();
            return;
        }
        et = getView().findViewById(R.id.firstName);
        firstName = et.getText().toString();
        et = getView().findViewById(R.id.txtLastName);
        lastName = et.getText().toString();
        et = getView().findViewById(R.id.email);
        email = et.getText().toString();
        et = getView().findViewById(R.id.txtBio);
        bio = et.getText().toString();
        et = getView().findViewById(R.id.txtCity);
        city = et.getText().toString();
        et = getView().findViewById(R.id.txtState);
        state = et.getText().toString();
        RegistrationController rc = new RegistrationController();
        boolean created = rc.createNewUser(2, username,email,password,firstName,lastName,
                city,state, encodedImage,bio,"","","","","",getContext());
        if(created){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    //For results of requests
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();

            //Put image in imageview
            ImageView imgProfile = getView().findViewById(R.id.imgProfile);
            imgProfile.setImageURI(imageUri);

            //Convert image to bitmap, then into base64
            Context applicationContext = RegisterActivity.getContextOfApplication();
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), imageUri);
            } catch (IOException e) { //for file not found
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrOutStrm = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrOutStrm);
            byte[] bArr = byteArrOutStrm.toByteArray();
            encodedImage = Base64.encodeToString(bArr, Base64.DEFAULT);
        }
    }
}
