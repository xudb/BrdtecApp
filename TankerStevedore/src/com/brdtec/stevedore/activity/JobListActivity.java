package com.brdtec.stevedore.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.brdtec.stevedore.BrdSDK;
import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.Constant;
import com.brdtec.stevedore.common.CustomTitleBarActivity;
import com.brdtec.stevedore.data.Work;
import com.brdtec.stevedore.data.response.JobListResponseData;
import com.brdtec.stevedore.utils.ViewUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/***
 * 任务列表界面
 * 
 * @author Allen
 * 
 */
public class JobListActivity extends CustomTitleBarActivity {

	TextView mJobTypeSel;

	EditText mJobSearch;

	ListView mJobListView;
	
	JobAdapter mAdapter;

	public PopupWindow mPopupMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_list);
		findView();
		getData("");
	}

	private void findView() {
		setTitle(R.string.main_txt_job);
		leftView.setOnClickListener(this);
		mJobTypeSel = (TextView) findViewById(R.id.job_type_tv);
		mJobSearch = (EditText) findViewById(R.id.job_type_search);
		mJobSearch.clearFocus();
		mJobListView = (ListView) findViewById(R.id.job_listview);
		mJobTypeSel.setOnClickListener(this);
		mAdapter = new JobAdapter(this);
		mJobListView.setAdapter(mAdapter);
		mJobListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				try{
					Work work = BrdSDK.getBrdSDK().mWorks.get(position);
					if(work != null) {
						if(work.stus.equals("IP")) {
							Intent i = new Intent(JobListActivity.this, JobDetailActivity.class);
							startActivity(i);
						} else if(work.stus.equals("NS")) {
							Toast.makeText(JobListActivity.this, "该作业还未开始", Toast.LENGTH_LONG).show();
						} else if(work.stus.equals("ED")) {
							Toast.makeText(JobListActivity.this, "该作业已结束", Toast.LENGTH_LONG).show();
						}
					}
				} catch(Exception e) {
					
				}
			}
		});
	}

	private void getData(String status) {
		String url = "http://brdtec.cn/otrm/service.php?func=qrywork&sessionid=" + mApplication.getSessionId()
				+ "&stus=" + status;
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000 * 10);
		http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Gson gson = new Gson();
				JobListResponseData mData = gson.fromJson(responseInfo.result, JobListResponseData.class);
				String status = mData.stuscode;
				if (status.equals(Constant.HTTPStatus.S0)) {
					BrdSDK.getBrdSDK().mWorks = mData.works;
					mAdapter.setData(BrdSDK.getBrdSDK().mWorks);
				} else if (status.equals(Constant.HTTPStatus.E6) || status.equals(Constant.HTTPStatus.E0)) {
					Toast.makeText(JobListActivity.this, "服务器解析错误", Toast.LENGTH_LONG).show();
				} else if (status.equals(Constant.HTTPStatus.E5)) {
					Toast.makeText(JobListActivity.this, "服务器未实现", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(JobListActivity.this, "认证失败，请重新登录", Toast.LENGTH_LONG).show();
					Intent i = new Intent(JobListActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				}
			}
		});
	}

	/**
	 * 显示帮助按钮
	 */
	private void showHelpMenu() {
		if (mPopupMenu != null && mPopupMenu.isShowing()) {
			mPopupMenu.setFocusable(false);
			mPopupMenu.dismiss();
		} else {
			createMenuPopWindow();
			mPopupMenu.showAsDropDown(mJobTypeSel, 0, 0);
		}
	}

	private void createMenuPopWindow() {

		LayoutInflater inflact = LayoutInflater.from(JobListActivity.this);
		View view = inflact.inflate(R.layout.layout_help_menus, null);
		mPopupMenu = new PopupWindow(view, ViewUtils.dpToPx(this, 110), LayoutParams.WRAP_CONTENT);
		(view.findViewById(R.id.btn_job_prepare)).setOnClickListener(this);
		(view.findViewById(R.id.btn_job_doing)).setOnClickListener(this);
		(view.findViewById(R.id.btn_job_done)).setOnClickListener(this);
		(view.findViewById(R.id.btn_job_all)).setOnClickListener(this);
		mPopupMenu.setFocusable(true); // 设置PopupWindow可获得焦点
		mPopupMenu.setTouchable(true); // 设置PopupWindow可触摸
		mPopupMenu.setOutsideTouchable(true); // 设置PopupWindow外部区域是否可触摸
		mPopupMenu.setBackgroundDrawable(new BitmapDrawable());
		// 监听返回按钮事件，因为此时焦点在popupwindow上，如果不监听，返回按钮没有效果
		view.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if (mPopupMenu != null && mPopupMenu.isShowing()) {
						mPopupMenu.dismiss();
					}
					break;
				}
				return true;
			}
		});
		// 监听点击事件，点击其他位置，popupwindow小窗口消失
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mPopupMenu != null && mPopupMenu.isShowing()) {
					mPopupMenu.dismiss();
					mPopupMenu = null;
				}
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.job_type_tv:
			showHelpMenu();
			break;
		case R.id.btn_job_prepare:
			if (mPopupMenu != null && mPopupMenu.isShowing()) {
				mPopupMenu.dismiss();
			}
			getData("NS");
			break;
		case R.id.btn_job_doing:
			if (mPopupMenu != null && mPopupMenu.isShowing()) {
				mPopupMenu.dismiss();
			}
			getData("IP");
			break;
		case R.id.btn_job_done:
			if (mPopupMenu != null && mPopupMenu.isShowing()) {
				mPopupMenu.dismiss();
			}
			getData("ED");
			break;
		case R.id.btn_job_all:
			if (mPopupMenu != null && mPopupMenu.isShowing()) {
				mPopupMenu.dismiss();
			}
			getData("");
			break;
		}
	}

	public class JobAdapter extends BaseAdapter {

		private Context mContext;
		
		private List<Work> mWorks = new ArrayList<Work>();

		public JobAdapter(Context context) {
			mContext = context;
		}
		
		public void setData(List<Work> works) {
			if(works != null && works.size() > 0) {
				mWorks = works;
			} else {
				mWorks = new ArrayList<Work>();
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mWorks.size();
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
				view = inflater.inflate(R.layout.layout_job_item, null);
				// 得到控件
				holder.mJobTitle = (TextView) view.findViewById(R.id.job_title);
				holder.mJobSummary = (TextView) view.findViewById(R.id.job_sub_title);
				holder.mJobStatus = (ImageView) view.findViewById(R.id.job_status);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			Work work = mWorks.get(position);
			holder.mJobTitle.setText(work.sname);
			if(!TextUtils.isEmpty(work.remark)) {
				holder.mJobSummary.setText(work.remark);
			}
			if(!TextUtils.isEmpty(work.stus)) {
				if("NS".equals(work.stus)) {
					holder.mJobStatus.setImageResource(R.drawable.ic_job_prepare);
				} else if("IP".equals(work.stus)) {
					holder.mJobStatus.setImageResource(R.drawable.ic_job_doing);
				} else if("ED".equals(work.stus)) {
					holder.mJobStatus.setImageResource(R.drawable.ic_job_don);
				}
			}
			return view;
		}

	}

	static class ViewHolder {
		TextView mJobTitle;
		TextView mJobSummary;
		ImageView mJobStatus;
	}
}
