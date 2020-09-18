package com.studentadvisor.noam.studentadvisor.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

/**
 * Created by Noam on 12/27/2015.
 */
public class TaskInsertUsersExtraInfo extends AsyncTask<Void, Void, Integer> {
    private WelcomeScreenDialogCommunicator mCommunicator;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String user_unique_id;
    private String user_type;
    private String user_year;
    private String user_degree_1;
    private String user_degree_2;
    private String user_degree_3;
    private Context mContext;

    public TaskInsertUsersExtraInfo(WelcomeScreenDialogCommunicator communicator,
                             String user_unique_id,
                             String user_type,
                             int user_year,
                             int user_degree_1,
                             int user_degree_2,
                             int user_degree_3) {
        this.user_unique_id = user_unique_id;
        this.user_type = user_type;
        this.user_year = String.valueOf(user_year);
        this.user_degree_1 = String.valueOf(user_degree_1);
        this.user_degree_2 = String.valueOf(user_degree_2);
        this.user_degree_3 = String.valueOf(user_degree_3);
        this.mCommunicator = communicator;
        this.mContext = (Context) communicator;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Integer doInBackground(Void... params) {
        int if_success = -1;
        if (user_unique_id != null && user_type != null && user_year != null
                && user_degree_1 != null && user_degree_2 != null && user_degree_3 != null) {
            if_success = DegreeUtils.insertUserExtraInfo(requestQueue,
                    user_unique_id,
                    user_type,
                    user_year,
                    user_degree_1,
                    user_degree_2,
                    user_degree_3);
        }

        L.m("if success : " + if_success);
        Integer toReturn = Integer.valueOf(if_success);
        return toReturn;
    }


    @Override
    protected void onPostExecute(Integer success) {
        if (success != null) {
            if (success.intValue() == 1) {
                // successfully created product
                if (mCommunicator != null) {
                    mCommunicator.onPostLoadedUsersExtraInfo(1, user_unique_id);
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
            }
            else{
                if (mCommunicator != null) {
                    mCommunicator.onPostLoadedUsersExtraInfo(0, user_unique_id);
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
            }
        }
    }
}

