package com.example.cdcfan;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager.UpnpServiceResponseListener;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
        String url = Uri.parse(mDomain).buildUpon().appendPath(mRes.getString(R.string.login_path))
                .appendQueryParameter(mContext.getResources().getString(R.string.login_param), userName)
                .toString();
        Log.d("CDC", "start checking user, url: " + url);
        mHttpClient.get(mContext, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("CDC", "check succ");
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

    public void startOrder(String psid, String depcode) throws UnsupportedEncodingException {
        String url = Uri.parse(mDomain).buildUpon().appendPath(mRes.getString(R.string.order_path)).toString();
        StringEntity postBody = new StringEntity("{" + "\"" + mRes.getString(R.string.order_param_order) + "\"" +  " : "
                + mRes.getString(R.string.order_param_order_dev_val) + ", "
                + "\"" + mRes.getString(R.string.order_param_psid) + "\" : " + psid + ", "
                + "\"" + mRes.getString(R.string.order_param_depcode) + "\" : " + depcode + "}");
        Log.d("CDC", "start order, url: " + url);
        Log.d("CDC", "start order, post body: " + postBody);
        mHttpClient.post(mContext, url, postBody, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("CDC", "order succ");
                if (mListener != null) {
                    mListener.onCheckOrderReturn(true, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("CDC", "order fail");
                Log.d("CDC", "status code: " + statusCode);
                Log.d("CDC", "error: " + error);
                if (mListener != null) {
                    mListener.onCheckOrderReturn(false, "" + statusCode);
                }
            }
        });
    }

    public static interface UserServiceCallback {
        void onCheckUserReturn(boolean flag, String jsonBody);
        void onCheckOrderReturn(boolean flag, String jsonBody);
    }
}
