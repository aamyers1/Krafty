package com.team6.krafty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class that allows the generation of a new user material and upload to the database.
 */
public class CreateMaterialActivity extends AppCompatActivity {
    private String encodedImage = "";

    /**
     * Constucts the Create Material Activity. Inflates the XML, sets toolbar and click listeners.
     * @param savedInstanceState Bundle object containing saved state information able to be retrieved
     *                           for use upon a reload of the class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_material);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView iv = findViewById(R.id.matImage);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity passing pick media intent and PICK_IMAGE code (100)
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 100);
            }
        });
        Button btnCreate = findViewById(R.id.submit);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });
    }

    /**
     * Handler for the submitClick action. Gathers and validates data before sending
     * to be stored in the external database.
     */
    public void onSubmitClick(){
        //essentially just gathers strings from various editTexts
        String matName, matPrice, matQuantity, matLocation;
        EditText mName = findViewById(R.id.materialName);
        EditText mPrice = findViewById(R.id.price);
        EditText mLoc = findViewById(R.id.location);
        EditText mQuant = findViewById(R.id.quantity);
        try{
            Validator.validateBasicEditText(mName, "name");
            Validator.validateIntEt(mQuant, "quantity");
            Validator.validateDoubleEt(mPrice, "price");
            Validator.validateBasicEditText(mLoc, "location");
        }
        catch(KraftyRuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        matName = mName.getText().toString();
        matPrice = mPrice.getText().toString();
        matQuantity = mQuant.getText().toString();
        matLocation = mLoc.getText().toString();
        MaterialController mc = new MaterialController();
        if(mc.addMaterial(matName, encodedImage, matQuantity, matPrice, matLocation,this)){
            //reset the adapter to show changes
            MaterialFragment.nullifyAdapter();
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

            ImageView imgProfile = findViewById(R.id.matImage);
            imgProfile.setImageDrawable(null); //clear imageview
            imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));

            //Convert image to bitmap, then into base64
            Bitmap imageAsBitmap = null;
            try {
                imageAsBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Validator.validateImage(imageAsBitmap, "Event Image");
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
}
