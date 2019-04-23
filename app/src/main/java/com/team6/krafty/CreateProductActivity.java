package com.team6.krafty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * Class that allows the generation of a new product specific to a user and allows
 * upload to the database
 */
public class CreateProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String encodedImage;
    cardAdapter ca;
    private HashMap<Integer, Integer> materials;
    Spinner matSpinner;

    /**
     * Constructs the Activity and its components by inflating the xml, setting listeners to components,
     * and initializing necessary variables.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        //initialize hashmap of materials
        materials = new HashMap<>();
        ImageView iv = findViewById(R.id.productImg);
        //set image view click listener
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });
        //set up recyclerView for materials
        RecyclerView rv = findViewById(R.id.recyclerMats);
        ca = new cardAdapter(getbmps(),getMatNames());
        rv.setAdapter(ca);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //set up material spinner with adapter
        matSpinner = findViewById(R.id.spinnerMats);
        String[] matNames = Inventory.getMaterialCaptions();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,matNames);
        matSpinner.setAdapter(arrayAdapter);
        //set button click listeners
        Button submitButton = findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });
        Button addMaterialButton = findViewById(R.id.btnAddMat);
        addMaterialButton.setOnClickListener(new MaterialClick());
        ca.setListener(new cardAdapter.Listener() {
            @Override
            public void onClick(final int position) {
                new AlertDialog.Builder(CreateProductActivity.this)
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
    }

    /**
     * Inner class to handle clicks to add material to Product
     */
    private class MaterialClick  implements View.OnClickListener{

        /**
         * Handles click of add material button
         * @param v View that was pressed (Button)
         */
        @Override
        public void onClick(View v){
            //get selected item position
            int itemPos = matSpinner.getSelectedItemPosition();
            //check that an item is selected. If no, return.
            if(itemPos == -1){return;}
            //get the corresponding material ID
            int id = Inventory.getMaterial(itemPos).getId();
            //get quantity
            EditText qty = findViewById(R.id.etQty);
            //validate that qty is an integer
            try {
                Validator.validateIntEt(qty, "Quantity");
            }
            catch(KraftyRuntimeException e){
                Toast.makeText(CreateProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            int quantity = Integer.parseInt(qty.getText().toString());
            //add this material and qty to the hashmap and update the adapter
            materials.put(id, quantity);
            Log.d("MatID",Integer.toString(id));
            nullifyAdapter();
        }
    }

    /**
     * Method to handle click of the submit button. Gathers field data, validates, and submits
     * to the database.
     */
    private void onSubmitClick() {
        //reference fields
        EditText pName = findViewById(R.id.etName);
        EditText pQuant = findViewById(R.id.etQuantity);
        EditText pPrice = findViewById(R.id.etPrice);
        EditText pDesc = findViewById(R.id.etDesc);
        //validate data
        try{
            Validator.validateBasicEditText(pName, "Name");
            Validator.validateIntEt(pQuant, "Quantity");
            Validator.validateDoubleEt(pPrice, "Price");
            Validator.validateBasicEditText(pDesc, "Description");
        }
        //try to send to database
        catch(KraftyRuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        //gather data
        String name = pName.getText().toString();
        int quantity = Integer.parseInt(pQuant.getText().toString());
        float price = Float.parseFloat(pPrice.getText().toString());
        String desc = pDesc.getText().toString();
        //create controller and pass data
        ProductController pc = new ProductController();
        if(pc.createProduct(name,desc, encodedImage, quantity, materials,price, this)){
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
