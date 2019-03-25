package com.team6.krafty;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DjangoAccess implements DBAccessImpl {


  private static DjangoAccess djangoAccess;

  public static DjangoAccess getInstance(){
      if(djangoAccess == null){
          djangoAccess = new DjangoAccess();
      }
      return djangoAccess;
  }

    //sets up the basic url connection for ANY post database transaction
    //the api path must be specified
  public HttpURLConnection generatePostConnection(String APIPath)throws KraftyRuntimeException{
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
      throw new KraftyRuntimeException("Bad Connection", null);
    }
  }

  //  public  String getResponse(HttpURLConnection connection, byte[] request){}
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
      Log.d("RESPONSE ERR", e.getMessage());
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

  public void createMaterial(Material material, String token){
      HttpURLConnection connection = generatePostConnection("/api/material/create/");
      connection.setRequestProperty ("Authorization", "token " + token);
      String materialString = material.createJson();
      byte[] request = materialString.getBytes();
      String response = getResponse(connection,request);
      try {
        JSONObject json = new JSONObject(response);
        material.setID(json.getInt("result"));
      }
      catch(Exception e){
        Log.d("MATERIAL RESPONSE", response);
        Log.d("MATERIAL ID ERROR", e.getMessage());
        Log.d("MESSAGE MATERIAL", response);
     }
    if(!response.toLowerCase().contains("material created")){
      Log.d("CREATE ERROR", response);
      throw new KraftyRuntimeException("Create Failed!", null);
    }
  }

  public void modifyMaterial(Material material, String token){
    HttpURLConnection connection = generatePostConnection("/api/material/update/");
    //extra header for authorization
    connection.setRequestProperty ("Authorization", "token " + token);
    String materialString = material.getModifyJson();
    byte[] request = materialString.getBytes();
    String response = getResponse(connection,request);
    if(! response.contains("Material Updated")){
      Log.d("UPDATE ERROR", response);
      throw new KraftyRuntimeException("Update Failed!", null);
    }
  }

  public void deleteMaterial(int id, String token){
    HttpURLConnection connection = generatePostConnection("/api/material/delete/");
    //extra header for authorization
    connection.setRequestProperty ("Authorization", "token " + token);
    String materialString = "id="+id;
    byte[] request = materialString.getBytes();
    String response = getResponse(connection,request);
    if(! response.contains("Material Deleted")){
      Log.d("DELETE ERROR", response);
      throw new KraftyRuntimeException("Delete Failed!", null);
    }
  }

  public User getUser(String token, String username){
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
      User profile = new User();
      profile.parseJson(new JSONObject(response));
      return profile;
    }
    catch(Exception e){
      Log.d("RESPONSE", "response: " + response);
      Log.d("ERROR GETTING USER", e.getMessage());
      return null;
    }
  }

  public ArrayList<Event> getAllEvents(String token){
    ArrayList<Event> eventsList = new ArrayList<>();
    try {
      URL url = new URL("http://75.128.150.130:2283/api/event/view/");
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
          JSONObject jsonObject = new JSONObject(response.toString());

          try {
            JSONArray jsonArr = jsonObject.getJSONArray("result");
            for(int i = 0; i < jsonArr.length(); i ++){
              Event newEvent = new Event();
              newEvent.parseJson(jsonArr.getJSONObject(i));
              //TODO: FILTER OUT EVENTS WHICH HAVE PASSED
              eventsList.add(newEvent);

            }
          }
          catch(Exception e){
            Log.d("ECNTR JSONARRAY", e.getMessage());
          }

          return eventsList;
        }
        catch(Exception i){
          return null;
        }
      }
      catch(java.io.IOException e){
        Log.d("CONNECTION ERROR", e.getMessage());
      }
    }
    catch(MalformedURLException e){
      Log.d("MALFORMED URL", "bad url");
    }
    return null;
  }

  public Event getSpecificEvent(int id, String token){
    HttpURLConnection connection = generatePostConnection("/api/event/viewspecific/");
    String string = "id="+id;
    byte[] request = string.getBytes();
    String response = getResponse(connection, request);
    try {
      Event event = new Event();
      JSONObject json = new JSONObject(response);
      try {
        JSONArray jsonArray = json.getJSONArray("result");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        event.parseJson(jsonObject);
      } catch (Exception e) {
        Log.d("JSONERROR", "SINGLE EVENT PARSE ERROR");
      }
      return event;
    }
    catch(Exception e){
      Log.d("SPECEVENT", "Invalid response" + e.getMessage());
      return null;
    }
  }

  public void createEvent(Event event, String token){
    HttpURLConnection connection = generatePostConnection("/api/event/create/");
    //extra header for authorization
    connection.setRequestProperty ("Authorization", "token " + token);
    String eventString = event.createJson();
    byte[] request = eventString.getBytes();
    String response = getResponse(connection,request);
    try {
      JSONObject json = new JSONObject(response);
      event.setID(json.getJSONArray("result").getJSONObject(0).getInt("id"));
    }
    catch(Exception e){
      Log.d("EVENT RESPONSE", response);
    }
    if(! response.toLowerCase().contains("event created")){
      Log.d("CREATE ERROR", response);
      throw new KraftyRuntimeException("Create Failed!", null);
    }
  }

  public void deleteEvent(int id, String token){
    HttpURLConnection connection = generatePostConnection("/api/event/delete/");
    //extra header for authorization
    connection.setRequestProperty ("Authorization", "token " + token);
    String eventString = "id="+id;
    byte[] request = eventString.getBytes();
    String response = getResponse(connection,request);
    if(! response.contains("Deleted")){
      Log.d("DELETE ERROR", response);
      throw new KraftyRuntimeException("Delete Failed!", null);
    }
  }

  public void updateEvent(String jsonString, String token)throws KraftyRuntimeException{
    HttpURLConnection connection = generatePostConnection("/api/event/modify/");
    connection.setRequestProperty("Authorization", "token " + token);
    byte[] request = jsonString.getBytes();
    String response = getResponse(connection,request);
    if(!response.contains("Updated")){
      throw new KraftyRuntimeException("Update Failed!", null);
    }
  }

  public void createProduct(Product product, String token) throws KraftyRuntimeException{
    HttpURLConnection connection = generatePostConnection("/api/product/create/");
    connection.setRequestProperty("Authorization", "token " + token);
    String jsonString = product.getJson();
    byte[] request = jsonString.getBytes();
    String response = getResponse(connection, request);
    if(!response.toLowerCase().contains("product created")){
      //throw new KraftyRuntimeException("Create product failed!", null);
        Log.d("PRODRESP", response);
    }
    try {
        JSONObject json = new JSONObject(response);
        JSONArray jarray = json.getJSONArray("result");
        int id = jarray.getJSONObject(0).getInt("id");
        product.setId(id);
    }
    catch (Exception e){

    }
  }

  public void getProducts(String token) {
      try {
          URL url = new URL("http://75.128.150.130:2283/api/product/view/");
          try {
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              connection.setRequestMethod("GET");
              //extra header for authorization
              connection.setRequestProperty("Authorization", "token " + token);
              StringBuilder response;
              try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                  String line;
                  response = new StringBuilder();
                  while ((line = in.readLine()) != null) {
                      response.append(line);
                  }
                  String reply = response.toString();
                  JSONObject json = new JSONObject(reply);
                  JSONArray jsonA = json.getJSONArray("result");
                  for (int i = 0; i < jsonA.length(); i++) {

                      Product product = new Product();
                      product.parseJSON(jsonA.getJSONObject(i));
                      Log.d("PRODUCT", product.getMaterials().toString());
                      Inventory.addProduct(product);
                      Log.d("PRODUCTGET", "retrieved " + product.getName());

                  }
              } catch (Exception i) {
                Log.d("GETPROD", i.getMessage());
              }
          } catch (java.io.IOException e) {
              Log.d("GETPROD", e.getMessage());
          }
      } catch (MalformedURLException e) {
          Log.d("GETPROD", e.getMessage());
      }
  }

    //the following methods will be moved to DjangoAccess class during implementation
    // todo: handle exception in KraftyException
    public Product getProduct(int id, String token){
        HttpURLConnection connection = generatePostConnection("/api/product/viewspecific/");
        String string = "id=" + id;
        byte[] request = string.getBytes();
        String response = getResponse(connection, request);
        Product product = new Product();
        try {
            product.parseJSON(new JSONObject(response));
        }
        catch(Exception e){
            Log.d("jsonException",e.getMessage());
        }
        return  product;
    }

    public void deleteProduct(int id, String token) throws KraftyRuntimeException{
        HttpURLConnection connection = generatePostConnection("/api/product/delete/");
        //extra header for authorization
        connection.setRequestProperty ("Authorization", "token " + token);
        String string = "id="+id;
        byte[] request = string.getBytes();
        String response = getResponse(connection,request);
        Log.d("DELETERESP", response);
        if(response.contains("ERROR")){
            //throw new KraftyRuntimeException("Delete Failed!", null);
        }
    }

    public void updateProduct(String jsonString, String token)throws KraftyRuntimeException{
        HttpURLConnection connection = generatePostConnection("/api/product/modify/");
        connection.setRequestProperty("Authorization", "token " + token);
        byte[] request = jsonString.getBytes();
        String response = getResponse(connection,request);
        if(response.contains("ERROR")){
            throw new KraftyRuntimeException("Update Failed!", null);
        }
    }

}
