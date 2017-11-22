package com.example.root.proto2.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.proto2.Adapters.ViewAdapter;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;
import com.google.gson.Gson;

import java.util.List;


public class CompActivity extends Fragment {

    private View compview;

    private Cachedocument cache;


    public static  CompActivity newInstance() {
        CompActivity fragment = new CompActivity();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        cache=new Cachedocument();

        compview=inflater.inflate(R.layout.activity_comp, container, false);
        return compview;
    }
}
