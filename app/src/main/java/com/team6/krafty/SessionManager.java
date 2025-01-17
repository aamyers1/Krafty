package com.team6.krafty;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class SessionManager {
    private static DBManager dbManager = DBManager.getInstance();
    private static String token;
    private static User profile;
    private static User external;

    public static void setUser(User user){
        profile = user;
    }

    public static User getUser(){
        return profile;
    }

    public static User getExternalUser(Context context, final String username){
        final String tok = getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    external = dbManager.getUser(tok, username);
                }
                catch(Exception e){
                    Log.d("ERRRRR", e.getMessage());
                }
            }
        });
        t.start();
        try{
            t.join();
            return external;
        }
        catch(Exception j){
            Log.d("ERR", j.getMessage());
        }
        return null;
    }

    //gets the current token
    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sp.getString("token", "0");
    }

    public static int getUserType(Context context){
        SharedPreferences sp = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sp.getInt("userType", 2);
    }

    //checks if a user is logged in;
    public static boolean isLoggedIn(Context context){
        if (dbManager.isOnline()){
            if(!getToken(context).equals("0")){
                return true;
            }
            return false;
        }
        else
            if(logout(context))
                Toast.makeText(context, "Network connection error!", Toast.LENGTH_SHORT).show();
        return  false;
    }

    //logs the user in using the username and password provided
    public static boolean login(final Context context, final String username, final String password){

        //NETWORKING MUST BE RUN IN A SEPERATE THREAD
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    token = dbManager.login(username, password);
                } catch (KraftyRuntimeException e){
                    Log.d("LOGIN ERROR", "login error " +  e);
                }
            }
        });
        t.start();
        try{
            //wait for thread to finish
            t.join();
            //todo: clean this up
            if(token != null && !token.equals("") && !token.contains("error")){
                SharedPreferences sp = context.getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", username);
                edit.putString("token", token);
                edit.apply();
                return true;
            }
        }
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    //logout at some point will have to destroy token in db, for now just does it in the prefs
    public static boolean logout(Context context){
        SharedPreferences sp = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove("token");
        ed.apply();
        ed.commit();
        return true;
    }

    public static String getUsername(Context context){
        SharedPreferences sp = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sp.getString("username", "none");
    }




}
