package com.brdtec.stevedore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.CustomTitleBarActivity;

/**
 * 作业中输入界面
 * 
 * @author Allen
 * 
 */
public class TaskDoingInputActivity extends CustomTitleBarActivity {

	TextView mBtnSave;
	TextView mBtnReset;
	EditText mDoneInput;
	EditText mLeftInput;
	LinearLayout mBtnToJobList;
	LinearLayout mBtnToGuide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_doing);
		findView();
	}

	private void findView() {
		setTitle(R.string.task_txt_zuoyezhong);
		mBtnToJobList = (LinearLayout) findViewById(R.id.btn_to_job_ll);
		mBtnToGuide = (LinearLayout) findViewById(R.id.btn_to_guide_ll);
		mBtnToJobList.setOnClickListener(this);
		mBtnToGuide.setOnClickListener(this);
		
		mBtnSave = (TextView) findViewById(R.id.btn_save);
		mBtnReset = (TextView) findViewById(R.id.btn_reset);
		mBtnSave.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);
		mDoneInput = (EditText) findViewById(R.id.yixieliang_et);
		mLeftInput = (EditText) findViewById(R.id.shengyuliang_et);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_to_job_ll:
			Intent i = new Intent(TaskDoingInputActivity.this, JobListActivity.class);
			startActivity(i);
			break;
		case R.id.btn_to_guide_ll:
			startMeChat();
			break;
		case R.id.btn_save:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.btn_reset:
			mDoneInput.setText("");
			mLeftInput.setText("");
			break;
		}
	}
}
