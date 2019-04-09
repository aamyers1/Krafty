package com.team6.krafty;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Login Activity is the starting activity for the application. This activity handles basic
 * session management via login and links to the registration and splash activities.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Method that generates the basis of the Activity. First checks if the user is logged in
     * currently. If yes, switch to splash page and load data. If no, display login page.
     * @param savedInstanceState Bundle containing state information that can be used on fragment
     *                           relaunch or resume to restore state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //if login credentials exist already
        if(SessionManager.isLoggedIn(this)){
            String token = SessionManager.getToken(this);
            MaterialGetter mg = new MaterialGetter();
            mg.execute(token);
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

    /**
     * Handles user action of clicking the login button. Attempts to log the user in by
     * validating credentials against the database.
     */
    public void onClickLogin(){
        //get the necessary values from user input
        EditText tv = findViewById(R.id.txtUsername);
        String username = tv.getText().toString();
        tv = findViewById(R.id.txtPassword);
        String password = tv.getText().toString();
        //attempt to login via SessionManager
        if(SessionManager.login(this, username, password)) {
            String token = SessionManager.getToken(this);
            MaterialGetter mg = new MaterialGetter();
            mg.execute(token);
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

    /**
     * Handles the Sign Up button click by sending the user to the registration page
     * @param view View clicked (Button)
     */
    public void onClickSignUp(View view){
        Intent intent= new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Asynchronous inner class used to retrieve inventory data without blocking the UI thread
     */
    private class MaterialGetter extends AsyncTask<String, Void, Void>{

        /**
         * Fetches user inventory information viea the Material and Products controllers
         * respectively.
         * @param args Contains the user token to fetch inventory
         * @return
         */
        @Override
        public Void doInBackground(String...args){
            //clear any previous inventory
            Inventory.getInstance().clearAll();
            //get all materials for inventory
            MaterialController.getMaterials(args[0]);
            ProductController pc = new ProductController();
            pc.getProducts(args[0]);
            return null;
        }
    }

}
