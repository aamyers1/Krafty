package com.team6.krafty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashMap;


public class EventsController {

    ArrayList<Event> eventsList;
    private Event theEvent;
    private static String token;
    private boolean isDeleted;
    private boolean isCreated;
    private boolean isUpdated;
    private boolean isScheduled;
    private boolean isUnscheduled;
    private HashMap<String, String> krafters;
    private DBManager dbManager = DBManager.getInstance();

    public EventsController(){

    }

    /**
     * Gets descriptions from all events to use to populate map markers
     * @return String array of descriptions
     */
    public String[] getDescriptions(){
        String[] descriptions = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            descriptions[i] = eventsList.get(i).getDescription();
        }
        return descriptions;
    }

    /**
     * Gets IDS for all events for map markers
     * @return Integer array of ID values
     */
    public int[] getIdentities(){
        int[] identities = new int[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            identities[i] = eventsList.get(i).getID();
        }
        return identities;
    }

    /**
     * Gets all latlng values for each event in the database to populate map with markers
     * @return Array of latlng objects
     */
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

    /**
     * Gets array list of event names used to populate the events map
     * @return Array of String event names
     */
    public String[] getNames(){
        String[] names = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i ++){
            names[i] = eventsList.get(i).getTitle();
        }
        return names;
    }

    /**
     * Gets all event details from the database
     * @param context calling context
     */
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

    /**
     * Retrieves the Events from the database as an ArrayList of events
     * @param context Context of the requesting class
     */
    public void retrieve(Context context){
        eventsList = new ArrayList<>();
        eventsList = dbManager.getAllEvents(SessionManager.getToken(context));
    }

    /**
     * Used to retrieve one event's specific details based on id
     * @param id ID of the requested event
     * @param context Context of teh calling object
     * @return
     */
    public Event getSpecificEvent(final int id, final Context context){
        theEvent = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
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

    /**
     * Deletes an event from the database.
     * @param id ID of the event to be deleted
     * @param context Context of the calling object
     * @return
     */
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

    /**
     * Creates a new event in the database
     * @param event Event object to be inserted into database
     * @param context Context of calling object
     * @return boolean of creation success
     */
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

    /**
     * Updates an event details in the database
     * @param event Event object being updated
     * @param id ID of the event being updated
     * @param context Context of the calling object
     * @return Bool representing success of the operation
     */
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
                    Log.d("UPDATE EVENT ERROR", e.getMessage());
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


    /**
     * Parses the image from base64 encoding to bitmap
     * @param encodedImage String image base64 encoded
     * @return Bitmap of the image
     */
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

    /**
     * Gets all krafters who have scheduled to be at the event
     * @param eventId ID of event
     * @param context Context of the calling class
     * @return Hashmap of the usernames and business names of krafters
     */
    public HashMap <String, String> getScheduledKrafters(final int eventId, final Context context){
        final String token = SessionManager.getToken(context);
        krafters = new HashMap<>();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    krafters = (HashMap<String, String>) dbManager.getEventKrafters(eventId, token);
                }
                catch(KraftyRuntimeException e){
                    Log.d("EVENT GETKRAFTERS ERROR", e.getMessage());
                }
            }
        });
        t.start();
        try{
            t.join();
        }
        catch(Exception e){ ;
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("GETKRAFTERSEVENTM", e.getMessage());
        }

        if (krafters == null){
            Log.d("EVENT GETKRAFTERS ERROR", "event error");
            Toast.makeText(context, "Error retrieving Krafters", Toast.LENGTH_SHORT).show();
        }

        return krafters;
    }
}