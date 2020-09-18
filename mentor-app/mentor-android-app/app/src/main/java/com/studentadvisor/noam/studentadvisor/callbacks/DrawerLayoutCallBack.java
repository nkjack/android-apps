package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;

import java.util.ArrayList;

/**
 * Created by Noam on 12/18/2015.
 */
public interface DrawerLayoutCallBack {
    public void onGetFollowedDegrees( ArrayList<FollowObj> followedDegrees);
    public void onGetLikedDegrees( ArrayList<LikeObj> likedDegrees);
}
