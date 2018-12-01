package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;


public class MaterialController {

    private boolean isCreated;
    private boolean isUpdated;
    private boolean isDeleted;
    private static String token;
    private static String response = "";

    //adds a material to database
    public boolean addMaterial(final String name,final String image,final  String quantity, final String price,final  String location, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //parse out numeric values
        //TODO:Verify these values are numeric
        int quant = Integer.parseInt(quantity);
        double dPrice = Double.parseDouble(price);
        final Material material = new Material(name, image, location, quant, dPrice);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to add material to database
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isCreated = dbManager.createMaterial(material, token);
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isCreated){
                Toast.makeText(context, "Creation Success!", Toast.LENGTH_SHORT).show();
                Inventory.addMaterial(material);
                return true;
            }
            else{
                Toast.makeText(context, "Creation Failure!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
    }

    //adds a material to database
    public boolean modifyMaterial(final Material material, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //parse out numeric values
        //TODO:Verify these values are numeric
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to add material to database
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isUpdated = dbManager.modifyMaterial(material, token);
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isUpdated){
                Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                Toast.makeText(context, "Update Failure!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
    }
    //deletes a material
    //TODO:VERIFY USER WANTS TO DELETE BEFORE DOING IT LOL ITS EASY TO ACCIDENTALLY PRESS THE BUTTON : USE A DIALOG
    public boolean deleteMaterial(final int id, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete material
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isDeleted = dbManager.deleteMaterial(id, token);
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isDeleted){
                Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
                Inventory.removeMaterial(id);
                return true;
            }
            else{
                Toast.makeText(context, "Delete Failure!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
    }


    //gets all materials for a given user based on token received on login
    public static boolean getMaterials(Context context){
        //get user token from sessionManager
        token = SessionManager.getToken(context);
        //ask DB for response
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                response  = dbManager.getMaterial(token);
            }
        });
        t.start();
        try{
            //wait for thread
            t.join();
            //send to parseMats method to get each material from response
           parseForMats(response);
           //return true only if some material has been returned
            if(Inventory.getCount() > 0){
                return true;
            }else return false;
        }
        //show user exception message
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private static void parseForMats(String jsonObject){
        try {
            //first attempt to get the JSONObject
            JSONObject primaryObject = new JSONObject(jsonObject);
            //then get the array of objects within the primaryObject
            JSONArray jsonArray = primaryObject.getJSONArray("result");
            //iterate through each array value
            for(int i = 0; i < jsonArray.length(); i++){
                //get the object, create a new material with it, and add to inventory
                JSONObject singleMat = jsonArray.getJSONObject(i);
                Material newMat = new Material();
                newMat.parseJson(singleMat);
                Inventory.addMaterial(newMat);
            }
        }
        catch(Exception e){
            Log.d("PARSE MATERIAL ERROR", e.getMessage());
        }
    }
}

