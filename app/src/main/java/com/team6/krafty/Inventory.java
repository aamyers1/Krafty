package com.team6.krafty;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Vector;

public class Inventory {
    //holds the inventory instance
    private static Inventory inventory = new Inventory();
    //holds a specific user's inventory of materials
    private static Vector<Material> personalMaterials;

    //constructor is PRIVATE
    private Inventory(){
        personalMaterials = new Vector<>();
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

    //returns the entire arraylist
    //TODO: Check that this will return a copy and not a reference because we don't actually want modification through this path.
    public static Vector<Material> getPersonalMaterials() {
        return personalMaterials;
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

    //clears all inventory items
    //TODO:Add clear Products when products are implemented
    public static void clearAll(){
        personalMaterials = new Vector<>();
    }
}
