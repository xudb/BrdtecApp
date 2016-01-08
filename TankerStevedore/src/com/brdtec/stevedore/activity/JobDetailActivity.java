package com.brdtec.stevedore.activity;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.Constant;
import com.brdtec.stevedore.common.CustomTitleBarActivity;
import com.brdtec.stevedore.widget.ActionSheet;
import com.brdtec.stevedore.widget.ActionSheet.MenuItemClickListener;

/***
 * 任务详情界面
 * 
 * @author Allen
 * 
 */
public class JobDetailActivity extends CustomTitleBarActivity implements MenuItemClickListener {

	TextView mJobUpload;

	TextView mJobAdd;

	ListView mJobDetailListView;

	LinearLayout mBtnToJobList;
	LinearLayout mBtnToGuide;
	
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTORESOULT = 2;// 结果
	public static final int REQUEST_IMAGE = 3;
	public static final String FILE_NAME = Constant.IMAGE_TEMP + "temp.jpg";
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private ArrayList<String> mSelectPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_detail);
		findView();
	}

	private void findView() {
		setTitle(R.string.job_txt_detail);
		mJobUpload = (TextView) findViewById(R.id.job_upload_file);
		mJobAdd = (TextView) findViewById(R.id.job_add_task);
		mJobDetailListView = (ListView) findViewById(R.id.job_detail_list);
		mBtnToJobList = (LinearLayout) findViewById(R.id.btn_to_job_ll);
		mBtnToGuide = (LinearLayout) findViewById(R.id.btn_to_guide_ll);
		mJobUpload.setOnClickListener(this);
		mJobAdd.setOnClickListener(this);
		mBtnToJobList.setOnClickListener(this);
		mBtnToGuide.setOnClickListener(this);
		JobDetailAdapter mAdapter = new JobDetailAdapter(this);
		mJobDetailListView.setAdapter(mAdapter);
		mJobDetailListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_to_job_ll:
			Intent i = new Intent(JobDetailActivity.this, JobListActivity.class);
			startActivity(i);
			break;
		case R.id.btn_to_guide_ll:
			startMeChat();
			break;
		case R.id.job_upload_file:
			setTheme(R.style.ActionSheetStyleIOS7);
			showActionSheet();
			break;
		case R.id.job_add_task:
			Intent i3 = new Intent(JobDetailActivity.this, TaskAddActivity.class);
			startActivity(i3);
			break;
		}
	}

	public void showActionSheet() {
		ActionSheet menuView = new ActionSheet(this);
		menuView.setCancelButtonTitle("取消");// before add items
		menuView.addItems("拍照", "从相册中选择");
		menuView.setItemClickListener(this);
		menuView.setCancelableOnTouchMenuOutside(true);
		menuView.showMenu();
	}

	@Override
	public void onItemClick(int itemPosition) {
		switch (itemPosition) {
		case 0:
			File tempDir = new File(Constant.IMAGE_TEMP);
			if(!tempDir.exists()) {
				tempDir.mkdirs();
			}
			File localFile = new File(FILE_NAME);
			if (localFile.exists()) {
				localFile.delete();
			}
			Intent intentTakePhoto = new Intent("android.media.action.IMAGE_CAPTURE");
			intentTakePhoto.putExtra("output", Uri.fromFile(localFile));
			intentTakePhoto.putExtra("android.intent.extra.videoQuality", 1);
			startActivityForResult(intentTakePhoto, PHOTOHRAPH);
			break;
		case 1:
			 Intent intent = new Intent(JobDetailActivity.this, MultiImageSelectorActivity.class);
             // 是否显示拍摄图片
             intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
             // 最大可选择图片数量
             intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
             // 选择模式
             intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
             // 默认选择
             if(mSelectPath != null && mSelectPath.size()>0){
                 intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
             }
             startActivityForResult(intent, REQUEST_IMAGE);
			break;
		case 2:
			Intent intent1 = new Intent(JobDetailActivity.this, TankDataInputAcitivity.class);
			intent1.putExtra("isqian", true);
			startActivity(intent1);
			break;
		case 3:
			Intent intent2 = new Intent(JobDetailActivity.this, TankDataInputAcitivity.class);
			startActivity(intent2);
			break;
		}
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTOHRAPH) {
			
		} else if(requestCode == REQUEST_IMAGE){
			if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            }
        }
	}

	public class JobDetailAdapter extends BaseAdapter {

		private Context mContext;

		public JobDetailAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return 2;
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
		public View getView(int position, View view, ViewGroup root) {
			final ViewHolder holder;
			// 判断View是否为空
			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				// 得到布局文件
				view = inflater.inflate(R.layout.layout_job_detail_item, null);
				// 得到控件
				holder.mJobTitle = (TextView) view.findViewById(R.id.job_title);
				holder.mJobSummary = (TextView) view.findViewById(R.id.job_sub_title);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			return view;
		}

	}

	static class ViewHolder {
		TextView mJobTitle;
		TextView mJobSummary;
	}
}
