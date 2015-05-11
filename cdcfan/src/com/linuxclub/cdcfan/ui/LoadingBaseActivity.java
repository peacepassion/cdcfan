package com.linuxclub.cdcfan.ui;

import android.view.View;
import com.linuxclub.cdcfan.R;

/**
 * Created by peace_da on 2015/5/8.
 */
abstract class LoadingBaseActivity extends BaseActivity {
    private View mLoadingView;

    @Override
    protected void initView() {
        super.initView();
        mLoadingView = findViewById(R.id.loading);
        showLoadingPage(false);
    }

    protected void showLoadingPage(boolean flag) {
        mLoadingView.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

}
