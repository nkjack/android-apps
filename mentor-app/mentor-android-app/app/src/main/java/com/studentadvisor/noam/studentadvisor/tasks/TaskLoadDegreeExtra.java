package com.studentadvisor.noam.studentadvisor.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.ExtraInfoDegreeLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;

/**
 * Created by Noam on 11/28/2015.
 */
public class TaskLoadDegreeExtra extends AsyncTask<Void, Void, ExtraInfoDegree> {
    private ExtraInfoDegreeLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String dbid_degree;
    private String unique_id;
//    private Context mContext;

    public TaskLoadDegreeExtra(ExtraInfoDegreeLoadedListener myComponent ,String dbid_degree, String unique_id) {
        this.dbid_degree = dbid_degree;
        this.unique_id = unique_id;
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ExtraInfoDegree doInBackground(Void... params) {
        ExtraInfoDegree extraInfoDegree = null;


        if ( dbid_degree != null && unique_id != null) {
            L.m("DegreeUtils.getExtraInfoDegree");
            extraInfoDegree = DegreeUtils.getExtraInfoDegree(requestQueue,
                    dbid_degree,
                    unique_id);
        }

        if (extraInfoDegree == null){
            L.m("extraInfoDegree IS NULL");
        }
        else{
            L.m("extraInfoDegree IS NOT NULL");
        }
//        L.m("if success : " + if_success);

//        L.m(extraInfoDegree.toString());
        return extraInfoDegree;
    }


    @Override
    protected void onPostExecute(ExtraInfoDegree extraInfoDegree) {

        if (myComponent != null /*&& extraInfoDegree!= null*/) {
            L.m("onExtraInfoDegreeLoaded started from TaskLoadDegreeExtra");
            myComponent.onExtraInfoDegreeLoaded(extraInfoDegree);
        }
    }


}
