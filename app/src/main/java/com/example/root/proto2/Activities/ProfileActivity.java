package com.example.root.proto2.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.example.root.proto2.Adapters.ViewAdapter;
import com.example.root.proto2.AppFireStore;
import com.example.root.proto2.Apploader;
import com.example.root.proto2.Apputil;
import com.example.root.proto2.Cachedocument;
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

import java.util.concurrent.TimeUnit;

import static com.example.root.proto2.Apputil.mAuth;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataModel>{


    private Apputil util;
    private Cachedocument cache;
    private AppFireStore fs;

    private ImageView profileimg;
    private FloatingActionButton fab;
    private FloatingActionButton fabsave;
    private TextView verify;
    private EditText username;
    private EditText phone;
    private EditText email;
    private EditText address_home;
    private EditText address_work;

    private AlertDialog OTPdialog;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        util=new Apputil();
        util.googleInit(ProfileActivity.this);
        util.firebaseInit();

        cache=new Cachedocument();
        cache.ctx=getApplicationContext();

        fs=new AppFireStore();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabsave = (FloatingActionButton) findViewById(R.id.fabsave);
        verify=(TextView) findViewById(R.id.verify);
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
                if(verify.getText()!="Verified") {
                    verifynumber();
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


    private void verifynumber(){

        EditText phonenumber=(EditText) findViewById(R.id.phone);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks callback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Log.d("verification","onVerificationCompleted:" + credential);
                        //signInWithPhoneAuthCredential(credential);
                        verify.setText("Verified");
                        OTPdialog.hide();
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
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
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
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber.getText().toString(),
                    60,
                    TimeUnit.SECONDS,
                    ProfileActivity.this,
                    callback);
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
        //editText.setKeyListener(null);
        //editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }
}
