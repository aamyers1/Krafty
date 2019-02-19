package com.team6.krafty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class CreateEventActivity  extends AppCompatActivity {
    private String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        Boolean power, wifi, outdoors, tables, food;
        String creator, city, street, state, zipcode, startTime, endTime, startDate, endDate, name, imgString, description;
        int vendorSpots;
        double longitude, latitude;
        Bitmap bmp;

        EditText et = findViewById(R.id.eventName);
        name = et.getText().toString();
        TextView tv = findViewById(R.id.tvStartDate);
        startDate = tv.getText().toString();
        tv = findViewById(R.id.tvEndDate);
        endDate = tv.getText().toString();
        tv = findViewById(R.id.tvStartTime);
        startTime = tv.getText().toString();
        tv = findViewById(R.id.tvEndTime);
        endTime = tv.getText().toString();
        et = findViewById(R.id.txtVendorNum);
        vendorSpots = Integer.parseInt(et.getText().toString());
        et = findViewById(R.id.txtStreet);
        street = et.getText().toString();
        et = findViewById(R.id.txtCity);
        city = et.getText().toString();
        et = findViewById(R.id.txtZip);
        zipcode = et.getText().toString();
        et = findViewById(R.id.txtDescription);
        description = et.getText().toString();
        Switch sw = findViewById(R.id.swOutdoors);
        outdoors = sw.getShowText();
        sw = findViewById(R.id.swPower);
        power = sw.getShowText();
        sw = findViewById(R.id.swFood);
        food = sw.getShowText();
        sw = findViewById(R.id.swWifi);
        wifi = sw.getShowText();
        sw = findViewById(R.id.swTables);
        tables = sw.getShowText();

        LatLng theLatLng = getLocationFromAddress()

        Event event = new Event(name,startDate,endDate,startTime,endTime,vendorSpots,street,city,zipcode,description,outdoors,power,food,wifi,tables);
        EventsController ec = new EventsController();
        if(ec.createEvent(event,getApplicationContext())){
            cardAdapter.resetData();
            finish();
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
            String timeYouChose =  (hour + 1) + ":" + minute;
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
            String timeYouChose =  (hour + 1) + ":" + minute;
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
}
