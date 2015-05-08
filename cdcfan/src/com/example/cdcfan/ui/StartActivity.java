package com.example.cdcfan.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.cdcfan.R;
import com.example.cdcfan.httptask.GetHttpTask;
import com.example.cdcfan.httptask.HttpTaskCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
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
        new GetHttpTask(this, this, mConst.getDomain(), mConst.getVersionPath(), null).execute();
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

}
