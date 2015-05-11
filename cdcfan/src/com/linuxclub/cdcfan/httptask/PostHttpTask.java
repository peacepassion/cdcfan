package com.linuxclub.cdcfan.httptask;

import android.content.Context;
import android.net.Uri;
import com.loopj.android.http.RequestParams;

import java.util.Map;

/**
 * Created by peace_da on 2015/5/8.
 */
public class PostHttpTask extends BaseHttpTask {

    private RequestParams mParams;

    public PostHttpTask(Context ctx, HttpTaskCallback listener, String domain, String path, Map<String, String> params) {
        super(ctx, listener);

        mUrl = Uri.parse(domain).buildUpon().appendEncodedPath(path).build().toString();
        mParams = new RequestParams();
        if (params != null) {
            for (String key : params.keySet()) {
                mParams.put(key, params.get(key));
            }
        }
    }

    @Override
    public void execute() {
        super.execute();
        sHttpClient.post(mContext, mUrl, mParams, mDefaultResponseHandler);
    }
}
