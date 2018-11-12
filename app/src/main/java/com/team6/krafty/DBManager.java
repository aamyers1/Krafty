package com.team6.krafty;


import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

class DBManager {

    DBManager() {

    }

    public boolean checkEmail(String email){
        HttpURLConnection connection = generatePostConnection("/api/user/email/");
        String APICall = "email=" + email;
        byte[] post = APICall.getBytes();
        String response = getResponse(connection, post);
        if(response.contains("Email Available")){
            return true;
        }
        else{
            return false;
        }
    }

    //sends byte stream and gets the response as a string to return to calling method.
    public String getResponse(HttpURLConnection connection, byte[] request){
        try{
            connection.connect();
            try(DataOutputStream out = new DataOutputStream(connection.getOutputStream())){
                out.write(request);
            }
            catch (Exception e){
                return e.getMessage();
            }
            StringBuilder response;
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                String line;
                response = new StringBuilder();
                while((line = in.readLine())!=null){
                    response.append(line);
                }
            }
            return response.toString();
        }
        catch(Exception e){
            return e.getMessage();
        }

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
               return  "Database Error" + e.getMessage();
           }
           StringBuilder response = new StringBuilder();
           try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
               String line;
               while ((line = in.readLine()) != null) {
                   response.append(line);
               }
           }
           catch(Exception e){
               return "Database Error" + e.getMessage();
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
           return "Database Error" + f.getMessage();
       }
    }

    //sets up the basic url connection for ANY post database transaction
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

            HttpURLConnection connection = generatePostConnection("/api/login/");
            String obj = "username=" + username+"&password=" + password;
            byte[] post = obj.getBytes(StandardCharsets.UTF_8);
            String response = getResponse(connection, post);
            if(response.contains("token")) {
                return response.substring(response.indexOf(":") + 2, response.lastIndexOf("\""));
            }
            else{
                if(response.contains("Database error")){
                    return response;
                }
                else{
                    return "Invalid credentials error";
                }
            }

    }



    public boolean createMaterial(){
        return false;
    }

}
