package com.team6.krafty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class DBManager {

    private static volatile DBManager instance;
    private static DBAccessImpl impl = null;

    private DBManager() {
            impl = new DjangoAccess();
    }



    public static DBManager getInstance() {
        if (instance == null){
            synchronized (DBAccessImpl.class){
                if (instance == null) instance = new DBManager();
            }
        }
        return instance;
    }

    public DBAccessImpl getImpl() {
        return impl;
    }


    public void setImpl(DBAccessImpl theImpl) {impl = theImpl;}
    public boolean isOnline(){return getImpl().isOnline();}
    public boolean checkEmail(String email){return getImpl().checkEmail(email);}
    public boolean checkUsername(String username){return getImpl().checkUsername(username);}
    public String createUser(User user){return getImpl().createUser(user);}
    public void updateProfile(User user, String token){getImpl().updateProfile(user,token);}
    public String login( String username, String password){return getImpl().login(username,password);}
    public void getMaterial(String token){getImpl().getMaterial(token);}
    public void createMaterial(Material material, String token){ getImpl().createMaterial(material, token);}
    public void modifyMaterial(Material material, String token){ getImpl().modifyMaterial(material, token);}
    public void deleteMaterial(int id, String token){ getImpl().deleteMaterial(id,token);}
    public User getUser(String token, String username){return getImpl().getUser(token, username);}
    public ArrayList<Event> getAllEvents(String token){return getImpl().getAllEvents(token);}
    public Event getSpecificEvent(int id, String token){return getImpl().getSpecificEvent(id, token);}
    public void createEvent(Event event, String token){getImpl().createEvent(event, token);}
    public void deleteEvent(int id, String token){getImpl().deleteEvent(id, token);}
    public void updateEvent(String event, String token){getImpl().updateEvent(event, token);}
    public void scheduleForEvent(Integer eventId, String token){getImpl().scheduleForEvent(eventId,token);}
    public void unscheduleForEvent(Integer scheduleId, String token, String type){getImpl().unscheduleForEvent(scheduleId,token,type);}
    public void createProduct(Product product, String token){getImpl().createProduct(product,token);}
    public void getProducts(String token){getImpl().getProducts(token);}
    public void deleteProduct(int id, String token){getImpl().deleteProduct(id, token);}
    public void updateProduct(String jsonString, String token){getImpl().updateProduct(jsonString, token);}
    public Product getProduct(int id, String token){return getImpl().getProduct(id, token);}
    public HashMap<String,String> getEventKrafters(int eventId, String token){return getImpl().getEventKrafters(eventId, token);}
    public void getSchedule(String token){getImpl().getSchedule(token);}
    public void createTask(Task task, String token){getImpl().createTask(task, token);}
    public HashMap<String, Product> getKrafterProducts(String username, String token){return getImpl().getKrafterProducts(username, token);}

}