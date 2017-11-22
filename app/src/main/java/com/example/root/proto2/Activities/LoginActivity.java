package com.example.root.proto2.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.proto2.AppFireStore;
import com.example.root.proto2.Appservice;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public  class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    // UI references.
    private AppFireStore fs;

    private Intent navintent;

    private  Apputil util;

    private DataModel dataModel;

    private GoogleSignInAccount acct;

    private Cachedocument cache;

    private Intent serviceintent;

    private Messenger ipcMessenger;

    private Message msg;

    private ServiceConnection ipcConnection;

    private boolean ipcBound;

    private int servicelock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        navintent=new Intent(LoginActivity.this, NavActivity.class);
        util=new Apputil();
        util.googleInit(LoginActivity.this);
        util.firebaseInit();

        fs=new AppFireStore();

        dataModel=new DataModel();

        cache=new Cachedocument();
        cache.ctx=getApplicationContext();

        serviceintent=new Intent(getBaseContext(),Appservice.class);

        ipcConnection=new ServiceConnection(){
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the object we can use to
                // interact with the service.  We are communicating with the
                // service using a Messenger, so here we get a client-side
                // representation of that from the raw IBinder object.
                ipcMessenger = new Messenger(service);
                ipcBound = true;
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                ipcMessenger = null;
                ipcBound = false;
            }
        };



        Button mGoogleSignInButton = (Button) findViewById(R.id.googlesignin);
        mGoogleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(util.mGoogleApiClient);
                startActivityForResult(signInIntent, 1);
                Log.d("google","sign in");
            }
        });

        TextView addtext=(TextView) findViewById(R.id.add_user);
        addtext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                util.mGoogleApiClient.clearDefaultAccountAndReconnect();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(util.mGoogleApiClient);
                startActivityForResult(signInIntent, 1);
                Log.d("Googlesignin","New sign in");
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(servicelock==1) {
            unbindService(ipcConnection);
            servicelock=0;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("LoginConnection failed", "onConnectionFailed:" + connectionResult);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Googlesignin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            acct=result.getSignInAccount();
            Toast.makeText(this, acct.getEmail(), Toast.LENGTH_SHORT).show();

            dataModel.setUserid(acct.getEmail());
            dataModel.setUsername(acct.getDisplayName());
            dataModel.setComplaint(cache.readDoc("dummy").getComplaint());
            dataModel.setTimeline(cache.readDoc("dummy").getTimeline());
            cache.ctx=getApplicationContext();
            cache.writeDoc(dataModel,"session");

            serviceintent.putExtra("docid",acct.getEmail());
            serviceintent.putExtra("datamodel",dataModel);
            serviceintent.putExtra("fieldid","");
            serviceintent.putExtra("updatemodel",dataModel);
            bindService(serviceintent,ipcConnection,Context.BIND_AUTO_CREATE);
            servicelock=1;

            fs.cRef.whereEqualTo("userid",acct.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d("firestore", document.getId() + " => " + document.getData());
                                        fs.docs = document.getId();
                                    }
                                } else {
                                    fs.docs = null;
                                }

                                if (fs.docs == null){
                                    msg=Message.obtain(null,1,0,0);
                                    try {
                                        ipcMessenger.send(msg);
                                    }catch(Exception e){
                                        Log.i("appservice",e.toString());
                                    }
                                    if (util.netInfo(LoginActivity.this) != null) {
                                        firebaseAuthWithGoogle(acct);
                                    }
                                    Intent profileintent = new Intent(LoginActivity.this, ProfileActivity.class);
                                    startActivity(profileintent);
                                }
                                else {
                                    Log.d("Googlesignin", acct.getEmail());
                                    fs.readDoc(acct.getEmail(),getApplicationContext());
                                    startActivity(navintent);
                                }
                            }
                            else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        else {
            Log.d("Googlesignin", "Sign in failed");
            Toast.makeText(this, "Sign in failed" + "\n" + "Check Your Connection\n" + "Try Again!", Toast.LENGTH_SHORT).show();
        }
    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(util.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.i("Access","Account removed succesfully");
                    }
                });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firebasesignin", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        util.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("firebasesignin", "signInWithCredential:success");

                            util.user=util.mAuth.getCurrentUser();
                        } else {
                            Log.w("firebasesignin", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }) ;
    }
}