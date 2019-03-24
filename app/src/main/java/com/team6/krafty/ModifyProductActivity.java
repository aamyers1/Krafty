package com.team6.krafty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ModifyProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String encodedImage = "";
    private int id;
    cardAdapter ca;
    private HashMap<Integer, Integer> materials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        id = getIntent().getIntExtra("id", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ModifyProductActivity.AsyncFillFields asyncFillFields = new ModifyProductActivity.AsyncFillFields();
        asyncFillFields.execute();
        ImageView iv = findViewById(R.id.productImg);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });

        RecyclerView rv = findViewById(R.id.recyclerMats);
        ca = new cardAdapter(getbmps(),getMatNames());
        rv.setAdapter(ca);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        final Spinner matSpinner = findViewById(R.id.spinnerMats);
        String[] matNames = Inventory.getMaterialCaptions();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,matNames);
        matSpinner.setAdapter(arrayAdapter);

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
                int itemPos = matSpinner.getSelectedItemPosition();
                int id = Inventory.getMaterial(itemPos).getId();
                EditText qty = findViewById(R.id.etQty);
                try {
                    Validator.validateIntEt(qty, "Quantity");
                }
                catch(KraftyRuntimeException e){
                    Toast.makeText(ModifyProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = Integer.parseInt(qty.getText().toString());
                materials.put(id, quantity);
                nullifyAdapter();
            }
        });
    }

    public void onSubmitClick(){
        EditText pName = findViewById(R.id.etName);
        EditText pQuant = findViewById(R.id.etQuantity);
        EditText pPrice = findViewById(R.id.etPrice);
        EditText pDesc = findViewById(R.id.etDesc);
        try{
            Validator.validateBasicEditText(pName, "Name");
            Validator.validateIntEt(pQuant, "Quantity");
            Validator.validateDoubleEt(pPrice, "Price");
            Validator.validateBasicEditText(pDesc, "Description");
        }
        catch(KraftyRuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        String name = pName.getText().toString();
        int quantity = Integer.parseInt(pQuant.getText().toString());
        float price = Float.parseFloat(pPrice.getText().toString());
        String desc = pDesc.getText().toString();
        ProductController pc = new ProductController();
        String creator = SessionManager.getUsername(this);
        Product product = new Product(name,desc,encodedImage,quantity,materials,price,creator);

        if(pc.updateProduct(product,id,this)){
            ProductsFragment.nullifyAdapter();
            finish();
        }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public String[] getMatNames(){
        String[] names = new String[materials.size()];
        int [] ids = getIds();
        for(int i = 0 ; i < materials.size(); i ++){
            names[i] = Inventory.getMaterialById(ids[i]).getName() + ": " + materials.get(ids[i]);
        }
        return names;
    }

    public Bitmap[] getbmps(){
        Bitmap[] bmp = new Bitmap[materials.size()];
        int [] ids = getIds();
        for(int i = 0; i < materials.size(); i++){
            bmp[i] = Inventory.getMaterialById(ids[i]).getBmp();
        }
        return bmp;
    }

    public void nullifyAdapter(){
        ca.updateData(getbmps(), getMatNames());
        ca.notifyDataSetChanged();
    }

    public int[] getIds(){
        int k = 0;
        int[] ids = new int[materials.size()];
        for(Integer i: materials.keySet()){
            ids[k] = i;
            k++;
        }
        return ids;
    }

    public class AsyncFillFields extends AsyncTask<Void, Void, Void> {
        Product p;

        @Override
        public Void doInBackground(Void... params) {
            ProductController pc = new ProductController();
            p = pc.getProduct(id, getApplicationContext());
            materials=p.getMaterials();
            return null;
        }

        @Override
        public void onPostExecute(Void v) {
            EditText et = findViewById(R.id.etName);
            et.setText(p.getName());
            et = findViewById(R.id.etDesc);
            et.setText(p.getDescription());
            et = findViewById(R.id.etPrice);
            et.setText(String.valueOf(p.getPrice()));
            et = findViewById(R.id.etQuantity);
            et.setText(String.valueOf(p.getQuantity()));
        }
    }


}

