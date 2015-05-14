package com.linuxclub.cdcfan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.github.snowdream.android.app.DownloadTask;
import com.github.snowdream.android.app.updater.DefaultUpdateListener;
import com.github.snowdream.android.app.updater.UpdateFormat;
import com.github.snowdream.android.app.updater.UpdateInfo;
import com.github.snowdream.android.app.updater.UpdateManager;
import com.github.snowdream.android.app.updater.UpdateOptions;
import com.github.snowdream.android.app.updater.UpdatePeriod;
import com.linuxclub.cdcfan.R;

/**
 * Created by peace_da on 2015/5/8.
 */
public class StartActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.start;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.logo).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAppAvailability();
            }
        }, 3000);
    }

    private void checkAppAvailability() {
        String url = mConst.getUpdateInfoUrl();
        Log.d(LOG_TAG, "check update info url: " + url);
        UpdateOptions options = new UpdateOptions.Builder(this)
                .checkUrl(url)
                .updateFormat(UpdateFormat.JSON)
                .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                .checkPackageName(true)
                .build();
        new UpdateManager(this).check(this, options, this.new UpdateListener());
    }

    private class UpdateListener extends DefaultUpdateListener {

        @Override
        public void onStart() {
            Log.d(LOG_TAG, "start checking update info");
        }

        @Override
        public void onFinish() {
            Log.d(LOG_TAG, "finish checking update info");
        }

        @Override
        public void onError(Throwable thr) {
            super.onError(thr);
            Log.d(LOG_TAG, "http error");
            showRealToast(mRes.getString(R.string.conn_fail));
            StartActivity.this.finish();
        }

        @Override
        public void onShowUpdateUI(final UpdateInfo info) {
            // change the info url for the server returns only url path
            info.setApkUrl(mConst.getDomain() + info.getApkUrl());
            Log.d(LOG_TAG, "new apk download url: " + info.getApkUrl());
            new MaterialDialog.Builder(StartActivity.this)
                    .title(mRes.getString(R.string.update_title2))
                    .content(info.getUpdateTips().get("zh_CN"))
                    .positiveText(mRes.getString(R.string.update_ok))
                    .negativeText(mRes.getString(R.string.update_no2))
                    .neutralText(mRes.getString(R.string.update_skip))
                    .callback(new ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Log.d(LOG_TAG, "start upgrade");
                            informUpdate(info);
                            finish();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            Log.d(LOG_TAG, "cancel upgrade");
                            dialog.dismiss();
                            startActivity(new Intent(StartActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void onNeutral(MaterialDialog dialog) {
                            Log.d(LOG_TAG, "skip this version");
                            informSkip(info);
                            startActivity(new Intent(StartActivity.this, LoginActivity.class));
                            finish();
                        }
                    }).show();
        }

        @Override
        public void onShowForceUpdateUI(final UpdateInfo info) {
            new MaterialDialog.Builder(StartActivity.this)
                    .title(mRes.getString(R.string.update_title))
                    .content(info.getUpdateTips().get("zh_CN"))
                    .positiveText(mRes.getString(R.string.update_ok))
                    .negativeText(mRes.getString(R.string.update_no))
                    .callback(new ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Log.d(LOG_TAG, "start upgrade");
                            informUpdate(info);
                            finish();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        }

        @Override
        public void onShowNoUpdateUI() {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
            finish();
        }

        @Override
        public void onShowUpdateProgressUI(UpdateInfo info, DownloadTask task, int progress) {
            super.onShowUpdateProgressUI(info, task, progress);
        }

        @Override
        public void ExitApp() {

        }

    }

}
