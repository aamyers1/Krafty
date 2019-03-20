package com.team6.krafty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.se.omapi.Session;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class EventsController {

    ArrayList<Event> eventsList;
    private static String token;
    private boolean isDeleted;
    private boolean isCreated;
    private boolean isUpdated;

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
        if (eventsList == null){
            eventsList = new ArrayList<Event>();
        }
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
        DBManager dbManager = new DBManager(new DjangoAccess());
        eventsList = dbManager.getAllEvents(SessionManager.getToken(context));
//        try {
//            JSONArray jsonArr = json.getJSONArray("result");
//            for(int i = 0; i < jsonArr.length(); i ++){
//                Event newEvent = new Event();
//                newEvent.parseJson(jsonArr.getJSONObject(i));
//                //TODO: FILTER OUT EVENTS WHICH HAVE PASSED
//                eventsList.add(newEvent);
//
//            }
//        }
//        catch(Exception e){
//            Log.d("ECNTR JSONARRAY", e.getMessage());
//        }
    }

    public Event getSpecificEvent(final int id, final Context context){
        final Event[] event = {new Event()};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager(new DjangoAccess());
                event[0] = dbManager.getSpecificEvent(id, SessionManager.getToken(context));
            }
        });
        t.start();
        try {
            t.join();
            return event[0];
        } catch(Exception e) {
            Log.d("ECNTR JSONARRAY", e.getMessage());
        }
        return null;
    }

    public boolean deleteEvent(final int id, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete event
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager(new DjangoAccess());
                dbManager.deleteEvent(id, token);
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
                Log.d("DELETE EVENT ERROR", "event delete error" +  id);
                return false;
            }
        }
        catch(Exception e){
            Log.d("DELETE EVENT ERROR", "event delete error" +  e.getMessage());
            return false;
        }
    }

    public boolean createEvent(final Event event, final Context context){
        //first get the user token so the db knows who the material will belong to (assume exists)
        token = SessionManager.getToken(context);
        //NETWORKING MUST BE DONE IN A SEPARATE THREAD. Attempts to delete material
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager(new DjangoAccess());
                dbManager.createEvent(event, token);
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isCreated){
                Toast.makeText(context, "Create Success!", Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                Toast.makeText(context, "Create Failure!", Toast.LENGTH_SHORT).show();
                Log.d("CREATE EVENT ERROR", "event error" +  event.getID());
                return false;
            }
        }
        catch(Exception e){
            Log.d("CREATE EVENT ERROR", "event error" +  e.getMessage());
            return false;
        }
    }

    public boolean updateEvent(Event event, int id,final Context context){
        String json = event.createJson();
        final String request = json + "&id=" + id;
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager(new DjangoAccess());
                try {
                    dbManager.updateEvent(request, token);
                }
                catch(KraftyRuntimeException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
        try{
            t.join();
            return true;
        }
        catch(Exception e){ ;
            Log.d("UPDATEEVENTM", e.getMessage());
        }
        return false;
    }

    public Bitmap parseEventImage(String encodedImage){
        Bitmap bmp = null;
        if(!encodedImage.equals("null") && !encodedImage.equals("no image") && !encodedImage.equals("") ) {
            try{
                byte[] encodeByte = Base64.decode(encodedImage.replace("<", "+"), Base64.DEFAULT);
                bmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            } catch (IllegalArgumentException e){

                Log.d("EVENT IMAGE ERROR", "bad image base-64");
            }
        }
        return bmp;
    }
}
