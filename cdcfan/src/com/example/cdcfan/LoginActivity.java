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
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.cdcfan.UserService.UserServiceCallback;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends SherlockFragmentActivity implements OnClickListener, UserServiceCallback {

    private EditText userName;
    private Button login;
    private String mLoginURL;

    private UserService mUserService;

    public static final String KEY_PSID = "key_psid";
    public static final String KEY_NAME = "name";
    public static final String KEY_DEPCODE = "depcode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBasicData();
        initView();
    }

    private void initBasicData() {
        mLoginURL = getResources().getString(R.string.portal);
        mUserService = UserService.getInstance(this);
    }

    private void initView() {
        userName = (EditText) findViewById(R.id.userName);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserService.setListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = userName.getText().toString();
        String url = Uri.parse(mLoginURL).buildUpon()
                .appendQueryParameter(getResources().getString(R.string.login_param), name).toString();
        mUserService.startCheckUser(url);
    }

    @Override
    public void onCheckUserReturn(boolean flag, String jsonObj) {
        Log.d("CDC", "user check task ends. result: " + flag);
        if (flag) {
            Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
            try {
                User user = parseResult(jsonObj);
                Log.d("CDC", "get user: " + user);
                Intent intent;
                intent = new Intent(this, OrderActivity.class);
                intent.putExtra(KEY_PSID, user.psid);
                intent.putExtra(KEY_NAME, user.name);
                intent.putExtra(KEY_DEPCODE, user.depcode);
                startActivity(intent);
            } catch (JSONException e) {
                Log.e("CDC", "json exception");
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCheckOrderReturn(boolean flag, String jsonBody) {

    }

    User parseResult(String jsonObj) throws JSONException {
        JSONObject json = new JSONObject(jsonObj);
        User user = new User();
        user.psid = json.getString("psid");
        user.name = json.getString("name");
        user.depcode = json.getString("depcode");
        return user;
    }

    public static class User {
        String psid;
        String name;
        String depcode;

        @Override
        public String toString() {
            return "psid: " + psid + ", name: " + name + ", depcode: " + depcode;
        }
    }

}
