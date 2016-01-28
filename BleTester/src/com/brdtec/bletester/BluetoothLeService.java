package com.brdtec.bletester;

import android.R.string;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;


public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    //当前状态标志
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";


    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("0000FFF4-0000-1000-8000-00805F9B34FB");

    //public final static UUID UUID_OBDC_DEVICE= UUID.fromString(OBDCProtocol.UUID_OBDC_DEVICE_CHA1);



    private final IBinder mBinder = new LocalBinder();


    public boolean initialize() {
        //获取蓝牙管理
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            //获取蓝牙的系统服务
            if (mBluetoothManager == null) {
                //Log.e(TAG, "Unable to initialize BluetoothManager.");
                Toast.makeText(this, "BluetoothManager获取失败", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //获取适配器
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            //Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            Toast.makeText(this, "BLE Adapter获取失败", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public BluetoothLeService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        //结束使用设备后保证使用BluetoothGatt.close()使设备和Gatt断开来保证资源完全释放
        //在本例中close会在UI（DeviceControl）和服务断开时调用
        //close（）->mBluetoothGatt.close()
        close();
        return super.onUnbind(intent);
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        //释放Gatt
    }

    public boolean connect(final String address) {
        //确认已经获取到适配器并且地址不为空
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }


        // 之前连接过的设备，进行再次连接
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        //通过设备地址获取设备
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        //连接Gatt，设置不自动重连，设置回调
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;//记录地址
        mConnectionState = STATE_CONNECTING;//设置状态值
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        //断开连接
    }

    //////////////////////////////////////////////////////////////////////////////////////
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
        //广播事件
    }


    private String last_str="";
    //从特征中分析返回的数据并表示为action后进行广播
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        //else if (UUID_OBDC_DEVICE.equals(characteristic.getUuid())){
            //获取到目标设备OBDC发送的特征

        //}
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            
            if (data != null && data.length > 0) {
               
                try {
                    String get_str = new String(data, "ASCII");
                    String get_str_trim=get_str.trim();
                    last_str+=get_str;
                    
                    //System.out.println("last_str"+last_str);
                    intent.putExtra(EXTRA_DATA,last_str);
                    sendBroadcast(intent);
                    last_str="";
                    

                    //intent.setAction(ACTION_DATA_AVAILABLE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //Gatt连接完后回调
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //连接状态发生变更
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //发现服务的广播
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        	
        	
        	
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////
    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }




    ////////////////////////////////////////////////////////////////////
    //读特征值
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    
    //写特征值
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
    
    //设置为特征值改变主动提醒
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
    	if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
    	mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    	List<BluetoothGattDescriptor> descriptors=characteristic.getDescriptors();
        for(BluetoothGattDescriptor dp:descriptors){
         dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
         mBluetoothGatt.writeDescriptor(dp);
        }
    
       
   }
    
    //返回所有的服务
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}



//01 0D 3F 0D 0D 3E
//01 0C