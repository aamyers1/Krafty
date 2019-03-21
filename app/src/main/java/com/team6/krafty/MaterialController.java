package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;


public class MaterialController {

    private static DBManager dbManager = DBManager.getInstance();
    private boolean isCreated;
    private boolean isUpdated;
    private boolean isDeleted;
    private static String token;
    private static String response = "";

    //adds a material to database
    public boolean addMaterial(final String name,final String image,final  String quantity, final String price,final  String location, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        isCreated = true;
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
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    isCreated = false;
                }

            }
        });
        t.start();
        try {
            t.join();
        }
        catch(Exception e){
           isCreated = false;
        }
        Toast.makeText(context, "Creation Success!", Toast.LENGTH_SHORT).show();
        return isCreated;
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
                isUpdated = true;
                try {
                    dbManager.modifyMaterial(material, token);
                }catch (KraftyRuntimeException e){
                    Log.d("UPDATE MAT ERROR", "mat error " +  e);
                    isUpdated = false;
                }
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isUpdated){
                Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Update Failure!", Toast.LENGTH_SHORT).show();
            }
            return isUpdated;
        }
        catch(Exception e){
            Toast.makeText(context, "Update Failure!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    //deletes a material
    public boolean deleteMaterial(final int id, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete material
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isDeleted = true;
                try {
                    dbManager.deleteMaterial(id, token);
                }catch (KraftyRuntimeException e){
                    Log.d("DELETE MAT ERROR", "mat error " +  e);
                    isDeleted = false;
                }
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isDeleted){
                Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Delete Failure!", Toast.LENGTH_SHORT).show();
                Log.d("DELETE MAT ERROR", "mat error" +  id);
            }
            return isDeleted;
        }
        catch(Exception e){
            Log.d("DELETE MAT ERROR", "mat error" +  e.getMessage());
            return false;
        }
    }


    //gets all materials for a given user based on token received on login
    public static boolean getMaterials(final String token){
        //get user token from sessionManager
        //ask DB for response
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    response = dbManager.getMaterial(token);
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
           parseForMats(response);
           //return true only if some material has been returned
            if(Inventory.getCount() > 0){
                return true;
            }else return false;
        }

        catch(Exception e){
            Log.d("ERROR GET MATS", e.getMessage());
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

