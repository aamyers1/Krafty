package com.team6.krafty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ModifyMaterialActivity extends AppCompatActivity {

    //Variable for passing the material array position
    private static final String EXTRA_ID = "matid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_material);
        //get the position id
        int id = getIntent().getIntExtra("EXTRA_ID", 1);
        //get the specified material using the id
        Material thisMat = Inventory.getMaterial(id);
        //Find the needed views, set values based on the material
        Button addQty = findViewById(R.id.Plus);
        Button minusQty = findViewById(R.id.Minus);
        EditText quantity = findViewById(R.id.etQuantity);
        quantity.setText(thisMat.getQuantity() + "");
        EditText materialName = findViewById(R.id.etTitle);
        materialName.setText(thisMat.getName());
        EditText price = findViewById(R.id.etPrice);
        price.setText(thisMat.getPrice() + "");
        EditText location = findViewById(R.id.etLocation);
        location.setText(thisMat.getLocation());
        //set the click listeners on + and - buttons
        addQty.setOnClickListener(new onQuantClick());
        minusQty.setOnClickListener(new onQuantClick());
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

}
