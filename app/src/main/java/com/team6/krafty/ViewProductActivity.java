package com.team6.krafty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewProductActivity extends AppCompatActivity  {

    private int id;
    private Context context = this;
    private Product product;
    cardAdapter ca;
    private HashMap<Integer, Integer> materials;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
     }

     @Override
    public void onStart(){
        super.onStart();
         Intent intent = getIntent();
         id = intent.getIntExtra("EXTRA_ID", 0);
         final ProductController controller = new ProductController();
         product = Inventory.getProduct(id);
         materials = product.getMaterials();

         TextView productName = (TextView)findViewById(R.id.productName);
         productName.setText(product.getName());

         TextView productDesc = (TextView)findViewById(R.id.productDesc);
         productDesc.setText("Description: " + product.getDescription());

         TextView productPrice = (TextView)findViewById(R.id.productPrice);
         productPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));
         TextView productQuantity = (TextView)findViewById(R.id.productQuantity);
         productQuantity.setText("In stock: " + String.valueOf(product.getQuantity()));

         final ImageView productImage = (ImageView)findViewById(R.id.imageView);
         if(product.getBmp() != null) {
             productImage.setImageBitmap(product.getBmp());
         }
         else {
             productImage.setBackgroundColor(Color.rgb(188,225,232));
         }

         setButtonsAndViews();
    }

    public void setButtonsAndViews(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String username = sp.getString("username", "test");


        if(username.equals(product.getCreator())) {
            Button btnProductUpdate = (Button) findViewById(R.id.btnProductUpdate);
            btnProductUpdate.setVisibility(Button.VISIBLE);
            btnProductUpdate.setClickable(true);
            Button btnProductDelete = (Button) findViewById(R.id.btnProductDelete);
            btnProductDelete.setVisibility(Button.VISIBLE);
            btnProductDelete.setClickable(false);

            btnProductDelete.setOnClickListener(new ViewProductActivity.onDeleteClick());
            btnProductUpdate.setOnClickListener(new ViewProductActivity.onUpdateClick());

            RecyclerView rv = findViewById(R.id.recyclerMats);
            try {
                ca = new cardAdapter(getbmps(), getMatNames());
                rv.setAdapter(ca);
                rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
            catch(Exception e){
                Log.d("WHTATTHEFUCK", e.getMessage());
            }

        }

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

    public int[] getIds(){
        int k = 0;
        int[] ids = new int[materials.size()];
        for(Integer i: materials.keySet()){
            ids[k] = i;
            k++;
        }
        return ids;
    }


    private class onUpdateClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            Intent intent = new Intent(getApplicationContext(), ModifyProductActivity.class);
            intent.putExtra("id", product.getId());
            startActivity(intent);
        }
    }

    private class onDeleteClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(ViewProductActivity.this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Do you really want to delete this product?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ProductController pc = new ProductController();
                            pc.deleteProduct(product.getId(), getApplicationContext());
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getApplicationContext(), "Event not deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }
}
