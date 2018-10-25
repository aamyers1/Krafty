package com.team6.krafty;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbarregister);
        setSupportActionBar(toolbar);
        PageAdapter pa = new PageAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pa);
        TabLayout tl = findViewById(R.id.registerTabs);
        tl.setupWithViewPager(pager);

    }

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0:
                    return new KrafteeRegisterFragment();
                case 1:
                    return new RegisterKrafterFragment();
            }
            return null;
        }

        @Override
        public int getCount(){
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int pos){
            switch(pos){
                case 0:
                    return getResources().getText(R.string.kraftee);
                case 1:
                    return getResources().getText(R.string.krafter);
            }
            return null;
        }
    }
}
