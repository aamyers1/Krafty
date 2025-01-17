package com.team6.krafty;

import android.graphics.Bitmap;
import java.util.Vector;

public class Inventory {

    //holds the inventory instance
    private static Inventory inventory = new Inventory();
    //holds a specific user's inventory of materials
    private static Vector<Material> personalMaterials;
    private static Vector<Product> personalProducts;

    //constructor is PRIVATE
    private Inventory(){
        personalMaterials = new Vector<>();
        personalProducts = new Vector<>();
    }

    //use as constructor: singleton class.
    public static Inventory getInstance(){
        return inventory;
    }

    //returns the number of materials
    //TODO:This should actually be getMaterialsCount() for when Products are added
    public static int  getCount(){
        return personalMaterials.toArray().length;
    }

    //adds a material to the arrayList
    public static void addMaterial(Material material){
        personalMaterials.add(material);
    }


    //gets a material from a specific location in the array
    public static Material getMaterial(int position){
        return personalMaterials.get(position);
    }

    public static void removeMaterial(int id){
            for(int i = 0; i < getCount(); i ++){
                if(personalMaterials.elementAt(i).getId() == id){
                    personalMaterials.removeElementAt(i);
                }
        }
    }

    public static void removeProduct(Product product){
        personalProducts.remove(product);
    }

    public static Bitmap[] getMaterialImages(){
        Bitmap[] materialImages = new Bitmap[personalMaterials.size()];
        for(int i = 0; i < personalMaterials.size(); i ++){
            materialImages[i] = personalMaterials.get(i).getBmp();
        }
        return materialImages;
    }

    public static String[] getMaterialCaptions(){
        String[] materialCaptions = new String[personalMaterials.size()];
        for(int i = 0; i < personalMaterials.size(); i ++){
            materialCaptions[i] = personalMaterials.get(i).getName();
        }
        return materialCaptions;
    }

    public static Bitmap[] getProductImages(){
        Bitmap[] productImages = new Bitmap[personalProducts.size()];
        for(int i = 0; i < personalProducts.size(); i ++){
            productImages[i] = personalProducts.get(i).getBmp();
        }
        return productImages;
    }

    public static String[] getProductCaptions(){
        String[] productCaptions = new String[personalProducts.size()];
        for(int i = 0; i < personalProducts.size(); i ++){
            productCaptions[i] = personalProducts.get(i).getName();
        }
        return productCaptions;
    }

    //clears all inventory items
    public static void clearAll(){
        personalMaterials = new Vector<>();
        personalProducts = new Vector<>();
    }

    public static Material getMaterialById(int id){
        for(int i = 0; i < personalMaterials.size(); i++){
            if(personalMaterials.get(i).getId() == id){
                return personalMaterials.get(i);
            }
        }
        return null;
    }

    //test
    public static Material getMaterialByName(String name){
        for(int i = 0; i < personalMaterials.size(); i++){
            if(personalMaterials.get(i).getName().equals(name)){
                return personalMaterials.get(i);
            }
        }
        return null;
    }

    public static Product getProductById(int id){
        for(int i = 0; i < personalProducts.size(); i++){
            if(personalProducts.get(i).getId() == id){
                return personalProducts.get(i);
            }
        }
        return null;
    }

    public static void addProduct(Product product){
        personalProducts.add(product);
    }

    public static Product getProduct(int id){
        return personalProducts.get(id);
    }
}
