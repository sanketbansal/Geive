package com.example.root.proto2;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.root.proto2.Models.CompModel;
import com.example.root.proto2.Models.DataModel;
import com.example.root.proto2.Models.TimeModel;

import java.util.List;

/**
 * Created by root on 10/14/17.
 */

public class ViewHolder extends RecyclerView.ViewHolder{

    public CardView cv;
    public View itemview;

     public ViewHolder(View v){
         super(v);
         itemview=v;
     }

    public void CompHolder(CompModel compmodel){
        cv = (CardView) itemView.findViewById(R.id.card_view);
        TextView category = (TextView)itemView.findViewById(R.id.category);
        TextView description = (TextView)itemView.findViewById(R.id.description);
        TextView city = (TextView)itemView.findViewById(R.id.city);
        category.setText(compmodel.getCategory());
        description.setText(compmodel.getDescription());
        city.setText(compmodel.getCity());
    }

    public void DashHolder(CompModel timemodel) {
        cv = (CardView)itemView.findViewById(R.id.card_view);
        TextView personName = (TextView)itemView.findViewById(R.id.person_name);
        TextView personAge = (TextView)itemView.findViewById(R.id.person_age);
        personName.setText(timemodel.getState());
        personAge.setText(timemodel.getCity());
    }
}
