package com.example.root.proto2.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.proto2.Adapters.ViewAdapter;
import com.example.root.proto2.AppFireStore;
import com.example.root.proto2.Apploader;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Fragments.DatePickerFrag;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Defaults;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jdeferred.DoneCallback;

import java.util.concurrent.TimeUnit;

import static com.example.root.proto2.Apputil.mAuth;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataModel>{


    private DataModel dm;

    private Apputil util;
    private Cachedocument cache;
    private AppFireStore fs;

    private ImageView profileimg;
    private FloatingActionButton fab;
    private FloatingActionButton fabsave;
    private TextView verify;
    private ProgressBar progress;
    private EditText username;
    private EditText phone;
    private EditText email;
    private EditText address_home;
    private EditText address_work;

    private AlertDialog OTPdialog;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int servicelock=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        util=new Apputil();
        util.googleInit(ProfileActivity.this);
        util.firebaseInit();
        util.ipc_util(getApplicationContext());

        cache=new Cachedocument();
        cache.ctx=getApplicationContext();
        dm=new DataModel();
        dm=cache.readDoc("session");
        dm=cache.readDoc(dm.userid);

        fs=new AppFireStore();
        fs.dm=dm;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabsave = (FloatingActionButton) findViewById(R.id.fabsave);
        verify=(TextView) findViewById(R.id.verify);
        progress=(ProgressBar) findViewById(R.id.progressBar);
        profileimg=(ImageView) findViewById(R.id.imgpro);


        username=(EditText) findViewById(R.id.username);
        phone=(EditText) findViewById(R.id.phone);
        email=(EditText) findViewById(R.id.email);
        address_home=(EditText) findViewById(R.id.address_home);
        address_work=(EditText) findViewById(R.id.address_work);

        disableEditText(username);
        disableEditText(phone);
        disableEditText(email);
        disableEditText(address_home);
        disableEditText(address_work);

        progress.setVisibility(View.INVISIBLE);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditText(username);
                enableEditText(phone);
                //enableEditText(email);
                enableEditText(address_home);
                enableEditText(address_work);
                fab.setVisibility(View.INVISIBLE);
                fabsave.setVisibility(View.VISIBLE);
            }
        });

        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditText(username);
                disableEditText(phone);
                disableEditText(email);
                disableEditText(address_home);
                disableEditText(address_work);
                fabsave.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
                if(verify.getText().equals("Verified")) {
                    savedata();
                    verify.setVisibility(View.VISIBLE);
                }
                else{
                    verifynumber();
                    verify.setVisibility(View.INVISIBLE);
                }
            }
        });


        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        ImageButton close=(ImageButton) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashintent=new Intent(ProfileActivity.this,NavActivity.class);
                startActivity(dashintent);
            }
        });
        getSupportLoaderManager().initLoader(3,null,this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(servicelock==1){
            servicelock=0;
            unbindService(util.ipcConnection);
        }
    }

    @Override
    public Loader<DataModel> onCreateLoader(int id, Bundle args) {
        return new Apploader(getApplicationContext(),cache.readDoc("session").userid);
    }

    @Override
    public void onLoadFinished(Loader<DataModel> loader, DataModel data) {

        if(data!=null) {
            username.setText(data.getUsername());
            phone.setText(data.getPhone());
            email.setText(data.getUserid());
            address_home.setText(data.getaddresshome());
            address_work.setText(data.getaddresswork());
            verify.setText(data.getPhoneVerification());
        }
    }

    @Override
    public void onLoaderReset(Loader<DataModel> loader) {
        username.setText(null);
        phone.setText(null);
        email.setText(null);
        address_home.setText(null);
        address_work.setText(null);
        verify.setText(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            try {
                Log.i("data", extras.get("data").toString());
            }
            catch (Exception e){Log.i("data", e.toString());}
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //profileimg.setImageBitmap(imageBitmap);
        }
    }

    private void savedata(){
        dm.setUsername(username.getText().toString());
        dm.setUserid(dm.userid);
        dm.setPhone(phone.getText().toString());
        dm.setaddresshome(address_home.getText().toString());
        dm.setaddresswork(address_work.getText().toString());

        util.serviceintent.putExtra("docid",dm.userid);
        util.serviceintent.putExtra("datamodel",dm);
        util.serviceintent.putExtra("fieldid","");
        util.serviceintent.putExtra("updatemodel",dm);
        bindService(util.serviceintent,util.ipcConnection, Context.BIND_AUTO_CREATE);
        servicelock=1;
        util.msg= Message.obtain(null,1,0,0);
        /*try {
            util.ipcMessenger.send(util.msg);
        } catch (Exception e) {
            Log.i("appservice", e.toString());
            Toast.makeText(this,"Failed Try Again!",Toast.LENGTH_SHORT).show();
        }*/
    }


    private void verifynumber(){

       final EditText phonenumber=(EditText) findViewById(R.id.phone);

       final PhoneAuthProvider.OnVerificationStateChangedCallbacks callback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Log.d("verification","onVerificationCompleted:" + credential);
                        //signInWithPhoneAuthCredential(credential);
                        verify.setText("Verified");
                        dm.setPhoneVerification("Verified");
                        verify.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                        OTPdialog.hide();
                        savedata();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("verification", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        Log.d("verification", "onCodeSent:" + verificationId);

                        AlertDialog.Builder OTPbuilder=new AlertDialog.Builder(new ContextThemeWrapper(ProfileActivity.this,R.style.OTPdialog));
                        View mview=getLayoutInflater().inflate(R.layout.verification,null);

                        mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        OTPbuilder.setView(mview);
                        OTPdialog=OTPbuilder.create();
                        OTPdialog.show();
                        // Save verification ID and resending token so we can use them later
                       String mVerificationId = verificationId;
                       PhoneAuthProvider.ForceResendingToken mResendToken = token;
                    }
                };

        if(phonenumber.getText()!=null) {
            progress.setVisibility(View.VISIBLE);
            fs.queryDocs("phone", phonenumber.getText().toString());
            fs.promise.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    Log.i("promises",result.toString()+"profileactivity");
                    if (fs.docs == 0||fs.docs==1){
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phonenumber.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                ProfileActivity.this,
                                callback);
                    }
                    else{
                        Toast.makeText(ProfileActivity.this,"User already registered with this number",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("verification", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w("verification", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }


    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }
}
