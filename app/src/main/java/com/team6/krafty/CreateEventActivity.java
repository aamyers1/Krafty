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
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                boolean is24Hour = false;
                TimePickerDialog timeEndDialog= new TimePickerDialog(v.getContext(), endTimePickerListener, mHour, mMinute, is24Hour);
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
                boolean is24Hour = false;
                TimePickerDialog timeStartDialog= new TimePickerDialog(v.getContext(), startTimePickerListener, mHour, mMinute, is24Hour);
                timeStartDialog.show();

            }
        });

//        Button btnSubmit = findViewById(R.id.submit);
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onSubmitClick();
//            }
//        });
    }




    //handles submit click
    public void onSubmitClick(){
//        //essentially just gathers strings from various editTexts
//        String matName, matPrice, matQuantity, matLocation;
//        EditText et = findViewById(R.id.materialName);
//        matName = et.getText().toString();
//        et = findViewById(R.id.price);
//        matPrice = et.getText().toString();
//        et = findViewById(R.id.quantity);
//        matQuantity = et.getText().toString();
//        et = findViewById(R.id.location);
//        matLocation = et.getText().toString();
//        MaterialController mc = new MaterialController();
//        if(mc.addMaterial(matName, encodedImage, matQuantity, matPrice, matLocation,this)){
//            cardAdapter.resetData();
//            InventoryFragment.nullifyAdapter();
//            finish();
//        }
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
            String dateYouChose = (monthOfYear + 1) + "/" + dayOfMonth  + "/" + year;
            TextView tvStartDate = findViewById(R.id.tvStartDate);
            tvStartDate.setText(dateYouChose );
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String dateYouChose =  (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
            TextView tvEndDate = findViewById(R.id.tvEndDate);
            tvEndDate.setText(dateYouChose );
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            String dateYouChoosed =  (hour + 1) + ":" + minute;
            TextView tvEndDate = findViewById(R.id.tvEndTime);
            tvEndDate.setText(dateYouChoosed );
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            String dateYouChoosed =  (hour + 1) + ":" + minute;
            TextView tvEndDate = findViewById(R.id.tvStartTime);
            tvEndDate.setText(dateYouChoosed );
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
