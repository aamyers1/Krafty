package com.team6.krafty;

import android.content.Context;
import android.widget.Toast;

public class RegistrationController {
    boolean isValid = false;
    public boolean createNewKrafter(int userType, String username, String email, String password,
                                 String first, String last, String city, String state, String imageString,
                                 String bio, String website, String etsy, String facebook,
                                 String instagram, String businessName, Context context){
        User newUser = new User( userType, username, email, password,
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
        return false;
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
        return false;
    }
}
