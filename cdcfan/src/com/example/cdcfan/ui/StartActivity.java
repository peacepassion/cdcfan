package com.example.cdcfan.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.example.cdcfan.R;
import com.example.cdcfan.httptask.GetHttpTask;
import com.example.cdcfan.httptask.HttpTaskCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.github.snowdream.android.app.DownloadTask;
import com.github.snowdream.android.app.updater.AbstractUpdateListener;
import com.github.snowdream.android.app.updater.DefaultUpdateListener;
import com.github.snowdream.android.app.updater.UpdateFormat;
import com.github.snowdream.android.app.updater.UpdateInfo;
import com.github.snowdream.android.app.updater.UpdateManager;
import com.github.snowdream.android.app.updater.UpdateOptions;
import com.github.snowdream.android.app.updater.UpdatePeriod;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by peace_da on 2015/5/8.
 */
public class StartActivity extends BaseActivity implements HttpTaskCallback {

    private double mMinSupportedVersion;
    private double mLatestVersion;

    @Override
    protected int getLayout() {
        return R.layout.start;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAppAvailability();
    }

    private void checkAppAvailability() {
        String url = mConst.getUpdataInfoUrl();
        Log.d(LOG_TAG, "check update info url: " + url);
        UpdateOptions options = new UpdateOptions.Builder(this)
                .checkUrl(url)
                .updateFormat(UpdateFormat.JSON)
                .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                .checkPackageName(true)
                .build();
        new UpdateManager(this).check(this, options, this.new UpdateListener());
    }

    @Override
    public void onSucc(int statusCode, String responseBody) {
        if (parseResponse(responseBody)) {
            checkVersion();
        }
    }

    private void checkVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            double version = Double.valueOf(packageInfo.versionName).doubleValue();
            int versionCode = packageInfo.versionCode;
            Log.d(LOG_TAG, "app version: " + version + ", app version code: " + versionCode);
            if (version >= mMinSupportedVersion) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                showUpdateDlg();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showUpdateDlg() {
        final Dialog dlg = new Dialog(this, mRes.getString(R.string.update_title), mRes.getString(R.string.update_msg));
        dlg.addCancelButton(mRes.getString(R.string.update_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        }));
        dlg.addOkButton(mRes.getString(R.string.update_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "start upgrade");
                finish();
            }
        });
        dlg.show();
    }

    @Override
    public void onErr(int responseCode, String responseBody) {
        Toast.makeText(this, mRes.getString(R.string.conn_fail), Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean parseResponse(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            mMinSupportedVersion = obj.getDouble("minimal_supported_version");
            mLatestVersion = obj.getDouble("latest_version");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {

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
        public void onShowUpdateUI(final UpdateInfo info) {
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
        public void onShowForceUpdateUI(UpdateInfo info) {
            new MaterialDialog.Builder(StartActivity.this)
                    .title(mRes.getString(R.string.update_title))
                    .content(info.getUpdateTips().get("zh_CN"))
                    .positiveText(mRes.getString(R.string.update_ok))
                    .negativeText(mRes.getString(R.string.update_no))
                    .callback(new ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Log.d(LOG_TAG, "start upgrade");
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
