package com.team6.krafty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * GUI controller class for view specific event activity
 */
public class ViewSpecificEvent extends AppCompatActivity{

    private int id;
    private Context context = this;
    private Event event;
    private HashMap <Integer, User> krafterList;
    private ListView krafterListView;

    /**
     * Method called on object instantiation. Inflates XML file activity_view_event.xml
     * @param savedInstanceState previous saved instance if any
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
    }

    /**
     * Method called once xml is loaded, populates all the containers with data from the event
     * Also sets Buttons
     */
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
        try{
            t.join();
        }
        catch (Exception e){

        }

        FloatingActionButton report = findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra("id", event.getID());
                intent.putExtra("category", "e");
                startActivity(intent);
            }
        });
        event.setKrafters(controller.getScheduledKrafters(event.getID(), context));

        TextView numSignedUp = (TextView)findViewById(R.id.numVendorsSigned);
        numSignedUp.setText(String.valueOf(event.getTakenSpots()));

        krafterListView = (ListView) findViewById(R.id.lvKrafters);
        fillListView();
        krafterListView.setOnItemClickListener(new onBusinessClick());
        setListViewHeightBasedOnChildren(krafterListView);

        setButtons();
    }

    /**
     * Method to set update and delete buttons, depending on if the current user is the original
     * event creator. Also sets schedule/unschedule buttons depending upon if the user is scheduled.
     * Also sets listeners for button clicks.
     */
    private void setButtons(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String username = sp.getString("username", "test");
        ScheduleController.getSchedule(getApplicationContext());
        // Check if/else event is in schedule, display schedule/unschedule buttons accordingly
        if (ScheduleController.getInstance().checkScheduled(event.getID())) {
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

    /**
     * Listener for schedule button click. Adds event to user's schedule
     */
    private class onScheduleClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            ScheduleController controller = ScheduleController.getInstance();
            controller.scheduleForEvent(id,context);
            Button b = (Button)view;
            b.setVisibility(View.INVISIBLE);
            b.setClickable(false);
            setButtons();
        }
    }

    /**
     * Listener for unschedule click. Removes from the user's schedule
     */
    private class onUnscheduleClick implements View.OnClickListener{
        @Override
        public void onClick(View view){
            ScheduleController controller = ScheduleController.getInstance();
            int schedID = Schedule.getInstance().getSchedIDByEventID(id);
            controller.unscheduleForEvent(schedID, view.getContext(), "e");
            Button b = (Button)view;
            b.setVisibility(View.INVISIBLE);
            b.setClickable(false);
            setButtons();
        }
    }

    /**
     * Listener for update button click. calls ModifyEventActivity
     */
    private class onUpdateClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            Intent intent = new Intent(getApplicationContext(), ModifyEventActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    /**
     * Listener for delete button click. Deletes event
     */
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

    /**
     * Lists all the Krafter's attending the event by their business name
     */
    private void fillListView(){
        ArrayList<String> krafterBusinesses = event.getKraftersBusinesses();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                krafterBusinesses
        );
        krafterListView.setAdapter(arrayAdapter);
    }

    /**
     * Listener for click on Business names of Krafters from a list view.
     * Calls the KrafterProfileActivity
     */
    private class onBusinessClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            String username = event.getKrafters().keySet().toArray()[(int)id].toString();
            Intent intent = new Intent(getApplicationContext(), KrafterProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    /**
     * Method to set the weight of ListView based on number of children
     * @param listView ListView element that contains the businesses attending
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
