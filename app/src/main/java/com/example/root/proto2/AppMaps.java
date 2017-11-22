package com.example.root.proto2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.root.proto2.Activities.AddActivity;
import com.example.root.proto2.Activities.MapsActivity;
import com.example.root.proto2.AppLocation;
import com.example.root.proto2.Apputil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by root on 11/9/17.
 */

public class AppMaps implements GoogleMap.OnMyLocationButtonClickListener,OnMapReadyCallback {

    public GoogleMap mMap;
    private UiSettings mUI;

    final int REQUEST_CODE_PERMISSION =2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private Context mcontext;

    public static Location location;
    public static int lock=1;

    private Activity act;
    private Apputil util;


    public AppMaps(MapView mapview,Context ctx,Activity act){
        mcontext=ctx;
        this.act=act;
        mapview.getMapAsync(this);
        mapview.onCreate(null);
        util=new Apputil();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarker();
        if(lock==1) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng tap) {
                    lock=0;
                    Intent mapintent=new Intent(act, MapsActivity.class);
                    act.startActivity(mapintent);
                }
            });
        }

        if(lock==0){
            addmaputil();
        }
    }

    public void addmaputil(){

        if (ContextCompat.checkSelfPermission(mcontext, mPermission) == PackageManager.PERMISSION_GRANTED) {
            Log.i("Locpermission","permission granted");
            mMap.setMyLocationEnabled(true);
        }
        else {

            Log.i("Locpermission","permission not granted");


            ActivityCompat.requestPermissions(act,
                    new String[]{mPermission},
                    REQUEST_CODE_PERMISSION);
            if (ContextCompat.checkSelfPermission(mcontext, mPermission) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Locpermission","permission granted");
                mMap.setMyLocationEnabled(true);
            }

            if(util.netInfo(act)==null) {
                Toast.makeText(mcontext, "Network Connection is not enabled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                mcontext.startActivity(intent);
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng tap) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(tap));
                Toast.makeText(mcontext, "Tap Location- " +tap, Toast.LENGTH_SHORT).show();
            }
        });

        mUI=mMap.getUiSettings();
        mMap.setBuildingsEnabled(true);
        mUI.setMyLocationButtonEnabled(true);
        mUI.setZoomControlsEnabled(true);
        mUI.setZoomGesturesEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
    }

    public void addMarker() {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(loc));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }
}
