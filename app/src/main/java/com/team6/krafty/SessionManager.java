package com.team6.krafty;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class SessionManager {
    private static DBManager dbManager = DBManager.getInstance();
    private static String token;

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
        if(!getToken(context).equals("0")){
            return true;
        }
        return false;
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




}
