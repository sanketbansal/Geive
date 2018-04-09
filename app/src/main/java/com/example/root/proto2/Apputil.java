package com.example.root.proto2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.root.proto2.Activities.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by root on 10/18/17.
 */

public class Apputil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public GoogleSignInOptions gso;
    public  GoogleApiClient mGoogleApiClient;
    public static GoogleApiClient mGoogleLocationClient;

    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static FirebaseUser user;

    public Intent serviceintent;

    public Messenger ipcMessenger;

    public Message msg;

    public ServiceConnection ipcConnection;

    private boolean ipcBound;



    public void googleInit(FragmentActivity act){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("383185290729-gll0pi5gpoh4po8ig4md0htm78hoakjn.apps.googleusercontent.com" )
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(act)
                .enableAutoManage(act,1,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void LocationInit(FragmentActivity act){
        mGoogleLocationClient=new GoogleApiClient.Builder(act)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void firebaseInit(){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("Connection", "onConnected:");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection", "onConnectionSuspendende:");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("LoginConnection failed", "onConnectionFailed:" + connectionResult);
    }


    public void ipc_util(Context ctx){
        final Context ctx1=ctx;
        serviceintent=new Intent(ctx,Appservice.class);
        ipcConnection=new ServiceConnection(){
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the object we can use to
                // interact with the service.  We are communicating with the
                // service using a Messenger, so here we get a client-side
                // representation of that from the raw IBinder object.
                ipcMessenger = new Messenger(service);
                ipcBound = true;
                Toast.makeText(ctx1,"IPC handler connected",Toast.LENGTH_SHORT).show();
                try {
                    if(msg!=null) {
                        ipcMessenger.send(msg);
                    }
                } catch (Exception e) {
                    Log.i("appservice", e.toString());
                    Toast.makeText(ctx1,"Failed Try Again!",Toast.LENGTH_SHORT).show();
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                ipcMessenger = null;
                ipcBound = false;
            }
        };

    }



    public NetworkInfo netInfo(Activity act){
        ConnectivityManager check = (ConnectivityManager)
                act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = check.getActiveNetworkInfo();
        return info;
    }

    public boolean netProvider(Activity act){
        LocationManager locationManager = (LocationManager) act.getSystemService(LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isNetworkEnabled;
    }

    public boolean GPSEnable(Activity act){
        LocationManager locationManager = (LocationManager) act.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }
}
