package com.brdtec.stevedore.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brdtec.stevedore.R;
import com.brdtec.stevedore.common.CustomTitleBarActivity;
import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnInitCallBackOn;

/**
 * 主功能页
 * @author Allen
 *
 */
public class MainActivity extends CustomTitleBarActivity {

	private GridView mFunGridView;
	
	private Integer[] mFunNames = new Integer[]{R.string.main_txt_job, R.string.main_txt_upload, R.string.main_txt_guide, R.string.main_txt_setting};
	
	private Integer[] mFunIcons = new Integer[]{R.drawable.ic_main_job, R.drawable.ic_main_upload, R.drawable.ic_main_guide, R.drawable.ic_main_setting};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLoc = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initMeChat();
	}
	
	private void findView() {
		setTitle(R.string.main_txt);
		leftView.setVisibility(View.GONE);
		mFunGridView = (GridView) findViewById(R.id.function_gv);
		FunctionsAdapter mAdapter = new FunctionsAdapter(this, Arrays.asList(mFunNames), Arrays.asList(mFunIcons));
		mFunGridView.setAdapter(mAdapter);
		mFunGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent mIntent = null;
				switch (position) {
				case 0:
					mIntent = new Intent(MainActivity.this, JobListActivity.class);
					break;
				case 1:
					mIntent = new Intent();
					mIntent.setClass(MainActivity.this, DataUploadStatusActivity.class);
					break;
				case 2:
					startMeChat();
					break;
				case 3:
					Toast.makeText(MainActivity.this, "敬请期待", Toast.LENGTH_LONG).show();
					break;
				}
				if(mIntent != null) {
					startActivity(mIntent);
				}
			}
		});
	}

	private void initMeChat() {
        MQManager.init(this, "b4de1d7b976d16448b115f4ae55dd258", new OnInitCallBackOn() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "init success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(MainActivity.this, "int failure", Toast.LENGTH_SHORT).show();
            }
        });
        MQManager.getInstance(this).setDebugMode(true);
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
				view = inflater.inflate(R.layout.gridview_items, null);
				// 得到控件
				holder.mFunctionName = (TextView) view.findViewById(R.id.item_tv);
				holder.mFunctionIcon = (ImageView) view.findViewById(R.id.item_icon);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.mFunctionName.setText(mFunctionNameList.get(position));
			holder.mFunctionIcon.setImageResource(mFunctionIconList.get(position));
			return view;
		}
	}

	/** 静态类 */
	static class ViewHolder {
		/** 功能名字 */
		TextView mFunctionName;
		/** 功能ICON */
		ImageView mFunctionIcon;
	}
}
