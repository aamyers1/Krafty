package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class ProductController {
    private boolean isCreated;

    public boolean createProduct(String name, String description, String image, int quantity, HashMap<Integer, Integer> mats, float price, final Context context){
        isCreated = true;
        final Product product = new Product(name, description, image, quantity, mats, price);
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DBManager.getInstance().createProduct(product, token);
                }
                catch(KraftyRuntimeException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
