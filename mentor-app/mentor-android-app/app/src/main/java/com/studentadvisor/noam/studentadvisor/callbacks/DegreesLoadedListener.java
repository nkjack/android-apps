package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.Degree;

import java.util.ArrayList;

/**
 * Created by Noam on 11/7/2015.
 */
public interface DegreesLoadedListener {
    public void onAllDegreesLoaded(ArrayList<Degree> listDegrees);
}
