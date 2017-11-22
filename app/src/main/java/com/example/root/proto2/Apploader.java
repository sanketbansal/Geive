package com.example.root.proto2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.root.proto2.Models.DataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by root on 10/21/17.
 */

public class Apploader extends android.support.v4.content.AsyncTaskLoader<DataModel>{

    private Context loadctx;

    private Cachedocument cache;

    private AppFireStore fs;

    private String docid;

    private DataModel dm;

    public Apploader(Context context,String docid) {
        super(context);
        loadctx=context;
        cache=new Cachedocument();
        fs=new AppFireStore();
        this.docid=docid;
        Log.i("loaders",docid);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        cache.ctx=loadctx;
        forceLoad();
    }

    @Override
    public DataModel loadInBackground() {
        fs.cRef.document("/"+docid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("firestore", "DocumentSnapshot data: " + task.getResult().getData());
                        dm=document.toObject(DataModel.class);
                        cache.writeDoc(dm,docid);
                        Apploader.super.deliverResult(dm);
                    } else {
                        Log.d("firestore", "No such document");
                        dm=null;
                    }
                } else {
                    Log.d("firestore", "get failed with ", task.getException());
                }
            }
        });
        return cache.readDoc(docid);
        //return null;
    }


    @Override
    public void deliverResult(DataModel data){
        super.deliverResult(data);
    }
}
