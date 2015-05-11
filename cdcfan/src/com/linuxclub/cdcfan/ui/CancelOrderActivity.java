package com.linuxclub.cdcfan.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.linuxclub.cdcfan.R;
import com.linuxclub.cdcfan.httptask.GetHttpTask;
import com.linuxclub.cdcfan.httptask.HttpTaskCallback;
import com.linuxclub.cdcfan.httptask.PostHttpTask;
import com.gc.materialdesign.views.Button;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peace_da on 2015/4/15.
 */
public class CancelOrderActivity extends LoadingBaseActivity implements OnClickListener {

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
    }

    private void startGetOrderInfo() {
        if (mPSID.equals("")) {
            showLoadingPage(false);
            showDisableBtnPage(true, mRes.getString(R.string.check_order_fail));
        } else {
            new GetHttpTask(this, new HttpTaskCallback() {
                @Override
                public void onSucc(int statusCode, String responseBody) {
                    showLoadingPage(false);
                    if (parseCheckOrderResult(responseBody)) {
                        showAbleBtnPage(true);
                    } else {
                        showDisableBtnPage(true, mRes.getString(R.string.check_order_fail2));
                    }
                }

                @Override
                public void onErr(int responseCode, String responseBody) {
                    showLoadingPage(false);
                    showDisableBtnPage(true, mRes.getString(R.string.check_order_fail2));
                }
            }, mConst.getDomain(), mConst.getCheckOrderPath(), mConst.getCheckOrderParams(mPSID)).execute();
        }
    }

    private void showAbleBtnPage(boolean flag) {
        if (flag) {
            mHasOrderTV.setText(String.format(mRes.getString(R.string.has_order), 1));
        }
    }

    private void showDisableBtnPage(boolean flag, String content) {
        if (flag) {
            showFixToast(content);
            mCancelBtn.setEnabled(false);
            mHasOrderTV.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            Log.d("CDC", "start cancel order");
            new PostHttpTask(this, new HttpTaskCallback() {
                @Override
                public void onSucc(int statusCode, String responseBody) {
                    showLoadingPage(false);
                    showDisableBtnPage(true, mRes.getString(R.string.cancel_order_succ));
                    showFixToast(mRes.getString(R.string.cancel_order_succ));
                }

                @Override
                public void onErr(int responseCode, String responseBody) {
                    showLoadingPage(false);
                    showDisableBtnPage(true, mRes.getString(R.string.cancel_order_fail));
                }
            }, mConst.getDomain(), mConst.getCancelOrderPath(), mConst.getCancelOrderParams(mOrderList.get(0).mOrderID)).execute();
            showLoadingPage(true);
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
