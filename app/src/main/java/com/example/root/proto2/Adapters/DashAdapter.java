package com.example.root.proto2.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.Models.TimeModel;
import com.example.root.proto2.R;
import com.example.root.proto2.ViewHolder;

import java.util.List;

/**
 * Created by root on 10/26/17.
 */

public class DashAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<TimeModel> mtimemodel;
    private View mview;

    public DashAdapter(List<TimeModel> myDatamodel) {
        mtimemodel = myDatamodel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vh= LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_inflate,parent,false);
        return new ViewHolder(vh);
    }

    @Override
    public int getItemCount() {
        if(mtimemodel!=null) {
            return mtimemodel.size();
        }
        else{
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.DashHolder(mtimemodel.get(position));
    }
}
