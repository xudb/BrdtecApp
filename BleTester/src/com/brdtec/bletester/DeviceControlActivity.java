package com.brdtec.bletester;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.EditText;


import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class DeviceControlActivity extends Activity {


    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    //控件
    private TextView dev_name;
    private TextView dev_addr;
    private TextView dev_connection;
    private ToggleButton connectBtn;
   
  

    private Button sendBtn1;//漆膜测厚
    private Button sendBtn2;//距离
    private Button sendBtn3;//电池电压
    private Button sendBtn4;//补光灯
    
    private Button Clear;


    private TextView mDataField;
    private ExpandableListView mGattServicesList;

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic2;
    private BluetoothGattCharacteristic mNotifyCharacteristic4;
    
    
    private BluetoothGattCharacteristic mBleChar;
    private BluetoothGattCharacteristic mBle_Char1;
    private BluetoothGattCharacteristic mBle_Char3;
    private BluetoothGattCharacteristic mBle_Char5;
    private BluetoothGattCharacteristic mBle_Char6;
    private BluetoothGattCharacteristic mBle_Char7;
    
    private String mDeviceName;
    private String mDeviceAddress;

    private final String LIST_NAME = "NAME";
    private final String CHARACTERISTIC = "CHAR ";
    private final String LIST_UUID = "UUID";
    byte[] WriteBytes = new byte[20];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);


        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);//获取设备名
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);//获取MAC地址

        this.dev_name=(TextView)findViewById(R.id.ctrl_txt2);
        this.dev_addr=(TextView)findViewById(R.id.ctrl_txt21);
        this.dev_connection=(TextView)findViewById(R.id.ctrl_txt4);
        this.mDataField = (TextView) findViewById(R.id.ctrl_txtData);
        this.connectBtn=(ToggleButton)findViewById(R.id.ctrl_btn1);
        
        
        this.sendBtn1=(Button)findViewById(R.id.ctrl_btn3);//漆膜测厚
        this.sendBtn2=(Button)findViewById(R.id.ctrl_btn4);//距离
        this.sendBtn3=(Button)findViewById(R.id.ctrl_btn5);//电池电压
        this.sendBtn4=(Button)findViewById(R.id.ctrl_btn6);//补光灯
        
        
        this.Clear=(Button)findViewById(R.id.ctrl_btn_clear);
        this.Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataField.setText("");
            }
        });


        // Sets up UI references.
        dev_name.setText(mDeviceName);
        dev_addr.setText(mDeviceAddress);
        
        mGattServicesList = (ExpandableListView) findViewById(R.id.ctrl_lv1);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleButton toggleButton=(ToggleButton)view;
                if(!toggleButton.isChecked())//连接开启时check为false
                {
                    //连接目标设备
                    try{
                        mBluetoothLeService.connect(mDeviceAddress);
                        dev_connection.setText("Connected");
                    }catch (Exception e)
                    {
                        Log.d(TAG,e.getMessage());
                    }
                }else
                {
                    //关闭设备连接
                    mBluetoothLeService.disconnect();
                    dev_connection.setText("Disconnected");
                }
            }
        });



        this.sendBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendString(mBle_Char1,"@");//测厚
            }
        });

        this.sendBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendString(mBle_Char3,"D");//测距
            }
        });
        this.sendBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	mBluetoothLeService.readCharacteristic(mBle_Char5);//读电池电压
            }
        });

        this.sendBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendString(mBle_Char7,"1");//开补光灯
            }
        });
        



        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        this.dev_connection.setText("自动连接设备中");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        mBluetoothLeService = null;
    }



/////////////////////////////////////////////////////////////////////////////////////////
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        //ComponentName（组件名称）是用来打开其他应用程序中的Activity或服务的
        //IBinder接口是对跨进程的对象的抽象
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                Toast.makeText(DeviceControlActivity.this,"无法初始化蓝牙设备",Toast.LENGTH_SHORT).show();
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            
            mBluetoothLeService.connect(mDeviceAddress);
            //自动连接到目标设备
            connectBtn.setChecked(false);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener(){
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    final EditText etHex;
                    final EditText etStr;

                    etHex=new EditText(parent.getContext());
                    etStr=new EditText(parent.getContext());

                    etHex.setSingleLine();
                    etStr.setSingleLine();
                    if (mGattCharacteristics != null) {
                        //final BluetoothGattCharacteristic
                        mBleChar =mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = mBleChar.getProperties();
                        //如果该特性可写
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0){
                            LayoutInflater factory = LayoutInflater.from(parent.getContext());
                            final View textEntryView = factory.inflate(R.layout.dialog, null);
                            final EditText editTextName = (EditText) textEntryView.findViewById(R.id.editTextName);
                            final EditText editTextNumEditText = (EditText)textEntryView.findViewById(R.id.editTextNum);
                            
                            AlertDialog.Builder ad1 = new AlertDialog.Builder(parent.getContext());//警告对话框
                            ad1.setTitle("写特性");
                            ad1.setView(textEntryView);
                            ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            	//确认按钮响应
                                public void onClick(DialogInterface dialog, int i) {
                                    byte[] value = new byte[20];
                                    value[0] = (byte) 0x00;
                                    if(editTextName.getText().length() > 0){
                                        //write string

                                        WriteBytes= editTextName.getText().toString().getBytes();
                                    }else if(editTextNumEditText.getText().length() > 0){
                                        try {
                                            WriteBytes = hex2byte(editTextNumEditText.getText().toString().getBytes());
                                        }catch (Exception e)
                                        {Toast.makeText(DeviceControlActivity.this,"长度不为偶数",Toast.LENGTH_SHORT).show();}
                                    }
                                    mBleChar.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                                    mBleChar.setValue(WriteBytes);

                                    mBluetoothLeService.writeCharacteristic(mBleChar);
                                }
                            });
                            ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {

                                }
                            });
                            ad1.show();

                            }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = mBleChar;
                           
                            mBluetoothLeService.setCharacteristicNotification(mBleChar, true);
                        }
                        return true;
                    }
                    return false;
                }
            };


    public static String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            //throw new IllegalArgumentException("长度不是偶数");
            //edit by me 由原来的抛出异常改为用回车补全
            byte[] b2=new byte[(b.length-1)/2+1];
            for (int i=0;i<b.length-1;i+=2)
            {
                String item=new String(b,i,2);
                b2[i/2]=(byte)Integer.parseInt(item,16);
            }
            b=null;
            return b2;
        }
        else {
            byte[] b2 = new byte[b.length / 2];
            for (int n = 0; n < b.length; n += 2) {
                String item = new String(b, n, 2);
                // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
                b2[n / 2] = (byte) Integer.parseInt(item, 16);
            }
            b = null;
            return b2;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //广播接收器，用来接收来自系统和应用中的广播
    //大多用于broadcast发送广播时 给intent set一个action 就是一个字符串 你可以通过receive接受intent 通过 getAction 得到的字符串 来决定做什么。
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();//广播的内容
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            	//如果是蓝牙GATT的连接
                mConnected = true;
                updateConnectionState(R.string.connected);//更新为连接
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            	//如果是蓝牙GATT的断开
                mConnected = false;
                updateConnectionState(R.string.disconnected);//更新为断开
                //invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	//接收到广播事件（发现GATT_SERVICES）后，显示得到的所有服务
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //System.out.println(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            }
        }
    };

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dev_connection.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            //data=mDataField.getText()+data;
            mDataField.setText(data);
        }
    }

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText("No Data...");
    }

    /////////////////////////////////////////////////////////////////////////
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "未知服务";
        String unknownCharaString = "未知特征";
        String split = "-"; 
        
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        //服务信息的动态数组
        
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        //特征值信息的动态数组
                
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        //罗列所有服务
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString().toUpperCase();//获取UUID字符串
            //根据UUID的值判断服务类型
            currentServiceData.put(LIST_NAME, lookup(uuid, unknownServiceString));
            StringTokenizer serviceToken = new StringTokenizer(uuid, split);  
            currentServiceData.put(LIST_UUID, serviceToken.nextToken());
          //currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);//放入服务数组

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();//针对某一项服务获取特征值
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // 罗列所有特征字
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);//特征值放入动态列表数组中
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString().toUpperCase();//特征值的UUID
             
            	currentCharaData.put(LIST_NAME, lookup(uuid, unknownCharaString));
            	StringTokenizer charToken = new StringTokenizer(uuid, split);  
            	currentCharaData.put(LIST_UUID, charToken.nextToken());
             
                gattCharacteristicGroupData.add(currentCharaData);//写接口
                
                if(uuid.equals("0000FFF1-0000-1000-8000-00805F9B34FB")){
                    mBle_Char1=gattCharacteristic;
                }else if(uuid.equals("0000FFF2-0000-1000-8000-00805F9B34FB")){
                    mNotifyCharacteristic2=gattCharacteristic;
                    
                }else if(uuid.equals("0000FFF3-0000-1000-8000-00805F9B34FB")){
                	mBle_Char3=gattCharacteristic;
                }else if(uuid.equals("0000FFF4-0000-1000-8000-00805F9B34FB")){
                    mNotifyCharacteristic4=gattCharacteristic;
                    //mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic4, true);
                }else if(uuid.equals("0000FFF5-0000-1000-8000-00805F9B34FB")){
                	mBle_Char5=gattCharacteristic;
                }else if(uuid.equals("0000FFF7-0000-1000-8000-00805F9B34FB")){
                	mBle_Char7=gattCharacteristic;
                }
                
            }
            
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
           
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
        
       
        //为什么放在这里好用！！！
        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic2, true);
        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic4, true);
        ///
    }

    ////////////////////////////////////////////////////////
    private static HashMap<String, String> attributes = new HashMap();

    static {
        
    	attributes.put("00001800-0000-1000-8000-00805F9B34FB","Generic Access Service");
    	attributes.put("00001801-0000-1000-8000-00805F9B34FB","Generic Attribute Service");
    	attributes.put("0000180A-0000-1000-8000-00805F9B34FB","Device Information Service");
    	attributes.put("0000FFF0-0000-1000-8000-00805F9B34FB","Simple Profile Service");
    	attributes.put("F000FFC0-0451-4000-B000-000000000000","OAD Service");
     
    	attributes.put("00002A00-0000-1000-8000-00805F9B34FB","Device Name");
        attributes.put("00002A01-0000-1000-8000-00805F9B34FB","Appearance");
        attributes.put("00002A02-0000-1000-8000-00805F9B34FB","Peripheral Privacy Flag");
        attributes.put("00002A03-0000-1000-8000-00805F9B34FB","Reconnection Address");
        attributes.put("00002A04-0000-1000-8000-00805F9B34FB","Peripheral Preferred Connection Parameters");
        attributes.put("00002A05-0000-1000-8000-00805F9B34FB","Service Changed");
        
        attributes.put("00002A23-0000-1000-8000-00805F9B34FB","System ID");
        attributes.put("00002A24-0000-1000-8000-00805F9B34FB","Model Number String");
        attributes.put("00002A25-0000-1000-8000-00805F9B34FB","Serial Number String");
        attributes.put("00002A26-0000-1000-8000-00805F9B34FB","Firmware Revision String");
        attributes.put("00002A27-0000-1000-8000-00805F9B34FB","Hardware Revision String");
        attributes.put("00002A28-0000-1000-8000-00805F9B34FB","Software Revision String");
        attributes.put("00002A29-0000-1000-8000-00805F9B34FB","Manufacturer Name String");
        attributes.put("00002A2A-0000-1000-8000-00805F9B34FB","IEEE 1*** Certification Data List");
        attributes.put("00002A50-0000-1000-8000-00805F9B34FB","PnP ID");
        
        attributes.put("0000FFF1-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 1");
        attributes.put("0000FFF2-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 2");
        attributes.put("0000FFF3-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 3");
        attributes.put("0000FFF4-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 4");
        attributes.put("0000FFF5-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 5");
        attributes.put("0000FFF6-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 6");
        attributes.put("0000FFF7-0000-1000-8000-00805F9B34FB","Simple Profile Characteristic 7");
        
        attributes.put("F000FFC1-0451-4000-B000-000000000000","OAD Image Identify");
        attributes.put("F000FFC2-0451-4000-B000-000000000000","OAD Image Block");
         
    
    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public void sendString(BluetoothGattCharacteristic bleChar,String command)
    {
        try {
            if (bleChar != null) {
//                if (!command.matches("\\r$"))//如果末尾没有回车，自动补全
//                    command += "\r";
                if (command.length() > 0) {
                    byte[] WriteBytes;
                    WriteBytes = command.getBytes();
                    System.out.println("command:"+command);
                    //WriteBytes = hex2byte(sendStr.getBytes());
                   // bleChar.setValue((byte) 0x00, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    bleChar.setValue(WriteBytes);
                    mBluetoothLeService.writeCharacteristic(bleChar);
                }
            }else
            {
                System.out.println("写特性未实例化");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}



//android.content.ServiceConnection是一个接口，实现（implementate）这个接口有2个方法需要重写（Override）。
// 一个是当Service成功绑定后会被回调的onServiceConnected()方法，
// 另一个是当Service解绑定或者Service被关闭时被回调的onServiceDisconnected()。
//前者（onServiceConnected()方法）会传入一个IBinder对象参数，
// 这个IBinder对象就是在Service的生命周期回调方法的onBind()方法中的返回值