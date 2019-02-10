package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

public class Material {
    //TODO:After imageString is parsed we may want to clear it just to dump the data and save space
    //private data fields for material
    private int id;
    private String name, image, purchased;
    private  int quantity;
    private  double price;
    Bitmap bmp;

    //Blank constructor used when information needs to be parsed from json object only
    Material(){
    }

    //regular constructor to construct a material from user input
    Material(String name, String image, String purchased, int quantity,double price){
        this.id = -1;
        this.name = name;
        this.image = image;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                parseBitmap();
            }
        });
        t.start();
        this.quantity = quantity;
        this.price = price;
        this.purchased = purchased;
    }

    //creates a simple json string. The default json generator does not work properly with our API.
    public String createJson() {
        return "name=" + name + "&image=" + image + "&purchased=" + purchased +
                "&qty=" + quantity + "&price=" + price;
    }

    public String getModifyJson() {
        return "id=" + id + "&name=" + name + "&image=" + image + "&purchased=" + purchased +
                "&qty=" + quantity + "&price=" + price;
    }

    public String getDeleteJson() {
        return "id=" + id;
    }

    //parses data from JSON object.
    //TODO: Add some defaults in case of error?
    public void parseJson(JSONObject json){
        try{
            this.id = json.getInt("id");
            this.name = json.getString("name");
            this.image = json.getString("image");
            this.quantity = json.getInt("qty");
            this.price = json.getDouble("price");
            this.purchased = json.getString("purchased");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    parseBitmap();
                }
            });
            t.start();
        }
        catch (Exception e){
            Log.d("ERROR PARSING MATERIAL", e.getMessage());
        }
    }

    //Getters
    public int getId(){ return this.id; }

    public String getName(){
        return this.name;
    }

    public String getImage(){
        return image;
    }

    public int getQuantity(){ return quantity;}

    public double getPrice(){return price;}

    public String getLocation(){return purchased;}

    public String getPurchased(){return purchased;}

    public Bitmap getBmp(){return bmp;}

    //Setters
    public void setName(String name){
        this.name = name;
    }

    public void setImage(String image){
        this.image = image;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                parseBitmap();
            }
        });
        t.start();
    }

    public void setPurchased(String purchased){
        this.purchased = purchased;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public void setID(int id){
        this.id = id;
    }

    //Override to string for the adapters
    @Override
    public String toString(){
        return name;
    }

    //parse the bitmap in this class
    private void parseBitmap(){
        if(!this.image.equals("null") && !this.image.equals("no image") && !this.image.equals("") ) {
            try{
                byte[] encodeByte = Base64.decode(image.replace("<", "+"), Base64.DEFAULT);
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

