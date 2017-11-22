package com.example.root.proto2.Activities;

import android.content.AsyncTaskLoader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.root.proto2.Adapters.ViewAdapter;
import com.example.root.proto2.Apploader;
import com.example.root.proto2.Cachedocument;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;


public class ScrollActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DataModel> {

    private RecyclerView mrecyclerview;
    private RecyclerView.LayoutManager mlinearlayout;
    private RecyclerView.Adapter madapter;

    private Cachedocument cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cache=new Cachedocument();
        cache.ctx=getApplicationContext();

        mrecyclerview=(RecyclerView) findViewById(R.id.comp_recycler_view);
        mlinearlayout = new LinearLayoutManager(getApplicationContext());

        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(mlinearlayout);

        getSupportLoaderManager().initLoader(1,null,this);
    }

    @Override
    public Loader<DataModel> onCreateLoader(int id, Bundle args) {
        return new Apploader(getApplicationContext(),cache.readDoc("session").userid);
    }

    @Override
    public void onLoadFinished(Loader<DataModel> loader, DataModel data) {

        if(data!=null) {
            //Log.i("scroll", data.userid);
            madapter = new ViewAdapter(data.getComplaint());
            mrecyclerview.setAdapter(madapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<DataModel> loader) {
        madapter = new ViewAdapter(null);
        mrecyclerview.setAdapter(madapter);
    }
}
