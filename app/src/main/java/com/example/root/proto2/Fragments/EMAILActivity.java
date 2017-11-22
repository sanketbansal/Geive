package com.example.root.proto2.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.root.proto2.R;

public class EMAILActivity extends Fragment {

    private View emailView;


    public static   EMAILActivity newInstance() {
        EMAILActivity fragment = new EMAILActivity();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        emailView = inflater.inflate(R.layout.activity_email, container, false);

        FloatingActionButton fab = (FloatingActionButton) emailView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendEmail();
            }
        });

        return emailView;
    }

}
