package com.team6.krafty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity {
    int id;
    String type;
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
                    ReportController.createReport(ReportActivity.this, type, et.getText().toString(), id);
                }
                catch(KraftyRuntimeException k){
                    Toast.makeText(ReportActivity.this, k.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
