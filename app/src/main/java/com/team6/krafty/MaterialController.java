package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;


public class MaterialController {

    private static DBManager dbManager = DBManager.getInstance();
    private static boolean valid;
    private static String message;
    private static String token;
    private static String response = "";

    //adds a material to database
    public boolean addMaterial(final String name,final String image,final  String quantity, final String price,final  String location, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        valid = true;
        token = SessionManager.getToken(context);
        //parse out numeric values
        int quant = Integer.parseInt(quantity);
        double dPrice = Double.parseDouble(price);
        final Material material = new Material(name, image, location, quant, dPrice);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to add material to database
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.createMaterial(material, token);
                    Inventory.addMaterial(material);
                }
                catch(KraftyRuntimeException e){
                    message = e.getMessage();
                    valid = false;
                }

            }
        });
        t.start();
        try {
            t.join();
        }
        catch(Exception e){
           valid = false;
           message = "Create Failed!";
        }
        if(valid) {
            Toast.makeText(context, "Creation Success!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    //adds a material to database
    public boolean modifyMaterial(final Material material, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to add material to database
        valid = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.modifyMaterial(material, token);
                }catch (KraftyRuntimeException e){
                    message = e.getMessage();
                    valid = false;
                }
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
        }
        catch(Exception e){
            valid = false;
            message = "Update failed!";
        }
        if(valid) {
            Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return valid;
    }


    //deletes a material
    public boolean deleteMaterial(final int id, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        valid = true;
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete material
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.deleteMaterial(id, token);
                }catch (KraftyRuntimeException e){
                    message = e.getMessage();
                    valid = false;
                }
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
        }
        catch(Exception e){
            valid = false;
            message = "Delete Failed!";
        }
        if(valid) {
            Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return valid;
    }


    //gets all materials for a given user based on token received on login
    public static void getMaterials(final String token){
        //ask DB for response
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.getMaterial(token);
                } catch (KraftyRuntimeException e){
                    Log.d("GET MATs ERROR", "mat error " +  e);
                }
            }
        });
        t.start();
        try{
            //wait for thread
            t.join();
            //send to parseMats method to get each material from response
        }
        catch(Exception e){
            Log.d("ERROR GET MATS", e.getMessage());
        }
    }



}

