package com.team6.krafty;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;


public class MaterialController {

    private boolean isCreated;
    private String token;
    private String response = "";

    //adds a material to database
    public boolean addMaterial(final String name,final String image,final  String quantity, final String price,final  String location, final Context context){
        //first get the user token so the db knows who the material will belong to
        SharedPreferences sp = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        token = sp.getString("token", "0");
        int quant = Integer.parseInt(quantity);
        double dPrice = Double.parseDouble(price);
        final Material material = new Material(name, image, location, quant, dPrice);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isCreated = dbManager.createMaterial(material, token);
            }
        });
        t.start();
        try {
            t.join();
            if(isCreated){
                Toast.makeText(context, "Creation Success!", Toast.LENGTH_SHORT).show();
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

    //gets all materials for a given user based on token recieved on login
    public void getMaterials(Context context){
        Material.allMats.clear();
        token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                response  = dbManager.getMaterial(token);
            }
        });
        t.start();
        try{
            t.join();
            JSONObject obj = new JSONObject(response);
            JSONArray jArr = obj.getJSONArray("result");

            for(int i = 0; i < jArr.length(); i++){
                JSONObject jobj = jArr.getJSONObject(i);
                try{
                    Material a = new Material();
                    a.parseJson(jobj);
                    Material.allMats.add(a);
                }
                catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

