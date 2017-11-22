package com.example.root.proto2.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.root.proto2.AppFireStore;
import com.example.root.proto2.AppLocation;
import com.example.root.proto2.Appservice;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Models.DummyData;
import com.example.root.proto2.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.taplytics.sdk.Taplytics;

public class SplahscreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final int UI_ANIMATION_DELAY = 3000;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    private Intent navintent;
    private Intent loginintent;

    private GoogleApiClient mGoogleLocationClient;

    private Apputil util;
    private AppLocation location;
    private AppFireStore fs=new AppFireStore();
    private Cachedocument cache=new Cachedocument();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Taplytics.startTaplytics(this, "e2f8b7a91063c9296ab6e3b1698c16319c96d1bd");

        setContentView(R.layout.activity_splahscreen);

        DummyData dummy =new DummyData();

        cache.ctx=getApplicationContext();
        cache.writeDoc(dummy.setdata(),"dummy");


        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        navintent=new Intent(SplahscreenActivity.this, NavActivity.class);
        loginintent=new Intent(SplahscreenActivity.this, LoginActivity.class);

        location=new AppLocation(this);

        util=new Apputil();
        util.googleInit(SplahscreenActivity.this);
        //util.LocationInit(SplahscreenActivity.this);

        mGoogleLocationClient=new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        if(!mGoogleLocationClient.isConnected()){
            Toast.makeText(this,"client not connected",Toast.LENGTH_SHORT).show();
            mGoogleLocationClient.connect();
        }

        location.mGoogleLocationClient=mGoogleLocationClient;
    }

    @Override
    protected   void onStart(){super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(util.mGoogleApiClient);
        if (opr.isDone()) {
            Log.d("Googlesignin", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            startActivity(loginintent);
            finish();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        location.getLocationAddress(location.getLastLocation());
        location.requestLocationUpdate();
        //mGoogleLocationClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection", "onConnectionSuspendende:");
        Toast.makeText(this,"Disconected",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("LoginConnection failed", "onConnectionFailed:" + connectionResult);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Googlesignin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Log.d("Googlesignin", "Sign in successful");
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("Googlesignin", acct.getEmail());
            finish();
            Intent serviceintent=new Intent(SplahscreenActivity.this, Appservice.class);
            serviceintent.putExtra("docid",acct.getEmail());
            startService(serviceintent);
            startActivity(navintent);
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this,"Something went wrong\nTry sign in again",Toast.LENGTH_SHORT).show();
            cache.ctx=getApplicationContext();
            cache.writeDoc(null,"session");
            startActivity(loginintent);
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }



    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }



    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }


    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
