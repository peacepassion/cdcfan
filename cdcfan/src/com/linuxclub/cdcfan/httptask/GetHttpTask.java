package com.linuxclub.cdcfan.httptask;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;

import java.util.Map;

/**
 * Created by peace_da on 2015/5/8.
 */
public class GetHttpTask extends BaseHttpTask {

    public GetHttpTask(Context ctx, HttpTaskCallback listener, String domain, String path, Map<String, String> params) {
        super(ctx, listener);
        Builder builder = Uri.parse(domain).buildUpon().appendEncodedPath(path);
        if (params != null) {
            for (String key : params.keySet()) {
                builder.appendQueryParameter(key, params.get(key));
            }
        }
        mUrl = builder.build().toString();
    }

    @Override
    public void execute() {
        super.execute();
        sHttpClient.get(mContext, mUrl, mDefaultResponseHandler);
    }

}
