package com.brdtec.stevedore.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.CustomTitleBarActivity;

/**
 * 新增步骤页面
 * @author Allen
 *
 */
public class TaskAddActivity extends CustomTitleBarActivity {

	private GridView mFunGridView;
	
	LinearLayout mBtnToJobList;
	LinearLayout mBtnToGuide;
	
	private Integer[] mFunNames = new Integer[] { R.string.empty, R.string.empty, R.string.task_txt_tufa,
			R.string.task_txt_zhunbei, R.string.task_txt_dengchuan, R.string.task_txt_qianyancang,
			R.string.task_txt_check_in, R.string.task_txt_pailiang, R.string.task_txt_kaishi,
			R.string.task_txt_zuoyezhong, R.string.task_txt_yazai, R.string.task_txt_xicang, 
			R.string.task_txt_saocang, R.string.task_txt_saoxian, R.string.empty, 
			R.string.task_txt_houyancang, R.string.task_txt_qianchi1, R.string.task_txt_houchi1 };

	private Integer[] mFunIcons = new Integer[] { R.drawable.white_drawable, R.drawable.white_drawable, R.drawable.ic_task_tufa,
			R.drawable.ic_task_zhunbei, R.drawable.ic_task_dengchuan, R.drawable.ic_task_yancang,
			R.drawable.ic_task_checkin, R.drawable.ic_task_pailiang, R.drawable.ic_task_kaishi,
			R.drawable.ic_task_zuoyezhong, R.drawable.ic_task_yazai, R.drawable.ic_task_xicang, 
			R.drawable.ic_task_saocang, R.drawable.ic_task_saoxian, R.drawable.white_drawable,
			R.drawable.ic_task_houyancang, R.drawable.ic_task_qianchi, R.drawable.ic_task_houchi};
	

	public static final int REQUEST_INPUT = 10;
	public static final int REQUEST_EMERGENCY = 11;
	public static final int REQUEST_QianChi = 12;
	public static final int REQUEST_HouChi = 13;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_add);
		findView();
	}
	
	private void findView() {
		setTitle(R.string.job_txt_detail_add);
		mBtnToJobList = (LinearLayout) findViewById(R.id.btn_to_job_ll);
		mBtnToGuide = (LinearLayout) findViewById(R.id.btn_to_guide_ll);
		mBtnToJobList.setOnClickListener(this);
		mBtnToGuide.setOnClickListener(this);
		mFunGridView = (GridView) findViewById(R.id.task_gv);
		FunctionsAdapter mAdapter = new FunctionsAdapter(this, Arrays.asList(mFunNames), Arrays.asList(mFunIcons));
		mFunGridView.setAdapter(mAdapter);
		mFunGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(mFunNames[position].equals(getString(R.string.empty))) {
					return;
				}
				switch (position) {
				case 2:
					Intent i = new Intent(TaskAddActivity.this, EmergencyActivity.class);
					startActivityForResult(i, REQUEST_EMERGENCY);
					break;
				case 9:
					Intent mIntent = new Intent(TaskAddActivity.this, TaskDoingInputActivity.class);
					startActivityForResult(mIntent, REQUEST_INPUT);
					break;
				case 16:
					Intent intent16 = new Intent(TaskAddActivity.this, TankDataInputAcitivity.class);
					intent16.putExtra("isqian", true);
					startActivityForResult(intent16, REQUEST_QianChi);
					break;
				case 17:
					Intent intent17 = new Intent(TaskAddActivity.this, TankDataInputAcitivity.class);
					startActivityForResult(intent17, REQUEST_HouChi);
					break;
				default:
					finish();
					break;
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_to_job_ll:
			Intent i = new Intent(TaskAddActivity.this, JobListActivity.class);
			startActivity(i);
			break;
		case R.id.btn_to_guide_ll:
			startMeChat();
			break;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if(requestCode == REQUEST_INPUT) {
				finish();
		    } else if(requestCode == REQUEST_EMERGENCY) {
		    	finish();
		    } else if(requestCode == REQUEST_QianChi) {
		    	finish();
		    } else if(requestCode == REQUEST_HouChi) {
		    	finish();
		    }
		}
	}

	public class FunctionsAdapter extends BaseAdapter {

		private Context context;
		/** 功能列表 */
		private List<Integer> mFunctionNameList = new ArrayList<Integer>();
		/** 功能图片列表 */
		private List<Integer> mFunctionIconList = new ArrayList<Integer>();

		/** 构造方法 实现初始化 */
		public FunctionsAdapter(Context context, List<Integer> names, List<Integer> icons) {
			this.context = context;
			this.mFunctionNameList = names;
			this.mFunctionIconList = icons;
		}

		public void setData(List<Integer> names, List<Integer> icons) {
			this.mFunctionNameList = names;
			this.mFunctionIconList = icons;
		}

		@Override
		public int getCount() {
			return mFunctionNameList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mFunctionNameList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			final ViewHolder holder;
			// 判断View是否为空
			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(context);
				// 得到布局文件
				view = inflater.inflate(R.layout.layout_task_item, null);
				// 得到控件
				holder.mTaskName = (TextView) view.findViewById(R.id.task_name);
				holder.mTaskName2 = (TextView) view.findViewById(R.id.task_name2);
				holder.mTaskIcon = (ImageView) view.findViewById(R.id.task_icon);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.mTaskName.setText(mFunctionNameList.get(position));
			holder.mTaskIcon.setImageResource(mFunctionIconList.get(position));
			if(TextUtils.isEmpty(holder.mTaskName.getText().toString())) {
				view.setBackgroundResource(R.color.transparent);
			} else {
				view.setBackgroundResource(R.drawable.task_item_bg);
			}
			if(position == 5 || position == 15) {
				holder.mTaskName2.setVisibility(View.VISIBLE);
			} else {
				holder.mTaskName2.setVisibility(View.GONE);
			}
				
			return view;
		}
	}

	/** 静态类 */
	static class ViewHolder {
		/** 功能名字 */
		TextView mTaskName;
		TextView mTaskName2;
		/** 功能ICON */
		ImageView mTaskIcon;
	}
}
