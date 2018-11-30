package com.team6.krafty;

import org.json.JSONObject;

import java.util.ArrayList;

public class Material {

    //private data fields for material
    private int id;
    private String name, image, purchased;
    private  int quantity;
    private  double price;

    //Blank constructor used when information needs to be parsed from json object only
    Material(){
    }

    //regular constructor to construct a material from user input
    Material(String name, String image, String purchased, int quantity,double price){
        this.id = -1;
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.purchased = purchased;
    }
    Material(int id, String name, String image, String purchased, int quantity,double price){
        this.id = id;
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.purchased = purchased;
    }

    public Material(int id) {
        this.id = id;
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
        }
        catch (Exception e){

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

    //Override to string for the adapters
    @Override
    public String toString(){
        return name;
    }

}

