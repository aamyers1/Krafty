package com.team6.krafty;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

class DBManager {
    //TODO: need to check the network connectivity before we try to connect to the api!
    //My idea: force dbmanager as singleton, when getInstance is called, do a check. force context pass
    //I.e. public DBManager getInstance(Context context){ try to get the network connection, if not show message}
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
        String response = getResponse(connection, post);
        if(response.contains("Username Available")) {
            return true;
        }
        return false;
    }

    //creates a new User in the database.
    public String createUser(User user){
        String APIPath =  "/api/user/create/";
        String request = user.createJson();
        byte[] post = request.getBytes();
        HttpURLConnection connection = generatePostConnection(APIPath);
        String response = getResponse(connection, post);
        return response;
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
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent","Android Client");
            return connection;
        }
        catch(Exception e){
            return null;
        }

    }


    //returns token string if user successfully logged in or null obj if not successful
    public String login( String username, String password){

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

    //do not refactor unless we have other get requests
    public String getMaterial(String token){
        try {
            URL url = new URL("http://75.128.150.130:2283/api/material/view/");
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //extra header for authorization
                connection.setRequestProperty ("Authorization", "token " + token);
                StringBuilder response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
                catch(Exception i){
                    return token;
                }
            }
            catch(java.io.IOException e){
                return"No connection";
            }
        }
        catch(MalformedURLException e){
            return "Malformed URL";
        }
    }

    //TODO: when we create the material, we need to return the id so i can use it to modify and delete without reloading all materials
    public boolean createMaterial(Material material, String token){
        HttpURLConnection connection = generatePostConnection("/api/material/create/");
        //extra header for authorization
        connection.setRequestProperty ("Authorization", "token " + token);
        String materialString = material.createJson();
        byte[] request = materialString.getBytes();
        String response = getResponse(connection,request);
        try {
            JSONObject json = new JSONObject(response);
            material.setID(json.getInt("result"));
        }
        catch(Exception e){
            Log.d("MATERIAL ID ERROR", e.getMessage());
            Log.d("MESSAGE MATERIAL", response);
        }
        if(response.toLowerCase().contains("material created")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean modifyMaterial(Material material, String token){
        HttpURLConnection connection = generatePostConnection("/api/material/update/");
        //extra header for authorization
        connection.setRequestProperty ("Authorization", "token " + token);
        String materialString = material.getModifyJson();
        byte[] request = materialString.getBytes();
        String response = getResponse(connection,request);
        if(response.contains("Material Updated")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean deleteMaterial(int id, String token){
        HttpURLConnection connection = generatePostConnection("/api/material/delete/");
        //extra header for authorization
        connection.setRequestProperty ("Authorization", "token " + token);
        String materialString = "id="+id;
        byte[] request = materialString.getBytes();
        String response = getResponse(connection,request);
        if(response.contains("Material Deleted")){
            return true;
        }
        else{
            return false;
        }
    }

    public JSONObject getUser(String token, String username){
        HttpURLConnection connection = generatePostConnection("/api/user/view/");
        connection.setRequestProperty("Authorization", "token " + token);
        String request;
        if (username.equals("")) {
            request = "";
        }
        else{
            request ="username=" + username;
        }
        byte[] query = request.getBytes();
        String response = getResponse(connection, query);
        try {
            JSONObject json = new JSONObject(response);
            return json;
        }
        catch(Exception e){
            Log.d("ERROR GETTING USER", e.getMessage());
            return null;
        }
    }

}
