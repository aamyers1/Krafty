package com.team6.krafty;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

public class KrafteeRegisterFragment extends Fragment {
    //TODO: put the encoding / decoding of images into a thread + new class because of complexity
    String encodedImage="";

    //Displays the kraftee register fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kraftee_register, container, false);
    }

    //loads image viewer and fileds and sets action for button is tapped and
    //when image viewer is tapped
    //Need to define listeners when view has been created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Define the image view and set listener
        // getView() because is a fragment, then find imageView by id
         ImageView imgProfile = (ImageView)getView().findViewById(R.id.imgProfile);
        //loads the image uploader and lets user upload image
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks if the android version is older than API 23,
                if (Build.VERSION.SDK_INT >= 23) {
                    //selects image from external storage in API 23 or older
                    //requires external storage read permission
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
                    //can select image files without requiring external storage permission
                    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), 100);
                }
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

    //action for when register button is clicked
    public void onButtonClick(){
        String username, firstName, lastName, email, bio,city, state, password;
        //gets all user inputs from the GUI
        EditText eUsername = getView().findViewById(R.id.username);
        EditText ePassword = getView().findViewById(R.id.password);
        EditText eConfirm = getView().findViewById(R.id.confirmPassword);
        EditText eFirst = getView().findViewById(R.id.firstName);
        EditText eLast = getView().findViewById(R.id.txtLastName);
        EditText eEmail = getView().findViewById(R.id.email);
        EditText eBio = getView().findViewById(R.id.txtBio);
        EditText eCity = getView().findViewById(R.id.txtCity);
        EditText eState = getView().findViewById(R.id.txtState);

        try {
            Validator.validateUsername(eUsername);
            Validator.validateBasicEditText(ePassword, "PASSWORD");
            Validator.validateBasicEditText(eConfirm, "PASSWORD CONFIRMATION");
            Validator.validateBasicEditText(eFirst, "FIRST NAME");
            Validator.validateBasicEditText(eLast, "LAST NAME");
            Validator.validateEmail(eEmail);
            Validator.validateCityEditText(eCity, "CITY");
            Validator.validateStateEditText(eState, "STATE");
        } catch (KraftyRuntimeException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        username = eUsername.getText().toString();
        password = ePassword.getText().toString();
        firstName = eFirst.getText().toString();
        lastName = eLast.getText().toString();
        email = eEmail.getText().toString();
        bio = eBio.getText().toString();
        city = eCity.getText().toString();
        state = eState.getText().toString();

        //checks if both passwords match
        if(!eConfirm.getText().toString().equals(password)){
            Toast.makeText(getContext(), "Password and Confirm Password do not match. Try again", Toast.LENGTH_SHORT).show();
            return;
        }

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
            imgProfile.setImageDrawable(null);
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));


            //Convert image to bitmap, then into base64
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), imageUri);
                Validator.validateImage(imageAsBitmap, "Event Image");
            } catch (IOException e) { //for file not found
                e.printStackTrace();
            } catch (KraftyRuntimeException e){
                Toast.makeText(applicationContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } catch (RuntimeException e){
                if (e.getMessage().contains("draw too large")){
                    Toast.makeText(applicationContext, "Image too large", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //Put image in imageview
            imgProfile.setImageURI(imageUri);

            ByteArrayOutputStream byteArrOutStrm = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrOutStrm);
            byte[] bArr = byteArrOutStrm.toByteArray();
            encodedImage = Base64.encodeToString(bArr, Base64.DEFAULT).replace("+","<");
            }
        }

        //lets user select profile image depending upon the access granted for the program
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
