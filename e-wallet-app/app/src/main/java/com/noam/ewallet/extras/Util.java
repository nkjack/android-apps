package com.noam.ewallet.extras;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by Noam on 3/31/2017.
 */
public class Util {
    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static Date loadDate(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }
}
