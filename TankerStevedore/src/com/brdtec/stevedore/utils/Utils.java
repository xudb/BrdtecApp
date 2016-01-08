package com.brdtec.stevedore.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.brdtec.stevedore.common.Constant;

/***
 * 工具类
 * 
 * @author Allen
 * 
 */
public class Utils {
	
	public static Context mContext;
	
	public static void init(Context context) {
		mContext = context;
	}
	
	/**
	 * 判断手机是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return (null != ni && ni.isAvailable());
	}
	
	/**
	 * 获取SharedPreference bool类型的返回值
	 * 
	 * @param context
	 * @param key
	 * @param loginMethod
	 * @return
	 */
	public static boolean getBooleanSharedPreferences(Context context, String key) {
		SharedPreferences sp = null;
		sp = context.getSharedPreferences(Constant.Preferences.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	/**
	 * 获取SharedPreference int类型的返回值
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @param loginMethod
	 * @return
	 */
	public static int getIntSharedPreferences(Context context, String key, int defaultValue) {
		SharedPreferences sp = null;
		sp = context.getSharedPreferences(Constant.Preferences.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	/**
	 * 获取SharedPreference String类型的返回值
	 * 
	 * @param context
	 * @param key
	 * @param loginMethod
	 * @return
	 */
	public static String getStringSharedPreferences(Context context, String key) {
		return getStringSharedPreferences(context, key, "");
	}

	/**
	 * 获取SharedPreference String类型的返回值
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @param loginMethod
	 * @return
	 */
	public static String getStringSharedPreferences(Context context, String key, String defaultValue) {
		SharedPreferences sp = null;
		sp = context.getSharedPreferences(Constant.Preferences.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	/**
	 * 保存到SharedPreference
	 * 
	 * @param context
	 * @param accept
	 * @param key
	 * @param loginMethod
	 */
	public static void setSharedPreferencesAll(Context context, Object accept, String key) {
		SharedPreferences sp = null;
		sp = context.getSharedPreferences(Constant.Preferences.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor e = sp.edit();
		if (accept instanceof Boolean) {
			e.putBoolean(key, (Boolean) accept);
		} else if (accept instanceof String) {
			e.putString(key, (String) accept);
		} else if (accept instanceof Integer) {
			e.putInt(key, (Integer) accept);
		}
		e.commit();
	}

	/**
	 * 保存到SharedPreference
	 * 
	 * @param context
	 * @param accept
	 * @param key
	 * @param loginMethod
	 */
	public static void setSharedPreferencesAll(Context context, String[] accept, String[] key) {
		SharedPreferences sp = null;
		sp = context.getSharedPreferences(Constant.Preferences.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor e = sp.edit();
		int length = accept.length;
		for (int i = 0; i < length; i++) {
			e.putString(key[i], accept[i]);
		}
		e.commit();
	}

	/**
	 * 保存到SharedPreference
	 * 
	 * @param context
	 * @param accept
	 * @param key
	 * @param loginMethod
	 */
	public static void setSharedPreferencesAll(Context context, int[] accept, String[] key) {
		SharedPreferences sp = null;
		sp = context.getSharedPreferences(Constant.Preferences.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor e = sp.edit();
		int length = accept.length;
		for (int i = 0; i < length; i++) {
			e.putInt(key[i], accept[i]);
		}
		e.commit();
	}
	
	/**
	 * 判断actiity栈顶
	 * 
	 * @param context
	 * @param activityName
	 * @return
	 */
	public static boolean isTopActivity(Context context, String activityName) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).getClassName().toString().equals(activityName);
		else
			return false;
	}
}
