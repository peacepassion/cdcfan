package com.example.cdcfan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.cdcfan.UserService.UserServiceCallback;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements OnClickListener, UserServiceCallback {

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserService.setListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login) {
            mUserName = mUserNameET.getText().toString();
            if (mUserName.equals("")) {
                showToast(mRes.getString(R.string.fail_empty));
            } else {
                startLogin();
            }
        }
    }

    private void startLogin() {
        mUserService.startCheckUser(mUserName);
        showLoadingPage(true);
    }

    @Override
    public void onCheckUserReturn(boolean flag, String jsonObj) {
        Log.d("CDC", "user check task ends. result: " + flag);
        showLoadingPage(false);
        if (flag) {
            User user = new User();
            if (parseResult(jsonObj, user)) {
                Log.d("CDC", "get user: " + user);
                startOrderActivity(user);
                saveUserInfo(user);
                finish();
                return;
            }
        }
        showToast(String.format(mRes.getString(R.string.fail), mUserName));
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

    @Override
    public void onCheckOrderReturn(boolean flag, String jsonBody) {

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

}
