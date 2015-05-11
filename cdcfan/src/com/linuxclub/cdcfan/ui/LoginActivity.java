package com.linuxclub.cdcfan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.linuxclub.cdcfan.R;
import com.linuxclub.cdcfan.httptask.GetHttpTask;
import com.linuxclub.cdcfan.httptask.HttpTaskCallback;
import com.gc.materialdesign.views.Button;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends LoadingBaseActivity implements OnClickListener, HttpTaskCallback {

    public static final String KEY_PSID = "key_psid";
    public static final String KEY_NAME = "name";
    public static final String KEY_DEPCODE = "depcode";

    private EditText mUserNameET;
    private Button mLoginBtn;
    private String mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkJumpToOrderPage();
    }

    private void checkJumpToOrderPage() {
        String userName = mPre.getLastUserName();
        if (userName.equals("") == false) {
            mUserName = userName;
            startLogin();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.login;
    }

    @Override
    protected void initBasicData() {
        super.initBasicData();
    }

    @Override
    protected void initView() {
        super.initView();
        mUserNameET = (EditText) findViewById(R.id.userName);
        mLoginBtn = (Button) findViewById(R.id.login);
        mLoginBtn.setOnClickListener(this);
        ((TextView) findViewById(R.id.title)).setText(mRes.getString(R.string.log_in));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login) {
            mUserName = mUserNameET.getText().toString();
            if (mUserName.equals("")) {
                showFixToast(mRes.getString(R.string.fail_empty));
            } else {
                startLogin();
            }
        }
    }

    private void startLogin() {
        new GetHttpTask(this, this, mConst.getDomain(), mConst.getLoginPath(), mConst.getLoginParams(mUserName)).execute();
        showLoadingPage(true);
    }

    private void startOrderActivity(User user) {
        Intent intent;
        intent = new Intent(this, OrderActivity.class);
        intent.putExtra(KEY_PSID, user.psid);
        intent.putExtra(KEY_NAME, user.name);
        intent.putExtra(KEY_DEPCODE, user.depcode);
        startActivity(intent);
    }

    private void saveUserInfo(User user) {
        mPre.setKeyLastUserName(user.name);
    }

    boolean parseResult(String jsonObj, User user) {
        try {
            JSONObject json = new JSONObject(jsonObj);
            user.psid = json.getString("psid");
            user.name = json.getString("name");
            user.depcode = json.getString("depcode");
            if (user.psid.equals("") == false
                    && user.name.equals("") == false
                    && user.depcode.equals("") == false) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onSucc(int statusCode, String responseBody) {
        Log.d("CDC", "user check task ends. result: " + true);
        showLoadingPage(false);
        User user = new User();
        if (parseResult(responseBody, user)) {
            Log.d("CDC", "get user: " + user);
            startOrderActivity(user);
            saveUserInfo(user);
            finish();
        }
    }

    @Override
    public void onErr(int responseCode, String responseBody) {
        showLoadingPage(false);
        showFixToast(String.format(mRes.getString(R.string.fail), mUserName));
    }
}
