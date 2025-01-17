package com.team6.krafty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Product {

    String name, description, image;
    int quantity, id;
    //form of: material id, quantity
    HashMap<Integer, Integer> Materials;
    float price;
    String creator;
    Bitmap bmp;

    public Product(){Materials = new HashMap<>();}

    public Bitmap getBmp() {
        return bmp;
    }

    public Product(String name, String description, String image, int quantity, HashMap<Integer, Integer> mats, float price, String creator){
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.Materials = mats;
        this.price = price;
        this.image = image;
        this.creator = creator;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                parseBitmap();
            }
        });
        t.start();
    }

    public Product(String name, String description, String image){
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }


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

    public String getCreator(){return creator;}

    public void parseJSON(JSONObject json){
        try {
            this.name = json.getString("name");
            this.description = json.getString("description");
            this.id = json.getInt("id");
            this.quantity = json.getInt("qty");
            this.price = (float)json.getDouble("price");
            JSONArray jsonA = json.getJSONArray("materials");
            getMaterialsJson(jsonA);
            this.creator = json.getString("creator");
            this.image = json.getString("image");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    parseBitmap();
                }
            });
            t.run();

        }
        catch(Exception e){

        }

    }

    private void getMaterialsJson(JSONArray json){
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject p = json.getJSONObject(i);
                String s = p.getString("mid");
                s = s.replaceAll("[^0-9]", "");
                int id = Integer.parseInt(s);

                Materials.put(id, p.getInt("qty"));
            }
        }
        catch(Exception e){
            Log.d("ERROR PM", json.toString());
            Log.d("ERROR PM", e.getMessage());
        }
    }

    private String convertMatsToJson(){
        StringBuilder sb = new StringBuilder();
        if(Materials != null && Materials.size() > 0) {
            for (int i : Materials.keySet()) {
                sb.append("{\"id\":" + i + ",\"qtyrequired\":" + Materials.get(i) + "},");
            }
            String s = sb.toString();
            s = s.substring(0, s.lastIndexOf(","));
            Log.d("CONVERTMATTOJS", s);
            return s;
        }
        else {
            return "";
        }
    }

    public String getJson(){
        return "name=" +name + "&description=" + description + "&qty=" + quantity + " &price=" +
                price + "&image=" + image + "&materials=[" + convertMatsToJson() + "]";
    }

    private void parseBitmap(){
        if(!(image == null) && !this.image.equals("no image") && !this.image.equals("") ) {
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
