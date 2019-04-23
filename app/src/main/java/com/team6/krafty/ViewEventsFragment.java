package com.team6.krafty;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

/**
 * Fragment used to display events on a map
 */
public class ViewEventsFragment extends Fragment implements OnMapReadyCallback {

 private GoogleMap mMap;
 FusedLocationProviderClient flpc;
    private View v;
    private boolean locationPermission;

    /**
     * Method called on object instantiation. Inflates XML file fragment_view_events.xml
     * @param inflater LayoutInflater to inflate the xml file
     * @param container Containing object for the inflated xml
     * @param savedInstanceState Bundle containing state information that can be used on fragment
     *                                   relaunch or resume to restore state
     * @return Inflated xml object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_view_events, container, false);
        int userType = SessionManager.getUserType(v.getContext());
        if(userType == 2){
            FloatingActionButton fab = v.findViewById(R.id.addEvent);
            fab.hide();
        }
        return v;
    }

    /**
     * Method called once the xml is loaded. Loads the mapFrame and floating button for create event
     */
    @Override
    public void onStart(){
        super.onStart();
        SupportMapFragment f = new SupportMapFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.mapFrame, f);
        ft.commit();
        f.getMapAsync(this);
        FloatingActionButton createEvent = v.findViewById(R.id.addEvent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Method to display the Google map with the current location in the center. Also calls the
     * method AsyncEventGetter to display Event markers on the map
     * @param googleMap google map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(android.os.Build.VERSION.SDK_INT >= 23){
            getPermission();
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || locationPermission) {
            mMap.setMyLocationEnabled(true);
        }
        flpc = LocationServices.getFusedLocationProviderClient(getActivity());
        flpc.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng lg = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lg,10));
                }
            }
        });
        AsyncEventGetter aeg = new AsyncEventGetter();
        aeg.doInBackground();
        mMap.setOnInfoWindowClickListener(new WindowListener());
    }

    /**
     * Listener for Event markers. Starts the ViewSpecificEvent activity.
     */
    private class WindowListener implements GoogleMap.OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            int a = (Integer) marker.getTag();
            Intent intent = new Intent(getActivity(), ViewSpecificEvent.class);
            intent.putExtra("ID",a);
            startActivity(intent);
        }
    }

    /**
     * Class to fetch events from the database and AddMarkers to the map
     */
    private class AsyncEventGetter extends AsyncTask<Void, Void, Void>{
        @Override
        public Void doInBackground(Void...args){
            EventsController ec = new EventsController();
            ec.fetchEvents(getContext());
            LatLng[] allLtLg = ec.getltlng();
            String[] names = ec.getNames();
            String[] descriptions= ec.getDescriptions();
            int[] identities = ec.getIdentities();
            for(int j = 0; j < allLtLg.length; j++){
                Marker mk = mMap.addMarker(new MarkerOptions().position(allLtLg[j]).title(names[j]).snippet(descriptions[j]));
                mk.setTag(identities[j]);
            }
            return null;
        }
    }

    /**
     * Method to request permission for device location info access
     */
    @TargetApi(23)
    private void getPermission(){
        ActivityCompat.requestPermissions(this.getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                200);

    }

    /**
     * Method to update locationPermission if granted
     * @param requestCode request code to check if thr request was sent
     * @param permissions array for permission
     * @param grantResults array of the response
     */
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true;
                }
                else {
                    locationPermission = false;
                }
            }
        }
    }


}
