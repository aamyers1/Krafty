package com.team6.krafty;

//provide basic information about users. Use database fields for more info.
//use getters and setters where required. Private data is good.

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


    User(){
    }

    User(int userType, String username, String email, String password,
         String first, String last, String city, String state, String imageString){
        this.userType = userType;
        this.username = username;
        this.email = email;
        this.password = password;
        this.first = first;
        this.last = last;
        this.city = city;
        this.state = state;
        this.imageString = imageString;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        dateJoined = (sdf.format(date));
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
        this.dateJoined = dateJoined;
        this.businessName = businessName;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        dateJoined = (sdf.format(date));
    }

    public String createJson(){

        String jsonObj = "first=" + first +"&last=" + last + "&city=" + city + "&state=" + state +
                "&usertype=" +userType +"&email=" + email + "&username=" + username + "&bio=" + bio +  "&website=" + website + "&password=" + password + "&businessname=" + businessName +
                "&etsy=" + etsy + "&facebook=" + facebook + "&instagram=" + instagram  + "&image=" +imageString ;
        return jsonObj;
    }
    
    public User parseJson(JSONObject json){
        try {
            this.userType = json.getJSONObject("").getInt("usertype");
            this.username = json.getJSONObject("").getString("username");
            this.email = json.getJSONObject("").getString("email");
            this.password = json.getJSONObject("").getString("password");
            this.first = json.getJSONObject("").getString("first");
            this.last = json.getJSONObject("").getString("last");
            this.city = json.getJSONObject("").getString("city");
            this.state = json.getJSONObject("").getString("state");
            this.imageString = json.getJSONObject("").getString("imageString");
            this.bio = json.getJSONObject("").getString("bio");
            this.website = json.getJSONObject("").getString("website");
            this.etsy = json.getJSONObject("").getString("etsy");
            this.facebook = json.getJSONObject("").getString("facebook");
            this.instagram = json.getJSONObject("").getString("instagram");
            this.dateJoined = json.getJSONObject("").getString("dateJoined");
            this.businessName = json.getJSONObject("").getString("businessName");
        }
        catch (Exception e){
            e.printStackTrace();
        }
       return  this;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }
}
