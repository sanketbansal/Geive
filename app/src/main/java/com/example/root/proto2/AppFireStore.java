package com.example.root.proto2;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.solver.Cache;
import android.util.Log;
import android.widget.Toast;

import com.example.root.proto2.Models.CompModel;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.Models.PhoneModel;
import com.example.root.proto2.Models.TimeModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;


public class AppFireStore {

    public Activity act;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DocumentReference dRef;
    public CollectionReference cRef = db.collection("Mobile").document("Grieveance").collection("Users");
    public DataModel dm;
    public PhoneModel pm;
    public CompModel cm;
    public TimeModel tm;
    public String docs;


    private FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

    public void AppFireStore(){
        db.setFirestoreSettings(settings);

    }

    public void setDoc(DocumentReference docRef){

            docRef.set(dm)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("firestore", "DocumentSnapshot successfully written!"+dm.userid);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("firestore", "Error writing document", e);
                }
            });
    }


    public void updateDoc(DocumentReference docRef,Map<String,Object>updatemodel){

        docRef.update(updatemodel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }


    public void readDoc(String id,Context ctx) {
        final String  id1=id;
        final Context ctx1=ctx;
        cRef.document("/" + id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("firestore", "DocumentSnapshot data: " + task.getResult().getData());
                        Cachedocument cache = new Cachedocument();
                        DataModel dm = document.toObject(DataModel.class);
                        cache.ctx=ctx1;
                        cache.writeDoc(dm, id1);
                    } else {
                        Log.d("firestore", "No such document");
                    }
                } else {
                    Log.d("firestore", "get failed with ", task.getException());
                }
            }
        });
    }


    public void queryDocs(CollectionReference coRef,String querykey,String queryvalue) {
        coRef.whereEqualTo(querykey,queryvalue)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult()!=null) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d("firestore", document.getId() + " => " + document.getData());
                                    //docs.put(document.getId(), document.toObject(DataModel.class));
                                    docs=document.getId();
                                }
                            }
                           else{
                                docs=null;
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void deleteDoc(DocumentReference docRef){
                docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firestore", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firestore", "Error deleting document", e);
                    }
                });
    }

    public void deletefield(DocumentReference docRef,String field){
        docRef.update(field, FieldValue.delete())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document field successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}
