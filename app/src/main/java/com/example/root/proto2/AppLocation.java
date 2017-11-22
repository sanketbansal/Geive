package com.example.root.proto2;

/**
 * Created by root on 10/9/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.root.proto2.Apputil.mGoogleLocationClient;

public class AppLocation  implements LocationListener {

    private Context mContext;


    boolean isGPSEnabled = false;

    boolean isNetworkEnabled = false;

    //Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 *60*1; // 1 minute

    public GoogleApiClient mGoogleLocationClient;

    private Geocoder geocoder;

    private LocationRequest locreq;
    protected LocationManager locationManager;


    public AppLocation(Context ctx){
        mContext=ctx;
        locreq=new LocationRequest();
        locreq.setInterval(MIN_TIME_BW_UPDATES);
        //locreq.setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES);
        locreq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        geocoder=new Geocoder(mContext,Locale.getDefault());
    }

    public Location getLastLocation(){
        Toast.makeText(mContext,"Checking permissions",Toast.LENGTH_LONG).show();

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleLocationClient);
            if (mLastLocation != null) {
                Toast.makeText(mContext,mLastLocation.toString(),Toast.LENGTH_LONG).show();
            }
            String s="NetworkEnabled";
            Log.d("Location",s);

            return mLastLocation;
        }

        else{
            showSettingsAlert();
            return null;
        }
    }



    public void requestLocationUpdate(){
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocationClient,locreq,this);
            String s="NetworkEnabled";
            Log.d("Location",s);
        }

        else{
            showSettingsAlert();
        }
    }

    public Address getLocationAddress(Location location) {
        //Address address=new Address(Locale.getDefault());
        List<Address> addresses = null;

        try {
            if(location!=null) {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
            }
        }
        catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e("geoerror", "NetworkvI/O problems", ioException);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e("geoerror", "invalid lang and lat" + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            Log.e("geoerror", "No address found");
            return null;
        }

        else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Toast.makeText(mContext,address.getAddressLine(0),Toast.LENGTH_LONG).show();
            Log.i("geoaddress",address.getAddressLine(0));
            return address;
        }
    }


    public double getLatitude(Location location){

        if(location != null){
            latitude =location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude(Location location){
        if(location != null){
            longitude =location.getLongitude();
        }
        return longitude;
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        //alertDialog.show();

        //AlertDialog dialog=alertDialog.create();
        //alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Toast.makeText(mContext, "Your Location has been changed" + " " + getLocationAddress(location).getAddressLine(0), Toast.LENGTH_SHORT).show();
        }
    }
}
