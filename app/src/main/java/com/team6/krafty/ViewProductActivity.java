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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ViewProductActivity extends AppCompatActivity {

    private int id;
    private Context context = this;
    private Product product;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
     }

     @Override
    public void onStart(){
        super.onStart();
         Intent intent = getIntent();
         id = intent.getIntExtra("ID", 0);
         final ProductController controller = new ProductController();
         product = controller.getProduct(id,context);

         TextView productName = (TextView)findViewById(R.id.productName);
         productName.setText(product.getName());

         TextView productDesc = (TextView)findViewById(R.id.productDesc);
         productDesc.setText(product.getDescription());

         TextView productPrice = (TextView)findViewById(R.id.productPrice);
         productPrice.setText(String.valueOf(product.getPrice()));

         TextView productQuantity = (TextView)findViewById(R.id.productQuantity);
         productQuantity.setText(String.valueOf(product.getQuantity()));

         final ImageView productImage = (ImageView)findViewById(R.id.imageView);
         final Bitmap bmp;
         Thread t = new Thread(new Runnable() {
             @Override
             public void run() {
                 Bitmap bmp = controller.parseProductImage(product.getImage());
                 if(bmp!= null){
                     productImage.setImageBitmap(bmp);
                 }
                 else{
                     productImage.setBackgroundColor(Color.rgb(188,225,232));
                 }
             }
         });
         t.start();

         setButtonsAndViews();
    }

    public void setButtonsAndViews(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String username = sp.getString("username", "test");


        if(username.equals(product.getCreator())) {
            Button btnProductUpdate = (Button)findViewById(R.id.btnProductUpdate);
            btnProductUpdate.setVisibility(Button.VISIBLE);
            btnProductUpdate.setClickable(true);
            Button btnProductDelete = (Button)findViewById(R.id.btnProductDelete);
            btnProductDelete.setVisibility(Button.VISIBLE);
            btnProductDelete.setClickable(false);

            btnProductDelete.setOnClickListener(new ViewProductActivity.onDeleteClick());
            btnProductUpdate.setOnClickListener(new ViewProductActivity.onUpdateClick());
        }

    }

    private class onUpdateClick implements View.OnClickListener{

        @Override
        public void onClick(View view){
            Intent intent = new Intent(getApplicationContext(), ModifyProductActivity.class);
            intent.putExtra("id", id);
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
                            pc.deleteProduct(id, getApplicationContext());
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
