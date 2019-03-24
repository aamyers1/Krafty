package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ProductController {

    public Product getProduct(final int id, final Context context){
        final Product[] product = new Product[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
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

    public void deleteProduct(final int id, final Context context){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                dbManager.deleteEvent(id, SessionManager.getToken(context));
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

    public boolean updateProduct(Product product, int id,final Context context){
        String json = product.createJson();
        final String request = json + "&id=" + id;
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager();
                try {
                    dbManager.updateProduct(request, token);
                }
                catch(KraftyRuntimeException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
