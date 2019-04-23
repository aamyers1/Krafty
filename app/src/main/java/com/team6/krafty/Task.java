package com.team6.krafty;

public class Task implements Schedulable {

    String date;
    String time;
    int id;
    int prodId;
    int qty;

    /**
     * Generic task constructor
     * @param id ID of the task object
     * @param prodId ID of the product that was scheduled by the task object
     * @param qty Quantity of product to be created
     * @param date Date the product is needed by
     * @param time Time the product is needed by
     */
    public Task(int id, int prodId, int qty, String date, String time){
        this.date = date;
        this.time = time;
        this.id = id;
        this.prodId = prodId;
        this.qty = qty;
    }

    /**
     * Returns task object's date
     * @return Date the task is due
     */
    public String getDate(){
        return date;
    }

    /**
     * Returns task object's time
     * @return Time the task is due
     */
    public String getTime(){
        return time;
    }

    /**
     * Returns task object's title
     * @return Title of the product
     */
    public String getTitle(){
        if(Inventory.getProductById(prodId) != null) {
            return Inventory.getProductById(prodId).getName() + ": " + qty;
        }
        else{
            return "Product: " + qty;
        }
    }

    /**
     * Returns task object's type
     * @return Type of the task, 2 for product
     */
    public int getType(){
        return 2;
    }

    /**
     * Returns task's ID
     * @return ID of the task object
     */
    public int getID(){
        return id;
    }

    /**
     * Sets task's ID
     * @param id ID to be set for the task
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Returns JSON string of task object
     * @return JSON string of the task object
     */
    public String getJson(){
        String date = getTime() + " " + getDate();
        return "pid=" +prodId + "&qty=" + qty + "&date=" + date;
    }
}
