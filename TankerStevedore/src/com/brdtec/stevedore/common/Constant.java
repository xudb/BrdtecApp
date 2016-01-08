package com.brdtec.stevedore.common;

import android.os.Environment;

/***
 * 应用常量类
 * 
 * @author Allen
 * 
 */
public interface Constant {
	
	public static String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String PATH_ROOT = SD_ROOT + "/brdtec/";
	public static String IMAGE_SAVE = PATH_ROOT + "images/";
	public static String IMAGE_TEMP = PATH_ROOT + "images_temp/";
	
	interface HTTPStatus {
		/** S0: 成功*/
		String S0 = "S0";
		/** E0 : 系统错误(未定义的失败类型,系统异常); */
		String E0 = "E0";
		/** E1 : 身份验证失败(密码错误) */
		String E1 = "E1";
		/** E2 : 帐号在别处登陆(需要重新登录) */
		String E2 = "E2";
		/** E3 : session超时(需要重新登录)*/
		String E3 = "E3";
		/** E4 : 用户未登录(需要登录) */
		String E4 = "E4";
		/** E5 : 服务未实现 */
		String E5 = "E5";
		/** E6 : 数据解析失败*/
		String E6 = "E6";
	}

	interface Preferences {
		String SHARED_PREFERENCES = "com.brdtec.stevedore";
		// 用于保存本地存储的key值
		/** 记住密码key */
		String KEY_RECORD = "key_record";
		/** 用户名key */
		String KEY_USER = "key_user";
		/** 密码key */
		String KEY_PASS = "key_pwd";
	}
}
