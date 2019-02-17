package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventsController {

    ArrayList<Event> eventsList;
    private static String token;
    private boolean isDeleted;

    public EventsController(){

    }

    public String[] getDescriptions(){
        String[] descriptions = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            descriptions[i] = eventsList.get(i).getDescription();
        }
        return descriptions;
    }
    public int[] getIdentities(){
        int[] identities = new int[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            identities[i] = eventsList.get(i).getID();
        }
        return identities;
    }
    public LatLng[] getltlng(){
        LatLng[] ltLng = new LatLng[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            ltLng[i] = new LatLng(eventsList.get(i).getLatitude(), eventsList.get(i).getLongitude());

        }
        return ltLng;
    }

    public String[] getNames(){
        String[] names = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i ++){
            names[i] = eventsList.get(i).getTitle();
        }
        return names;
    }

    public void fetchEvents(final Context context){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                retrieve(context);
            }
        });
        t.start();
        try{
            t.join();
        }
        catch (Exception e){

        }
    }

    public void retrieve(Context context){
        eventsList = new ArrayList<>();
        DBManager dbManager = new DBManager();
        JSONObject json = dbManager.getAllEvents(SessionManager.getToken(context));
        try {
            JSONArray jsonArr = json.getJSONArray("result");
            for(int i = 0; i < jsonArr.length(); i ++){
                Event newEvent = new Event();
                newEvent.parseJson(jsonArr.getJSONObject(i));
                eventsList.add(newEvent);
            }
        }
        catch(Exception e){
            Log.d("ECNTR JSONARRAY", e.getMessage());
        }
    }

    public Event getSpecificEvent(final int id, final Context context){
        final Event event = new Event();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                JSONObject json = dbManager.getSpecificEvent(id, SessionManager.getToken(context));
                try {
                    JSONArray jsonArray = json.getJSONArray("result");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    event.parseJson(jsonObject);
                    //JSONObject getJson = json.get("result");
                    //event.parseJson(getJson);
                } catch (Exception e) {
                    Log.d("json error", json.toString());
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try{
            t.join();

            return event;
        }
        catch(Exception e){
            Log.d("ECNTR JSONARRAY", e.getMessage());
            return null;
        }
    }

    public boolean deleteEvent(final int id, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete material
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                isDeleted = dbManager.deleteMaterial(id, token);
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
                Log.d("DELETE MAT ERROR", "mat error" +  id);
                return false;
            }
        }
        catch(Exception e){
            Log.d("DELETE MAT ERROR", "mat error" +  e.getMessage());
            return false;
        }
    }
}
