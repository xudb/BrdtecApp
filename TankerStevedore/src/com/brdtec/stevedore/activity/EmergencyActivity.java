package com.brdtec.stevedore.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.CustomTitleBarActivity;

/***
 * 突发情况
 * @author Allen
 *
 */
public class EmergencyActivity extends CustomTitleBarActivity {
	
	TextView mLeiDian;
	TextView mDaFeng;
	TextView mBaoYu;
	TextView mQiTa;
	
	TextView mHuoZai;
	TextView mXielou;
	TextView mChuJiao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency);
		findView();
	}
	
	private void findView() {
		setTitle("突发事件");
		mLeiDian = (TextView) findViewById(R.id.e_leidian_tv);
		mDaFeng = (TextView) findViewById(R.id.e_dafeng_tv);
		mBaoYu = (TextView) findViewById(R.id.e_baoyu_tv);
		mQiTa = (TextView) findViewById(R.id.e_qita_tv);
		
		mHuoZai = (TextView) findViewById(R.id.e_huozai_tv);
		mXielou = (TextView) findViewById(R.id.e_xielou_tv);
		mChuJiao = (TextView) findViewById(R.id.e_chujiao_tv);
		
		mLeiDian.setOnClickListener(this);
		mDaFeng.setOnClickListener(this);
		mBaoYu.setOnClickListener(this);
		mQiTa.setOnClickListener(this);
		mHuoZai.setOnClickListener(this);
		mXielou.setOnClickListener(this);
		mChuJiao.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.e_leidian_tv:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.e_dafeng_tv:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.e_baoyu_tv:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.e_qita_tv:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.e_huozai_tv:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.e_xielou_tv:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.e_chujiao_tv:
			setResult(RESULT_OK);
			finish();
			break;
		}
	}
	
}
