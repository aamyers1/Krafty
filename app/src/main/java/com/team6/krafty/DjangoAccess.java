package com.team6.krafty;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class DjangoAccess implements DBAccessImpl {


  private static DjangoAccess djangoAccess;

  public static DjangoAccess getInstance(){
      if(djangoAccess == null){
          djangoAccess = new DjangoAccess();
      }
      return djangoAccess;
  }

    /**
     * tests for active network connection to the server
     * @return boolean if the network connection was successful.
     */
    public boolean isOnline(){
        Runtime runtime = Runtime.getRuntime();
        try{
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e) {

        }
        catch (InterruptedException e){

        }
        return false;
    }

    /**
     * sets up the basic url connection for ANY post database transaction
     * @param APIPath the path for specific api
     * @return connection for API Post request
     * @throws KraftyRuntimeException
     */
  public HttpURLConnection generatePostConnection(String APIPath)throws KraftyRuntimeException{
    HttpURLConnection connection;
    try {
      URL url = new URL("http://kraftyapp.servehttp.com" + APIPath);
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

    /**
     * Checks if the email is already used by a registered account
     * @param email to be checked
     * @return boolean if its available
     */
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

    /**
     * sends byte stream and gets the response as a string to return to calling method.
     * @param connection
     * @param request
     * @return response string
     */
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
    finally {
        connection.disconnect();
    }
  }

    /**
     * hecks if the username is already used by a registered account
     * @param username
     * @return boolean if its available
     */
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

    /**
     * creates a new User in the database.
      * @param user
     * @return response string
     */
  public String createUser(User user){
    String APIPath =  "/api/user/create/";
    String request = user.createJson();
    byte[] post = request.getBytes();
    HttpURLConnection connection = generatePostConnection(APIPath);
    String response = getResponse(connection, post);
    return response;
  }
    /**
     * updates existing user in the database.
     * @param user
     * @param token for authentication
     */
    public void updateProfile(User user,String token){
        HttpURLConnection connection = generatePostConnection("/api/user/modify/");
        //extra header for authorization
        connection.setRequestProperty ("Authorization", "token " + token);
        String userString = user.createJson();
        byte[] request = userString.getBytes();
        String response = getResponse(connection,request);
        if(! response.contains("OK")){
            Log.d("UPDATE ERROR", response);
            throw new KraftyRuntimeException("Update Failed!", null);
        }

    }

    /**
     * Checks credentials for authentication
      * @param username
     * @param password
     * @return returns token string if user successfully logged in or null obj if not successful
     */
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

    /**
     * gets all materials of a user
      * @param token for authentication
     */
  public void getMaterial(String token){
    try {
      URL url = new URL("http://kraftyapp.servehttp.com/api/material/view/");
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
          parseForMats(response.toString());
          connection.disconnect();
        }
        catch(Exception i){
          Log.d("GETMATERR", i.getMessage());
          connection.disconnect();
        }
      }
      catch(java.io.IOException e){
          Log.d("GETMATERR", "No connection");
      }

    }
    catch(MalformedURLException e){
        Log.d("GETMATERR", "Malformed URL");
    }

  }

    /**
     * creates a new material
     * @param material
     * @param token for authentication
     */
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

    /**
     * Modifies a material
     * @param material
     * @param token for authentication
     */
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

    /**
     * deletes a material
     * @param id
     * @param token for authentication
     */
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

    /**
     * gets a user with a given username
     * @param token for autehtication
     * @param username
     * @return the user
     */
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
      Log.d("REQUEST: " , request);
    byte[] query = request.getBytes();
    String response = getResponse(connection, query);
      Log.d("RESPONSE", "response: " + response);
    try {
      User profile = new User();
      profile.parseJson(new JSONObject(response));
      return profile;
    }
    catch(Exception e){
        Log.d("RES", username + " " + token);
      Log.d("RESPONSE", "response: " + response);
      Log.d("ERROR GETTING USER", e.getMessage());
      return null;
    }
  }

    /**
     * Gets all events
     * @param token
     * @return ArrayList of all events
     */
  public ArrayList<Event> getAllEvents(String token){
    ArrayList<Event> eventsList = new ArrayList<>();
    try {
      URL url = new URL("http://kraftyapp.servehttp.com/api/event/view/");
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
          connection.disconnect();
          JSONObject jsonObject = new JSONObject(response.toString());

          try {
            JSONArray jsonArr = jsonObject.getJSONArray("result");
            for(int i = 0; i < jsonArr.length(); i ++){
              Event newEvent = new Event();
              newEvent.parseJson(jsonArr.getJSONObject(i));
              eventsList.add(newEvent);

            }

          }
          catch(Exception e){
            Log.d("ECNTR JSONARRAY", e.getMessage());
            connection.disconnect();
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

    /**
     * gets a specific event with the given id
     * @param id
     * @param token for authentication
     * @return Event
     */
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
        Log.d("JSONERROR", "SINGLE EVENT PARSE ERROR" + e.getMessage() + " " + response);
      }
      return event;
    }
    catch(Exception e){
      Log.d("SPECEVENT", "Invalid response" + e.getMessage());
      return null;
    }
  }

    /**
     * creates a new event
     * @param event
     * @param token for authentication
     */
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

    /**
     * delets an event
     * @param id
     * @param token for authentication
     */
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

    /**
     * updates event
     * @param jsonString of the event
     * @param token for authentication
     * @throws KraftyRuntimeException
     */
  public void updateEvent(String jsonString, String token)throws KraftyRuntimeException{
    HttpURLConnection connection = generatePostConnection("/api/event/modify/");
    connection.setRequestProperty("Authorization", "token " + token);
    byte[] request = jsonString.getBytes();
    String response = getResponse(connection,request);
    if(!response.contains("Updated")){
        try{
            JSONObject jsonResponse = new JSONObject(response);
            throw new KraftyRuntimeException(jsonResponse.getString("message"), null);
        } catch (Exception e){
            throw new KraftyRuntimeException("Update Failed!: "+e.getMessage(), null);
        }
    }
  }

    /**
     * Add an event to schedule
     * @param eventId
     * @param token for authentication
     * @throws KraftyRuntimeException
     */
    public void scheduleForEvent(Integer eventId, String token ) throws KraftyRuntimeException {
        HttpURLConnection connection = generatePostConnection("/api/schedule/event/");
        connection.setRequestProperty("Authorization", "token " + token);

        String jsonString = "event="+eventId;
        byte[] request = jsonString.getBytes();
        String response = getResponse(connection,request);
        if(!response.contains("scheduled")){
            try{
                JSONObject jsonResponse = new JSONObject(response);
                throw new KraftyRuntimeException(jsonResponse.getString("message"), null);
            } catch (Exception e){
                throw new KraftyRuntimeException("Schedule Failed!: "+e.getMessage(), null);
            }
        }
    }

    /**
     * remove an event from schedule
     * @param scheduleItemId
     * @param token for authentication
     * @param type whether its event for producr
     * @throws KraftyRuntimeException
     */
    public void unscheduleForEvent(Integer scheduleItemId, String token, String type) throws KraftyRuntimeException {
        HttpURLConnection connection = generatePostConnection("/api/schedule/delete/");
        connection.setRequestProperty("Authorization", "token " + token);
        String jsonString = "id="+scheduleItemId+"&type=" + type;
        Log.d("FFFFFFff", jsonString);
        byte[] request = jsonString.getBytes();
        String response = getResponse(connection,request);
        if(!response.contains("Item deleted from schedule")){
            try{
                throw new KraftyRuntimeException(response, null);
            } catch (Exception e){
                throw new KraftyRuntimeException("Unschedule Failed!: "+response, null);
            }
        }
    }

    /**
     * creare a new product
      * @param product
     * @param token for authentication
     * @throws KraftyRuntimeException
     */
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

    /**
     * gets all products of a user
     * @param token for authentication
     */
  public void getProducts(String token) {
      try {
          URL url = new URL("http://kraftyapp.servehttp.com/api/product/view/");
          try {
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              connection.setRequestMethod("GET");
              //extra header for authorization
              connection.setRequestProperty("Authorization", "token " + token);
              StringBuilder response = new StringBuilder();
              try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                  String line;
                  while ((line = in.readLine()) != null) {
                      response.append(line);
                  }
                  connection.disconnect();
                  String reply = response.toString();
                  JSONObject json = new JSONObject(reply);
                  JSONArray jsonA = json.getJSONArray("result");
                  for (int i = 0; i < jsonA.length(); i++) {

                      Product product = new Product();
                      product.parseJSON(jsonA.getJSONObject(i));
                      Inventory.addProduct(product);
                  }
              } catch (Exception i) {
                  Log.d("GETPROD3", i.getMessage());
                  connection.disconnect();
              }
          } catch (java.io.IOException e) {
              Log.d("GETPROD2", e.getMessage());
          }
      } catch (MalformedURLException e) {
          Log.d("GETPROD1", e.getMessage());
      }
  }


    /**
     * gets specific product
     * @param id
     * @param token for authentication
     * @return Product object
     */
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

    /**
     * deletes a product
     * @param id
     * @param token for authentication
     * @throws KraftyRuntimeException
     */
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

    /**
     * updates a product
     * @param jsonString of the updated product
     * @param token for authentication
     * @throws KraftyRuntimeException
     */
    public void updateProduct(String jsonString, String token)throws KraftyRuntimeException{
        HttpURLConnection connection = generatePostConnection("/api/product/modify/");
        connection.setRequestProperty("Authorization", "token " + token);
        byte[] request = jsonString.getBytes();
        String response = getResponse(connection,request);
        if(response.contains("ERROR")){
            throw new KraftyRuntimeException("Update Failed!", null);
        }
    }

    /**
     * parses materials
     * @param jsonObject
     */
    private static void parseForMats(String jsonObject){
        try {
            //first attempt to get the JSONObject
            JSONObject primaryObject = new JSONObject(jsonObject);
            //then get the array of objects within the primaryObject
            JSONArray jsonArray = primaryObject.getJSONArray("result");
            //iterate through each array value
            for(int i = 0; i < jsonArray.length(); i++){
                //get the object, create a new material with it, and add to inventory
                JSONObject singleMat = jsonArray.getJSONObject(i);
                Material newMat = new Material();
                newMat.parseJson(singleMat);
                Inventory.addMaterial(newMat);
            }
        }
        catch(Exception e){
            Log.d("PARSE MATERIAL ERROR", e.getMessage());
        }
    }

    /**
     * get all krafters attending an event
     * @param eventId
     * @param token for authentication
     * @return HashMap of krafters' usernames and business names
     */
    public HashMap<String, String> getEventKrafters(int eventId, String token){
      HashMap<String, String> krafters = new HashMap<>();
        HttpURLConnection connection = generatePostConnection("/api/schedule/krafters/");
        connection.setRequestProperty("Authorization", "Token " + token);

        String string = "id="+eventId;
        byte[] request = string.getBytes();
        String response = getResponse(connection,request);
        if(response.contains("ERROR") || response.contains("http") || response.contains("unexpected end of stream")){
            throw new KraftyRuntimeException("Failed to get scheduled Krafters", null);
        }


        //first attempt to get the JSONObject
        try {
            JSONObject primaryObject = new JSONObject(response);
            //then get the array of objects within the primaryObject
            JSONArray jsonArray = primaryObject.getJSONArray("result");
            //iterate through each array value
            for (int i = 0; i < jsonArray.length(); i++) {
                //get the object, create a new material with it, and add to inventory
                JSONObject userObject = jsonArray.getJSONObject(i);
                String username = userObject.getString("username");
                String name = userObject.getString("business");
                krafters.put(username, name);
            }
        } catch (JSONException e){
            throw new KraftyRuntimeException("Failed to get scheduled Krafters", null);
        }
        return krafters;
    }

    /**
     * gets the schedule of a user
     * @param token for authentication
     */
    public void getSchedule(String token){
      HttpURLConnection connection = generatePostConnection("/api/schedule/view/");
      connection.setRequestProperty ("Authorization", "token " + token);
      String response = getResponse(connection,"".getBytes());
      try{
          JSONObject jsonObject = new JSONObject(response);
          JSONArray allItems = jsonObject.getJSONArray("result");
          for(int i =0; i < allItems.length(); i++){
              JSONObject current = allItems.getJSONObject(i);
              String type = current.getString("type");
              if(type.toLowerCase().equals("p")){
                  //is a task
                  int schedID = current.getInt("scheduleid");
                  int productID = current.getInt("taskid");
                  String dateTime = current.getString("start");
                  String date = dateTime.substring(dateTime.indexOf("M") + 1).trim();
                  String time = dateTime.substring(0, dateTime.indexOf("M") + 1).trim();
                  int qty = current.getInt("qty");
                  Task task = new Task(schedID, productID, qty, date, time);
                  Schedule.getInstance().addItem(task, schedID);
              }
              else{
                  //is an event
                  int schedID = current.getInt("scheduleid");
                  int eventID = current.getInt("taskid");
                  Event e = getSpecificEvent(eventID, token);
                  Schedule.getInstance().addItem(e, schedID);
              }
          }
      }
      catch(Exception e){
          Log.d("PARSE ERROR SCHED", e.getMessage());
      }
    }

    /**
     * Creates a task object within the database
     * @param task The task object to be sent to the database
     * @param token The user's token used for authentication
     * @throws KraftyRuntimeException
     */
    public void createTask(Task task, String token) throws KraftyRuntimeException {
        HttpURLConnection connection = generatePostConnection("/api/schedule/product/");
        connection.setRequestProperty("Authorization", "token " + token);
        String jsonString = task.getJson();
        byte[] request = jsonString.getBytes();
        String response = getResponse(connection,request);
        Log.d("PRODRESP", response);
        if(response.toLowerCase().contains("ERROR")){
            Log.d("PRODRESP", response);
            throw new KraftyRuntimeException("Create task failed!", null);

        }
        try {
            JSONObject json = new JSONObject(response);
            JSONArray jarray = json.getJSONArray("result");
            int id = jarray.getJSONObject(0).getInt("id");
            task.setId(id);
        }
        catch (Exception e){

        }
    }

    /**
     * gets all products of a given krafter
     * @param username
     * @param token for authentication
     * @return
     */
    public HashMap<String, String> getKrafterProducts(String username, String token) {
        HashMap<String, String> products = new HashMap<>();
        HttpURLConnection connection = generatePostConnection("/api/krafters/products/");
        connection.setRequestProperty("Authorization", "token " + token);
        String string = "username="+username;

        byte[] request = string.getBytes();
        String response = getResponse(connection,request);
        if(!response.contains("OK")){
            Log.d("response", response);
            throw new KraftyRuntimeException("Failed to get  Krafter's prodcuts", null);
        }

        try {
            JSONObject primaryObject = new JSONObject(response);
            //then get the array of objects within the primaryObject
            JSONArray jsonArray = primaryObject.getJSONArray("result");
            //iterate through each array value
            for (int i = 0; i < jsonArray.length(); i++) {
                //get the object, create a new product with it, and add to hashmap
                JSONObject prodObject = jsonArray.getJSONObject(i);
                String name = prodObject.getString("name");
                String image = "";
                if(prodObject.has("image")) {
                    image = prodObject.getString("image");
                }
                //String desc = prodObject.getString("description");
                //Product product = new Product(name, image, desc);
                products.put(name, image);
            }
        } catch (JSONException e){
            Log.d("KPRODSRESP",response);
            //throw new KraftyRuntimeException("Failed to get download products", null);
        }
        return products;
    }

    /**
     * creates a report against events
     * @param token
     * @param report
     * @param type
     * @param id
     * @throws KraftyRuntimeException
     */
    public void createReport(String token, String report, String type, int id) throws KraftyRuntimeException{
        HttpURLConnection connection = generatePostConnection("/api/report/");
        connection.setRequestProperty("Authorization", "token " + token);
        String req = "type=" + type + "&reason=" +report + "&id=" + id;
        byte[] request = req.getBytes();
        String response = getResponse(connection, request);
        if(!response.contains("Reported Successfully")){
            throw new KraftyRuntimeException("Report Failed!", null);
        }

    }
}
