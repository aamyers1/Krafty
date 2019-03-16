package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

public class Product {

    String name, description, image;
    int quantity, id;
    HashMap<Integer, Integer> Materials;
    float price;

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    Bitmap bmp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Integer, Integer> getMaterials() {
        return Materials;
    }

    public void setMaterials(HashMap<Integer, Integer> materials) {
        Materials = materials;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void parseJSON(JSONObject json){
        try {
            this.name = json.getString("name");
            this.description = json.getString("description");
        }
        catch(Exception e){

        }

    }

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
