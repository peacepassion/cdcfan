package com.linuxclub.cdcfan.config;

/**
 * Created by peace_da on 2015/4/15.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String NAME = "CDC_FOOD";
    private static PreferenceHelper sInstance;

    private static final String KEY_LAST_USER_NAME = "key_last_user_name";

    private Context mContext;
    private SharedPreferences mSharedPre;

    public static PreferenceHelper getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (PreferenceHelper.class) {
                if (sInstance == null) {
                    sInstance = new PreferenceHelper(ctx);
                }
            }
        }
        return sInstance;
    }

    private PreferenceHelper(Context ctx) {
        mContext = ctx;
        mSharedPre = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public synchronized String getLastUserName() {
        return mSharedPre.getString(KEY_LAST_USER_NAME, "");
    }

    public synchronized void setKeyLastUserName(String userName) {
        mSharedPre.edit().putString(KEY_LAST_USER_NAME, userName).commit();
    }

}
