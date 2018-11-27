package com.team6.krafty;

import org.json.JSONObject;

import java.util.ArrayList;

public class Material {

    //private data fields for material
    //TODO: need the id of the material for modification/deletion purposes
    private String name, image, purchased;
    private  int quantity;
    private  double price;

    //Blank constructor used when information needs to be parsed from json object only
    Material(){
    }

    //regular constructor to construct a material from user input
    Material(String name, String image, String purchased, int quantity,double price){
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.purchased = purchased;
    }

    //creates a simple json string. The default json generator does not work properly with our API.
    public String createJson() {
        return "name=" + name + "&image=" + image + "&purchased=" + purchased +
                "&qty=" + quantity + "&price=" + price;
    }

    //parses data from JSON object.
    //TODO: Add some defaults in case of error?
    public void parseJson(JSONObject json){
        try{
            this.name = json.getString("name");
            this.image = json.getString("image");
            this.quantity = json.getInt("qty");
            this.price = json.getDouble("price");
            this.purchased = json.getString("purchased");
        }
        catch (Exception e){

        }
    }

    //Getters
    public String getName(){
        return this.name;
    }

    public String getImage(){
        return image;
    }

    public int getQuantity(){ return quantity;}

    public double getPrice(){return price;}

    public String getLocation(){return purchased;}

    //Override to string for the adapters
    @Override
    public String toString(){
        return name;
    }

}

