package com.example.root.proto2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.root.proto2.Activities.LoginActivity;
import com.example.root.proto2.Models.DataModel;
import com.google.firebase.firestore.DocumentReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by root on 10/21/17.
 */

public class Cachedocument {

    private FileOutputStream fout;
    private FileInputStream fin;
    private ObjectOutputStream obout;
    private ObjectInputStream obin;
    public Context ctx;

    public void writeDoc(DataModel dm,String filename){
        try {
            //filename=ctx.getFilesDir()+filename;
            fout = ctx.openFileOutput(filename,Context.MODE_PRIVATE);
            obout=new ObjectOutputStream(fout);
            obout.writeObject(dm);
            obout.close();
            fout.close();
            Toast.makeText(ctx,"Data cached succesfully",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            //Toast.makeText(ctx,e.toString(),Toast.LENGTH_LONG).show();
            Log.i("cache",e.toString());
        }
    }

    public DataModel readDoc(String filename){
        DataModel dm;
        try {
            fin = ctx.openFileInput(filename);
            obin=new ObjectInputStream(fin);
            dm=(DataModel) obin.readObject();
            obin.close();
            fin.close();
        }
        catch(Exception e){
            //Toast.makeText(ctx,"Error in caching data file not found",Toast.LENGTH_SHORT).show();
            Log.i("cache",e.toString());
            dm=null;
        }
        return dm;
    }
}
