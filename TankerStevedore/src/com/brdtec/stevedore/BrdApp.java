package com.brdtec.stevedore;

import android.app.Application;

import com.brdtec.stevedore.utils.Utils;

/***
 * 装卸管理系统APP
 * 
 * @author Allen
 * 
 */
public class BrdApp extends Application {
	/** 用户登录的ID */
	private String sessionId;

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.init(this);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
