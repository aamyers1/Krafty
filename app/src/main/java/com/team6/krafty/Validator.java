package com.team6.krafty;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class Validator{

    public Validator(){

    }

    /**
     * Validates image
     * @param imageAsBitmap Bitmap image
     * @param field sending field
     */
    public static void validateImage(Bitmap imageAsBitmap, String field){


        if (imageAsBitmap.getWidth() < 30){
            throw new KraftyRuntimeException("Image width too small.", null);
        } else if (imageAsBitmap.getWidth() > 4000){
            throw new KraftyRuntimeException("Image width too large.", null);
        }

        if (imageAsBitmap.getHeight() < 30){
            throw new KraftyRuntimeException("Image height too small.", null);
        } else if (imageAsBitmap.getHeight() > 4000){
            throw new KraftyRuntimeException("Image height too large.", null);
        }
    }

    /**
     * Validates username field
     * @param et username Edittext field
     * @throws KraftyRuntimeException
     */
    public static void validateUsername(EditText et)throws KraftyRuntimeException{
        if (et.getText().toString().equals("")) {
            throw new KraftyRuntimeException("No value entered for USERNAME.", null);
        }
    }

    /**
     * Basic email validation check
     * @param et EditText field to be validated from
     * @throws KraftyRuntimeException
     */
    public static void validateEmail(EditText et) throws KraftyRuntimeException{
        if (!isValidEmail(et.getText().toString())){
            throw new KraftyRuntimeException("Invalid EMAIL", null);
        }
    }

    /**
     * Validates a date to ensure correctly set
     * @param tv TextView in which the date was entered
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateDateSet(TextView tv, String field) throws KraftyRuntimeException {
        if (tv.equals("No date selected") )
            throw new KraftyRuntimeException("Date not set for " + field, null);
    }

    /**
     * Validates time set to ensure correctly set
     * @param tv TextView containing the time
     * @param field Name of the field being authenticated
     * @throws KraftyRuntimeException
     */
    public static void validateTimeSet(TextView tv, String field) throws KraftyRuntimeException {
        if(tv.equals("No opening time") || tv.equals("No closing time")) {
            throw new KraftyRuntimeException("Date not set for " + field, null);
        }
    }

    /**
     * Basic edit text validator checking emptiness
     * @param et EditText being validated
     * @param field Name of the edit text
     * @throws KraftyRuntimeException
     */
    public static void validateBasicEditText(EditText et, String field) throws KraftyRuntimeException{
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No text in field " + field, null);
        }
    }

    /**
     * Ensures an integer has been written into the EditText specified
     * @param et EditText To be checked
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateIntEt(EditText et, String field) throws KraftyRuntimeException{
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No input in field " + field, null);
        }
        try{
            String n = et.getText().toString();
            int nint = Integer.parseInt(n);
        }
        catch (Exception e){
            throw new KraftyRuntimeException("Not a valid number in field " + field, null);
        }
    }

    /**
     * Ensures a double value has been entered into a specified edit text
     * @param et EditText to be checked
     * @param field Name of field
     * @throws KraftyRuntimeException
     */
    public static void validateDoubleEt(EditText et, String field)throws KraftyRuntimeException{
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No input in field " + field, null);
        }
        try{
            String n = et.getText().toString();
            double ndouble = Double.parseDouble(n);
        }
        catch (Exception e){
            throw new KraftyRuntimeException("Not a valid value in field " + field, null);
        }
    }

    /**
     * Validates editText for emptiness and length
     * @param et EditText to be checked
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateNameEditText(EditText et, String field)throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Event Name is required in " + field, null);
        } else if (et.getText().length() > 256){
            throw new KraftyRuntimeException("Event Name too long in " + field, null);
        }
    }

    /**
     * Validates street address by length only
     * @param et EditText to be checked
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateStreetEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Street requires entry in " + field, null);
        } else if (et.getText().length() > 64){
            throw new KraftyRuntimeException("Street entry too long in " + field, null);
        }
    }

    /**
     * Validates city name by length only
     * @param et EditText to be checked
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateCityEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("City requires entry in " + field, null);
        } else if (et.getText().length() > 64){
            throw new KraftyRuntimeException("City entry too long in " + field, null);
        }
    }

    /**
     * Validates State edit text
     * @param et EditText to be checked
     * @param field field name
     * @throws KraftyRuntimeException
     */
    public static void validateStateEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("State requires entry in " + field, null);
        } else if (et.getText().length() > 2){
            throw new KraftyRuntimeException("State invalid entry in " + field, null);
        }
    }

    /**
     * Validates zip code Edit Text
     * @param et EditText to be checked
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateZipEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Zip code requires entry in " + field, null);
        } else if (et.getText().length() > 10){
            throw new KraftyRuntimeException("Zip code invalid entry in " + field, null);
        }
    }

    /**
     * Validates appropriate length of description
     * @param et EditText to be checked
     * @param field Field name
     * @throws KraftyRuntimeException
     */
    public static void validateDescriptionEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Description requires entry in " + field, null);
        } else if (et.getText().length() > 256){
            throw new KraftyRuntimeException("Description too long in " + field, null);
        }
    }

    /**
     * Validates address to ensure can be properly geolocated
     * @param street Street
     * @param city City
     * @param state State initials
     * @param zipcode Zipcode
     * @param context Context
     * @throws KraftyRuntimeException
     */
    public static void validateAddress(String street, String city, String state, String zipcode, Context context) throws KraftyRuntimeException {
        LatLng theLatLng = getLocationFromAddress(context, street + " " + city + " " + state + " " + zipcode);
        if (theLatLng == null) {
            throw new KraftyRuntimeException("Unable to validate address", null);
        }
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    /**
     * Checks validity of email
     * @param target charsequence
     * @return boolean true if valid email
     */
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
