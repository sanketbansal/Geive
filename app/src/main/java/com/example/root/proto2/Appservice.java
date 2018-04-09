package com.example.root.proto2;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.root.proto2.Activities.LoginActivity;
import com.example.root.proto2.Activities.ScrollActivity;
import com.example.root.proto2.Models.DataModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by root on 10/8/17.
 */

public class Appservice extends Service {

    int mStartMode;

    boolean mAllowRebind;

    private Apputil util;

    private AppFireStore fs;

    private AppLocation locate;

    private final Messenger messenger=new Messenger(new IncomingHandler());

   // public static GoogleSignInAccount acct;
    FusedLocationProviderClient fusedlocation;

   // public static DataModel dataModel;

    private  ConnectivityManager check;

    private static ConnectivityManager.NetworkCallback netcheck;

    private DataModel datamodel;

    private String docid;

    private String fieldid;

    private DataModel updatemodel;


    @Override
    public void onCreate() {
        Log.d("appservices","service created");
        Toast.makeText(this,"service created",Toast.LENGTH_SHORT).show();
        check = (ConnectivityManager)
                Appservice.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        util=new Apputil();
        fs=new AppFireStore();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        /*locate=new AppLocation(this);
        locate.getLocation();

        Toast.makeText(this,locate.getLocation().toString(),Toast.LENGTH_SHORT).show();*/

        final String id;
        id=intent.getExtras().getString("docid");
       Toast.makeText(this,"service started",Toast.LENGTH_SHORT).show();
        NetworkInfo info = check.getActiveNetworkInfo();
        if(info==null) {
            //unregisterNetworkCallback();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            netcheck = new ConnectivityManager.NetworkCallback() {
                int lock = 0;
                @Override
                public void onAvailable(Network network) {
                    Toast.makeText(Appservice.this, "you are online now", Toast.LENGTH_SHORT).show();
                    if (lock == 0) {
                        fs.readDoc(id,getApplicationContext());
                        //docid=id;
                        //fireOperation(2);
                        lock = 1;
                        stopSelf();
                    }
                }
                @Override
                public void onLost(Network network) {
                    Toast.makeText(Appservice.this, "you are offline now", Toast.LENGTH_SHORT).show();
                }
            };
            check.registerNetworkCallback(builder.build(), netcheck);
        }
        else{
            //fs.readDoc(id,getApplicationContext());
            //docid=id;
            //fireOperation(2);
            stopSelf();
        }
        return mStartMode;
    }



    @Override
    public IBinder onBind(Intent intent) {
       Toast.makeText(this,"service binded",Toast.LENGTH_SHORT).show();

        datamodel= (DataModel) intent.getExtras().getSerializable("datamodel");
        docid=intent.getExtras().getString("docid");
        fieldid=intent.getExtras().getString("fieldid");
        updatemodel=(DataModel) intent.getExtras().getSerializable("updatemodel");
        Log.i("service",docid);
        return messenger.getBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {
       Toast.makeText(this,"service Unbinded",Toast.LENGTH_SHORT).show();
        return mAllowRebind;
    }


    @Override
    public void onRebind(Intent intent) {
        Toast.makeText(this,"service rebinded",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this,"service destroyed",Toast.LENGTH_SHORT).show();
        Log.d("appservices","service destroyed");
    }


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final int msg1=msg.what;
            NetworkInfo info = check.getActiveNetworkInfo();
                    if(info != null) {
                        fireOperation(msg1);
                    } else {
                        Toast.makeText(Appservice.this, "You are offline", Toast.LENGTH_SHORT).show();
                        //unregisterNetworkCallback();
                        NetworkRequest.Builder builder = new NetworkRequest.Builder();
                        netcheck =new ConnectivityManager.NetworkCallback() {
                            int lock=0;
                            @Override
                            public void onAvailable(Network network) {
                                Toast.makeText(Appservice.this,"you are online now",Toast.LENGTH_SHORT).show();
                                if(lock==0) {
                                    fireOperation(msg1);
                                    lock = 1;
                                }
                            }
                            @Override
                            public void onLost(Network network) {
                                Toast.makeText(Appservice.this,"you are offline now",Toast.LENGTH_SHORT).show();
                            }
                        };
                        check.registerNetworkCallback(builder.build(),netcheck);
                        Toast.makeText(Appservice.this,"Network listener Attached",Toast.LENGTH_SHORT).show();
                    }
        }
    }


    public void unregisterNetworkCallback(){
        if(netcheck!=null) {
            check.unregisterNetworkCallback(netcheck);
            Toast.makeText(Appservice.this, "Network listener unregistered", Toast.LENGTH_SHORT).show();
        }
    }

    private void fireOperation(int i){
        switch(i){
            case 1:
                Toast.makeText(getApplicationContext(), "hello! 1", Toast.LENGTH_SHORT).show();
                Log.i("appservices","handlemessage casse 1"+docid);
                    fs.dm = datamodel;
                    fs.setDoc(fs.cRef.document("/" + docid));
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "hello! 2", Toast.LENGTH_SHORT).show();
                Log.i("appservices","handlemessage casse 2"+docid);
                    fs.deleteDoc(fs.cRef.document("/"+docid));
                break;
            case 3:
                Toast.makeText(getApplicationContext(), "hello! 3", Toast.LENGTH_SHORT).show();
                 Log.i("appservices","handlemessage casse 3"+docid+fieldid);
                fs.deletefield(fs.cRef.document("/"+docid),fieldid);
                break;

            case 4:
                Toast.makeText(getApplicationContext(), "hello! 4", Toast.LENGTH_SHORT).show();
                Log.i("appservices","handlemessage casse 4"+docid);
                fs.dm = datamodel;
                fs.cRef=fs.db.collection("Mobile").document("Grieveance").collection("Timeline");
                fs.addDoc();
                break;
        }
    }
}


