package com.example.root.proto2.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.proto2.AppFireStore;
import com.example.root.proto2.AppLocation;
import com.example.root.proto2.AppMaps;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Fragments.CompActivity;
import com.example.root.proto2.Fragments.EMAILActivity;
import com.example.root.proto2.Fragments.MapsActivity;
import com.example.root.proto2.Models.CompModel;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.root.proto2.Activities.ProfileActivity.REQUEST_IMAGE_CAPTURE;

public class AddActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{


    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private FragmentTransaction ft;

    private GoogleApiClient mGoogleLocationClient;

    private Apputil util;
    private AppLocation location;
    private AppMaps addressmap;
    private Cachedocument cache;
    private AppFireStore fs=new AppFireStore();

    final int REQUEST_CODE_PERMISSION =2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private TextView dateview;
    private ImageView photoview;
    private EditText street_edit;
    private ImageButton street_location;
    private MapView addressview;
    private ImageButton send;
    private ImageButton close;

    private DataModel dm;
    private int servicelock=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.root.proto2.R.layout.activity_add);

        cache=new Cachedocument();
        util=new Apputil();
        util.ipc_util(getApplicationContext());

        dm=new DataModel();
        cache.ctx=getApplicationContext();
        dm=cache.readDoc("session");
        Log.i("Addactivity",dm.userid);
        dm=cache.readDoc(dm.userid);

        mGoogleLocationClient=new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        location=new AppLocation(this);
        location.mGoogleLocationClient=mGoogleLocationClient;

        ft=getSupportFragmentManager().beginTransaction();

        dateview =(TextView) findViewById(R.id.date_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateview.setOnClickListener(new View.OnClickListener() {
            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //DatePickerDialog datepicker=new DatePickerDialog(getApplicationContext());
                //datepicker.show();
                DatePicker datePicker=new DatePicker(getApplicationContext());
                datePicker.setEnabled(true);
                //datePicker.getMonth();
            }

        });

        photoview =(ImageView) findViewById(R.id.photo_view);
        photoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photointent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (photointent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(photointent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        street_edit=(EditText) findViewById(R.id.street_edit);
        street_location=(ImageButton) findViewById(R.id.street_location);
        street_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleLocationClient.connect();
            }
        });

        addressview=(MapView) findViewById(R.id.street_map);
        //addressview.onCreate(null);

        send=(ImageButton) findViewById(R.id.send);
        close=(ImageButton) findViewById(R.id.close);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                Intent dashintent=new Intent(AddActivity.this,NavActivity.class);
                startActivity(dashintent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        AppMaps.lock=1;
    }

    @Override
    public void onStop(){
        super.onStop();
        if(servicelock==1){
            servicelock=0;
            unbindService(util.ipcConnection);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("resPermission","response for permission");
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (permissions.length == 1 &&
                    permissions[0] == mPermission &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, mPermission)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Locpermission","permission granted");
                    addressmap.mMap.setMyLocationEnabled(true);
                }

            } else {
                // Permission was denied. Display an error message.
                Log.i("Locpermission","PERMISSION DENIED");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            try {
                Log.i("data", extras.get("data").toString());
            }
            catch (Exception e){Log.i("data", e.toString());}
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoview.setImageBitmap(imageBitmap);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Location loc=location.getLastLocation();
        if(loc!=null) {
            Address address = location.getLocationAddress(loc);
            street_edit.setText(address.getAddressLine(0));
            street_location.setVisibility(View.INVISIBLE);

            CompModel comp =new CompModel();
            comp.setLandmark(address.getAddressLine(0));
            comp.setCity(address.getAdminArea());
            Log.i("Addactivity",comp.getCity());

            dm.getComplaint().set(0, comp);
            dm.setComplaint(dm.getComplaint());
            cache.writeDoc(dm,dm.userid);

            util.serviceintent.putExtra("docid",dm.userid);
            util.serviceintent.putExtra("datamodel",dm);
            util.serviceintent.putExtra("fieldid","");
            util.serviceintent.putExtra("updatemodel",dm);
            bindService(util.serviceintent,util.ipcConnection, Context.BIND_AUTO_CREATE);
            servicelock=1;
            //fs.dm=dm;
            //fs.setDoc(fs.cRef.document(dm.userid));

            util.msg= Message.obtain(null,1,0,0);


            AppMaps.location = loc;
            addressmap = new AppMaps(addressview, getApplicationContext(), AddActivity.this);
            mGoogleLocationClient.disconnect();
        }

        else{
            mGoogleLocationClient.connect();
        }
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


    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"sanketbansal57@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finish","Finished sending email");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
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


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment currfragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            switch (position+1) {

                case 3:
                    return MapsActivity.newInstance();

                case 1:
                    return CompActivity.newInstance();

                case 2:
                    return EMAILActivity.newInstance();

                default:
                    return CompActivity.newInstance();
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position+1) {
                case 1:
                    return "SECTION 1";
                case 2:
                    return "SECTION 2";
                case 3:
                    return "SECTION 3";
            }
            return "Section";
        }
    }
}
