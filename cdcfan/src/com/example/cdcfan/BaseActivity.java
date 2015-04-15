package com.example.cdcfan;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by peace_da on 2015/4/15.
 */
public abstract class BaseActivity extends SherlockFragmentActivity {

    private View mLoadingView;
    protected UserService mUserService;
    protected PreferenceHelper mPre;
    protected Resources mRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initBasicData();
        initView();
    }

    protected abstract int getLayout();

    protected void initBasicData() {
        mUserService = UserService.getInstance(this);
        mPre = PreferenceHelper.getInstance(this);
        mRes = getResources();
    }

    protected void initView() {
        mLoadingView = findViewById(R.id.loading);
        showLoadingPage(false);
    }

    protected void showLoadingPage(boolean flag) {
        mLoadingView.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

}
