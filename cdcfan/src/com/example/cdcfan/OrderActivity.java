package com.example.cdcfan;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.cdcfan.UserService.UserServiceCallback;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class OrderActivity extends BaseActivity implements UserServiceCallback, OnClickListener {

    private TextView mBasicInfo;
    private EditText mCounter;
    private Button mOrderBtn;
    private Button mLogoutBtn;
    private OrderResult mOrderResult;

    User mUser;

    @Override
    protected int getLayout() {
        return R.layout.order;
    }

    @Override
    protected void initBasicData() {
        super.initBasicData();

        Intent intent = getIntent();
        mUser = new User();
        mUser.psid = intent.getStringExtra(LoginActivity.KEY_PSID);
        mUser.name = intent.getStringExtra(LoginActivity.KEY_NAME);
        mUser.depcode = intent.getStringExtra(LoginActivity.KEY_DEPCODE);
    }

    @Override
    protected void initView() {
        super.initView();
        mBasicInfo = (TextView) findViewById(R.id.basic_info);
        mBasicInfo.setText(mUser.name + " / " + mUser.depcode);
        mCounter = (EditText) findViewById(R.id.counter);
        mOrderBtn = (Button) findViewById(R.id.order);
        mOrderBtn.setOnClickListener(this);
        mLogoutBtn = (Button) findViewById(R.id.log_out);
        mLogoutBtn.setOnClickListener(this);
        showOrderSuccPage(false);
        showOrderFailPage(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserService.setListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.order) {
            try {
                mUserService.startOrder(mUser.psid, mUser.depcode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                showToast("fail to build post body");
            }
            showLoadingPage(true);
        } else if (id == R.id.log_out) {
            mPre.setKeyLastUserName("");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCheckUserReturn(boolean flag, String jsonBody) {
    }

    @Override
    public void onCheckOrderReturn(boolean flag, String jsonBody) {
        Log.d("CDC", "order result: " + flag + ", body: " + jsonBody);
        showLoadingPage(false);
        if (flag && parseResult(jsonBody)) {
            showOrderSuccPage(true);
        } else {
            showOrderFailPage(false);
        }
    }

    private boolean parseResult(String jsonObj) {
        try {
            JSONObject obj = new JSONObject(jsonObj);
            int num = obj.getInt("succeed_count");
            if (num > 0) {
                mOrderResult = OrderResult.SUCC;
                return true;
            }
            num = obj.getInt("exceed_count");
            if (num > 0) {
                mOrderResult = OrderResult.EXCEED;
                return false;
            }
            num = obj.getInt("rejected_count");
            if (num > 0) {
                mOrderResult = OrderResult.OVER_TIME;
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mOrderResult = OrderResult.FAIL;
        return false;
    }

    private void showOrderSuccPage(boolean flag) {
        // TODO
        showLoadingPage(flag);
    }

    private void showOrderFailPage(boolean flag) {
        if (flag) {
            showToast(String.format(mRes.getString(R.string.order_fail), mOrderResult.getDescription(mRes)));
        }
    }

    static enum OrderResult {
        SUCC(R.string.succ_des),
        EXCEED(R.string.exceed_des),
        OVER_TIME(R.string.overtime_des),
        FAIL(R.string.fail_des);

        private int mDescriptionID;

        OrderResult(int des) {
            mDescriptionID = des;
        }

        public String getDescription(Resources res) {
            return res.getString(mDescriptionID);
        }
    }

}
