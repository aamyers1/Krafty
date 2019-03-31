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

        EditText eName = findViewById(R.id.eventName);
        TextView tvStartDate = findViewById(R.id.tvStartDate);
        TextView tvEndDate = findViewById(R.id.tvEndDate);
        TextView tvStartTime = findViewById(R.id.tvStartTime);
        TextView tvEndTime = findViewById(R.id.tvEndTime);
        EditText eVendorNum = findViewById(R.id.txtVendorNum);
        EditText eStreet = findViewById(R.id.txtStreet);
        EditText eCity = findViewById(R.id.txtCity);
        EditText eState = findViewById(R.id.txtState);
        EditText eZip = findViewById(R.id.txtZip);
        EditText eDescription = findViewById(R.id.txtDescription);

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

        try{
            Validator.validateImage();
            Validator.validateNameEditText(eName,"Name");
            Validator.validateDateSet(tvStartDate, "START");
            Validator.validateDateSet(tvEndDate, "END");
            Validator.validateTimeSet(tvStartTime, "OPENS");
            Validator.validateTimeSet(tvEndTime, "CLOSES");
            Validator.validateIntEt(eVendorNum, "Capacity of Krafters");
            Validator.validateDescriptionEditText(eDescription, "Let people know...");
            Validator.validateStreetEditText(eStreet, "Event Address");
            street = eStreet.getText().toString();
            Validator.validateCityEditText(eCity, "City");
            city = eCity.getText().toString();
            Validator.validateStateEditText(eState, "State");
            state = eState.getText().toString();
            Validator.validateZipEditText(eZip, "Zip");
            zipcode = eZip.getText().toString();
            String address = street + " " + city + " " + state + " " + zipcode;
            Validator.validateAddress(street, city, state, zipcode, getApplicationContext());
            theLatLng = Validator.getLocationFromAddress(getApplicationContext(), address);
        } catch (KraftyRuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        name = eName.getText().toString();
        startDate = tvStartDate.getText().toString();
        endDate = tvEndDate.getText().toString();
        startTime = tvStartDate.getText().toString();
        endTime = tvEndTime.getText().toString();
        vendorSpots = Integer.parseInt(eVendorNum.getText().toString());
        description = eDescription.getText().toString();
        latitude = theLatLng.latitude;
        longitude = theLatLng.longitude;

        Event event = new Event(encodedImage, name,startDate,endDate,startTime,endTime,vendorSpots,
                street,city,state,zipcode,latitude,longitude,description,outdoors,power,food,wifi,
                tables);
        EventsController ec = new EventsController();
        if(ec.updateEvent(event, id, this)){
            finish();
            Toast.makeText(this, "Event Updated!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Event update failed!", Toast.LENGTH_SHORT).show();
        }
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
            EditText et = findViewById(R.id.eventName);
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
