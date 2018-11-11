package com.team6.krafty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(SessionManager.isLoggedIn(this)){
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
        loginButton  = findViewById(R.id.btnLogin);

        //set the login button listener
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onClickLogin();
            }
        });

    }

    //login button click
    //sends to session manager.
    public void onClickLogin(){
        EditText tv = findViewById(R.id.txtUsername);
        String username = tv.getText().toString();
        tv = findViewById(R.id.txtPassword);
        String password = tv.getText().toString();
        if(SessionManager.login(this, username, password)) {
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
