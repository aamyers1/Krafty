package com.team6.krafty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity allows a user to write a report on either an event or
 * a user depending on the sending class
 */
public class ReportActivity extends AppCompatActivity {
    int id;
    String type;

    /**
     * Method that runs on activity start. Sets up the view and sets listeners for events.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        id = getIntent().getIntExtra("id", -1);
        type = getIntent().getStringExtra("category");
        if(id ==-1){
            finish();
        }
        Button reportButton = findViewById(R.id.submitBtn);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText et = findViewById(R.id.etReport);
                    Validator.validateBasicEditText(et, "Report");
                    if(ReportController.createReport(ReportActivity.this, type, et.getText().toString(), id)){
                        Toast.makeText(ReportActivity.this, "Report Sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(ReportActivity.this, "Report send failure!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(KraftyRuntimeException k){
                    Toast.makeText(ReportActivity.this, k.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
