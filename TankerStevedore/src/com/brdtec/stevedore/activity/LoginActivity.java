package com.brdtec.stevedore.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.Constant;
import com.brdtec.stevedore.common.CustomTitleBarActivity;
import com.brdtec.stevedore.data.response.JSONResponseData;
import com.brdtec.stevedore.utils.Utils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class LoginActivity extends CustomTitleBarActivity {

	CheckBox mRemember;
	
	EditText accountEt;
	EditText passwordEt;
	
	private ProgressDialog proDialog;
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!TextUtils.isEmpty(mApplication.getSessionId())) {
			Intent i = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(i);
			finish();
			return;
		}
		setContentView(R.layout.activity_login);
		setTitle(R.string.login_button_name);
		initUI();
	}
	
	private void initUI(){
		((TextView) findViewById(R.id.login_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});		
		mRemember = (CheckBox) findViewById(R.id.remember_me);
		accountEt = (EditText) findViewById(R.id.username_text);
		passwordEt = (EditText) findViewById(R.id.pwd_text);
		if (Utils.getBooleanSharedPreferences(this, Constant.Preferences.KEY_RECORD)) {
			accountEt.setText(Utils.getStringSharedPreferences(this, Constant.Preferences.KEY_USER));
			passwordEt.setText(Utils.getStringSharedPreferences(this, Constant.Preferences.KEY_PASS));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					//doLogin();
				}
			}, 500);
		}

	}
	
	public void logout() {
		finish();
	}
	
	public void login(){
		if(accountEt.getText().toString().equals("")){
			Toast.makeText(LoginActivity.this, getString(R.string.login_name) + getString(R.string.is_not_empty), Toast.LENGTH_LONG).show();
			return;
		}
		
		/*if(!StringUtils.isMobileNO(accountEt.getText().toString())) {
			Toast.makeText(LoginActivity.this, "手机号码格式不正确", Toast.LENGTH_LONG).show();
			//return;
		}*/
		
		if(passwordEt.getText().toString().equals("")){
			Toast.makeText(LoginActivity.this, getString(R.string.login_pwd) + getString(R.string.is_not_empty), Toast.LENGTH_LONG).show();
			return;
		}
		doLogin();		
	}
	
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (proDialog != null && proDialog.isShowing()) {
					proDialog.dismiss();
				}
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
				Intent i = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(i);
				break;
			case 2:
				if (proDialog != null && proDialog.isShowing()) {
					proDialog.dismiss();
				}
				Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
				break;
			case 3:
				if (proDialog != null && proDialog.isShowing()) {
					proDialog.dismiss();
				}
				if(msg.obj != null) {
					Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
				}
				finish();
				break;
			}
		};
	};	
	
	/**
	 * 登录
	 */
	public void doLogin() {
		if(!Utils.hasNetwork(this)) {
			Toast.makeText(LoginActivity.this, getString(R.string.err_network), Toast.LENGTH_LONG).show();
			return;
		}
		if(proDialog == null) {
			proDialog = new ProgressDialog(LoginActivity.this);
			proDialog.setMessage("加载中...");
		}
		proDialog.show();
		
		final String name = accountEt.getText().toString().trim();
		final String pwd = passwordEt.getText().toString().trim();
		
		if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
			String url = "http://brdtec.cn/otrm/service.php?func=userauth&uname=" + name + "&passwd=" + pwd;
			HttpUtils http = new HttpUtils();
			http.configCurrentHttpCacheExpiry(1000 * 10);
			http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					mHandler.sendEmptyMessageDelayed(2, 2000);
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					Gson gson = new Gson();
					JSONResponseData mData = gson.fromJson(responseInfo.result, JSONResponseData.class);
					if(mData.stuscode.equals(Constant.HTTPStatus.S0)) {
						if(!TextUtils.isEmpty(mData.sessionid)) {
							mApplication.setSessionId(mData.sessionid);
							Utils.setSharedPreferencesAll(LoginActivity.this, true, Constant.Preferences.KEY_RECORD);
							Utils.setSharedPreferencesAll(LoginActivity.this, new String[]{name, pwd}, new String[]{Constant.Preferences.KEY_USER, Constant.Preferences.KEY_PASS});
						}
						mHandler.sendEmptyMessage(1);							
					} else if(mData.stuscode.equals(Constant.HTTPStatus.E1)) {
						mHandler.sendEmptyMessage(2);							
					} else {
						Message msg = mHandler.obtainMessage();
						msg.what = 3;
						msg.obj = responseInfo.result;
						mHandler.sendMessage(msg);
					}
				}
			});
		}
	}
	
}
