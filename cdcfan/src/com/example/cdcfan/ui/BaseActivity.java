package com.example.cdcfan.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.cdcfan.R;
import com.example.cdcfan.config.Const;
import com.example.cdcfan.config.PreferenceHelper;
import com.gc.materialdesign.widgets.SnackBar;

/**
 * Created by peace_da on 2015/4/15.
 */
public abstract class BaseActivity extends SherlockFragmentActivity {

    private View mLoadingView;
    protected Const mConst;
    protected PreferenceHelper mPre;
    protected Resources mRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initBasicData();
        initView();
    }

    protected abstract int getLayout();

    protected void initBasicData() {
        mPre = PreferenceHelper.getInstance(this);
        mRes = getResources();
        mConst = Const.getInstance(this);
    }

    protected void initView() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLoadingView = findViewById(R.id.loading);
        showLoadingPage(false);
    }

    protected void showLoadingPage(boolean flag) {
        mLoadingView.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    protected void showToast(String content) {
        new SnackBar(this, content).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
