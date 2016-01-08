package com.brdtec.stevedore.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.CustomTitleBarActivity;

/***
 * 前后尺数据录入
 * 
 * @author Allen
 * 
 */
public class TankDataInputAcitivity extends CustomTitleBarActivity {

	LinearLayout mBtnToJobList;
	LinearLayout mBtnToGuide;

	TextView mBtnMinus;
	TextView mBtnAdd;
	TextView mTankNos;
	// 前尺
	RelativeLayout mQianChiRl;
	EditText mAnGuanGaodu;
	TextView mQianChiDate;
	EditText mQianChiYeWeiGaoDu;
	EditText mQianChiWenDu;
	EditText mQianChiBuKeLiang;

	// 后尺
	RelativeLayout mHouChiRl;
	TextView mHouChiDate;
	EditText mHouChiYeWeiGaoDu;
	EditText mHouChiWenDu;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
	
	boolean isQianChi = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tank_data);
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null && mBundle.containsKey("isqian")) {
			isQianChi = true;
		}
		findView();
		initData();
	}

	private void findView() {
		setTitle(R.string.task_txt_qianchi);
		rightViewText.setText(R.string.button_save);
		rightViewText.setVisibility(View.VISIBLE);
		rightViewText.setOnClickListener(this);
		mBtnToJobList = (LinearLayout) findViewById(R.id.btn_to_job_ll);
		mBtnToGuide = (LinearLayout) findViewById(R.id.btn_to_guide_ll);
		mBtnToJobList.setOnClickListener(this);
		mBtnToGuide.setOnClickListener(this);

		mBtnMinus = (TextView) findViewById(R.id.btn_minus);
		mBtnAdd = (TextView) findViewById(R.id.btn_add);
		mBtnMinus.setOnClickListener(this);
		mBtnAdd.setOnClickListener(this);

		mTankNos = (TextView) findViewById(R.id.tank_no);

		mQianChiRl = (RelativeLayout) findViewById(R.id.input_rl);
		mAnGuanGaodu = (EditText) findViewById(R.id.anguangaodu_et);
		mQianChiDate = (TextView) findViewById(R.id.qianchishijian_tv);
		mQianChiDate.setOnClickListener(this);
		mQianChiYeWeiGaoDu = (EditText) findViewById(R.id.yeweigaodu_et);
		mQianChiWenDu = (EditText) findViewById(R.id.qianchiwendu_et);
		mQianChiBuKeLiang = (EditText) findViewById(R.id.bukejiliang_et);

		mHouChiRl = (RelativeLayout) findViewById(R.id.input_rl2);
		mHouChiDate = (TextView) findViewById(R.id.houchishijian_tv);
		mHouChiDate.setOnClickListener(this);
		mHouChiYeWeiGaoDu = (EditText) findViewById(R.id.houchiyeweigaodu_et);
		mHouChiWenDu = (EditText) findViewById(R.id.houchiwendu_et);
		
		if(isQianChi) {
			mQianChiRl.setVisibility(View.VISIBLE);
			mHouChiRl.setVisibility(View.GONE);
		} else {
			mQianChiRl.setVisibility(View.GONE);
			mHouChiRl.setVisibility(View.VISIBLE);
		}
	}

	private void initData() {
		Date date = new Date();
		String ymd = sdf.format(date);
		String time = sdfTime.format(date);
		mQianChiDate.setText(ymd + " " + time);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_to_job_ll:
			Intent i = new Intent(TankDataInputAcitivity.this, JobListActivity.class);
			startActivity(i);
			break;
		case R.id.btn_to_guide_ll:
			startMeChat();
			break;
		case R.id.qianchishijian_tv:
			break;
		case R.id.houchishijian_tv:
			break;
		case R.id.ivTitleBtnRightText:
			finish();
			break;
		}
	}

	/** 设置时间 */
	private void setTime(final View v) {
		int hourDefaule = 0;
		int minDefaule = 0;
		String time = ((TextView) v).getText().toString().trim().split(" ")[1].split(": ")[0].replace(":", "");
		if (!"".equals(time) && time.length() == 4) {
			hourDefaule = Integer.parseInt(time.substring(0, 2));
			minDefaule = Integer.parseInt(time.substring(2, 4));
		}
		new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				((TextView) v).setText(getTimeZone().split(":")[0] + " " + getFormartTime(hourOfDay, minute)
						+ ":" + getTimeZone().split(":")[1]);
			}
		}, hourDefaule, minDefaule, true).show();
	}

	/*
	 * 获取手机系统的时区 eg：+0800
	 */
	private String getTimeZone() {
		Date date = new Date();
		String ymd = sdf.format(date);
		String mDate = ymd;
		return mDate;
	}

	public String getFormartTime(int hour, int min) {
		StringBuffer sb = new StringBuffer();
		if (hour < 10) {
			sb.append("0" + hour);
		} else {
			sb.append(hour);
		}
		sb.append(":");
		if (min < 10) {
			sb.append("0" + min);
		} else {
			sb.append(min);
		}
		return sb.toString();
	}
}
