package com.brdtec.stevedore.common;

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.brdtec.stevedore.R;


/**
 * 导航栏基类
 */
public class CustomTitleBarActivity extends BaseActivity {
	protected TextView leftView, centerView, rightViewText;
	protected ViewGroup vg;
	public static final int LAYER_TYPE_SOFTWARE = 1;
	protected float mDensity;
//	<item name="android:windowContentOverlay">@null</item>
	
	@Override
	public void setContentView(int layoutResID) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_commen_title);
		
		mDensity = getResources().getDisplayMetrics().density;
		init(getIntent());
	}

	public void setContentView(View view,boolean titlebar) {
        mDensity = getResources().getDisplayMetrics().density;
		if(titlebar){
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			super.setContentView(view);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_commen_title);
			init(getIntent());
		}else{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			super.setContentView(view);
		}
	}
	
	@Override
	public void setContentView(View view) {
		setContentView(view,true);
	}
	
	public void setContentViewNoBackground(int layoutResID)
	{
		setContentView(layoutResID);
		getWindow().setBackgroundDrawable(null);
	}

	public void setContentViewNoTitle(int layoutResID) {
        mDensity = getResources().getDisplayMetrics().density;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(layoutResID);
		init(getIntent());
	}
	
    /**
     * 请使用 {@link setContentViewB}
     * @param layoutResId
     * @return
     */

	private void init(Intent data) {
		FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
		content.setForeground(getResources().getDrawable(R.drawable.header_bar_shadow));
		
		if (leftView == null) {
			vg = (ViewGroup) findViewById(R.id.rlCommenTitle);
			setLayerType(vg);
			onCreateLeftView();
			onCreateCenterView();
			onCreateRightView();
			setLeftViewName(data);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		if (centerView != null && centerView instanceof TextView) {
			TextView titleTextView = (TextView) centerView;
			titleTextView.setText(title);
			super.setTitle(title);
		}
	}
	public void setTitle(CharSequence title,String contentDescription ) {
		if (centerView != null && centerView instanceof TextView) {
			TextView titleTextView = (TextView) centerView;
			titleTextView.setText(title);
			super.setTitle(contentDescription);
		}
	}

	public String getTextTitle() {
		String result = null;

		if (centerView != null && centerView instanceof TextView) {
			TextView titleTextView = (TextView) centerView;
			CharSequence charSeq = titleTextView.getText();

			if (charSeq != null) {
				result = charSeq.toString();
			}
		}

		return result;
	}

	@Override
	public void setTitle(int titleId) {
		String title = getString(titleId);
		setTitle(title);
	}

	protected View onCreateLeftView() {
		leftView = (TextView) findViewById(R.id.ivTitleBtnLeft);
		leftView.setOnClickListener(onBackListeger);
		leftView.setVisibility(View.VISIBLE);
		//leftView.setText(R.string.button_back);
		setLayerType(leftView);
		return leftView;
	}

	// 这个方法是普遍的行为，放在基类中
	protected void setLeftViewName(Intent data) {
		if (leftView != null && leftView instanceof TextView && data != null
				&& data.getExtras() != null) {
			TextView leftTextView = (TextView) leftView;
			String leftviewString = data.getExtras().getString("leftViewText");
			if (leftviewString == null)
				leftviewString = getString(R.string.button_back);
			leftTextView.setText(leftviewString);
		}
	}
	// 这个方法是普遍的行为，放在基类中
	protected void setLeftViewName(int resId) {
		if (leftView != null && leftView instanceof TextView) {
			String str = getString(resId);
			TextView leftTextView = (TextView) leftView;
			if (str == null || "".equals(str))
				str = getString(R.string.button_back);
			leftTextView.setText(str);
		}
	}

	protected View onCreateCenterView() {
		centerView = (TextView) findViewById(R.id.ivTitleName);
		return centerView;
	}

	protected View onCreateRightView() {
		rightViewText = (TextView) findViewById(R.id.ivTitleBtnRightText);
		setLayerType(rightViewText);
		return rightViewText;
	}


	/**
	 * 开启右上角button模式
	 * 
	 * @param textId
	 * @param l
	 */
	protected void setRightButton(int textId, OnClickListener l) {
		// 右侧按钮
		rightViewText.setVisibility(View.VISIBLE);
		rightViewText.setText(textId);
		rightViewText.setEnabled(true);
		// FontUtils.setFontStyle(rightView, R.style.I1_Font);
		if (l != null)
			rightViewText.setOnClickListener(l);
	}

	protected OnClickListener onBackListeger = new OnClickListener() {
		public void onClick(View v) {
			onBackEvent();
		}
	};

	public static void setLayerType(View view) {
		// I9300title显示有问题，加这段代码
		if (view == null) {
			return;
		}
		if (android.os.Build.VERSION.SDK_INT > 10) {
			Method m;
			try {
				m = view.getClass().getMethod("setLayerType", Integer.TYPE,	Paint.class);
				m.invoke(view, LAYER_TYPE_SOFTWARE, null);
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected String setLastActivityName() {
		if (centerView == null || centerView.getText() == null
				|| centerView.getText().length() == 0) {
			return getString(R.string.button_back);
		}
		return centerView.getText().toString();
	}
	
	/**
	 * 4.1系统软加速和webview有冲突，在setContentView后关掉背景的软加速
	 */
	@TargetApi(11)
	public void removeWebViewLayerType() {
		if (android.os.Build.VERSION.SDK_INT == 16) {// 4.1的版本 9patch + webview + 软加速会进不了webview
			vg.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}

    private Drawable ad;

    private Drawable[] mOldDrawables;

    private int mOldPadding;

    protected boolean startTitleProgress() {
        if (ad == null) {
//            ad = getResources().getDrawable(R.drawable.common_loading6);
            mOldDrawables = centerView.getCompoundDrawables();
            mOldPadding = centerView.getCompoundDrawablePadding();
            centerView.setCompoundDrawablePadding(10);
            centerView.setCompoundDrawablesWithIntrinsicBounds(ad, mOldDrawables[1], mOldDrawables[2], mOldDrawables[3]);
            ((Animatable)ad).start();
            return true;
        }
        return false;
    }

    protected boolean stopTitleProgress() {
        if (ad != null) {
        	((Animatable)ad).stop();
            ad = null;
            centerView.setCompoundDrawablePadding(mOldPadding);
            centerView.setCompoundDrawablesWithIntrinsicBounds(mOldDrawables[0], mOldDrawables[1], mOldDrawables[2], mOldDrawables[3]);
            return true;
        }
        return false;
    }
}
