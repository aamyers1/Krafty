package com.team6.krafty;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public interface DBAccessImpl {

    public abstract boolean checkEmail(String email);
    public abstract String getResponse(HttpURLConnection connection, byte[] request);
    public abstract boolean checkUsername(String username);
    public abstract String createUser(User user);
    public abstract String login( String username, String password);
    public abstract String getMaterial(String token);
    public abstract void createMaterial(Material material, String token);
    public abstract void modifyMaterial(Material material, String token);
    public abstract void deleteMaterial(int id, String token);
    public abstract User getUser(String token, String username);
    public abstract ArrayList<Event> getAllEvents(String token);
    public abstract Event getSpecificEvent(int id, String token);
    public abstract void createEvent(Event event, String token);
    public abstract void deleteEvent(int id, String token);
    public abstract void updateEvent(String jsonString, String token);
    public abstract void createProduct(Product product, String token);
    public abstract void getProducts(String token);
}
