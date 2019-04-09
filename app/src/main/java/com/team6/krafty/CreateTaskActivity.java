package com.team6.krafty;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateTaskActivity extends AppCompatActivity {
    cardAdapter ca;
    private int prodId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        final Spinner prodSpinner = findViewById(R.id.spinnerProds);
        String[] prodNames = Inventory.getProductCaptions();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,prodNames);
        prodSpinner.setAdapter(arrayAdapter);

        Button submitButton = findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });

        prodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == -1){return;}
                prodId = Inventory.getProduct(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Button btnGetDate = findViewById(R.id.btnGetDate);
        btnGetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateStartDialog= new DatePickerDialog(v.getContext(), datePickerListener, mYear, mMonth, mDay);
                dateStartDialog.show();

            }
        });

        Button btnGetTime = findViewById(R.id.btnGetTime);
        btnGetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timeEndDialog= new TimePickerDialog(v.getContext(), timePickerListener, mHour, mMinute, false);
                timeEndDialog.show();

            }
        });
    }


    //handles submit click
    public void onSubmitClick(){
        //essentially just gathers strings from various editTexts

        int qty;
        String date, time;

        EditText eQuantity = findViewById(R.id.etQty);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);

        try{
            Validator.validateDateSet(tvDate, "DATE");
            Validator.validateTimeSet(tvTime, "TIME");
            Validator.validateIntEt(eQuantity, "Quantity Required");
        } catch (KraftyRuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        qty = Integer.parseInt(eQuantity.getText().toString());
        date = tvDate.getText().toString();
        time = tvTime.getText().toString();

        Task task = new Task(0, prodId, qty, date, time);
        ScheduleController sc = ScheduleController.getInstance();
        if(sc.createTask(task,getApplicationContext())){
            finish();
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String dateYouChose = (monthOfYear + 1) + "-" + dayOfMonth  + "-" + year;
            TextView tvDate = findViewById(R.id.tvDate);
            tvDate.setText(dateYouChose );
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
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
            TextView tvTime = findViewById(R.id.tvTime);
            tvTime.setText(_12HourSDF.format(_24HourDt).replace(" ",""));

        }
    };

    //For results of requests
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }
}
