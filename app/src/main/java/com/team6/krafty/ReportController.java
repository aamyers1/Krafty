package com.team6.krafty;

import android.content.Context;
import android.util.Log;

/**
 * Provides a controller for reporting functionalities
 */
public class ReportController {

    static String message;
    static boolean isCreated;

    /**
     * Method that generates a short report and sends to the database
     * @param context Used for retrieving user token
     * @param type  e for event or u for user
     * @param report User text detailing incident
     * @param id Id of either the user or the event
     */
    public static boolean createReport(Context context, final String type,final String report, final int id){
        message = "";
        isCreated = true;
        final DBManager dbManager = DBManager.getInstance();
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.createReport(token, report, type, id);
                }
                catch (KraftyRuntimeException k){
                    isCreated = false;
                    message = k.getMessage();
                }
            }
        });
        t.start();
        try{
            t.join();
        }
        catch (Exception e){
            Log.d("REPO ERROR", e.getMessage());
            isCreated = false;
        }
        return isCreated;

    }
}
