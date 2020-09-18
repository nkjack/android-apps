package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 11/28/2015.
 */
public interface ExtraInfoDegreeLoadedListener {
    public void onExtraInfoDegreeLoaded(ExtraInfoDegree extraInfoDegree);
    public void onLikeDegree(int decision);
}
