package com.example.cdcfan;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager.UpnpServiceResponseListener;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class UserService {

    private static UserService sInstance;

    private Context mContext;
    private AsyncHttpClient mHttpClient = new AsyncHttpClient();
    private UserServiceCallback mListener;

    private UserService(Context ctx) {
        mContext = ctx;
        mHttpClient.setURLEncodingEnabled(false);
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

    public void startCheckUser(String url) {
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

    public void startOrder(String url) {
        mHttpClient.get(mContext, url, new AsyncHttpResponseHandler() {
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
