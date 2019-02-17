package com.team6.krafty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSpecificEvent extends AppCompatActivity{

    private int id;
    private Context context = this;
    private Event event;
    private  User profile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);
        EventsController controller = new EventsController();
        event = controller.getSpecificEvent(id,context);
        //set values of Event to controls
        TextView eventName = (TextView)findViewById(R.id.eventName);
        eventName.setText(event.getTitle());

        TextView eventDesc = (TextView)findViewById(R.id.eventDesc);
        eventDesc.setText(event.getDescription());

        TextView eventAddress = (TextView)findViewById(R.id.eventAddress);
        eventAddress.setText(event.getAddress());

        TextView eventDate = (TextView)findViewById(R.id.eventDate);
        eventAddress.setText(event.getDate());

        TextView eventTimeStart = (TextView)findViewById(R.id.eventTimeStart);
        eventTimeStart.setText(event.getTime());

        TextView eventTimeEnd = (TextView)findViewById(R.id.eventTimeEnd);
        eventTimeEnd.setText(event.getEndTime());

        TextView numOfVendors = (TextView)findViewById(R.id.eventVendors);
        numOfVendors.setText(String.valueOf(event.getVendorSpots()));

        TextView numSignedUp = (TextView)findViewById(R.id.numVendorsSigned);
        numSignedUp.setText(String.valueOf(event.getTakenSpots()));
       /*
        Switch eventOutdoors = (Switch)findViewById(R.id.eventOutdoors);
        if (event.getOutDoors()) eventOutdoors.setChecked(true);

        Switch eventPower = (Switch)findViewById(R.id.eventPower);
        if (event.getPower()) eventPower.setChecked(true);

        //Switch eventFood = (Switch)findViewById(R.id.eventFood);
        //if (event.getFood()) eventFood.setChecked(true);

        Switch eventWiFi = (Switch)findViewById(R.id.eventWiFi);
        if (event.getWifi()) eventWiFi.setChecked(true);

        Switch eventTables = (Switch)findViewById(R.id.eventTables);
        if (event.getTables()) eventTables.setChecked(true);
        */
        ImageView materialImage = findViewById(R.id.imageView);
        if(event.getBmp()!= null){
            materialImage.setImageBitmap(event.getBmp());
        }
        else{
            materialImage.setBackgroundColor(Color.rgb(188,225,232));
        }
        setButtons();
    }

    private void setButtons(){

        final DBManager dbManager = new DBManager();
        final String token = SessionManager.getToken(this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                profile = new User();
                profile.parseJson(dbManager.getUser(token, ""));
            }
        });
        t.start();
        try{
            t.join();
            //If the event creator, show and activate Update and delete buttons.
            SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("userType", profile.getUsername());
            edit.apply();

            if(profile.getUsername().equals(event.getCreator())) {
                Button btnEventUpdate = (Button)findViewById(R.id.btnEventUpdate);
                btnEventUpdate.setVisibility(Button.VISIBLE);
                btnEventUpdate.setClickable(true);
                Button btnEventDelete = (Button)findViewById(R.id.btnEventDelete);
                btnEventDelete.setVisibility(Button.VISIBLE);
                btnEventDelete.setClickable(false);

                btnEventDelete.setOnClickListener(new onDeleteClick());
                //Todo: Update button listener
                //btnEventUpdate.setOnClickListener(new onUpdateClick);

            }
            else{

            }
        }
        catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }
    }

    private class onDeleteClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(ViewSpecificEvent.this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Do you really want to delete this material?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            EventsController ec = new EventsController();
                            if (ec.deleteEvent(id, getApplicationContext())) {
                                //Inventory.removeMaterial(matId);
                                //cardAdapter.resetData();
                                //InventoryFragment.nullifyAdapter();
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getApplicationContext(), "Material not deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }



}
