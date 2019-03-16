package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductController {

    private boolean isCreated;
    private boolean isUpdated;
    private boolean isDeleted;
    private static String token;
    private static String response = "";

    //adds a product to database
    public boolean addProduct(final String name,final String image,final  String quantity, final String price,final  String location, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //parse out numeric values
        //TODO:Verify these values are numeric
        int quant = Integer.parseInt(quantity);
        double dPrice = Double.parseDouble(price);
        //TODO enter constructer parms
        final Product product = new Product();
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to add material to database
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isCreated = dbManager.createProduct(product, token);
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isCreated){
                Toast.makeText(context, "Creation Success!", Toast.LENGTH_SHORT).show();
                Inventory.addProduct(product);
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

    //adds a product to database
    public boolean modifyProduct(final Product product, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //parse out numeric values
        //TODO:Verify these values are numeric
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to add product to database
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isUpdated = dbManager.modifyProduct(product, token);
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
    //deletes a product
    public boolean deleteProduct(final int id, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete product
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isDeleted = dbManager.deleteProduct(id, token);
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isDeleted){
                Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                Toast.makeText(context, "Delete Failure!", Toast.LENGTH_SHORT).show();
                Log.d("DELETE PRD ERROR", "prd error" +  id);
                return false;
            }
        }
        catch(Exception e){
            Log.d("DELETE PRD ERROR", "prd error" +  e.getMessage());
            return false;
        }
    }


    //gets all products for a given user based on token received on login
    public static boolean getProducts(final String token){
        //get user token from sessionManager
        //ask DB for response
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                response  = dbManager.getProduct(token);
            }
        });
        t.start();
        try{
            //wait for thread
            t.join();
            //send to parseProducts method to get each product from response
            parseForProducts(response);
            //return true only if some material has been returned
            if(Inventory.getCount() > 0){
                return true;
            }else return false;
        }

        catch(Exception e){
            Log.d("ERROR GET PRODUCTS", e.getMessage());
            return false;
        }
    }


    private static void parseForProducts(String jsonObject){
        try {
            //first attempt to get the JSONObject
            JSONObject primaryObject = new JSONObject(jsonObject);
            //then get the array of objects within the primaryObject
            JSONArray jsonArray = primaryObject.getJSONArray("result");
            //iterate through each array value
            for(int i = 0; i < jsonArray.length(); i++){
                //get the object, create a new material with it, and add to inventory
                JSONObject singleProduct = jsonArray.getJSONObject(i);
                Product newProduct = new Product();
                newProduct.parseJson(singleProduct);
                Inventory.addProduct(newProduct);
            }
        }
        catch(Exception e){
            Log.d("PARSE PRODUCT ERROR", e.getMessage());
        }
    }
}
