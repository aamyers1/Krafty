package com.team6.krafty;


import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DBManager {

    private String ip= "http://75.128.150.130:2283";

    public DBManager() {

    }


    public boolean checkUsername(String username){
        HttpURLConnection connection = generatePostConnection("/api/user/username/");
        String APIcall ="username="+username;
        byte[] post = APIcall.getBytes();
        try{
            connection.connect();
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(post);
            }
            catch(Exception e){
                return false;
            }
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }
            catch(Exception e){
                return false;
            }



            if(response.toString().contains("Username Available")){
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            return false;
        }

    }

    public String createUser(User user){
        String APIPath =  "/api/user/create/";
        String request = user.createJson();
       byte[] post = request.getBytes();
       HttpURLConnection connection = generatePostConnection(APIPath);
       try{
           connection.connect();
           try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
               wr.write(post);
           }
           catch(Exception e){
               return  e.getMessage();
           }
           StringBuilder response = new StringBuilder();
           try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
               String line;
               while ((line = in.readLine()) != null) {
                   response.append(line);
               }
           }
           catch(Exception e){
               return "E3" + e.getMessage();
           }
           String finalResponse = response.toString();
           if(finalResponse.contains("Registered Successfully")){
               return "Registration successful!";
           }
           else{
               return "Database Error";
           }
       }
       catch(Exception f){
           return f.getMessage();
       }
    }

    //sets up the basic url connection for ANY database transaction
    //the api path must be specified
    public HttpURLConnection generatePostConnection(String APIPath){
        HttpURLConnection connection;
        try {
            URL url = new URL("http://75.128.150.130:2283" + APIPath);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent","Android Client");
            return connection;
        }
        catch(Exception e){
            return null;
        }

    }


    //returns token string if user successfully logged in or null obj if not successful
    public String login(Context context, String username, String password){
        try {
            HttpURLConnection connection = generatePostConnection("/api/login/");
            String obj = "username=" + username+"&password=" + password;
            byte[] post = obj.getBytes(StandardCharsets.UTF_8);
            connection.connect();
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(post);
            }
            catch(Exception e){
                return null;
            }
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }

            catch(Exception e){
                return null;
            }
            String resp = response.toString();
            return resp.substring(resp.indexOf(":") + 2, resp.lastIndexOf("\""));

        }
        catch(Exception e){
            return null;
        }
    }



    public boolean createMaterial(){
        return false;
    }

}
