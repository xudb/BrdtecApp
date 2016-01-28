package com.brdtec.bletester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;

@SuppressLint("NewApi")

public class DeviceStateActivity extends Activity {
    private ListView listView;
    private Button buttonScan;
    private Button buttonStop;
    private TextView stateTextView;

    
    private Handler mHandler;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 20000;//一次扫描时间设置 ms單位

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_state);

        //控件初始化
        this.listView=(ListView)findViewById(R.id.dev_lv1);
        this.buttonScan=(Button)findViewById(R.id.dev_btn1);
        this.buttonStop=(Button)findViewById(R.id.dev_btn2);
        this.stateTextView=(TextView)findViewById(R.id.dev_txt2);
      

        //扫描时间控制
        mHandler = new Handler();
        
        //绑定监听
        this.buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });
        this.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(false);
            }
        });
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	//点击LISTVIEW的某一项目
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
            	final BluetoothDevice device = mLeDeviceListAdapter.getDevice(i);
                if (device == null) return;
                
                final Intent intent = new Intent(DeviceStateActivity.this, DeviceControlActivity.class);
                //进入到设备控制Activity
                
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                //设置2个字符串的内容
                
                if (mScanning) {//如果正在扫描，停止扫描
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });

        //初始化蓝牙部分，检测蓝牙是否支持
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "你的设备不支持BLE功能", Toast.LENGTH_SHORT).show();
            finish();
        }
        //获取Adapeter
        final BluetoothManager bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        //检测是否获取到蓝牙Adapter
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
        	//如果蓝牙未开启，启动蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            startActivityForResult(enableBtIntent, 1);
        }
        scanLeDevice(true);//进入APP就扫描
    }


    @Override
    protected void onResume() {
        super.onResume();

        

        // 将数据源绑定到UI
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(mLeDeviceListAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            //如果请求开启蓝牙被拒绝则关闭活动
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    stateTextView.setText("已停止扫描");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        if(mScanning)
        {
            stateTextView.setText("正在扫描设备");
        }else
        {
            stateTextView.setText("已停止扫描");
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.device_state, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Adapter适配器
    private class LeDeviceListAdapter extends BaseAdapter {
    	//基于BaseAdapter的设备列表适配器
        private ArrayList<BluetoothDevice> mLeDevices;
        
        private LayoutInflater mInflator;
        private String mRssi;
        //LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化；
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceStateActivity.this.getLayoutInflater();
            //调用Activity的getLayoutInflater() 
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {//若存在，则添加
                mLeDevices.add(device);
            }
        }
        public void setRssi(int rssi) {
        	mRssi = Integer.toString(rssi);
        	
        	
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
      

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
     

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                //通过listitem_device.layout找到对应的VIEW
                
                viewHolder = new ViewHolder();
                
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                //通过id找设备地址的TextView
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                //通过id找设备名称的TextView
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                //通过id找设备信号强度的TextView
                view.setTag(viewHolder);
            
            } else {
            
            	viewHolder = (ViewHolder) view.getTag();
            
            }

            BluetoothDevice device = mLeDevices.get(i); //获取蓝牙设备
           
            final String deviceName = device.getName();//获取设备名称
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);//显示设备名称
            else
                viewHolder.deviceName.setText("未知设备");
            
            viewHolder.deviceAddress.setText(device.getAddress());//显示设备地址
            viewHolder.deviceRssi.setText("RSSI:" + mRssi);//显示设备信号强度
          
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        
        //ViewHolder通常出现在适配器里，为的是listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
    }

    // Device scan callback.设备扫描回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
    	    new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device,  final int rssi, byte[] scanRecord) {
                    //识别的远程设备,信号强度 ,信号强度 
                	runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);//添加设备
                            mLeDeviceListAdapter.setRssi(rssi);//添加信号强度                            
                            mLeDeviceListAdapter.notifyDataSetChanged();//数据有变化，主动提醒
                        }
                    });
                }
            };
}
