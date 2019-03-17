package com.team6.krafty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModifyEventActivity extends AppCompatActivity {

    private String encodedImage = "";
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        id = getIntent().getIntExtra("id", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AsyncFillFields asyncFillFields = new AsyncFillFields();
        asyncFillFields.execute();
        ImageView iv = findViewById(R.id.imgEvent);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });

    Button btnGetStart = findViewById(R.id.btnGetStartDate);
        btnGetStart.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dateStartDialog= new DatePickerDialog(v.getContext(), startDatePickerListener, mYear, mMonth, mDay);
            dateStartDialog.show();

        }
    });

    Button btnGetEnd = findViewById(R.id.btnGetEndDate);
        btnGetEnd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dateEndDialog= new DatePickerDialog(v.getContext(), endDatePickerListener, mYear, mMonth, mDay);
            dateEndDialog.show();

        }
    });

    Button btnGetClose = findViewById(R.id.btnGetEndTime);
        btnGetClose.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timeEndDialog= new TimePickerDialog(v.getContext(), endTimePickerListener, mHour, mMinute, false);
            timeEndDialog.show();

        }
    });

    Button btnGetOpen = findViewById(R.id.btnGetStartTime);
        btnGetOpen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timeStartDialog= new TimePickerDialog(v.getContext(), startTimePickerListener, mHour, mMinute, false);
            timeStartDialog.show();

        }
    });

    Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onSubmitClick();
        }
    });
}


    //TODO: PLEASE DEAR GOD BREAK UP SOME OF THIS (AND WE CAN REUSE IN THE OTHER SIMILAR FILE IF WE DO)
    //handles submit click
    public void onSubmitClick(){
        //essentially just gathers strings from various editTexts

        Boolean power = false, wifi= false, outdoors= false, tables= false, food= false;
        String creator, city, street, state, zipcode, startTime, endTime, startDate,
                endDate, name, description;
        int vendorSpots = 0;
        double longitude = 0, latitude = 0;
        Bitmap bmp = null;
        LatLng theLatLng = null;
        EditText et = null;

        et = findViewById(R.id.eventName);
        name = et.getText().toString();
        if (name.length() <= 0 ){
            Toast.makeText(getApplicationContext(), "Event Name is required", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.length() > 256){
            Toast.makeText(getApplicationContext(), "Event Name too long", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView tv = findViewById(R.id.tvStartDate);
        startDate = tv.getText().toString();
        if (startDate.equals("No date selected") ){
            Toast.makeText(getApplicationContext(), "Start date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        tv = findViewById(R.id.tvEndDate);
        endDate = tv.getText().toString();
        if (endDate.equals("No date selected")){
            //TODO check endDate is after startDate
            Toast.makeText(getApplicationContext(), "End date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        tv = findViewById(R.id.tvStartTime);
        startTime = tv.getText().toString();
        if (startTime.equals("No opening time" )){
            Toast.makeText(getApplicationContext(), "Opening time is required", Toast.LENGTH_SHORT).show();
            return;
        }
        tv = findViewById(R.id.tvEndTime);
        endTime = tv.getText().toString();
        if (endTime.equals( "No closing time" )){
            //TODO check endTime is after startTime
            Toast.makeText(getApplicationContext(), "Closing time is required", Toast.LENGTH_SHORT).show();
            return;
        }
        et = findViewById(R.id.txtVendorNum);
        try {
            vendorSpots = Integer.parseInt(et.getText().toString());
            if (vendorSpots <= 0) throw new InvalidParameterException();
        } catch (InvalidParameterException e){
            Toast.makeText(getApplicationContext(), "Capacity of Krafters too low", Toast.LENGTH_SHORT).show();
            return;
        } catch (NumberFormatException e) {
            if (et.getText().toString().length() > 0) {
                Toast.makeText(getApplicationContext(), "Capacity of Krafters too high", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Capacity of Krafters required", Toast.LENGTH_SHORT).show();
            }
            return;
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Capacity of Krafters error", Toast.LENGTH_SHORT).show();
            return;
        }
        et = findViewById(R.id.txtStreet);
        street = et.getText().toString();
        if (street.length() > 64){
            Toast.makeText(getApplicationContext(), "Street address too long", Toast.LENGTH_SHORT).show();
            return;
        } else if (street.length() <= 0){
            Toast.makeText(getApplicationContext(), "Street address required", Toast.LENGTH_SHORT).show();
            return;
        }
        et = findViewById(R.id.txtCity);
        city = et.getText().toString();
        if (city.length() > 64){
            Toast.makeText(getApplicationContext(), "City too long", Toast.LENGTH_SHORT).show();
            return;
        } else if (city.length() <= 0) {
            Toast.makeText(getApplicationContext(), "City required", Toast.LENGTH_SHORT).show();
            return;
        }
        et = findViewById(R.id.txtState);
        state = et.getText().toString();
        if (state.length() > 2){
            Toast.makeText(getApplicationContext(), "State too long", Toast.LENGTH_SHORT).show();
            return;
        } else if (state.length() <= 0){
            Toast.makeText(getApplicationContext(), "State required", Toast.LENGTH_SHORT).show();
            return;
        }
        et = findViewById(R.id.txtZip);
        zipcode = et.getText().toString();
        if (zipcode.length() > 10){
            Toast.makeText(getApplicationContext(), "Zip Code too long", Toast.LENGTH_SHORT).show();
            return;
        } else if(zipcode.length() <= 0){
            Toast.makeText(getApplicationContext(), "Zip Code required", Toast.LENGTH_SHORT).show();
            return;
        }
        et = findViewById(R.id.txtDescription);
        description = et.getText().toString();
        if (description.length() > 256){
            Toast.makeText(getApplicationContext(), "Description too long", Toast.LENGTH_SHORT).show();
            return;
        }
        Switch sw = findViewById(R.id.swOutdoors);
        outdoors = sw.isChecked();
        sw = findViewById(R.id.swPower);
        power = sw.isChecked();
        sw = findViewById(R.id.swFood);
        food = sw.isChecked();
        sw = findViewById(R.id.swWifi);
        wifi = sw.isChecked();
        sw = findViewById(R.id.swTables);
        tables = sw.isChecked();

        try {
            theLatLng = getLocationFromAddress(getApplicationContext(), street + " " + city + " " + state + " " + zipcode);

            if (theLatLng == null) {
                throw new Exception();
            } else {
                longitude = theLatLng.longitude;
                latitude = theLatLng.latitude;
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Unable to validate address", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event(encodedImage, name,startDate,endDate,startTime,endTime,vendorSpots,street,city,state,zipcode,latitude,longitude,description,outdoors,power,food,wifi,tables);
        EventsController ec = new EventsController();
        if(ec.updateEvent(event, id, this)){
            finish();
            Toast.makeText(this, "Event Updated!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Event update failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String dateYouChose = (monthOfYear + 1) + "-" + dayOfMonth  + "-" + year;
            TextView tvStartDate = findViewById(R.id.tvStartDate);
            tvStartDate.setText(dateYouChose );
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String dateYouChose =  (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
            TextView tvEndDate = findViewById(R.id.tvEndDate);
            tvEndDate.setText(dateYouChose );
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            String timeYouChose =  (hour) + ":" + minute;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = null;
            try {
                _24HourDt = _24HourSDF.parse(timeYouChose);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView tvEndTime = findViewById(R.id.tvEndTime);
            tvEndTime.setText(_12HourSDF.format(_24HourDt).replace(" ",""));

        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            String timeYouChose =  (hour) + ":" + minute;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = null;
            try {
                _24HourDt = _24HourSDF.parse(timeYouChose);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView tvStartTime = findViewById(R.id.tvStartTime);
            tvStartTime.setText(_12HourSDF.format(_24HourDt).replace(" ",""));
        }
    };

    //For results of requests
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();

            //Put image in imageview
            ImageView imgProfile = findViewById(R.id.imgEvent);
            imgProfile.setImageURI(imageUri);

            //Convert image to bitmap, then into base64
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) { //for file not found
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrOutStrm = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrOutStrm);
            byte[] bArr = byteArrOutStrm.toByteArray();
            encodedImage = Base64.encodeToString(bArr, Base64.DEFAULT).replace("+", "<");
        }
    }

    public class AsyncFillFields extends AsyncTask<Void, Void, Void>{
        Event e;
        @Override
        public Void doInBackground(Void...params){
            EventsController ec = new EventsController();
            e = ec.getSpecificEvent(id, getApplicationContext());
            return null;
        }

        @Override
        public void onPostExecute(Void v){
            EditText et = findViewById(R.id.materialName);
            et.setText(e.getName());
            et = findViewById(R.id.txtVendorNum);
            et.setText(e.getVendorSpots() + "");
            et = findViewById(R.id.txtStreet);
            et.setText(e.getStreet());
            et = findViewById(R.id.txtCity);
            et.setText(e.getCity());

            et = findViewById(R.id.txtState);
            et.setText(e.getState());

            et = findViewById(R.id.txtZip);
            et.setText(e.getZipCode());

            et = findViewById(R.id.txtDescription);
            et.setText(e.getDescription());

            TextView tv = findViewById(R.id.tvStartDate);
            tv.setText(e.getStartDate());

            tv = findViewById(R.id.tvEndDate);
            tv.setText(e.getEndDate());

            tv = findViewById(R.id.tvStartTime);
            tv.setText(e.getStartTime());

            tv = findViewById(R.id.tvEndTime);
            tv.setText(e.getEndTime());

            Switch tb = findViewById(R.id.swFood);
            tb.setChecked(e.getFood());

            tb = findViewById(R.id.swOutdoors);
            tb.setChecked(e.getOutDoors());

            tb = findViewById(R.id.swPower);
            tb.setChecked(e.getPower());

            tb = findViewById(R.id.swTables);
            tb.setChecked(e.getTables());

            tb = findViewById(R.id.swWifi);
            tb.setChecked(e.getWifi());


        }
    }

}
