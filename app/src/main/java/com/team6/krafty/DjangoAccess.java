package com.team6.krafty;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class DjangoAccess implements DBAccessImpl {
    public  void checkEmail(String email){}
  //  public  String getResponse(HttpURLConnection connection, byte[] request){}
    public  void checkUsername(String username){}
    public String createUser(User user){return " ";}
    public  String login( String username, String password){return " ";}
    public  Material getMaterial(String token){}
    public  void createMaterial(Material material, String token){}
    public  void modifyMaterial(Material material, String token){}
    public  void deleteMaterial(int id, String token){}
    public  User getUser(String token, String username){}
    public  ArrayList<Event> getAllEvents(String token){}
    public  Event getSpecificEvent(int id, String token){}
    public  void createEvent(Event event, String token){}
    public  void deleteEvent(int id, String token){}
    public  void updateEvent(String jsonString, String token){}
}
