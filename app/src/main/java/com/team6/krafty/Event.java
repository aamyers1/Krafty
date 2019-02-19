package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

public class Event implements Schedulable {
    private Boolean power, wifi, outdoors,tables,food;
    private String creator, city, street, state, zipcode,startTime, endTime, startDate, endDate, name, imgString, description;
    private int id, vendorSpots, takenSpots;
    private double longitude, latitude;
    private Bitmap bmp;


    public void parseJson(JSONObject json){
        try {
            Log.d("json", json.toString());
            longitude = json.getDouble("longitude");
            latitude = json.getDouble("latitude");
            name = json.getString("name");
            vendorSpots = json.getInt("vendorspots");
            takenSpots = json.getInt("takenspots");
            creator = json.getString("creator");
            city = json.getString("city");
            street = json.getString("street");
            state = json.getString("state");
            zipcode = json.getString("zipcode");
            String temp = json.getString("start");
            startTime = temp.substring(0, temp.indexOf(" "));
            String temp2 = json.getString("end");
            endTime = temp2.substring(0,temp2.indexOf(" "));
            startDate = temp.substring(temp.indexOf(" ") + 1);
            temp = json.getString("end");
            endTime = temp.substring(0, temp.indexOf(" "));
            endDate = temp.substring(temp.indexOf(" "));
            description = json.getString("description");
            wifi = json.getBoolean("wifi");
            power = json.getBoolean("power");
            outdoors = json.getBoolean("outdoors");
            tables = json.getBoolean("tables");
            food = json.getBoolean("food");
            id = json.getInt("id");
            //imgString = json.getString("image");
        }
        catch(Exception e){
            Log.d("Error event parse", "Event could not be parsed from json");
            e.printStackTrace();
        }
    }


    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    @Override
    public String getDate() {
        return startDate;
    }

    @Override
    public String getTime() {
        return startTime;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public int getID() {
        return id;
    }

    public String getDescription(){return description;}

    public String getAddress(){
        String str = street + "\n" + city + ", " + state + ", " + zipcode;
        return  str;
    }

    public int getVendorSpots() { return vendorSpots; }

    public int getTakenSpots() { return takenSpots; }

    public String getEndTime() { return  endTime;}

    public String getCreator(){ return  creator;}

    public boolean getFood(){ return  food;}

    public boolean getOutDoors(){return outdoors;}

    public Boolean getPower() { return power; }

    public Boolean getTables() { return tables; }

    public Boolean getWifi() { return wifi; }

    public String getEndDate(){return endDate;}

    public String getImgString() {return  imgString;}

    public Bitmap getBmp(){return bmp;}

    private void parseBitmap(){
        if(!this.imgString.equals("null") && !this.imgString.equals("no image") && !this.imgString.equals("") ) {
            try{
                byte[] encodeByte = Base64.decode(imgString.replace("<", "+"), Base64.DEFAULT);
                bmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            } catch (IllegalArgumentException e){

                Log.d("MATERIAL IMAGE ERROR", "bad image base-64" +  id);
                bmp = null;
            }
        }
        else{
            bmp = null;
        }
    }

}
