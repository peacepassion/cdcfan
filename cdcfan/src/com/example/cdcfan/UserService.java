package com.example.cdcfan;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import net.sf.json.JSONObject;

import com.example.cdcfan.StreamTool;

public class UserService {
	
	public static JSONObject checkUser(String path, String name) {		
		try {
			return sendGETRequest(path,name,"?identity=","UTF-8");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject checkOrderSucc(String path, String psid) {
		try {
			return sendGETRequest(path,psid,"?psid=","UTF-8");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static JSONObject sendGETRequest(String path,String params,String parasName,
										String encode) throws MalformedURLException, IOException {
		StringBuilder url=new StringBuilder(path);
		url.append(parasName);
		url.append(URLEncoder.encode(params,encode));
        HttpURLConnection conn=(HttpURLConnection)new URL(url.toString()).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode()==200)
         {
    	   InputStream inStream = conn.getInputStream();
    	   try {
    		   	byte[] data = StreamTool.readInputStream(inStream);
    		   	String json = new String(data);
    		   	JSONObject jsonObject = new JSONObject();
				return jsonObject.fromObject(json);
    	   } catch (Exception e) {
    		   	e.printStackTrace();
    		   	return null;
    	   }
         }
		   return null;
	 }
}
