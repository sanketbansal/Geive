package com.example.root.proto2.Models;

import com.example.root.proto2.Cachedocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/27/17.
 */

public class DummyData {

    private DataModel dm;
    private List<CompModel> lcm;
    private  List<TimeModel> ltm;
    private Cachedocument cache;

    public DataModel setdata(){

        cache=new Cachedocument();
        dm=new DataModel();
        lcm=new ArrayList<CompModel>();
        ltm=new ArrayList<TimeModel>();

        for(int i=0; i<10;i++){
        CompModel cm =new CompModel();
            lcm.add(cm);
        }

        for(int i=0; i<10;i++){
            TimeModel tm =new TimeModel();
            ltm.add(tm);
        }
        dm.setComplaint(lcm);
        dm.setTimeline(ltm);
        return dm;
    }
}
