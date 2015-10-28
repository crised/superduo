package it.jaschke.alexandria;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by crised on 28-10-15.
 */
public class Utility {

    static public void setBarCodeAppStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.app_status_key), MainActivity.ACTIVITY_BAR_CODE_STATUS);
        spe.apply();
    }

    static public void setNormalAppStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.app_status_key), MainActivity.ACTIVITY_NORMAL_STATUS);
        spe.apply();
    }

    static public int getBarCodeAppStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        int status = sp.getInt(c.getString(R.string.app_status_key), 0);
        return status;

    }
}
