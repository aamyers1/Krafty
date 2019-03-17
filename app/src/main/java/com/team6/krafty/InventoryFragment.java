package com.team6.krafty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {
    private static cardAdapter ca;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager vp = getView().findViewById(R.id.InvViewPager);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new MaterialFragment(), "Materials");
        adapter.addFragment(new ProductsFragment(), "Products");
        vp.setAdapter(adapter);
        TabLayout tabLayout = getView().findViewById(R.id.InventoryTabs);
        tabLayout.setupWithViewPager(vp);

    }

    static class Adapter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> fragmentNames = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return new MaterialFragment();
            }
            else if(position == 1){
                return new ProductsFragment();
            }
            return null;
        }

        public void addFragment(Fragment fragment, String name){
            fragments.add(fragment);
            fragmentNames.add(name);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentNames.get(position);
        }

    }


}
