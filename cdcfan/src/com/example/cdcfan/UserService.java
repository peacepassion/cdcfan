package com.example.cdcfan;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

public class UserService {

    private static UserService sInstance;

    private Context mContext;
    private AsyncHttpClient mHttpClient = new AsyncHttpClient();
    private UserServiceCallback mListener;
    private String mDomain;
    private Resources mRes;

    private UserService(Context ctx) {
        mContext = ctx;
        mHttpClient.setURLEncodingEnabled(false);
        mDomain = mContext.getResources().getString(R.string.portal);
        mRes = mContext.getResources();
    }

    public static UserService getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (UserService.class) {
                if (sInstance == null) {
                    sInstance = new UserService(ctx);
                }
            }
        }
        return sInstance;
    }

    public void setListener(UserServiceCallback listener) {
        mListener = listener;
    }

    public void startCheckUser(String userName) {
        String url = Uri.parse(mDomain).buildUpon().appendEncodedPath(mRes.getString(R.string.login_path))
                .appendQueryParameter(mContext.getResources().getString(R.string.login_param), userName)
                .build().toString();
        Log.d("CDC", "start checking user, url: " + url);
        mHttpClient.get(mContext, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("CDC", "check user succ");
                if (mListener != null) {
                    mListener.onCheckUserReturn(true, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("CDC", "check fail");
                Log.d("CDC", "status code: " + statusCode);
                Log.d("CDC", "error: " + error);
                if (mListener != null) {
                    mListener.onCheckUserReturn(false, "" + statusCode);
                }
            }
        });

    }

    public void startOrder(String psid, String depcode, String type) throws UnsupportedEncodingException {
        String url = Uri.parse(mDomain).buildUpon().appendEncodedPath(mRes.getString(R.string.order_path)).build().toString();
        Log.d("CDC", "start order, url: " + url);
        RequestParams params = new RequestParams();
        params.put(mRes.getString(R.string.order_param_order), type);
        params.put(mRes.getString(R.string.order_param_psid), psid);
        params.put(mRes.getString(R.string.order_param_depcode), depcode);
        mHttpClient.post(mContext, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("CDC", "order succ");
                if (mListener != null) {
                    mListener.onOrderReturn(true, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] haders, byte[] responseBody, Throwable error) {
                Log.d("CDC", "order fail");
                Log.d("CDC", "status code: " + statusCode);
                Log.d("CDC", "error: " + error);
                if (mListener != null) {
                    mListener.onOrderReturn(false, "" + statusCode);
                }
            }
        });
    }

    public void startCheckOrderID(String psid) {
        String url = Uri.parse(mDomain).buildUpon().appendEncodedPath(mRes.getString(R.string.check_order_path))
                .appendQueryParameter(mRes.getString(R.string.check_order_param_psid), psid).build().toString();
        Log.d("CDC", "start check order, url: " + url);
        mHttpClient.get(mContext, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("CDC", "check order succ");
                if (mListener != null) {
                    mListener.onCheckOrderReturn(true, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("CDC", "check order fail");
                Log.d("CDC", "status code: " + statusCode);
                Log.d("CDC", "error: " + error);
                if (mListener != null) {
                    mListener.onCheckOrderReturn(false, "" + statusCode);
                }
            }
        });
    }

    public void startCancelOrder(String orderID) throws UnsupportedEncodingException {
        String url = Uri.parse(mDomain).buildUpon().appendEncodedPath(mRes.getString(R.string.cancel_order_path)).build().toString();
        Log.d("CDC", "cancel order, url: " + url);
        RequestParams params = new RequestParams();
        params.put(mRes.getString(R.string.cancel_order_param_orderid), orderID);
        mHttpClient.post(mContext, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("CDC", "order succ");
                if (mListener != null) {
                    mListener.onCancelOrderReturn(true, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] haders, byte[] responseBody, Throwable error) {
                Log.d("CDC", "order fail");
                Log.d("CDC", "status code: " + statusCode);
                Log.d("CDC", "error: " + error);
                if (mListener != null) {
                    mListener.onCancelOrderReturn(false, "" + statusCode);
                }
            }
        });
    }

    public static interface UserServiceCallback {
        void onCheckUserReturn(boolean flag, String jsonBody);
        void onOrderReturn(boolean flag, String jsonBody);
        void onCheckOrderReturn(boolean flag, String jsonBody);
        void onCancelOrderReturn(boolean flag, String jsonBody);
    }
}
