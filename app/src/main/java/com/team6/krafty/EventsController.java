package com.team6.krafty;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventsController {

    ArrayList<Event> eventsList;

    public EventsController(){

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

    public Event getSpecificEvent(int id, Context context){
        DBManager dbManager = new DBManager();
        JSONObject json = dbManager.getSpecificEvent(id, SessionManager.getToken(context));
        try{
            Event event = new Event();
            event.parseJson(json);
            return event;
        }
        catch(Exception e){
            Log.d("ECNTR JSONARRAY", e.getMessage());
            return null;
        }
    }
}
