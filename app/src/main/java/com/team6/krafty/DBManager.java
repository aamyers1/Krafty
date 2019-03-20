package com.team6.krafty;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

class DBManager {

   DBAccessImpl impl = null;

    //TODO: need to check the network connectivity before we try to connect to the api!
    //My idea: force dbmanager as singleton, when getInstance is called, do a check. force context pass
    //I.e. public DBManager getInstance(Context context){ try to get the network connection, if not show message}
    DBManager(DBAccessImpl theImpl) {
        setImpl(theImpl);
    }

    public DBAccessImpl getImpl() {
        return impl;
    }
    public void setImpl(DBAccessImpl theImpl) {impl = theImpl;}
    public boolean checkEmail(String email){return getImpl().checkEmail(email);}
    public String getResponse(HttpURLConnection connection, byte[] request){return getImpl().getResponse(connection,request);}
    public boolean checkUsername(String username){return getImpl().checkUsername(username);}
    public String createUser(User user){return getImpl().createUser(user);}
//    public HttpURLConnection generatePostConnection(String APIPath){return getImpl().generatePostConnection(APIPath);}
    public String login( String username, String password){return getImpl().login(username,password);}
    public String getMaterial(String token){return getImpl().getMaterial(token);}
    public void createMaterial(Material material, String token){ getImpl().createMaterial(material, token);}
    public void modifyMaterial(Material material, String token){ getImpl().modifyMaterial(material, token);}
    public void deleteMaterial(int id, String token){ getImpl().deleteMaterial(id,token);}
    public User getUser(String token, String username){return getImpl().getUser(token, username);}
    public ArrayList<Event> getAllEvents(String token){return getImpl().getAllEvents(token);}
    public Event getSpecificEvent(int id, String token){return getImpl().getSpecificEvent(id, token);}
    public void createEvent(Event event, String token){getImpl().createEvent(event, token);}
    public void deleteEvent(int id, String token){getImpl().deleteEvent(id, token);}
    public void updateEvent(String event, String token){getImpl().updateEvent(event, token);}
}