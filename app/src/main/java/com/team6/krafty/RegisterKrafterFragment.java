package com.team6.krafty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class RegisterKrafterFragment extends Fragment {
   private String encodedImage = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_krafter, container, false);
    }

    //Need to define listeners when view has been created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //Define the image view and set listener
        //  getView() because is a fragment, then find imageView by id
        ImageView imgProfile = (ImageView)getView().findViewById(R.id.imgProfile);
        imgProfile = (ImageView)getView().findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    111);
                        }
                    }else{
                        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
                    }
                }else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 100);
                }
            }
        });
        Button registerButton = (Button)getView().findViewById(R.id.submit);
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });

    }

    public  void onButtonClick(){
        //gather all the data fields and send them to the controller for processing;
        int userType = 1;
        String username, email, password, firstName, lastName, city, state, bio, website, etsy, facebook, instagram, businessName;
        EditText et = (EditText) getView().findViewById(R.id.username);
        username = et.getText().toString();
        et = getView().findViewById(R.id.password);
        password = et.getText().toString();
        et = getView().findViewById(R.id.confirmPassword);
        if(!et.getText().toString().equals(password)){
            Toast.makeText(getContext(), "Passwords do not match. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        et = getView().findViewById(R.id.email);
        email = et.getText().toString();
        et = getView().findViewById(R.id.txtFname);
        firstName = et.getText().toString();
        et = getView().findViewById(R.id.txtLName);
        lastName = et.getText().toString();
        et = getView().findViewById(R.id.txtBio);
        bio = et.getText().toString();
        et = getView().findViewById(R.id.txtCity);
        city = et.getText().toString();
        et = getView().findViewById(R.id.txtState);
        state = et.getText().toString();
        et = getView().findViewById(R.id.txtWebsite);
        website = et.getText().toString();
        et = getView().findViewById(R.id.txtEtsy);
        etsy = et.getText().toString();
        et = getView().findViewById(R.id.txtFacebook);
        facebook = et.getText().toString();
        et = getView().findViewById(R.id.txtInstagram);
        instagram = et.getText().toString();
        et = getView().findViewById(R.id.business);
        businessName = et.getText().toString();
        RegistrationController rc = new RegistrationController();
        boolean isCreated =rc.createNewUser(userType, username, email,  password,
                firstName,lastName, city, state, encodedImage,
                bio, website, etsy, facebook,
                instagram,businessName,getContext());
        if(isCreated){
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
            Context applicationContext = RegisterActivity.getContextOfApplication();
            InputStream imageStream = null;
            try {
                imageStream = applicationContext.getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                Toast.makeText(applicationContext, "File not found.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (RuntimeException e) {
                Toast.makeText(applicationContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            //Put image in imageview
            ImageView imgProfile = getView().findViewById(R.id.imgProfile);
            imgProfile.setImageBitmap(selectedImage);


            //Convert image to bitmap, then into base64
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 100);
            } else {
                Toast.makeText(RegisterActivity.getContextOfApplication(), "Image Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
