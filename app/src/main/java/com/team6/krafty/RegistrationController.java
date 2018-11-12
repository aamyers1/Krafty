package com.team6.krafty;

import android.content.Context;
import android.widget.Toast;

public class RegistrationController {
    private boolean isValid = false;
    private String returnString  = "";


    public boolean createNewKrafter(int userType, String username, String email, String password,
                                 String first, String last, String city, String state, String imageString,
                                 String bio, String website, String etsy, String facebook,
                                 String instagram, String businessName, Context context){
        final User newUser = new User( userType, username, email, password,
                first, last, city, state,imageString,
                bio,website, etsy, facebook,
                instagram, businessName);
        if(!checkUsername(newUser)){
            Toast.makeText(context, "Username Taken", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!checkEmail(newUser)){
            Toast.makeText(context,"Email In Use",Toast.LENGTH_SHORT).show();
            return false;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                returnString = dbManager.createUser(newUser);
            }
        });
        t.start();
        try{
            t.join();
            if(returnString.equals("Registration successful!")){
                isValid = true;
                Toast.makeText(context, returnString, Toast.LENGTH_LONG).show();
                return true;

            }
            else{
                Toast.makeText(context, returnString, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
    }



    private boolean checkUsername(final User user){
        final DBManager dbManager = new DBManager();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isValid = dbManager.checkUsername(user.getUsername());
            }
        });
        t.start();
        try{
            t.join();
            if(isValid){
                isValid = false;
                return  true;
            }
            else{
                return  false;
            }

        }
        catch(java.lang.InterruptedException e){
            return false;
        }
    }

    public boolean checkEmail(User user){
        return true;
    }
}
