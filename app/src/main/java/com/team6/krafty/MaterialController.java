package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Material controller class acts as an intermediate between the GUI and business objects/database access
 * for operations relating to Material objects.
 */
public class MaterialController {

    private static DBManager dbManager = DBManager.getInstance();
    private static boolean valid;
    private static String message;
    private static String token;

    /**
     * Adds a user material to the database
     * @param name String name of the material
     * @param image String base 64 encoding of the material image
     * @param quantity String quantity owned of material
     * @param price String price of the material
     * @param location String location purchased
     * @param context Context to link back to the original context of the caller
     * @return boolean of success or failure
     */
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

    /**
     * Modifies an existing material in the database
     * @param material Material object to be updated
     * @param context Context of the calling object
     * @return Boolean indicating success or failure of the operation
     */
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


    /**
     * Deletes a material from the database
     * @param id The integer id of the material to be deleted
     * @param context Context of the calling object
     * @return Boolean describing success or failure of the operation
     */
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


    /**
     * Retrieves all materials for the specified user from the database
     * @param token Token received upon login used for authentication in the database
     */
    public static void getMaterials(final String token){
        //ask DB for response
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.getMaterial(token);
                } catch (KraftyRuntimeException e){
                    Log.d("GET MATS ERROR", "mat error " +  e);
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

