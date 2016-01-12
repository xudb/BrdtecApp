package com.brdtec.stevedore.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.BaseActivity;
import com.brdtec.stevedore.common.Constant;
import com.brdtec.stevedore.data.response.JSONResponseData;
import com.brdtec.stevedore.utils.Utils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		doLogin();
	}
	
	/***
	 * 自动登录
	 */
	private void doLogin() {
		if(!Utils.hasNetwork(this)) {
			Toast.makeText(SplashActivity.this, getString(R.string.err_network), Toast.LENGTH_LONG).show();
			return;
		}
		try {
			boolean isRemberPwd = false;//Utils.getBooleanSharedPreferences(this, Constant.Preferences.KEY_RECORD);
			if(isRemberPwd) {
				String name = Utils.getStringSharedPreferences(this, Constant.Preferences.KEY_USER);
				String pwd = Utils.getStringSharedPreferences(this, Constant.Preferences.KEY_PASS);
				if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
					//String url = "http://brdtec.cn/otrm/service.php?func=userauth&uname=" + "test" + "&passwd=" + "text";
					String url = "http://brdtec.cn/otrm/service.php?func=userauth&uname=" + name + "&passwd=" + pwd;
					HttpUtils http = new HttpUtils();
					http.configCurrentHttpCacheExpiry(1000 * 10);
					http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
						
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							mHandler.sendEmptyMessageDelayed(1001, 1000);
						}
						
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Gson gson = new Gson();
							JSONResponseData mData = gson.fromJson(responseInfo.result, JSONResponseData.class);
							if(mData.stuscode.equals(Constant.HTTPStatus.S0)) {
								if(!TextUtils.isEmpty(mData.sessionid)) {
									mApplication.setSessionId(mData.sessionid);
								}
								mHandler.sendEmptyMessageDelayed(1000, 1000);							
							} else {
								mHandler.sendEmptyMessageDelayed(1001, 1000);
							}
						}
					});
				}
			} else {
				mHandler.sendEmptyMessageDelayed(1001, 1500);
			}
		} catch (Exception e) {

		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 1001:
				Intent intent1 = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent1);
				finish();
				break;
			}
		}
	};
}
