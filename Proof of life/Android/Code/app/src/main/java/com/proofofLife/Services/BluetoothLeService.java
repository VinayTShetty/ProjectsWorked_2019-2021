package com.proofofLife.Services;

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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.proofofLife.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.proofofLife.BlePacketHelper.PacketCreator.decryptPacket;
import static com.proofofLife.BlePacketHelper.PacketCreator.get_16_bytes_encry;
import static com.proofofLife.UUID.FirmwareUUID.CLIENT_CHARACTERISTIC_CONFIG;
import static com.proofofLife.UUID.FirmwareUUID.PROOF_OF_LIFE_CHARCTERSTICS_UUID;
import static com.proofofLife.UUID.FirmwareUUID.PROOF_OF_LIFE_SERVICE_UUID;
import static com.proofofLife.BleUtility.BleUtilityHelper.ble_on_off;
import static com.proofofLife.UtilitConversion.ConversionHelper.getHexStringFromByteArray;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private static BluetoothGatt mBluetoothGatt;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private int mConnectionState = STATE_DISCONNECTED;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.googleble.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.googleble.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.googleble.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.googleble.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.googleble.le.EXTRA_DATA";

    /**
     * Connection Time out timer
     */
    private Map<String, BluetoothGatt> mutlipleBluetooDeviceGhatt;
    @Override
    public void onCreate() {
        super.onCreate();
        mutlipleBluetooDeviceGhatt = new HashMap<String, BluetoothGatt>();
    }
    /**
     * Service IBinder to get the data to transfer the data.
     */
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    /**
     * Local I Binder.
     */
    public class LocalBinder extends Binder {
        public BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            //      Log.d(TAG,"onPhyUpdate ");
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            //     Log.d(TAG,"onPhyRead");
        }


        int retryOptionForConnection=0;
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice bleDevice=gatt.getDevice();
            String bleAddress=bleDevice.getAddress();
            Log.d(TAG, "onConnectionStateChange: STATUS= "+status+" NEW STATE= "+newState+" GATT INSANCE= "+gatt.getDevice().getAddress());
            if(status==133&&newState==0){

                switch (retryOptionForConnection){
                    case 2:
                        retryOptionForConnection=0;
                        Log.d(TAG, "    133_STATUS_ISSUE onConnectionStateChange: Retry Count= "+retryOptionForConnection);
                        gatt.disconnect();
                        gatt.close();
                        gatt=null;
                        Log.d(TAG, "133_STATUS_ISSUE onConnectionStateChange: GATT Disconnected and Null");
                        break;
                    default:
                        retryOptionForConnection++;
                        Log.d(TAG, "133_STATUS_ISSUE onConnectionStateChange: Retry Connection= Connecting again retryCounnt= "+retryOptionForConnection);
                        connect(gatt.getDevice().getAddress());
                        break;
                }
            }
            else if (newState == BluetoothProfile.STATE_CONNECTED&&status==0) {
                retryOptionForConnection=0;
                if(!mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
                    mutlipleBluetooDeviceGhatt.put(bleAddress,gatt);
                    mutlipleBluetooDeviceGhatt.get(bleAddress).discoverServices();
                    sendDevice_StatusToMainActivty(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS),gatt.getDevice().getAddress(),true);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED&&status==8) {
                retryOptionForConnection=0;
                if (mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
                    BluetoothGatt bluetoothGatt = mutlipleBluetooDeviceGhatt.get(bleAddress);
                    if( bluetoothGatt != null ){
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                    }
                    mutlipleBluetooDeviceGhatt.remove(bleAddress);
                    sendDevice_StatusToMainActivty(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS),gatt.getDevice().getAddress(),false);

                }
            }else if((newState==BluetoothProfile.STATE_DISCONNECTED)&&(status==19)){
                /**
                 * Authenication disconnection
                 */
                if (mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
                    BluetoothGatt bluetoothGatt = mutlipleBluetooDeviceGhatt.get(bleAddress);
                    if( bluetoothGatt != null ){
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                    }
                    mutlipleBluetooDeviceGhatt.remove(bleAddress);
                    sendDevice_StatusToMainActivty(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS),gatt.getDevice().getAddress(),false);

                }
            }
            else {

                Log.d(TAG,"BLE_SERVICE__DIFFERENT_STATUS "+status+" New State= "+newState);
             //   Log.d(TAG,"BLE_SERVICE DIFFERENT NEW STATE=  "+newState);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //      Log.d(TAG,"onServicesDiscovered");
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                enableChartersticNotification(gatt);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            /**
             * Confermed data recieved in the firmware.
             */
            send_Confermation_WhatDataWriteen_InFirmware(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION),gatt.getDevice().getAddress(),characteristic.getWriteType(),characteristic.getValue(),status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            sendDataRecievedFromFirmware(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED),gatt.getDevice().getAddress(),characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            enableNotiticationToFirmwareCompleted(true,gatt.getDevice().getAddress());
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };
    /**
     * BroadCast Update to send Data to the MainActivity.
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void sendDevice_StatusToMainActivty(final String action,String bleAddress,boolean connectionStatus){
        final Intent intent = new Intent(action);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_BLE_ADDRESS), bleAddress);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_CONNECTED_DISCONNECTED),connectionStatus);
        sendBroadcast(intent);
    }

    private void send_Confermation_WhatDataWriteen_InFirmware(final String action,final String bleaddress,int dataWriteenType,final  byte byteArrayData[],int status){
        Intent intent=new Intent(action);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_ADDRESS),bleaddress);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN),byteArrayData);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN_TYPE),dataWriteenType);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_STATUS),status);
        sendBroadcast(intent);
    }

    private void sendDataRecievedFromFirmware(final String action,final String bleAddress,final byte[] dataRecieved){
        Intent intent=new Intent(action);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_BLE_ADDRESS),bleAddress);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_DATA_RECIEVED),dataRecieved);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
        }

        sendBroadcast(intent);
    }


    /**
     * Set Chanrcterstic Notificaiton.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setCharacteristicNotifications(BluetoothGatt bluetoothGatt,BluetoothGattCharacteristic characteristic,
                                               boolean enabled,String bleAddress) {
        if (mBluetoothAdapter == null || bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = null;
        descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor == null) {
            enableNotiticationToFirmwareCompleted(false,bleAddress);
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);

    }

    private void enableNotiticationToFirmwareCompleted(boolean result,String bleAddress) {
        Intent intent=new Intent(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE));
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE_DATA),result);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE_BLE_AADRESS),bleAddress);
        sendBroadcast(intent);
    }

    public void sendDataToBleDevice(String bleAddress,byte [] data){
        //  System.out.println("Sending Data to BLE Device. ");
        if(mutlipleBluetooDeviceGhatt!=null && mutlipleBluetooDeviceGhatt.size()>0 &&mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
            BluetoothGattService service = mutlipleBluetooDeviceGhatt.get(bleAddress).getService(PROOF_OF_LIFE_SERVICE_UUID);
            BluetoothGattCharacteristic characteristic= service.getCharacteristic(PROOF_OF_LIFE_CHARCTERSTICS_UUID);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            characteristic.setValue(data);
            boolean status=false;
            BluetoothGatt bluetoothGatt=  mutlipleBluetooDeviceGhatt.get(bleAddress);
            status=bluetoothGatt.writeCharacteristic(characteristic);
             System.out.println("DATA WRITTEN SUCESSFULLY "+status);
            if(data.length<3){
                Log.d(TAG, "PACKET_SENT= "+getHexStringFromByteArray(data,true)+" Length = "+data.length);
            }else if(data.length>15) {
                Log.d(TAG, "(Decrypted )PACKET_SENT= "+getHexStringFromByteArray(decryptPacket(data),true)+" Length = "+data.length);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnect(String bleAddress) {
        if (mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
            BluetoothGatt bluetoothGatt = mutlipleBluetooDeviceGhatt.get(bleAddress);
            if( bluetoothGatt != null ){
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt = null;
                mutlipleBluetooDeviceGhatt.remove(bleAddress);
            }
            sendDevice_StatusToMainActivty(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS),bleAddress,false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        mBluetoothGatt=null;
        mBluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }

        return true;
    }
    /**
     * We will Recieve data from the Charcterstic.
     * To recieve the data we need to enable the charcterstic
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void enableChartersticNotification(BluetoothGatt loc_bluetoothGatt){
        BluetoothGattService service=loc_bluetoothGatt.getService(PROOF_OF_LIFE_SERVICE_UUID);
        BluetoothGattCharacteristic characteristic= service.getCharacteristic(PROOF_OF_LIFE_CHARCTERSTICS_UUID);
        setCharacteristicNotifications(loc_bluetoothGatt,characteristic,true,loc_bluetoothGatt.getDevice().getAddress());
    }

    /**
     *
     * It is used to show the list of charctesrtic in the UI.i.e Expandable list View.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public List<BluetoothDevice> getListOfConnectedDevices(){
        return    mBluetoothManager.getConnectedDevices(7);
    }


    public boolean checkDeviceIsAlreadyConnected(String bleAddressToCheckConnectionStatus){
        boolean result=false;
        if(ble_on_off()){
            BluetoothDevice device=  mBluetoothAdapter.getRemoteDevice(bleAddressToCheckConnectionStatus);
            int connectionStatus=mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            if(connectionStatus==BluetoothProfile.STATE_DISCONNECTED){
                result=false;
            }else if(connectionStatus==BluetoothProfile.STATE_CONNECTED){
                result=true;
            }
        }else {
            result=false;
        }
        return result;
    }


    public Map<String ,BluetoothGatt> getConnectedBluetoothGhatt(){
        return mutlipleBluetooDeviceGhatt;
    }

    public BluetoothDevice getBluetoothDevice_From_BleAddress(String bleAddressForBluetoothDevice){
        BluetoothDevice bluetoothDevice=null;
        if(checkDeviceIsAlreadyConnected(bleAddressForBluetoothDevice)){
            BluetoothDevice deviceFromBluetootAdapter=  mBluetoothAdapter.getRemoteDevice(bleAddressForBluetoothDevice);
            bluetoothDevice=deviceFromBluetootAdapter;
            return bluetoothDevice;
        }else {
            return null;
        }
    }

    /**
     * 133 status error logs
     * BluetoothLeService: onConnectionStateChange: STATUS= 133 NEW STATE= 0
     */

}
