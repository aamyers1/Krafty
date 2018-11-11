package com.team6.krafty;

//provide basic information about users. Use database fields for more info.
//use getters and setters where required. Private data is good.

import org.json.JSONObject;

public class User {
     private static int userType;
     private static String username, email, password;
     private static String first, last, city, state;
     private static String imageString;
     private static String bio, businessName;
     private static String website, etsy, facebook, instagram;
     private static String phone, dateJoined;
    //todo: constructor
    //todo: method to generate a json object
    //todo: method to parse a json object for user

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
    }
    User(int userType, String username, String email, String password,
         String first, String last, String city, String state, String imageString,
         String bio, String website, String etsy, String facebook,
         String instagram, String phone, String dateJoined, String businessName){
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
        this.phone = phone;
        this.dateJoined = dateJoined;
        this.businessName = businessName;
    }

    public static JSONObject createJson(){
        JSONObject json = new JSONObject();
        try{
            json.put("first", first);
            json.put("last", last);
            json.put("city", city);
            json.put("state", state);
            json.put("usertype", userType);
            json.put("email",email);
            json.put("username", username);
            json.put("password", password);
            json.put("bio", bio);
            json.put("website",website);
            json.put("businessname", businessName);
            json.put("etsy", etsy);
            json.put("facebook", facebook);
            json.put("instagram", instagram);
            json.put("phone", phone);
        }
            catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    
    public static User parseJson(JSONObject json){
        try {
            userType = json.getJSONObject("").getInt("usertype");
            username = json.getJSONObject("").getString("username");
            email = json.getJSONObject("").getString("email");
            password = json.getJSONObject("").getString("password");
            first = json.getJSONObject("").getString("first");
            last = json.getJSONObject("").getString("last");
            city = json.getJSONObject("").getString("city");
            state = json.getJSONObject("").getString("state");
            //imageString = json.getJSONObject("").getString("imageString");
            bio = json.getJSONObject("").getString("bio");
            website = json.getJSONObject("").getString("website");
            etsy = json.getJSONObject("").getString("etsy");
            facebook = json.getJSONObject("").getString("facebook");
            instagram = json.getJSONObject("").getString("instagram");
            phone = json.getJSONObject("").getString("phone");
            dateJoined = json.getJSONObject("").getString("dateJoined");
            businessName = json.getJSONObject("").getString("businessName");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        User user = new User(userType, username, email, password,  first,
                last, city, state, imageString, bio, website, etsy, facebook,
                instagram, phone, dateJoined, businessName);
       return  user;
    }
}
