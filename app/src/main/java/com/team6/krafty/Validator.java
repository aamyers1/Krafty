package com.team6.krafty;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class Validator{

    public Validator(){

    }

    //TODO validate image. need to decide on criteria
    public static void validateImage(){

    }

    public static void validateUsername(EditText et)throws KraftyRuntimeException{
        if (et.getText().toString().equals("")) {
            throw new KraftyRuntimeException("No value entered for USERNAME.", null);
        }
    }

    public static void validateEmail(EditText et) throws KraftyRuntimeException{
        if (!isValidEmail(et.getText().toString())){
            throw new KraftyRuntimeException("Invalid EMAIL", null);
        }
    }

    public static void validateDateSet(TextView tv, String field) throws KraftyRuntimeException {
        if (tv.equals("No date selected") )
            throw new KraftyRuntimeException("Date not set for " + field, null);
    }

    public static void validateTimeSet(TextView tv, String field) throws KraftyRuntimeException {
        if(tv.equals("No opening time") || tv.equals("No closing time")) {
            throw new KraftyRuntimeException("Date not set for " + field, null);
        }
    }

    public static void validateBasicEditText(EditText et, String field) throws KraftyRuntimeException{
        if(et.getText().toString().equals("")){
            throw new KraftyRuntimeException("No text in field " + field, null);
        }
    }

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

    public static void validateNameEditText(EditText et, String field)throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Event Name is required in " + field, null);
        } else if (et.getText().length() > 256){
            throw new KraftyRuntimeException("Event Name too long in " + field, null);
        }
    }

    public static void validateStreetEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Street requires entry in " + field, null);
        } else if (et.getText().length() > 64){
            throw new KraftyRuntimeException("Street entry too long in " + field, null);
        }
    }

    public static void validateCityEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("City requires entry in " + field, null);
        } else if (et.getText().length() > 64){
            throw new KraftyRuntimeException("City entry too long in " + field, null);
        }
    }

    public static void validateStateEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("State requires entry in " + field, null);
        } else if (et.getText().length() > 2){
            throw new KraftyRuntimeException("State invalid entry in " + field, null);
        }
    }

    public static void validateZipEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Zip code requires entry in " + field, null);
        } else if (et.getText().length() > 10){
            throw new KraftyRuntimeException("Zip code invalid entry in " + field, null);
        }
    }

    public static void validateDescriptionEditText(EditText et, String field) throws KraftyRuntimeException{
        if (et.getText().length() <= 0 ){
            throw new KraftyRuntimeException("Description requires entry in " + field, null);
        } else if (et.getText().length() > 256){
            throw new KraftyRuntimeException("Description too long in " + field, null);
        }
    }

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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
