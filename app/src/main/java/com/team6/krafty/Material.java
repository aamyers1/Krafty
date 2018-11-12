package com.team6.krafty;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Material {
    private String name, image, purchased;
    private  int quantity;
    private  double price;

    Material(String name, String image, int quantity,double price){
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
    }

    public String createJson(){
       String jsonMaterial = "name=" + name + "&image=" + image + "purchased=" + purchased +
                "&quantity=" + quantity + "&price=" + price;
       return jsonMaterial;
    }

    public Material parseJson(JSONObject json){
        try{
            this.name = json.getJSONObject("").getString("name");
            this.image = json.getJSONObject("").getString("image");
            this.quantity = json.getJSONObject("").getInt("quantity");
            this.price = json.getJSONObject("").getDouble("price");
            this.purchased = json.getJSONObject("").getString("purchased");
        }
        catch (Exception e){

        }
        return  this;
    }
}
