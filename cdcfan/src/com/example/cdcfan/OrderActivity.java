package com.example.cdcfan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.cdcfan.LoginActivity.User;
import com.example.cdcfan.UserService.UserServiceCallback;

public class OrderActivity extends SherlockFragmentActivity implements UserServiceCallback, OnClickListener {

    private String mOrderURL;
    private UserService mUserService;
    private TextView mBasicInfo;
    private EditText mCounter;
    private Button mOrder;


    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initBasicData();
        initView();
    }

    private void initBasicData() {
        mOrderURL = getResources().getString(R.string.portal);
        mUserService = UserService.getInstance(this);
        mUserService.setListener(this);

        Intent intent = getIntent();
        mUser = new User();
        mUser.psid = intent.getStringExtra(LoginActivity.KEY_PSID);
        mUser.name = intent.getStringExtra(LoginActivity.KEY_NAME);
        mUser.depcode = intent.getStringExtra(LoginActivity.KEY_DEPCODE);
    }

    private void initView() {
        mBasicInfo = (TextView) findViewById(R.id.basic_info);
        mBasicInfo.setText(mUser.name + " / " + mUser.depcode);

        mCounter = (EditText) findViewById(R.id.counter);

        mOrder = (Button) findViewById(R.id.order);
        mOrder.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserService.setListener(this);
    }

    @Override
    public void onCheckUserReturn(boolean flag, String jsonBody) {
    }

    @Override
    public void onCheckOrderReturn(boolean flag, String jsonBody) {
        Log.d("CDC", "order result: " + flag + ", body: " + jsonBody);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.order) {
            String url = Uri.parse(getResources().getString(R.string.portal)).buildUpon()
                    .appendQueryParameter(getResources().getString(R.string.order_param), mUser.psid).toString();
            Log.d("CDC", "order url: " + url);
            mUserService.startOrder(url);
        }
    }
}
