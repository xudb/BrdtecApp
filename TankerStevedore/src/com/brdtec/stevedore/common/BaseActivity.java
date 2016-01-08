package com.brdtec.stevedore.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.brdtec.stevedore.BrdApp;
import com.brdtec.stevedore.R;
import com.brdtec.stevedore.utils.Utils;
import com.meiqia.meiqiasdk.activity.MQConversationActivity;

/**
 * Activity基类
 */
public class BaseActivity extends Activity implements OnClickListener, AMapLocationListener{
	static final String TAG = "BaseActivity";
	private String className = getClass().getSimpleName();
	boolean isMoveToBG;
	private static final Collection<String> mActivitys = new ConcurrentLinkedQueue<String>(new ArrayList<String>());
	
	public BrdApp mApplication;
	
	public boolean isNeedLoc = false;
	
	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	//声明定位回调监听器
	public AMapLocationListener mLocationListener = null;
	private AMapLocationClientOption locationOption = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (BrdApp)getApplicationContext();
		if(isNeedLoc) {
			initLocation();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mLocationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			mLocationClient.onDestroy();
			mLocationClient = null;
			locationOption = null;
		}
	}

	@Override
	public void onBackPressed() {
		if (!onBackEvent()) {
			try {
				super.onBackPressed();
			} catch (Throwable e) {
				finish();
			}
		}
	}

	protected boolean onBackEvent() {
		if(Utils.isTopActivity(this, "com.brdtec.stevedore.activity.MainActivity")) {
			moveTaskToBack(true);
			return true;
		}
		return false;
	}

	public static boolean isMoveTaskToBack(Context context, Intent intent) {
		return intent.getComponent() == null ? true : !intent.getComponent()
				.getPackageName().equals(context.getPackageName());
	}

	protected String setLastActivityName() {
		String name = getString(R.string.button_back);
		return name;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		Iterator<String> iter = mActivitys.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			if (className.equals(name)) {
				iter.remove();
				break;
			}
		}
	}

	public static String getActivitys() {
		return mActivitys.toString();
	}

	/**
	 * 返回titlebar的高度 in pixels
	 * 
	 * @return
	 */
	public int getTitleBarHeight() {
		return getResources().getDimensionPixelSize(R.dimen.title_bar_height);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void setContentBackgroundResource(int resId) {
		findViewById(android.R.id.content).setBackgroundResource(resId);
	}

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
	
	public void startMeChat() {
		// 设置用户上线参数
		/*MCOnlineConfig onlineConfig = new MCOnlineConfig();
		//onlineConfig.setChannel("channel"); // 设置渠道

		// onlineConfig.setSpecifyAgent("4840", false); // 设置指定客服
		// onlineConfig.setSpecifyGroup("1"); // 设置指定分组

		// 更新用户信息，可选. 
		// 详细信息可以到文档中查看：https://meiqia.com/docs/sdk/android.html
		MCUserConfig mcUserConfig = new MCUserConfig();
		Map<String,String> userInfo = new HashMap<String,String>();
		userInfo.put(MCUserConfig.PersonalInfo.REAL_NAME, Utils.getStringSharedPreferences(this, Constant.Preferences.KEY_USER));
		userInfo.put(MCUserConfig.Contact.TEL,"130000000");
		Map<String,String> userInfoExtra = new HashMap<String,String>();
		userInfoExtra.put("extra_key","extra_value");
		userInfoExtra.put("gold","10000");
		mcUserConfig.setUserInfo(this, userInfo, userInfoExtra, null);
		
		// 启动客服对话界面
		MCClient.getInstance().startMCConversationActivity(onlineConfig);*/
		startActivity(new Intent(BaseActivity.this, MQConversationActivity.class));
	}

	/***
	 * 初始化定位
	 */
	public void initLocation() {
		mLocationClient = new AMapLocationClient(this.getApplicationContext());
		locationOption = new AMapLocationClientOption();
		// 设置定位监听
		mLocationClient.setLocationListener(this);
		// 设置定位模式为高精度模式
		locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		locationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		locationOption.setOnceLocation(true);
		//设置是否强制刷新WIFI，默认为强制刷新
		locationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		locationOption.setMockEnable(false);
		/**
		 * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
		 * 注意：只有在高精度模式下的单次定位有效，其他方式无效
		 */
		locationOption.setGpsFirst(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		//locationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(locationOption);
		//启动定位
		mLocationClient.startLocation();
	}
	
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				// 定位成功回调信息，设置相关消息
				amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
				amapLocation.getLatitude();// 获取纬度
				amapLocation.getLongitude();// 获取经度
				amapLocation.getAccuracy();// 获取精度信息
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date(amapLocation.getTime());
				df.format(date);// 定位时间
				amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果
				amapLocation.getCountry();// 国家信息
				amapLocation.getProvince();// 省信息
				amapLocation.getCity();// 城市信息
				amapLocation.getDistrict();// 城区信息
				amapLocation.getRoad();// 街道信息
				amapLocation.getCityCode();// 城市编码
				amapLocation.getAdCode();// 地区编码
			} else {
				// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:"
						+ amapLocation.getErrorInfo());
			}
		}
		mLocationClient.stopLocation();//停止定位
	}
	
	
}
