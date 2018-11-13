package com.team6.krafty;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Material {
    private String name, image, purchased;
    private  int quantity;
    private  double price;
    static ArrayList<Material> allMats = new ArrayList<>();

    Material(){

    }

    Material(String name, String image, String purchased, int quantity,double price){
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.purchased = purchased;
    }

    public String createJson() {
        return "name=" + name + "&image=" + image + "&purchased=" + purchased +
                "&qty=" + quantity + "&price=" + price;
    }



    public Material parseJson(JSONObject json){
        try{
            this.name = json.getString("name");
            this.image = json.getString("image");
            this.quantity = json.getInt("qty");
            this.price = json.getDouble("price");
            this.purchased = json.getString("purchased");
        }
        catch (Exception e){

        }
        return  this;
    }

    public String getName(){
        return this.name;
    }

    public String getPurchased(){
        return purchased;
    }

    @Override
    public String toString(){
        return name;
    }
}
