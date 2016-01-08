package com.brdtec.stevedore.utils;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	public static String getBetweenValue(String a, String b) {
		String result = "";

		if (a == null) {
			if (b == null) {
				result = "";
			} else {
				result = "<" + b + "%";
			}
		} else {
			if (b == null) {
				result = ">" + a + "%";
			} else {
				result = a + "%" + "~" + b + "%";
			}
		}

		return result;
	}

	public static boolean verifyNull(String obj) {
		boolean flag = false;
		if (obj == null || obj.trim().equals("null") || obj.trim().length() == 0) {
			flag = false;
		} else {
			flag = true;
		}
		return !flag;
	}

	public static String stringToUnicode(String str) {
		str = (str == null ? "" : str);
		String tmp;
		StringBuffer sb = new StringBuffer(1000);
		char c;
		int i, j;
		sb.setLength(0);
		for (i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			sb.append("\\u");
			j = (c >>> 8); // 取出高8位
			tmp = Integer.toHexString(j);
			if (tmp.length() == 1)
				sb.append("0");
			sb.append(tmp);
			j = (c & 0xFF); // 取出低8位
			tmp = Integer.toHexString(j);
			if (tmp.length() == 1)
				sb.append("0");
			sb.append(tmp);

		}
		return (new String(sb));

	}

	public static final int MINUTE = 60;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;
	public static final int MONTH = 30 * DAY;
	public static final int YEAR = 12 * MONTH;

	/**
	 * 时间戳转换
	 */
	public static String timestampToString(String mUnixTime) {
		String publicedTime = "";
		Date date = new Date();
		long unixTimeGMT = date.getTime() / 1000;
		long seconds = unixTimeGMT - Long.valueOf(mUnixTime);
		if (seconds < MINUTE) {
			publicedTime = seconds + "秒前";
		} else if (seconds < HOUR) {
			publicedTime = seconds / MINUTE + "分钟前";
		} else if (seconds < DAY) {
			publicedTime = seconds / HOUR + "小时前";
		} else if (seconds < MONTH) {
			publicedTime = seconds / DAY + "天前";
		} else if (seconds < YEAR) {
			publicedTime = seconds / MONTH + "月前";
		} else {
			publicedTime = seconds / YEAR + "年前";
		}

		// long epoch = new
		// java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/1970 01:00:00").getTime();
		return publicedTime;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String unixTimeStampToString(String unixTime) {
		return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(Long.valueOf(unixTime) * 1000));
	}

	/*
	 * url 转码
	 */
	public static String encode(String str) {

		if (str == null) {
			return "";
		}

		return URLEncoder.encode(str).toString();
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
