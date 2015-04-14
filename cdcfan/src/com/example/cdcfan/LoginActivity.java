package com.example.cdcfan;

import net.sf.json.JSONObject;

import com.example.cdcfan.UserService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener {

	private EditText userName;
	private Button login;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText) findViewById(R.id.userName);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
    }
  
    @Override
    public void onClick(View v) {
    	String name=userName.getText().toString();
    	String path="http://cdcfan/api/user-search";
    	JSONObject userInfo=UserService.checkUser(path,name);
        if(userInfo!=null)  
        {  
            Toast.makeText(getApplicationContext(),R.string.success,Toast.LENGTH_LONG).show();
            Intent intent;    
            intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
        }else  
        {  
            Toast.makeText(getApplicationContext(),R.string.fail,Toast.LENGTH_LONG).show();  
        }         
    }
    
}
