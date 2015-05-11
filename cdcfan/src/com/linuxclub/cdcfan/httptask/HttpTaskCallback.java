package com.linuxclub.cdcfan.httptask;

/**
 * Created by peace_da on 2015/5/8.
 */
public interface HttpTaskCallback {
    void onSucc(int statusCode, String responseBody);

    void onErr(int responseCode, String responseBody);
}
