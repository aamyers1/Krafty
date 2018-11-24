package com.team6.krafty;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class SplashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static  int MENU_INVENTORY = 500;

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

        //fill the frame with a lame blank fragment for now
        //TODO:THIS WILL BE THE PROFILE PAGE
        Fragment fragment = new BlankFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        //get the navView
        NavigationView navView =  (NavigationView)findViewById(R.id.nav_view);
        //TODO: GET USERTYPE FROM DB ON LOGIN?
        //If a krafter, add inventory option to the menu
        //TODO: FIGURE OUT HOW TO ADD IT TO THE RIGHT PLACE IN THE LIST
        int userType = 1;
        if(userType == 1) {
            Menu menu = navView.getMenu();
            menu.add(menu.NONE, MENU_INVENTORY, 0, "Inventory");
        }
        //set the listener
        navView.setNavigationItemSelectedListener(this);
    }

    //navigation drawer menu listener
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;
        //TODO: Other pages: Schedule, Events, Profile(Which will likely just be home)
        switch(id) {
            case R.id.home:
                fragment = new BlankFragment();
                break;
            case R.id.logout:
                SessionManager.logout(this);
                intent = new Intent(this, LoginActivity.class);
                break;
            case 500:
                fragment = new InventoryFragment();
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
}
