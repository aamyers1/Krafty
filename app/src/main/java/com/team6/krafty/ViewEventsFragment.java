package com.team6.krafty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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


public class ViewEventsFragment extends Fragment implements OnMapReadyCallback {

 private GoogleMap mMap;
 FusedLocationProviderClient flpc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_events, container, false);
        SupportMapFragment f = new SupportMapFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.mapFrame, f);
        ft.commit();
        f.getMapAsync(this);
        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        flpc = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            flpc.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng lg = new LatLng(location.getLatitude(), location.getLongitude());
                        Marker mk = mMap.addMarker(new MarkerOptions().position(lg).title("current"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lg));
                    }
                }
            });

        }
        EventsController ec = new EventsController();
        ec.fetchEvents(getContext());
        LatLng[] allLtLg = ec.getltlng();
        String[] names = ec.getNames();
        int[] identities = ec.getIdentities();
        for(int j = 0; j < allLtLg.length; j++){
            Marker mk = mMap.addMarker(new MarkerOptions().position(allLtLg[j]).title(names[j]));
            mk.setTag(identities[j]);
        }
        mMap.setOnMarkerClickListener(new MarkerListener());
    }


    private class MarkerListener implements GoogleMap.OnMarkerClickListener{

        @Override
        public boolean onMarkerClick(Marker marker){
            //on click take to the page of the event details!
            String a = marker.getTag() + "";
            return true;
        }
    }

    public void onAddEventClick(){
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        startActivity(intent);
    }
}
