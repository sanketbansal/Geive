package com.example.root.proto2.Activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.root.proto2.AppMaps;
import com.example.root.proto2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity  {

    private AppMaps mapsutil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapView mapview = (MapView) findViewById(R.id.map);
        mapsutil=new AppMaps(mapview,getApplicationContext(),MapsActivity.this);
    }
}
