package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class RegistrationController {
    private boolean isValidUsername = false;
    private boolean isValidEmail = false;
    private String returnString  = "";
    private static DBManager dbManager = DBManager.getInstance();

    //sets up a new user of the Krafter type, attempts to create the krafter in the database.
    //A message is displayed in a toast on return of either error or success.
    //return true if successful and false if unsuccessful
    //will updaate to work for all users
    public boolean createNewUser(int userType, String username, String email, String password,
                                 String first, String last, String city, String state, String imageString,
                                 String bio, String website, String etsy, String facebook,
                                 String instagram, String businessName, Context context){
        //create new user with the input fields.
        final User newUser = new User( userType, username, email, password,
                first, last, city, state,imageString,
                bio,website, etsy, facebook,
                instagram, businessName);
        //check that the username is unique
        if(!checkUsername(newUser)){
            Toast.makeText(context, "Username Taken", Toast.LENGTH_SHORT).show();
            return false;
        }
        //check that the email is unique
        if(!checkEmail(newUser)){
            Toast.makeText(context,"Email In Use",Toast.LENGTH_SHORT).show();
            return false;
        }
        //attempt to create the new user
        //ALL NETWORKING MUST BE RUN IN A DIFFERENT THREAD THAN MAIN
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //create the db manager and try to create the user
                try {
                    returnString = dbManager.createUser(newUser);
                }catch(KraftyRuntimeException e){
                    Log.d("CREATE USER ERROR", "user error " +  e);
                    returnString = "";
                }
            }
        });
        t.start();
        try{
            t.join();
            //if the registration is successful, return true and toast the message
            if(returnString.contains("Registered Successfully")){
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_LONG).show();
                return true;

            }
            //else, display the error and return false
            else{
                Toast.makeText(context, returnString, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //all other exceptions return a false
        catch(Exception e){
            return false;
        }
    }

    //updates user profile
    public boolean updateProfile(final User user, final Context context){
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    dbManager.updateProfile(user,token);
                }
                catch(KraftyRuntimeException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
        try{
            t.join();
            Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
            return true;
        }
        catch (Exception e){
            Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    //checks if a username is already taken based on a User object
    private boolean checkUsername(final User user){
        //NETWORKING MUST BE DONE IN A DIFFERENT THREAD THAN MAIN
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isValidUsername = dbManager.checkUsername(user.getUsername());
                } catch (KraftyRuntimeException e){
                    Log.d("CHECK USER ERROR", "reg error " +  e);
                    isValidUsername = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            if(isValidUsername){
                isValidUsername = false;
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

    //ensure an email is only used one time!
    private boolean checkEmail(final User user){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isValidEmail = dbManager.checkEmail(user.getEmail());
                } catch (KraftyRuntimeException e){
                    Log.d("CHECK EMAIL ERROR", "reg error " +  e);
                    isValidEmail = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            if(isValidEmail){
                isValidEmail = false;
                return true;
            }
            else{
                return false;
            }
        }
        catch(java.lang.InterruptedException e){
            return false;
        }
    }
}
