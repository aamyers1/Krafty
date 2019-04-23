package com.team6.krafty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SplashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static  int MENU_INVENTORY = 500;
    private User profile;
    static DBManager dbManager = DBManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the nav drawer toggle listener (open/close)
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        //get the navView
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);

        setUpNavMenu(navView);
        SessionManager.setUser(profile);
        //set the listener
        navView.setNavigationItemSelectedListener(this);

        Fragment fragment = new ProfileFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    //navigation drawer menu listener
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;
        switch(id) {
            case R.id.home:
                fragment = new ProfileFragment();
                break;
            case R.id.logout:
                SessionManager.logout(this);
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.inventory:
                fragment = new InventoryFragment();
                break;
            case R.id.events:
                fragment = new ViewEventsFragment();
                break;
            case R.id.schedule:
                fragment = new ScheduleFragment();
                break;
            default:
                break;
        }
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
            //make sure the drawer is closed now
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            startActivity(intent);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            finish();
            return true;

        }
        return true;
    }

    public void setUpNavMenu(NavigationView navView){
        final String token = SessionManager.getToken(this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    profile = dbManager.getUser(token, "");
                } catch (KraftyRuntimeException e){
                    Log.d("GET USER ERROR", "user error " +  e);
                }
            }
        });
        t.start();
        try{
            t.join();
            //If a krafter, add inventory option to the menu
            SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("username", profile.getUsername());
            edit.putInt("userType", profile.getUserType());
            edit.apply();

            if(profile.getUserType() == 2) {
                Menu menu = navView.getMenu();
                MenuItem inventory = menu.findItem(R.id.inventory);
                inventory.setVisible(false);
            }
        }
        catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }
    }

}
