package com.brdtec.stevedore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brdtec.stevedore.R;

public class ViewUtils {

	public static void showMessage(Context ctx, String tag) {
		Toast.makeText(ctx, tag, Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(Context ctx, int tag) {
		Toast.makeText(ctx, ctx.getString(tag), Toast.LENGTH_LONG).show();
	}

	public static void showNetworkError(Context ctx) {
		showMessage(ctx, R.string.err_network);
	}

	public static void showErrorMessage(Context ctx) {
		showMessage(ctx, "即将推出...");
	}
	/**
	 * 拨打电话
	 * @param ctx
	 * @param tel
	 */
	public static void call(Context ctx, String tel) {
		Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
		ctx.startActivity(phoneIntent);
	}
	/**
	 * 获取ListView的高度
	 * @param listView
	 * @return
	 */
	public static int setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		return params.height;
	}

	/**
	 * 设置ListView的高度
	 * @param listView
	 * @return
	 */
	public static int setViewHeight(ViewGroup listView) {
		if (listView == null) {
			return 0;
		}
		int totalHeight = 0;
		for (int i = 0, len = listView.getChildCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listView.getChildAt(i);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		listView.setLayoutParams(params);
		return params.height;
	}

	/**
	 * 获取TextView字符的宽度
	 * @param tv
	 * @return
	 */
	public static float getCharacterWidth(TextView tv) {
		if (null == tv)
			return 0f;
		return getCharacterWidth(tv.getText().toString(), tv.getTextSize());
	}
	/**
	 * 获取字符宽度
	 * @param text
	 * @param size
	 * @return
	 */
	public static float getCharacterWidth(String text, float size) {
		if (null == text || "".equals(text))
			return 0;
		float width = 0;
		Paint paint = new Paint();
		paint.setTextSize(size);
		float text_width = paint.measureText(text);// 得到总体长度
		width = text_width / text.length();// 每一个字符的长度
		return width;
	}

	/**
	 * 获取屏幕宽度
	 * @param act
	 * @return
	 */
	public static int getWidth(Context act) {
		WindowManager wm = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();// 屏幕宽度
	}

	/**
	 * 获取屏幕高度
	 * @param act
	 * @return
	 */
	public static int getHeight(Activity act) {
		WindowManager wm = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();// 屏幕高度
	}

	/**
	 * Dp转换为Px
	 * @param act
	 * @param dp
	 * @return
	 */
	public static int dpToPx(Activity act, int dp) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return (int) (metric.density * dp + 0.5);
	}
	
	 /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dpToPx2(Activity act, float d) {  
    	DisplayMetrics metric = new DisplayMetrics();
    	act.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return (int) (d * metric.density + 0.5f);  
    }  
  
	/**
	 * 分享
	 * @param context
	 * @param szChooserTitle
	 * @param title
	 * @param msg
	 */
	public static void startShareApp(Context context, final String szChooserTitle, final String title, final String msg) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, msg);
		context.startActivity(Intent.createChooser(intent, szChooserTitle));
	}
	
	/**
     * 收起软键盘
     */
    public static void collapseSoftInputMethod(Context context, View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInputMethod(Context context, View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, 0);
        }
    }
	
}
