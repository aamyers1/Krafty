package com.team6.krafty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ViewSpecificEvent extends AppCompatActivity{

    private int id;
    private Context context = this;
    private Event event;
    private HashMap <Integer, User> krafterList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
    }

    @Override
    public void onStart(){
        super.onStart();
        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);
        final EventsController controller = new EventsController();
        event = controller.getSpecificEvent(id,context);
        if (event == null){
            Toast.makeText(getApplicationContext(), "Failed to open event.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //set values of Event to controls
        TextView eventName = (TextView)findViewById(R.id.eventName);
        eventName.setText(event.getTitle());

        TextView eventDesc = (TextView)findViewById(R.id.eventDesc);
        eventDesc.setText(event.getDescription());

        TextView eventAddress = (TextView)findViewById(R.id.eventAddress);
        eventAddress.setText(event.getAddress());

        TextView eventDate = (TextView)findViewById(R.id.eventDate);
        eventDate.setText(event.getDate() + " to " + event.getEndDate());

        TextView eventTimeStart = (TextView)findViewById(R.id.eventTimeStart);
        eventTimeStart.setText(event.getTime() + " to " + event.getEndTime());

        TextView numOfVendors = (TextView)findViewById(R.id.eventVendors);
        numOfVendors.setText(String.valueOf(event.getVendorSpots()));

        TextView numSignedUp = (TextView)findViewById(R.id.numVendorsSigned);
        numSignedUp.setText(String.valueOf(event.getTakenSpots()));

        Switch eventOutdoors = (Switch)findViewById(R.id.eventOutdoors);
        if (event.getOutDoors()) eventOutdoors.setChecked(true);

        Switch eventPower = (Switch)findViewById(R.id.eventPower);
        if (event.getPower()) eventPower.setChecked(true);

        Switch eventFood = (Switch)findViewById(R.id.eventFood);
        if (event.getFood()) eventFood.setChecked(true);

        Switch eventWiFi = (Switch)findViewById(R.id.eventWiFi);
        if (event.getWifi()) eventWiFi.setChecked(true);

        Switch eventTables = (Switch)findViewById(R.id.eventTables);
        if (event.getTables()) eventTables.setChecked(true);

        final ImageView eventImage = findViewById(R.id.imageView);
        final Bitmap bmp;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = controller.parseEventImage(event.getImgString());
                if(bmp!= null){
                    eventImage.setImageBitmap(bmp);
                }
                else{
                    eventImage.setBackgroundColor(Color.rgb(188,225,232));
                }
            }
        });
        t.start();

        event.setKrafters(controller.getScheduledKrafters(event.getID(), context));

        setButtons();

        //TODO we have the list of krafters, now put them in the listview
        fillListView();
    }
    private void setButtons(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String username = sp.getString("username", "test");
        // Check if/else event is in schedule, display schedule/unschedule buttons accordingly
        if (event.usernameIsScheduled(username)) {
            Button btnEventUnschedule = (Button) findViewById(R.id.btnEventUnschedule);
            btnEventUnschedule.setVisibility(Button.VISIBLE);
            btnEventUnschedule.setClickable(true);
            btnEventUnschedule.setOnClickListener(new onUnscheduleClick());
        } else {
            Button btnEventSchedule = (Button) findViewById(R.id.btnEventSchedule);
            btnEventSchedule.setVisibility(Button.VISIBLE);
            btnEventSchedule.setClickable(true);
            btnEventSchedule.setOnClickListener(new onScheduleClick());
        }

        if(username.equals(event.getCreator())) {
            Button btnEventUpdate = (Button)findViewById(R.id.btnSubmit);
            btnEventUpdate.setVisibility(Button.VISIBLE);
            btnEventUpdate.setClickable(true);
            Button btnEventDelete = (Button)findViewById(R.id.btnEventDelete);
            btnEventDelete.setVisibility(Button.VISIBLE);
            btnEventDelete.setClickable(false);

            btnEventDelete.setOnClickListener(new onDeleteClick());
            btnEventUpdate.setOnClickListener(new onUpdateClick());
        }
    }

    private class onScheduleClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            final EventsController controller = new EventsController();
            if (controller.scheduleForEvent(id,context)){
                final ScheduleController scheduleController = new ScheduleController();
                // TODO add event to schedule when that shizz is all hooked up
                //scheduleController.addEvent(id, event);
            }
        }
    }

    private class onUnscheduleClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            final EventsController controller = new EventsController();
            // TODO Get scheduleId  and send for unscheduling
//            if (controller.unscheduleForEvent(id,context)){
//                final ScheduleController scheduleController = new ScheduleController();
//                // TODO remove event to schedule when that shizz is all hooked up
//                //scheduleController.removeEvent(id, event);
//            }
        }
    }

    private class onUpdateClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            Intent intent = new Intent(getApplicationContext(), ModifyEventActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    private class onDeleteClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(ViewSpecificEvent.this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Do you really want to delete this event?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            EventsController ec = new EventsController();
                            ec.deleteEvent(id, getApplicationContext());
                                finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getApplicationContext(), "Event not deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }

    private void fillListView(){

    }
}
