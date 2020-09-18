package com.studentadvisor.noam.studentadvisor.callbacks;

import android.graphics.Bitmap;

import com.studentadvisor.noam.studentadvisor.pojo.Degree;

/**
 * Created by Noam on 12/26/2015.
 */
public interface WelcomeScreenDialogCommunicator {
    public void continueToNext(int fragmentNum);
    public void showFileChooser();
    public void uploadImage();
    public void loadToolBarSearch();
    public void setIfBaby(boolean bool);
    public void setYearsStudying(int years);
    public void setDegreeStudying(int num, Degree degree);
    public void deleteDegreeStudying(int num);
    public void finishButton();
    public void onPostLoadedUsersExtraInfo(int success, String user_unique_id);
    public void backToLoginActivity();
    public void finishConfirmDialog(String userType);
}
