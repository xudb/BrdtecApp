package com.brdtec.stevedore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.CustomTitleBarActivity;

/***
 * 数据上传状态一览
 * @author Allen
 *
 */
public class DataUploadStatusActivity extends CustomTitleBarActivity {

	ListView mDUList;
	
	RadioGroup mDUGroup;
	
	LinearLayout mBtnToJobList;
	LinearLayout mBtnToGuide;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_status);
		findView();
	}
	
	private void findView() {
		setTitle(R.string.main_txt_upload);
		leftView.setOnClickListener(this);
		mBtnToJobList = (LinearLayout) findViewById(R.id.btn_to_job_ll);
		mBtnToGuide = (LinearLayout) findViewById(R.id.btn_to_guide_ll);
		mBtnToJobList.setOnClickListener(this);
		mBtnToGuide.setOnClickListener(this);
		
		mDUGroup = (RadioGroup) findViewById(R.id.du_radioGroup);
		mDUList = (ListView) findViewById(R.id.du_list);
		DUAdapter mAdapter = new DUAdapter(this);
		mDUList.setAdapter(mAdapter);
		mDUGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radioId = arg0.getCheckedRadioButtonId();
				if(radioId == R.id.du_radioDone) {
					
				} else {
					
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.btn_to_job_ll:
			Intent i = new Intent(DataUploadStatusActivity.this, JobListActivity.class);
			startActivity(i);
			break;
		case R.id.btn_to_guide_ll:
			startMeChat();
			break;
		}
	}
	
	public class DUAdapter extends BaseAdapter {
		
		private Context mContext;
		
		public DUAdapter(Context context) {
			mContext = context;
		}
		
		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewRoot) {
			final ViewHolder holder;
			// 判断View是否为空
			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				// 得到布局文件
				view = inflater.inflate(R.layout.layout_du_item, null);
				// 得到控件
				holder.mDUTitle = (TextView) view.findViewById(R.id.du_title);
				holder.mDUSubTitle = (TextView) view.findViewById(R.id.du_sub_title);
				holder.mDUPercent = (TextView) view.findViewById(R.id.du_percent);
				holder.mDUPgb = (ProgressBar) view.findViewById(R.id.du_progressbar);
				holder.mDUStatus = (TextView) view.findViewById(R.id.du_progressbar_status);
				holder.mDUCancel = (ImageView) view.findViewById(R.id.du_cancel);
				holder.mDUStart = (ImageView) view.findViewById(R.id.du_strat);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.mDUPgb.setProgress(10);
			return view;
		}
	}
	
	static class ViewHolder {
		TextView mDUTitle;
		TextView mDUSubTitle;
		TextView mDUPercent;
		ProgressBar mDUPgb;
		TextView mDUStatus;
		ImageView mDUCancel;
		ImageView mDUStart;
		
	}
}
