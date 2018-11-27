package com.team6.krafty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //if login credentials exist already
        if(SessionManager.isLoggedIn(this)){
            //clear current inventory in case still exists
            Inventory.clearAll();
            //Get all materials from DB and add to material list in inventory
            MaterialController.getMaterials(this);
            //launch the Splash Activity class
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            //make so user cannot return to this screen without logging out
            finish();
        }
        Button loginButton  = findViewById(R.id.btnLogin);
        //set the login button listener
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onClickLogin();
            }
        });

    }

    //login button click
    //sends username/password to session manager.
    public void onClickLogin(){
        //get the necessary values from user input
        EditText tv = findViewById(R.id.txtUsername);
        String username = tv.getText().toString();
        tv = findViewById(R.id.txtPassword);
        String password = tv.getText().toString();
        //attempt to login via SessionManager
        if(SessionManager.login(this, username, password)) {
            //clear any previous inventory
            Inventory.clearAll();
            //get all materials for inventory
            MaterialController.getMaterials(this);
            //launch the SplashActivity
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
            //end the activity so that it cannot be returned to
            finish();
        }
        //popup message for failure
        else{
            Toast.makeText(this, "Incorrect credentials, try again", Toast.LENGTH_SHORT).show();
        }
    }

    //sign up button click -> sends to registration
    public void onClickSignUp(View view){
        Intent intent= new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


}
