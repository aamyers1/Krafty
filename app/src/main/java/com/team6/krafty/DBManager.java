package com.team6.krafty;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DBManager {

    private String ip= "http://75.128.150.130:2283";

    public DBManager() {

    }

    //generates the basic connection to be used. must be passed the directory
    //for the api call in order to execute!
    public HttpURLConnection generateConnection(String URLDirectory) {
        URL url = null;
        try{
         url = new URL(ip + URLDirectory);
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setDoOutput(true);
         connection.setRequestMethod("POST");
         connection.setRequestProperty("User-Agent","Android Client");
         return connection;
        } catch(java.io.IOException f){

        }
        return null;
    }

    public boolean createUser(User user){
        return false;
    }

    public boolean login(String username, String password){
        return false;

    }

    public boolean createMaterial(){
        return false;
    }

}
