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


import java.util.ArrayList;

/**
 * Class that acts as a container for the two tabs of Material Fragment and Products Fragment
 */
public class InventoryFragment extends Fragment {

    private static cardAdapter ca;

    /**
     * Method launched when fragment is initialized. Inflates the xml fragment_inventory.xml
     * @param inflater Object which inflates xml to display to user
     * @param container Container object which holds this xml (parent)
     * @param savedInstanceState Bundle containing state information that can be used on fragment
     *                           relaunch or resume to restore state
     * @return Inflated XML
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);

    }

    /**
     * Method launched once all xml is inflated. Sets up the inventory tabs.
     * @param view The overall fragment generated
     * @param savedInstanceState Bundle containing state information that can be used on fragment
     *      *                           relaunch or resume to restore state
     */
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager vp = getView().findViewById(R.id.InvViewPager);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new MaterialFragment(), "Materials");
        adapter.addFragment(new ProductsFragment(), "Products");
        vp.setAdapter(adapter);
        TabLayout tabLayout = getView().findViewById(R.id.InventoryTabs);
        tabLayout.setupWithViewPager(vp);
    }

    /**
     * Inner class that creates the pager adapter to slide between tabs
     */
    static class Adapter extends FragmentPagerAdapter{

        //stores the data to generate the tabs
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> fragmentNames = new ArrayList<>();

        /**
         * Constructor
         * @param manager FragmentManger which holds the appropriate fragments used by the
         *                PagerAdapter
         */
        Adapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Gets the currently selected tab by index
         * @param position Currently selected index
         * @return The fragment corresponding to the proper index
         */
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

        /**
         * Adds new tab to the list of tabs
         * @param fragment Fragment to be displayed
         * @param name Title of the tab
         */
        public void addFragment(Fragment fragment, String name){
            fragments.add(fragment);
            fragmentNames.add(name);
        }

        /**
         * Gets the total number of tabs
         * @return Int number of tabs
         */
        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * Gets the title of a tab based on position index
         * @param position Index of the current tab
         * @return String title of the tab
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentNames.get(position);
        }
    }

}
