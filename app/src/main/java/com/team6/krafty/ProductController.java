package com.team6.krafty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductController {
    private boolean isCreated;
    private HashMap<String, Product> products;

    public boolean createProduct(String name, String description, String image, int quantity, HashMap<Integer, Integer> mats, float price, final Context context){
        isCreated = true;
        String username = SessionManager.getUsername(context);
        final Product product = new Product(name, description, image, quantity, mats, price, username);
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DBManager.getInstance().createProduct(product, token);
                }
                catch(KraftyRuntimeException e){
                    isCreated = false;
                }
            }
        });
        t.start();
        try{
            t.join();
        }
        catch (Exception e){
            isCreated = false;
        }
        Inventory.addProduct(product);
        return isCreated;
    }

    public void getProducts(final String token){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DBManager.getInstance().getProducts(token);
                }
                catch (Exception e){
                    Log.d("GETPROD", e.getMessage());
                }
            }
        });
        t.start();
    }

    public Product getProduct(final int id, final Context context){
        final Product[] product = new Product[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = DBManager.getInstance();
                product[0] = dbManager.getProduct(id, SessionManager.getToken(context));
            }
        });
        t.start();
        try {
            t.join();
            return product[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Product> getKrafterProducts(final String username, final Context context){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run(){
                DBManager dbManager = DBManager.getInstance();
                products =  dbManager.getKrafterProducts(username,SessionManager.getToken(context));
            }
        });
        t.start();
        try{
            t.join();
            return products;
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        return  null;
    }

    public void deleteProduct(final int id, final Context context){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = DBManager.getInstance();
                dbManager.deleteProduct(id, SessionManager.getToken(context));
            }
        });
        t.start();
        try {
            t.join();
            Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean updateProduct(Product p,final Context context){
        isCreated = true;
        int id = p.getId();
        final String request = p.getJson() + "&id=" + id;
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = DBManager.getInstance();
                try {
                    dbManager.updateProduct(request, token);
                }
                catch(KraftyRuntimeException e){
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", e.getMessage());
                    isCreated = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            return true;
        }
        catch(Exception e){ ;
            Log.d("UPDATEPRODUCTM", e.getMessage());
        }
        return false;
    }



}
