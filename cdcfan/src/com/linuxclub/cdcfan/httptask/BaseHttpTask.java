package com.linuxclub.cdcfan.httptask;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.linuxclub.cdcfan.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

/**
 * Created by peace_da on 2015/5/8.
 */
class BaseHttpTask {

    protected static AsyncHttpClient sHttpClient = new AsyncHttpClient();

    static {
        sHttpClient.setURLEncodingEnabled(false);
    }

    protected final String LOG_TAG = LogHelper.getNativeSimpleLogTag(this.getClass(), LogHelper.DEFAULT_LOG_TAG);

    protected Context mContext;
    protected Resources mRes;

    protected String mUrl;
    protected HttpTaskCallback mListener;
    protected AsyncHttpResponseHandler mDefaultResponseHandler;

    protected BaseHttpTask(Context ctx, HttpTaskCallback listener) {
        mContext = ctx;
        mRes = mContext.getResources();
        mListener = listener;
        mDefaultResponseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(LOG_TAG, "task succ");
                String res = "";
                if (responseBody != null) {
                    res = new String(responseBody);
                }
                Log.d(LOG_TAG, "response code: " + statusCode);
                Log.d(LOG_TAG, "response: " + res);
                mListener.onSucc(statusCode, res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(LOG_TAG, "task fail");
                error.printStackTrace();
                String res = "";
                if (responseBody != null) {
                    res = new String(responseBody);
                }
                Log.d(LOG_TAG, "response code: " + statusCode);
                Log.d(LOG_TAG, "response: " + res);
                mListener.onErr(statusCode, res);
            }
        };
    }

    public void execute() {
        Log.d(LOG_TAG, "start execution, url: " + mUrl);
    }

}
