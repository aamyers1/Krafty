package com.team6.krafty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;


public class EventsController {

    ArrayList<Event> eventsList;
    private Event theEvent;
    private static String token;
    private boolean isDeleted;
    private boolean isCreated;
    private boolean isUpdated;
    private boolean isScheduled;
    private DBManager dbManager = DBManager.getInstance();

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
        eventsList = dbManager.getAllEvents(SessionManager.getToken(context));
    }

    public Event getSpecificEvent(final int id, final Context context){
        theEvent = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ECNTR JSONARRAY", String.valueOf(id));
                theEvent = dbManager.getSpecificEvent(id, SessionManager.getToken(context));
            }
        });
        t.start();
        try {
            t.join();
            return theEvent;
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
                try {
                    isDeleted = true;
                    dbManager.deleteEvent(id, token);
                } catch (KraftyRuntimeException e){
                    Log.d("DELETE EVENT ERROR", "event error" +  e.getMessage());
                    isDeleted = false;
                }
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if(isDeleted){
                Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(context, "Delete Failure!", Toast.LENGTH_SHORT).show();
                Log.d("DELETE EVENT ERROR", "event delete error" +  id);
            }

            return isDeleted;
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
                isCreated = true;
                try {
                    dbManager.createEvent(event, token);
                }catch (KraftyRuntimeException e ){
                    Log.d("CREATE EVENT ERROR", "error creating event" + e);
                    isCreated = false;
                }
            }
        });
        t.start();
        try {
            //wait for thread to finish
            t.join();
            //show user Message
            if (isCreated){
                Toast.makeText(context, "Create Success!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("CREATE EVENT ERROR", "event error");
                Toast.makeText(context, "Create Failure!", Toast.LENGTH_SHORT).show();
            }
            return isCreated;
        } catch(Exception e){
            Log.d("CREATE EVENT ERROR", "event error" +  e.getMessage());
            Toast.makeText(context, "Create Failure!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean updateEvent(Event event, int id,final Context context){
        String json = event.createJson();
        final String request = json + "&id=" + id;
        final String token = SessionManager.getToken(context);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isUpdated = true;
                try {
                    dbManager.updateEvent(request, token);
                }
                catch(KraftyRuntimeException e){
                    Log.d("UPDATE EVENT ERROR", "event error");
                    isUpdated = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            if (isUpdated){
                Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
            }else {
                Log.d("UPDATE EVENT ERROR", "event error");
                Toast.makeText(context, "Update Failure!", Toast.LENGTH_SHORT).show();
            }
            return isUpdated;
        }
        catch(Exception e){ ;
            Log.d("UPDATEEVENTM", e.getMessage());
        }
        return false;
    }

    public boolean scheduleForEvent(final Integer eventId, Context context){
        final String token = SessionManager.getToken(context);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isScheduled = true;
                try {
                    dbManager.scheduleForEvent(eventId, token);
                }
                catch(KraftyRuntimeException e){
                    Log.d("SCHEDULE EVENT ERROR", e.getMessage());
                    isScheduled = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            if (isScheduled){
                Toast.makeText(context, "Schedule Success!", Toast.LENGTH_SHORT).show();
            }else {
                Log.d("SCHEDULE EVENT ERROR", "event error");
                Toast.makeText(context, "Schedule Failure!", Toast.LENGTH_SHORT).show();
            }
            return isScheduled;
        }
        catch(Exception e){ ;
            Log.d("SCHEDULEEVENTM", e.getMessage());
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