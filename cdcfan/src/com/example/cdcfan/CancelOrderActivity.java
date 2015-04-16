package com.example.cdcfan;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.example.cdcfan.UserService.UserServiceCallback;
import com.gc.materialdesign.views.Button;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peace_da on 2015/4/15.
 */
public class CancelOrderActivity extends BaseActivity implements OnClickListener, UserServiceCallback {

    public Button mCancelBtn;
    private String mPSID;
    private List<Order> mOrderList;
    private TextView mHasOrderTV;
    private View mCancelView;

    @Override
    protected int getLayout() {
        return R.layout.cancel_order;
    }

    @Override
    protected void initBasicData() {
        super.initBasicData();
        Intent intent = getIntent();
        mPSID = intent.getStringExtra(LoginActivity.KEY_PSID);
        mOrderList = new ArrayList<Order>();
    }

    @Override
    protected void initView() {
        super.initView();
        mCancelBtn = (Button) findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(this);
        mHasOrderTV = (TextView) findViewById(R.id.has_order);
        mCancelView = findViewById(R.id.cancel_view);

        ((TextView) findViewById(R.id.title)).setText(mRes.getString(R.string.check_order));

        showLoadingPage(true);
        showDisableBtnPage(false, "");

        startGetOrderInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserService.setListener(this);
    }

    private void startGetOrderInfo() {
        if (mPSID.equals("")) {
            showLoadingPage(false);
            showDisableBtnPage(true, mRes.getString(R.string.check_order_fail));
        } else {
            mUserService.startCheckOrderID(mPSID);
        }
    }

    private void showAbleBtnPage(boolean flag) {
        if (flag) {
            mHasOrderTV.setText(String.format(mRes.getString(R.string.has_order), 1));
        }
    }

    private void showDisableBtnPage(boolean flag, String content) {
        if (flag) {
            showToast(content);
            mCancelBtn.setEnabled(false);
            mHasOrderTV.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
    int id = v.getId();
        if (id == R.id.cancel) {
            Log.d("CDC", "start cancel order");
            try {
                mUserService.startCancelOrder(mOrderList.get(0).mOrderID);
                showLoadingPage(true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCheckUserReturn(boolean flag, String jsonBody) {
    }

    @Override
    public void onOrderReturn(boolean flag, String jsonBody) {
    }

    @Override
    public void onCheckOrderReturn(boolean flag, String jsonBody) {
        Log.d("CDC", "check order return, flag: " + flag + ", body: " + jsonBody);
        showLoadingPage(false);
        if (flag) {
            if (parseCheckOrderResult(jsonBody)) {
                showAbleBtnPage(true);
            } else {
                showDisableBtnPage(true, mRes.getString(R.string.check_order_fail2));
            }
        } else {
            showDisableBtnPage(true, mRes.getString(R.string.check_order_fail));
        }
    }

    @Override
    public void onCancelOrderReturn(boolean flag, String jsonBody) {
        Log.d("CDC", "cancel order return, flag: " + flag + ", body: " + jsonBody);
        showLoadingPage(false);
        if (flag) {
            showDisableBtnPage(true, mRes.getString(R.string.cancel_order_succ));
            showToast(mRes.getString(R.string.cancel_order_succ));
        } else {
            showDisableBtnPage(true, mRes.getString(R.string.cancel_order_fail));
        }
    }

    private boolean parseCheckOrderResult(String jsonBody) {
        try {
            JSONArray arr = new JSONArray(jsonBody);
            for (int i = 0; i < arr.length(); ++i) {
                Order order = new Order();
                if (parseOrder(arr.getJSONObject(i), order)) {
                    mOrderList.add(order);
                }
            }
            if (mOrderList.size() > 0) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean parseOrder(JSONObject obj, Order order) {
        try {
            order.mOrderID = obj.getString("orderid");
            if (order.mOrderID.equals("")) {
                return false;
            }
            order.mFoodName = obj.getString("foodname");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    static class Order {
        String mOrderID = "";
        String mFoodName = "";
    }
}
