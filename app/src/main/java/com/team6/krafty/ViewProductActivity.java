package com.team6.krafty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
     }
}
