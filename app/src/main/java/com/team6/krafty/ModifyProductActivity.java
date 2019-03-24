package com.team6.krafty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ModifyProductActivity extends AppCompatActivity {
    private String encodedImage = "";
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        id = getIntent().getIntExtra("id", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ModifyProductActivity.AsyncFillFields asyncFillFields = new ModifyProductActivity.AsyncFillFields();
        asyncFillFields.execute();
        ImageView iv = findViewById(R.id.imgEvent);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });

        Button btnAddMat = findViewById(R.id.btnAddMat);
        btnAddMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddMatClick();
            }
        });
    }

    public void onSubmitClick(){
        String name , quantity, price;

        EditText pName = findViewById(R.id.etName);
        EditText pQuant = findViewById(R.id.etQuantity);
        EditText pPrice = findViewById(R.id.etPrice);
        try{
            Validator.validateBasicEditText(pName, "Name");
            Validator.validateIntEt(pQuant, "Quantity");
            Validator.validateDoubleEt(pPrice, "Price");
        }
        catch(KraftyRuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void onAddMatClick(){
        // TODO: this!
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();

            //Put image in imageview
            ImageView imgProfile = findViewById(R.id.matImage);
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

    public class AsyncFillFields extends AsyncTask<Void, Void, Void> {
        Product p;

        @Override
        public Void doInBackground(Void... params) {
            ProductController pc = new ProductController();
            p = pc.getProduct(id, getApplicationContext());
            return null;
        }

        @Override
        public void onPostExecute(Void v) {
            EditText et = findViewById(R.id.etName);
            et.setText(p.getName());
           // et = findViewById(R.id.etDescription);
            //et.setText(p.getDesc);
            et = findViewById(R.id.etPrice);
            et.setText(String.valueOf(p.getPrice()));
            et = findViewById(R.id.etQuantity);
            et.setText(String.valueOf(p.getQuantity()));
        }
    }
}
