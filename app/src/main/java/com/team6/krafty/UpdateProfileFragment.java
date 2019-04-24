package com.team6.krafty;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


/**
 * Fragment that allows a user to update their profile information
 */
public class UpdateProfileFragment extends Fragment {
    static DBManager dbManager = DBManager.getInstance();
    private String encodedImage = "";

    /**
     * Called on fragment creation to inflate XML
     * @param inflater inflates the XML
     * @param container Parent container of the fragment
     * @param savedInstanceState Bundle of previous information
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.update_fragment_profile, container, false);
    }


    /**
     * Called once the above method finishes. Sets view fields and listeners
     * @param view Containing view
     * @param savedInstanceState Bundle for previous state information
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String token = SessionManager.getToken(getView().getContext());
        FillFields ff = new FillFields();
        ff.execute(token);

        ImageView iv = view.findViewById(R.id.imgProfile);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });
        Button b = view.findViewById(R.id.btnSubmit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateClick();

            }
        });

    }

    /**
     * click listener to update profile in the database
     */
    public void onUpdateClick(){
        EditText uFirst = getView().findViewById(R.id.etFirst);
        EditText uLast = getView().findViewById(R.id.etLast);
        EditText uCity = getView().findViewById(R.id.etCity);
        EditText uState = getView().findViewById(R.id.etState);
        EditText uBio = getView().findViewById(R.id.etBio);
        EditText uBuss = getView().findViewById(R.id.etBusinessName);
        EditText uEtsy = getView().findViewById(R.id.etEtsy);
        EditText uFacebook= getView().findViewById(R.id.etFacebook);
        EditText uInstagram = getView().findViewById(R.id.etInstagram);
        EditText uWebsite = getView().findViewById(R.id.etWebsite);
        try{
            Validator.validateBasicEditText(uFirst,"First");
            Validator.validateBasicEditText(uLast,"Last");
            Validator.validateBasicEditText(uCity,"City");
            Validator.validateBasicEditText(uState,"State");
            Validator.validateBasicEditText(uBio,"Bio");
        }
        catch(KraftyRuntimeException e){
           Toast.makeText(getView().getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            return;
        }

        User user = SessionManager.getUser();
        user.setFirst(uFirst.getText().toString());
        user.setLast(uLast.getText().toString());
        user.setCity(uCity.getText().toString());
        user.setState(uState.getText().toString());
        user.setBio(uBio.getText().toString());
        user.setBusinessName(uBuss.getText().toString());
        user.setEtsy(uEtsy.getText().toString());
        user.setFacebook(uFacebook.getText().toString());
        user.setInstagram(uInstagram.getText().toString());
        user.setWebsite(uWebsite.getText().toString());
        if(!(encodedImage.equals("")) & !encodedImage.equals(user.getImageString()))
            user.setImageString(encodedImage);
        RegistrationController rc = new RegistrationController();
        if (rc.updateProfile(user,getView().getContext())){
            getActivity().onBackPressed();
        }
    }

    /**
     * Asynchronous inner class to fill fields without hanging the application
     */
    private class FillFields extends AsyncTask<String, Void, User> {

        @Override
        public User doInBackground(String...args){
            User  profile = new User();
            String token = args[0];
            try {
                profile = dbManager.getUser(token, "");
            }catch (KraftyRuntimeException e){
                Log.d("GET USER ERROR", "Profile error " + e);
                //TODO force logout?
            }
            return profile;
        }

        @Override
        public void onPostExecute(User profile){
            profile.getUserType();
            if(profile.getUserType()!= 2) {
                FrameLayout fl = getView().findViewById(R.id.krafterFrame);
                View v = getLayoutInflater().inflate(R.layout.fragment_update_krafter_profile, fl, true);
                // set these fields
                EditText etBusiness = getView().findViewById(R.id.etBusinessName);
                if(profile.getBusinessName() != null){
                    etBusiness.setText(profile.getBusinessName());
                } else {
                    etBusiness.setText("");
                }

                EditText etEtsy = getView().findViewById(R.id.etEtsy);
                if(profile.getEtsy() != null){
                    etEtsy.setText(profile.getEtsy());
                } else {
                    etEtsy.setText("");
                }

                EditText etFacebook = getView().findViewById(R.id.etFacebook);
                if(profile.getFacebook() != null){
                    etFacebook.setText(profile.getFacebook());
                } else {
                    etFacebook.setText("");
                }

                EditText etInstagram = getView().findViewById(R.id.etInstagram);
                if(profile.getInstagram() != null){
                    etInstagram.setText(profile.getInstagram());
                } else {
                    etInstagram.setText("");
                }

                EditText etWebsite = getView().findViewById(R.id.etWebsite);
                if(profile.getWebsite() != null){
                    etWebsite.setText(profile.getWebsite());
                } else {
                    etWebsite.setText("");
                }

            }

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


        }
    }

    /**
     * Gets the image from gallery
     * @param requestCode requesting code
     * @param resultCode result code for success or failure
     * @param data the intent data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if (resultCode == RESULT_OK && requestCode == 100) {
            Uri imageUri = data.getData();
            Context applicationContext = getView().getContext();
            //Put image in imageview
            ImageView imgProfile = getView().findViewById(R.id.imgProfile);
            imgProfile.setImageDrawable(null);
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));

            //Convert image to bitmap, then into base64
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), imageUri);
                Validator.validateImage(imageAsBitmap, "Profile Image");
            } catch (IOException e) { //for file not found
                e.printStackTrace();
            } catch (KraftyRuntimeException e){
                Toast.makeText(getView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } catch (RuntimeException e){
                if (e.getMessage().contains("draw too large")){
                    Toast.makeText(getView().getContext(), "Image too large", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            imgProfile.setImageURI(imageUri);

            ByteArrayOutputStream byteArrOutStrm = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutStrm);
            byte[] bArr = byteArrOutStrm.toByteArray();
            encodedImage = Base64.encodeToString(bArr, Base64.DEFAULT).replace("+", "<");
        }
    }
}
