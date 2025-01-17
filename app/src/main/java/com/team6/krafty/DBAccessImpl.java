package com.team6.krafty;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public interface DBAccessImpl {

    public abstract boolean isOnline();
    public abstract boolean checkEmail(String email);
    public abstract String getResponse(HttpURLConnection connection, byte[] request);
    public abstract boolean checkUsername(String username);
    public abstract String createUser(User user);
    public abstract void updateProfile(User user,String token);
    public abstract String login( String username, String password);
    public abstract void getMaterial(String token);
    public abstract void createMaterial(Material material, String token);
    public abstract void modifyMaterial(Material material, String token);
    public abstract void deleteMaterial(int id, String token);
    public abstract User getUser(String token, String username);
    public abstract ArrayList<Event> getAllEvents(String token);
    public abstract Event getSpecificEvent(int id, String token);
    public abstract void createEvent(Event event, String token);
    public abstract void deleteEvent(int id, String token);
    public abstract void updateEvent(String jsonString, String token);
    public abstract void scheduleForEvent(Integer eventID, String token);
    public abstract void unscheduleForEvent(Integer scheduleId, String token,String type);
    public abstract void createProduct(Product product, String token);
    public abstract void getProducts(String token);
    public abstract Product getProduct(int id, String token);
    public abstract void deleteProduct(int id, String token);
    public abstract void updateProduct(String jsonString, String token);
    public abstract HashMap<String, String> getEventKrafters(int eventId, String token);
    void getSchedule(String token);
    public abstract void createTask(Task task, String token);
    public abstract HashMap<String, String> getKrafterProducts(String username, String token);
    public abstract void createReport(String token, String report, String type, int id);

}
