package com.team6.krafty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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

/**
 * GUI controller class for Modify Product activity
 */
public class ModifyProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String encodedImage = "";
    private int id;
    cardAdapter ca;
    private HashMap<Integer, Integer> materials;
    Product p;

    /**
     * Constructs the Activity and its components by inflating the xml, setting listeners to components,
     * and initializing necessary variables.
     * @param savedInstanceState previous saved instance if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        id = getIntent().getIntExtra("id", 0);
        p = Inventory.getProduct(id);
        materials = p.getMaterials();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView iv = findViewById(R.id.productImg);
        if(p.getBmp() != null) {
            iv.setImageBitmap(p.getBmp());
        }
        else{
            iv.setBackgroundColor(Color.rgb(188,225,232));
        }
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

        ca.setListener(new cardAdapter.Listener() {
            @Override
            public void onClick(final int position) {
                new AlertDialog.Builder(ModifyProductActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Do you want to remove this Material?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int id = 0;
                                String[] matNames =getMatNames();
                                for (int i=0; i <matNames.length; i ++){
                                    String matName = "";
                                    if (matNames[i].equals(ca.getCaption(position)))
                                        matName = matNames[i].substring(0,matNames[i].indexOf(":"));
                                    id = Inventory.getMaterialByName(matName).getId();
                                }
                                materials.remove(id);
                                nullifyAdapter();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(getApplicationContext(), "Material not removed.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
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

        EditText et = findViewById(R.id.etName);
        et.setText(p.getName());
        et = findViewById(R.id.etDesc);
        et.setText(p.getDescription());
        et = findViewById(R.id.etPrice);
        et.setText(String.valueOf(p.getPrice()));
        et = findViewById(R.id.etQuantity);
        et.setText(String.valueOf(p.getQuantity()));
    }

    /**
     * Listener for update button click. Validates the fields and updates the
     * product in the database
     */
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
        p.setName( pName.getText().toString());
        p.setQuantity(Integer.parseInt(pQuant.getText().toString()));
        p.setPrice(Float.parseFloat(pPrice.getText().toString()));
        p.setDescription(pDesc.getText().toString());
        p.setMaterials(materials);
        ProductController pc = new ProductController();

        if(pc.updateProduct(p,this)){
            ProductsFragment.nullifyAdapter();
            finish();
        }

    }

    /**
     * Gets the result of a request for a gallery image from the user
     * @param requestCode Code that identifies the request from others
     * @param resultCode Code that signals the state of the result
     * @param data Data returned from the request (Image data)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();


            ImageView imgProfile = findViewById(R.id.productImg);
            imgProfile.setImageDrawable(null);
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));

            //Convert image to bitmap, then into base64
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Validator.validateImage(imageAsBitmap, "Product Image");
            } catch (IOException e) { //for file not found
                e.printStackTrace();
            } catch (KraftyRuntimeException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } catch (RuntimeException e){
                if (e.getMessage().contains("draw too large")){
                    Toast.makeText(this, "Image too large", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //Put image in imageview
            imgProfile.setImageURI(imageUri);
            ByteArrayOutputStream byteArrOutStrm = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrOutStrm);
            byte[] bArr = byteArrOutStrm.toByteArray();
            encodedImage = Base64.encodeToString(bArr, Base64.DEFAULT).replace("+", "<");
        }
    }

    /**
     * Necessary method for the spinner adapter
     * @param parent AdapterView
     * @param view View
     * @param position Selected item index
     * @param id id of the selected item
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    /**
     * Necessary method for spinner adapter. If nothing selected, do nothing.
     * @param arg0
     */
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    /**
     * Gets all material names based on the Inventory
     * @return String array of material names
     */
    public String[] getMatNames(){
        String[] names = new String[materials.size()];
        int [] ids = getIds();
        for(int i = 0 ; i < ids.length; i ++){
            names[i] = Inventory.getMaterialById(ids[i]).getName() + ": " + materials.get(ids[i]);
        }
        return names;
    }

    /**
     * Gets all Bitmap images from the array of materials
     * @return Bitmap image array
     */
    public Bitmap[] getbmps(){
        Bitmap[] bmp = new Bitmap[materials.size()];
        int [] ids = getIds();
        for(int i = 0; i < ids.length; i++){
            Log.d("GET BMP ID", ids[i] + " ");
            Log.d("GET BMP ID", Inventory.getMaterialById(ids[i]).getName());
            bmp[i] = Inventory.getMaterialById(ids[i]).getBmp();
        }
        return bmp;
    }

    /**
     * Nullifies adapter when data is updated externally
     */
    public void nullifyAdapter(){
        ca.updateData(getbmps(), getMatNames());
        ca.notifyDataSetChanged();
    }

    /**
     * Gets the IDs of the materials in the materials HashMap
     * @return Array of integer material IDs
     */
    public int[] getIds(){
        int k = 0;
        int[] ids = new int[materials.size()];
        for(Integer i: materials.keySet()){
            ids[k] = i;
            k++;
        }
        return ids;
    }



}

