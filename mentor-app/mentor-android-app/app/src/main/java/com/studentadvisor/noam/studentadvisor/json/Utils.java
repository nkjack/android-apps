package com.studentadvisor.noam.studentadvisor.json;

import org.json.JSONObject;

/**
 * Created by Noam on 11/7/2015.
 */
public class Utils {
    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

}
