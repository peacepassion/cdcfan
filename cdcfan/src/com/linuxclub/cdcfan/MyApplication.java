package com.linuxclub.cdcfan;

import android.app.Application;
import com.linuxclub.cdcfan.utils.LogHelper;

/**
 * Created by peace_da on 2015/5/9.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        com.github.snowdream.android.util.Log.setTag(LogHelper.DEFAULT_LOG_TAG);
    }
}
