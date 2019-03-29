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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ModifyMaterialActivity extends AppCompatActivity {

    //Variable for passing the material array position
    private int matId;
    private int id;
    private String encodedImage = "";
    Material copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_material);
        //get the position id
        id = getIntent().getIntExtra("EXTRA_ID", 1);
        //get the specified material using the id
        Material thisMat = Inventory.getMaterial(id);
        matId = thisMat.getId();
        copy = new Material();
        copy.setName(thisMat.getName());
        copy.setPrice(thisMat.getPrice());
        copy.setQuantity(thisMat.getQuantity());
        copy.setBmp(thisMat.getBmp());
        copy.setPurchased(thisMat.getPurchased());
        //Find the needed views, set values based on the material
        Button addQty = findViewById(R.id.Plus);
        Button minusQty = findViewById(R.id.Minus);
        Button modify = findViewById(R.id.btnSubmit);
        Button delete = findViewById(R.id.btnDelete);
        EditText quantity = findViewById(R.id.etQuantity);
        quantity.setText(thisMat.getQuantity() + "");

        ImageView materialImage = findViewById(R.id.imgMat);
        if(thisMat.getBmp()!= null){
            materialImage.setImageBitmap(thisMat.getBmp());
        }
        else{
            materialImage.setBackgroundColor(Color.rgb(188,225,232));
        }

        materialImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });

        EditText materialName = findViewById(R.id.etTitle);
        materialName.setText(thisMat.getName());
        EditText price = findViewById(R.id.etPrice);
        price.setText(thisMat.getPrice() + "");
        EditText location = findViewById(R.id.etLocation);
        location.setText(thisMat.getLocation());
        //set the click listeners on buttons
        addQty.setOnClickListener(new onQuantClick());
        minusQty.setOnClickListener(new onQuantClick());
        modify.setOnClickListener(new onModifyClick());
        delete.setOnClickListener(new onDeleteClick());
    }

    private class onQuantClick implements View.OnClickListener {

        @Override
        public void onClick(View view){
            //get the quantity editText
            EditText quantity = findViewById(R.id.etQuantity);
            //get the current value as an integer
            int qty = Integer.parseInt(quantity.getText().toString());
            Button btnClicked = (Button)view;
            //if user clicks the + button, add one, if they click -, subtract one
            if(btnClicked.getText().toString().equals("+")) {
                quantity.setText((qty + 1) + "");
            }
            else{
                if(qty >= 1) {
                    quantity.setText((qty - 1) + "");
                }
            }
        }
    }

    private class onModifyClick implements View.OnClickListener {

        @Override
        public void onClick(View view){
            //essentially just gathers strings from various editTexts
            Material mt = Inventory.getMaterial(id); //position id not matId
            Boolean updated = false;
            String matName, matPrice, matQuantity, matLocation = "";
            EditText et = findViewById(R.id.etTitle);
            matName = et.getText().toString();
            if(mt.getName() != matName){
                updated = true;
                mt.setName(matName);
            }
            et = findViewById(R.id.etPrice);
            matPrice = et.getText().toString();
            if(Double.parseDouble(matPrice)!=mt.getPrice()){
                updated = true;
                mt.setPrice(Double.parseDouble(matPrice));
            }
            et = findViewById(R.id.etQuantity);
            matQuantity = et.getText().toString();
            if(Integer.parseInt(matQuantity)!= mt.getQuantity()){
                mt.setQuantity(Integer.parseInt(matQuantity));
                updated = true;
            }
            et = findViewById(R.id.etLocation);
            matLocation = et.getText().toString();
            if(!(matLocation.equals(mt.getPurchased()))){
                mt.setPurchased(matLocation);
                updated = true;
            }
            if(!(encodedImage.equals("")) & !encodedImage.equals(mt.getImage())){
                updated = true;
                mt.setImage(encodedImage);
            }
            MaterialController mc = new MaterialController();
            if (updated) {
                if (mc.modifyMaterial(mt, getApplicationContext())) {
                    MaterialFragment.nullifyAdapter();
                    finish();
                }
                else{
                    mt.setQuantity(copy.getQuantity());
                    mt.setPrice(copy.getPrice());
                    mt.setBmp(copy.getBmp());
                    mt.setName(copy.getName());
                    mt.setPurchased(copy.getPurchased());
                }
            }
        }
    }

    private class onDeleteClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(ModifyMaterialActivity.this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Do you really want to delete this material?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            MaterialController mc = new MaterialController();
                            if (mc.deleteMaterial(matId, getApplicationContext())) {
                                Inventory.removeMaterial(matId);
                                MaterialFragment.nullifyAdapter();
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getApplicationContext(), "Material not deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }

    //For results of requests
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //If result was ok and was of PICK_IMAGE activity
        if(resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();

            //Put image in imageview
            ImageView imgProfile = findViewById(R.id.imgMat);
            imgProfile.setImageURI(imageUri);

            //Convert image to bitmap, then into base64
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
            catch (IOException e) { //for file not found
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrOutStrm = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrOutStrm);
            byte[] bArr = byteArrOutStrm.toByteArray();
            encodedImage = Base64.encodeToString(bArr, Base64.DEFAULT).replace("+", "<");
        }
    }
}
