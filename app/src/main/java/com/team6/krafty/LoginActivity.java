package com.team6.krafty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void switchView(View view){
        SharedPreferences sp = getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt("yeah", 1);
        spe.apply();

        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    public void goToRegister(View view){
        Intent intent= new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


}
