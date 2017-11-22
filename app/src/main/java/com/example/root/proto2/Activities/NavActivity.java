package com.example.root.proto2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.root.proto2.Adapters.DashAdapter;
import com.example.root.proto2.Adapters.ViewAdapter;
import com.example.root.proto2.Apploader;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.taplytics.sdk.Taplytics;


public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<DataModel> {

    private Apputil util;

    private RecyclerView mrecyclerview;
    private RecyclerView.LayoutManager mlinearlayout;
    private RecyclerView.Adapter madapter;

    private Cachedocument cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Taplytics.startTaplytics(this, "e2f8b7a91063c9296ab6e3b1698c16319c96d1bd");

        final Intent addintent=new Intent(NavActivity.this,AddActivity.class);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        util=new Apputil();
        util.googleInit(NavActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addintent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cache=new Cachedocument();
        cache.ctx=getApplicationContext();

        mrecyclerview=(RecyclerView) findViewById(R.id.dash_recycler_view);
        mlinearlayout = new LinearLayoutManager(getApplicationContext());

        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(mlinearlayout);

        getSupportLoaderManager().initLoader(2,null,this);
    }


    @Override
    public Loader<DataModel> onCreateLoader(int id, Bundle args) {
        return new Apploader(getApplicationContext(),cache.readDoc("session").userid);
    }

    @Override
    public void onLoadFinished(Loader<DataModel> loader, DataModel data) {

        if(data!=null) {
            //Log.i("scroll", data.userid);
            madapter = new DashAdapter(data.getTimeline());
            mrecyclerview.setAdapter(madapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<DataModel> loader) {
        madapter = new DashAdapter(null);
        mrecyclerview.setAdapter(madapter);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_complaint) {
            // Handle the camera action
            Intent complaintintent =new Intent(NavActivity.this,ScrollActivity.class);
            startActivity(complaintintent);
        } else if (id == R.id.nav_analysis) {
            Intent analysisintent =new Intent(NavActivity.this,AnalysisActivity.class);
            startActivity(analysisintent);

        } else if (id == R.id.nav_notifications) {
            //Intent loginintent =new Intent(NavActivity.this,LoginActivity.class);
            //startActivity(loginintent);
            signOut();
            Intent loginintent =new Intent(NavActivity.this,LoginActivity.class);
            finish();
            startActivity(loginintent);

        } else if (id == R.id.nav_manage) {
            Intent profileintent =new Intent(NavActivity.this,ProfileActivity.class);
            startActivity(profileintent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(util.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.i("Googlesignout","signedout succesfully");
                        Toast.makeText(NavActivity.this,"Signed out successfully",Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
