package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {

     private int userType;
     private String username, email, password;
     private String first, last, city, state;
     private String imageString;
     private String bio, businessName;
     private String website, etsy, facebook, instagram;
     private String dateJoined;
     Bitmap bmp;

    //sets up an empty User -> to be used when a json object is being parsed for user data
    User(){
    }

    User(int userType, String username, String email, String password,
         String first, String last, String city, String state, String imageString,
         String bio, String website, String etsy, String facebook,
         String instagram, String businessName){
        this.userType = userType;
        this.username = username;
        this.email = email;
        this.password = password;
        this.first = first;
        this.last = last;
        this.city = city;
        this.state = state;
        this.imageString = imageString;
        this.bio = bio;
        this.website = website;
        this.etsy = etsy;
        this.facebook = facebook;
        this.instagram = instagram;
        this.businessName = businessName;
        parseBitmap();
    }

    //creates a simple json object that can be handled by the api
    public String createJson(){
        String jsonObj = "first=" + first +"&last=" + last + "&city=" + city + "&state=" + state +
                "&usertype=" +userType +"&email=" + email + "&username=" + username + "&bio=" + bio +  "&website=" + website + "&password=" + password + "&businessname=" + businessName +
                "&etsy=" + etsy + "&facebook=" + facebook + "&instagram=" + instagram  + "&image=" +imageString ;
        return jsonObj;
    }

    //gets the fields from a json object for a user and stores them in the user object
    public void parseJson(JSONObject jsonReturn){
        try {
            JSONObject json = jsonReturn.getJSONObject("result");
            this.userType = json.getInt("usertype");
            this.username = json.getString("username");
            this.email = json.getString("email");
            this.password = json.getString("password");
            this.first = json.getString("first");
            this.last = json.getString("last");
            this.city = json.getString("city");
            this.state = json.getString("state");
            this.imageString = json.getString("imageString");
            parseBitmap();
            this.bio = json.getString("bio");
            this.website = json.getString("website");
            this.etsy = json.getString("etsy");
            this.facebook = json.getString("facebook");
            this.instagram = json.getString("instagram");
            this.dateJoined = json.getString("dateJoined");
            this.businessName = json.getString("businessName");
        }
        catch (Exception e){
            Log.d("ERROR PARSING USER", e.getMessage());
        }
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public int getUserType(){return userType;}

    public String getFirst(){return first;}

    public String getLast(){return last;}

    public String getCity(){return city;}

    public String getState(){return state;}

    public String getBio(){return bio;}

    public String getWebsite(){return  website;}

    public String getBusinessName(){return businessName;}

    public String getEtsy(){return etsy;}

    public String getFacebook(){return facebook;}

    public String getInstagram(){return instagram;}

    public Bitmap getBmp() {
        return bmp;
    }

    public String getDateJoined(){
        return dateJoined;
    }

    private void parseBitmap(){
        if(!this.imageString.equals("null") && !this.imageString.equals("no image")) {
            byte[] encodeByte = Base64.decode(imageString.replace("<", "+"), Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }
        else{
            bmp = null;
        }
    }
}
