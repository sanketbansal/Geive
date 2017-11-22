package com.example.root.proto2.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.root.proto2.Appservice;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.LOCATION_SERVICE;

public class MapsActivity extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,OnMapReadyCallback {

    private  View mapView;
    private GoogleMap mMap;
    private UiSettings mUI;
    private double lat = -34;
    private double lng = 151;
    private Apputil util;

    final int REQUEST_CODE_PERMISSION =2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;

    public static MapsActivity newInstance() {
        MapsActivity fragment = new MapsActivity();
        Bundle args = new Bundle();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        util=new Apputil();

        if (mapView != null) {
            ViewGroup parent = (ViewGroup) mapView.getParent();
            if (parent != null)
                parent.removeView(mapView);
        }
        try {
            mapView=inflater.inflate(R.layout.activity_maps, container,false);
        }

        catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        final SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return mapView;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (ContextCompat.checkSelfPermission(mapView.getContext(), mPermission) == PackageManager.PERMISSION_GRANTED) {
            Log.i("Locpermission","permission granted");
            mMap.setMyLocationEnabled(true);
        }
        else {

            Log.i("Locpermission","permission not granted");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
            if (ContextCompat.checkSelfPermission(mapView.getContext(), mPermission) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Locpermission","permission granted");
                mMap.setMyLocationEnabled(true);
            }

            if(util.netInfo(this.getActivity())==null) {
                Toast.makeText(mapView.getContext(), "Network Connection is not enabled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                mapView.getContext().startActivity(intent);
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng tap) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(tap));
                Toast.makeText(mapView.getContext(), "Tap Location- " +tap, Toast.LENGTH_SHORT).show();
            }
        });

        mUI=mMap.getUiSettings();
        mMap.setBuildingsEnabled(true);
        mUI.setMyLocationButtonEnabled(true);
        mUI.setZoomControlsEnabled(true);
        mUI.setZoomGesturesEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
    }


    @Override
    public boolean onMyLocationButtonClick() {

        if(!util.GPSEnable(this.getActivity())) {
            Toast.makeText(mapView.getContext(), "GPS is not enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mapView.getContext().startActivity(intent);
        }
        return false;
    }

}

