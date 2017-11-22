package com.example.root.proto2.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.proto2.Models.CompModel;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.R;
import com.example.root.proto2.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/14/17.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<CompModel> mcompmodel;
    private View mview;

    public ViewAdapter(List<CompModel> myDatamodel) {
        mcompmodel = myDatamodel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vh= LayoutInflater.from(parent.getContext()).inflate(R.layout.comp_inflate,parent,false);
        return new ViewHolder(vh);
    }

    @Override
    public int getItemCount() {
        if(mcompmodel!=null) {
            return mcompmodel.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.CompHolder(mcompmodel.get(position));
    }
}


