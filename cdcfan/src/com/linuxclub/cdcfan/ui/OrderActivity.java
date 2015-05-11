package com.linuxclub.cdcfan.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.linuxclub.cdcfan.R;
import com.linuxclub.cdcfan.httptask.HttpTaskCallback;
import com.linuxclub.cdcfan.httptask.PostHttpTask;
import com.gc.materialdesign.views.Button;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends LoadingBaseActivity implements OnClickListener, HttpTaskCallback {

    private TextView mBasicInfo;
    private Button mOrderBtn;
    private Button mLogoutBtn;
    private OrderResult mOrderResult;
    private Button mCheckOrderBtn;

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
        mBasicInfo = (TextView) findViewById(R.id.title);
        mBasicInfo.setText(mUser.name + " / " + mUser.depcode);
        mOrderBtn = (Button) findViewById(R.id.order);
        mOrderBtn.setOnClickListener(this);
        mLogoutBtn = (Button) findViewById(R.id.log_out);
        mLogoutBtn.setOnClickListener(this);
        mCheckOrderBtn = (Button) findViewById(R.id.check_order);
        mCheckOrderBtn.setOnClickListener(this);

        showOrderSuccPage(false);
        showOrderFailPage(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.order) {
            new PostHttpTask(this, this, mConst.getDomain(), mConst.getOrderPath(), mConst.getOrderParams(mUser.psid, mUser.depcode, mRes.getString(R.string.order_param_order_dev_val))).execute();
            showLoadingPage(true);
        } else if (id == R.id.log_out) {
            mPre.setKeyLastUserName("");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.check_order) {
            Intent intent = new Intent(this, CancelOrderActivity.class);
            intent.putExtra(LoginActivity.KEY_PSID, mUser.psid);
            startActivity(intent);
        }
    }

    @Override
    public void onSucc(int statusCode, String responseBody) {
        Log.d("CDC",  "response: " + responseBody);
        showLoadingPage(false);
        if (parseResult(responseBody)) {
            showOrderSuccPage(true);
        } else {
            showOrderFailPage(true);
        }
    }

    @Override
    public void onErr(int responseCode, String responseBody) {
        Log.d("CDC",  "response: " + responseBody);
        showLoadingPage(false);
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
        if (flag) {
            showFixToast(mRes.getString(R.string.order_succ));
        }
    }

    private void showOrderFailPage(boolean flag) {
        if (flag) {
            showFixToast(String.format(mRes.getString(R.string.order_fail), mOrderResult.getDescription(mRes)));
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

    static enum OrderType {
        DINNER(R.string.order_type_dinner),
        LUNCH(R.string.order_type_lunch);

        private int mDesID;

        OrderType(int id) {
            mDesID = id;
        }

        public String getDescription(Resources res) {
            return res.getString(mDesID);
        }
    }

}
