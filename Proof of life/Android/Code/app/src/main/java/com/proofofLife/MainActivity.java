package com.proofofLife;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.proofofLife.CustomBleObjects.FingerPrintData;
import com.proofofLife.CustomObjects.CustBluetootDevice;
import com.proofofLife.DataBase.RoomDBHelper;
import com.proofofLife.DataBaseRoomDAO.DeviceRegistation_Room;
import com.proofofLife.Fragments.FragmentDevicesAdded;
import com.proofofLife.Fragments.FragmentDevieOwner;
import com.proofofLife.Fragments.FragmentRegistration;
import com.proofofLife.Fragments.FragmentScan;
import com.proofofLife.Fragments.FragmentUserSetting;
import com.proofofLife.InterfaceActivityToFragment.BleScanHelper;
import com.proofofLife.InterfaceActivityToFragment.DataInterface;
import com.proofofLife.InterfaceActivityToFragment.DeviceRegistration;
import com.proofofLife.InterfaceActivityToFragment.PassConnectionStatusToFragment;
import com.proofofLife.InterfaceFragmentToActivity.DeviceConnectDisconnect;
import com.proofofLife.Services.BluetoothLeService;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.AskAuthenicationNumber_Array;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.Authenication_ValuePacket_Array;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.add_verify_delete_DeviceInfo;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.getAllFingerPrints;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.getNextFingerPrintData;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.getPresetTime;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.getSettingArray;
import static com.proofofLife.ArrayPacketCreator.ArrayPacketHelper.index_ScanFingerPrintData;
import static com.proofofLife.BlePacketHelper.PacketCreator.decryptPacket;
import static com.proofofLife.BlePacketHelper.PacketCreator.getCompleteFingerPrintData;
import static com.proofofLife.BlePacketHelper.PacketCreator.getNextPacketFingerPrintData;
import static com.proofofLife.BlePacketHelper.PacketCreator.getSettingPacketForFirmware;
import static com.proofofLife.BlePacketHelper.PacketCreator.get_AuthenicationPacket_Magic_Number;
import static com.proofofLife.BlePacketHelper.PacketCreator.get_ScanningFingerPrintData;
import static com.proofofLife.BlePacketHelper.PacketCreator.get_VerifyDevice_Info;
import static com.proofofLife.BlePacketHelper.PacketCreator.get_delete_user_info;
import static com.proofofLife.BlePacketHelper.PacketCreator.setTimeStampToFirmware;
import static com.proofofLife.BlePacketHelper.PacketCreator.set_AuthenicationPacket_Magic_Number;
import static com.proofofLife.BlePacketHelper.PacketCreator.set_VerifyDevice_Info;
import static com.proofofLife.BleUtility.BleUtilityHelper.ble_on_off;
import static com.proofofLife.ConstUpCode.BLE_UpCode.ADD_DEVICE_INFO;
import static com.proofofLife.ConstUpCode.BLE_UpCode.ALL_FINGER_PRINT_DATA;
import static com.proofofLife.ConstUpCode.BLE_UpCode.CONST_3_DIGIT_NUMBER;
import static com.proofofLife.ConstUpCode.BLE_UpCode.CONST_TIME_STAMP;
import static com.proofofLife.ConstUpCode.BLE_UpCode.DATA_ENDING;
import static com.proofofLife.ConstUpCode.BLE_UpCode.DATA_STARTING;
import static com.proofofLife.ConstUpCode.BLE_UpCode.DELETE_DEVIE_INFO;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FAILURE;
import static com.proofofLife.ConstUpCode.BLE_UpCode.READ_AUTHNECIATION;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SCAN_FINGER_PRINT_DATA;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SET_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SUCESS;
import static com.proofofLife.ConstUpCode.BLE_UpCode.TENTH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.UNKNOWN;
import static com.proofofLife.ConstUpCode.BLE_UpCode.USER_SETTING;
import static com.proofofLife.ConstUpCode.BLE_UpCode.VERIFY_DEVICE_INFO;
import static com.proofofLife.ConstUpCode.BLE_UpCode.WRITE_AUTHNECIATION;
import static com.proofofLife.DataBase.RoomDBHelper.getRoomDBInstance;
import static com.proofofLife.UtilitConversion.ConversionHelper.getHexStringFromByteArray;
import static com.proofofLife.UtilitConversion.ConversionHelper.getMinValueFromTimeStamp;

public class MainActivity extends AppCompatActivity implements
        DeviceConnectDisconnect {

    /**
     * Room DataBase
     */
    public static RoomDBHelper roomDBHelperInstance;

    public static BluetoothLeService mBluetoothLeService;
    private BleScanHelper bleScanHelperInterface;
    private DataInterface dataInterface;
    FragmentTransaction fragmentTransction;
    /**
     * Scan Part.
     */
    private BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private Handler handler = new Handler();
    public static String SCAN_TAG = "";
    private static final long SCAN_PERIOD = 30000;

    private static final String TAG = MainActivity.class.getSimpleName() + " " + MainActivity.class.getPackage().getName();
    public static final String COMMMON_TAG = "";
    List<FingerPrintData> fingerPrintDataList;

    /**
     * Activity to Fragment
     */
    // OwnerInfo ownerInfo;
    PassConnectionStatusToFragment passConnectionStatusToFragment;
    DeviceRegistration deviceRegistrationInterface;
    public static UIHelper uiHelper;
    AlertDialog.Builder builder;

    /**
     * Fragemtnt to Activity
     */

    /**
     * Exit window
     */
    private boolean exit = false;

    /**
     * Bottom Navigation View
     */
    public  BottomNavigationView bottomNavigationView;

    /**
     * Finger Print Generation parameters.
     */
    StringBuffer completeFingerPrintData;

    public static String bleAddressInCommunication_MainActivity="";
    public static String tokenNumber_MainActivity="";
    List<DeviceRegistation_Room> deviceRegistation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intializeUiHelper();
        intializeRoomDataBaseInstance();
        bindBleServiceToMainActivity();
        interfaceImpleMainActivity();
        checkRecords();
        bottomNavigationOnClickListner();
        interfaceImplementation_MainActivity();
       replaceFragment(new FragmentScan(), null);
     //   replaceFragment(new FragmentUserSetting(), null);
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetootServiceRecieverData, makeGattUpdateIntentFilter());
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetootServiceRecieverData);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_Container);
        if (fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
            if (exit) {
                showExitDialog(getString(R.string.str_exit_confirmation));
            } else {
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 2000);
            }
        } else if (fragment.toString().equalsIgnoreCase(new FragmentRegistration().toString())) {
            replaceFragment(new FragmentScan(), null);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentDevieOwner().toString())) {
//            replaceFragment(new FragmentScan(), null);
            replaceFragment(new FragmentDevicesAdded(), null);
        }else if(fragment.toString().equalsIgnoreCase(new FragmentUserSetting().toString())){
            replaceFragment(new FragmentDevieOwner(),null);
        }else if(fragment.toString().equalsIgnoreCase(new FragmentDevicesAdded().toString())){
            replaceFragment(new FragmentScan(), null);
        }

    }


    private void showExitDialog(String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Exit");
        builder.setCancelable(false);
        builder.setMessage(dialogMessage);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ble_on_off()) {
                    Map<String, BluetoothGatt> instanceOFBluetoothGhatt = mBluetoothLeService.getConnectedBluetoothGhatt();
                    for (Map.Entry<String, BluetoothGatt> entry : instanceOFBluetoothGhatt.entrySet()) {
                        if (entry.getValue() != null) {
                            BluetoothGatt ghatt = entry.getValue();
                            ghatt.disconnect();
                            ghatt.close();
                            ghatt = null;
                        }
                    }
                }
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void intializeUiHelper() {
        uiHelper = new UIHelper(this);
        builder = new AlertDialog.Builder(this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id_botttomViewNavigation);
    }

    public void bottoNavigationVisibility(boolean invisible_true_false) {
        if (invisible_true_false) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    private void interfaceImplementation_MainActivity() {
        setUpbeScanHelperInterface_Intialization(new BleScanHelper() {
            @Override
            public void scanDevices(CustBluetootDevice custBluetootDevice, String TAG) {

            }

            @Override
            public void scanStarted(String TAG) {

            }

            @Override
            public void scanStoped(String TAG) {

            }
        });
        setupPassConnectionStatusToFragment(new PassConnectionStatusToFragment() {
            @Override
            public void connectDisconnect(String bleAddress, boolean connected_disconnected) {

            }
        });

        setUpbledataInterface(new DataInterface() {
            @Override
            public void sendData(String data) {

            }

            @Override
            public void ownerInfo(byte data) {

            }
        });
       /* setUpOwnerInfo(new OwnerInfo() {
            @Override
            public void deviceOwnerInfo(byte owerInfo) {

            }
        });*/
        setUpDeviceRegistration(new DeviceRegistration() {
            @Override
            public void registrationDetails(byte[] registrationDeviceData) {

            }
        });

    }

    public void interfaceImpleMainActivity() {
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(hexStringToByteArray(input));
    }

    public static byte[] getSHAUsingText(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Fragment Transaction
     */
    public void replaceFragment(Fragment fragment, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransction = fragmentManager.beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
            fragmentTransction.replace(R.id.mainActivity_Container, fragment, fragment.toString());
            fragmentTransction.commit();
        } else {
            fragmentTransction.replace(R.id.mainActivity_Container, fragment, fragment.toString());
            fragmentTransction.commit();
        }
    }

    /**
     * Ble part
     */
    private void bindBleServiceToMainActivity() {
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE));
        return intentFilter;
    }

    private final BroadcastReceiver bluetootServiceRecieverData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS)))) {
                String bleAddress = intent.getStringExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_BLE_ADDRESS)));
                boolean connectionStatus = intent.getBooleanExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_CONNECTED_DISCONNECTED), false);
                passConnectionSucesstoFragmentScanForUIChange(bleAddress, connectionStatus);
            } else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE)))) {
                String bleAddress = intent.getStringExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE_BLE_AADRESS)));
                boolean enable_notification = intent.getBooleanExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE_DATA)), false);
                /**
                 * Ask firmware to send Magic Number
                 */
                mBluetoothLeService.sendDataToBleDevice(bleAddress, get_AuthenicationPacket_Magic_Number(READ_AUTHNECIATION, (byte) 0x01, AskAuthenicationNumber_Array(), false));
                Log.d(TAG, "onReceive: asking authenication");
            } else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION)))) {
                String bleAddress = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_ADDRESS));
                byte[] dataWritten = intent.getByteArrayExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN));
                int dataWrittenType = intent.getIntExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN_TYPE), -1);
                int status = intent.getIntExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_STATUS), -1);
            } else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED)))) {
                String bleAddress_to_WriteData = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_BLE_ADDRESS));
                byte[] obtained_byteArray_from_Fimware = intent.getByteArrayExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_DATA_RECIEVED));

                if (obtained_byteArray_from_Fimware != null) {
                    if (obtained_byteArray_from_Fimware.length > 4) {
                        /***
                         * Its encrypted Data.
                         */
                        Log.d(TAG, "Encrypted Data_From_Firmware= " + getHexStringFromByteArray(obtained_byteArray_from_Fimware, true));

                        byte[] complete_data_array = decryptPacket(obtained_byteArray_from_Fimware);// incluedes upcode and length.
                        byte upcode = complete_data_array[0];//Upcode
                        byte length = complete_data_array[1];// Length
                        Log.d(TAG, "Decrypt_Data_From_Firmware= " + getHexStringFromByteArray(complete_data_array, true));
                        byte[] dataArray = new byte[16];// Contains only data.
                        dataArray = Arrays.copyOfRange(complete_data_array, DATA_STARTING, DATA_ENDING);

                        switch (upcode) {
                            case VERIFY_DEVICE_INFO: {
                                byte deviceInfo_Registration = dataArray[0];
                                switch (deviceInfo_Registration) {
                                    case SUCESS: {

                                        /**
                                         * Ur the owner of the Device.
                                         */
                                        Log.d(TAG, "onReceive: UR THE OWNER OF THE DEVICE ");
                                        uiHelper.hide_progressDialog();

                                        bleAddressInCommunication_MainActivity=bleAddress_to_WriteData;
                                    /*    Bundle bundle = new Bundle();
                                        bundle.putString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS), bleAddress);*/
                                        replaceFragment(new FragmentDevieOwner(), null);
//


//                                        dialgoShow(bleAddress_to_WriteData, getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_OWNWER), getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_OWNWER), VERIFY_DEVICE_INFO, SUCESS);
                                        break;
                                    }
                                    case FAILURE: {
                                        /**
                                         * Fresh Device..
                                         */
                                        Log.d(TAG, "onReceive: FRESH DEVICE ");
                                        uiHelper.hide_progressDialog();
                                        dialgoShow(bleAddress_to_WriteData, getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_FRESH_DEVICE), getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_FRESH_DEVICE), VERIFY_DEVICE_INFO, FAILURE);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        /**
                                         * some one is the owner of the deivce.
                                         */
                                        Log.d(TAG, "onReceive: SOME ONE IS THE OWNER ");
                                        uiHelper.hide_progressDialog();
                                        dialgoShow(bleAddress_to_WriteData, getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_BELONGS_TO_SOMEONE), getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_BELONGS_TO_SOMEONE), VERIFY_DEVICE_INFO, UNKNOWN);
                                        break;
                                    }

                                }
                                break;
                            }
                            case SCAN_FINGER_PRINT_DATA: {
                                if (deviceRegistrationInterface != null) {
                                    deviceRegistrationInterface.registrationDetails(complete_data_array);
                                    /**
                                     * Change it to the 10 th count after final release..
                                     */
                                    byte fingerPrintIndex = complete_data_array[2];
                                    switch (fingerPrintIndex) {
                                        case TENTH_TIME: { // change to TENTH_TIME
                                            Log.d(TAG, "onReceive: Create New Finger Print Data Object List ");
                                            fingerPrintDataList = new ArrayList<FingerPrintData>();
                                            break;
                                        }
                                    }

                                }
                                break;
                            }
                            case ALL_FINGER_PRINT_DATA: {
                                byte[] fingerPrintCompleted = new byte[16];
                                for (int i = 0; i < fingerPrintCompleted.length; i++) {
                                    fingerPrintCompleted[i] = (byte) 0xff;
                                }
                                if ((length == 0x10) && (Arrays.equals(fingerPrintCompleted, dataArray))) {
                                    addDeviceInfo(bleAddress_to_WriteData);
                                    return;
                                }

                                byte indexRecieved = dataArray[0];
                                Log.d(TAG, "FingerPrintData_All " + getHexStringFromByteArray(dataArray, true) + " Index= " + Byte.toUnsignedInt(indexRecieved) + " Length= " + Byte.toUnsignedInt(length));
                                mBluetoothLeService.sendDataToBleDevice(bleAddress_to_WriteData, getNextPacketFingerPrintData(ALL_FINGER_PRINT_DATA, (byte) 0x01, getNextFingerPrintData(indexRecieved), true));
                                byte dataArrayContainingFingerPrint[] = Arrays.copyOfRange(dataArray, 1, Byte.toUnsignedInt(length));
                                Log.d(TAG, "FingerPrintData_only " + getHexStringFromByteArray(dataArrayContainingFingerPrint, true) + " ");
                                fingerPrintDataList.add(new FingerPrintData(Byte.toUnsignedInt(indexRecieved), getHexStringFromByteArray(dataArrayContainingFingerPrint, false)));
                                if (indexRecieved == 0x00) {
                                    completeFingerPrintData = new StringBuffer();
                                    /**
                                     * Finger packet ends.
                                     * Start Processing
                                     */
                                    for (FingerPrintData fingerPrintData : fingerPrintDataList) {
                                        completeFingerPrintData = completeFingerPrintData.append(fingerPrintData.getHexFingerPrintData());
                                    }
                                    insertFingerPrintRecordToTbale(bleAddress_to_WriteData, completeFingerPrintData.toString());
                                }

                                break;
                            }
                            case ADD_DEVICE_INFO: {
                                byte deviceAddedACK = dataArray[0];
                                switch (deviceAddedACK) {
                                    case SUCESS: {
                                       /* uiHelper.hide_progressDialog();
                                        registrationCompleteDialog("Registration Sucess", "Change Setting ", bleAddress_to_WriteData);*/
                                        break;
                                    }
                                    case FAILURE: {
                                        uiHelper.hide_progressDialog();
                                        Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                        uiHelper.hide_progressDialog();
                                        break;
                                    }
                                }
                                break;
                            }
                            case DELETE_DEVIE_INFO: {

                                byte deletedeviceAck = dataArray[0];
                                switch (deletedeviceAck) {
                                    case SUCESS: {
                                        Toast.makeText(getApplicationContext(), "Device Deleted SucessFuly", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case FAILURE: {
                                        Toast.makeText(getApplicationContext(), "Operation Failed", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                break;
                            }
                            case USER_SETTING:{
                                byte settingDeviceAck = dataArray[0];
                                switch (settingDeviceAck){
                                    case SUCESS:{
                                     //   addDeviceInfo(bleAddress_to_WriteData);
                                        sendDeviceInfoKeyViaBleToFirmware(bleAddress_to_WriteData,secretKeyFromFragmentUserSetting);
                                        settingSaved("Sucess","Saved Sucessfully", bleAddress_to_WriteData);
                                        break;
                                    }
                                    case FAILURE:{
                                        settingSaved("Failure","Try Again",bleAddress_to_WriteData);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    } else if (obtained_byteArray_from_Fimware.length <= 4) {
                        /**
                         * Data is not encrypted.
                         */
                        Log.d(TAG, "Firmware Data No Encryption= " + getHexStringFromByteArray(obtained_byteArray_from_Fimware, false));
                        byte UPCODE = obtained_byteArray_from_Fimware[0];
                        switch (UPCODE) {
                            case READ_AUTHNECIATION: {
                                int m_auth_key = Byte.toUnsignedInt(obtained_byteArray_from_Fimware[2]);
                                Log.d(TAG, "Magic Number Obtained= " + m_auth_key);
                                Log.d(TAG, "hex Value Written= " + getHexStringFromByteArray(set_AuthenicationPacket_Magic_Number(WRITE_AUTHNECIATION, (byte) 0x04, Authenication_ValuePacket_Array(m_auth_key), false), true));
                                mBluetoothLeService.sendDataToBleDevice(bleAddress_to_WriteData, set_AuthenicationPacket_Magic_Number(WRITE_AUTHNECIATION, (byte) 0x04, Authenication_ValuePacket_Array(m_auth_key), false));
                                break;
                            }
                            case WRITE_AUTHNECIATION: {
                                byte sucess_failure = obtained_byteArray_from_Fimware[2];
                                switch (sucess_failure) {
                                    case SUCESS: {
                                        getSecretKeyFromDatabase(bleAddress_to_WriteData);
                                        //Log.d(TAG, "PACEKT_SENT VERIFY_DEVICE_INFO "+getHexStringFromByteArray(get_VerifyDevice_Info(VERIFY_DEVICE_INFO,verifyDeviceInfo(CONST_TIME_STAMP,CONST_3_DIGIT_NUMBER),false),false));
                                       /*  String secretKey=  getSecretKeyFromDatabase(bleAddress_to_WriteData);
                                         if((secretKey!=null)&&(secretKey.length()>0)){
                                             Log.d(TAG, "onReceive: Secret Key Obtained "+secretKey);
                                             mBluetoothLeService.sendDataToBleDevice(bleAddress_to_WriteData, get_VerifyDevice_Info(VERIFY_DEVICE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(secretKey), true));
                                         }else {
                                             Log.d(TAG, "onReceive: Secret Key not Obtained "+secretKey);
                                             mBluetoothLeService.sendDataToBleDevice(bleAddress_to_WriteData, get_VerifyDevice_Info(VERIFY_DEVICE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(CONST_TIME_STAMP+CONST_3_DIGIT_NUMBER), true));
                                         }*/
                                        break;
                                    }
                                    case FAILURE: {
                                        Log.d(TAG, "Autenication_FAILURE");
                                        break;
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }


        private void registrationCompleteDialog(String topic, String subtopic, String bleAddress) {
            builder.setTitle(topic);
            builder.setMessage(subtopic)
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            bleAddressInCommunication_MainActivity=bleAddress;
                            replaceFragment(new FragmentUserSetting(), null);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        private void passConnectionSucesstoFragmentScanForUIChange(String bleAddress, boolean connectionStatus) {

            if (passConnectionStatusToFragment != null) {
                passConnectionStatusToFragment.connectDisconnect(bleAddress, connectionStatus);
            }
            /**
             * For De-bugging Remove later..used for LoopBack checking DFU.
             */


        }
    };

    public void insertFingerPrintRecordToTbale(String bleAddress, String fingerPintData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!roomDBHelperInstance.get_deviceRegistration_dao().isRecordAvaliableForBleAddress(bleAddress.toUpperCase().replace(":", ""))){
                    roomDBHelperInstance.get_deviceRegistration_dao().insert_ActionInfo(new DeviceRegistation_Room(bleAddress.toUpperCase().replace(":", ""), fingerPintData,bleAddress));
                }else {
                    roomDBHelperInstance.get_deviceRegistration_dao().setfingerPrintData(fingerPintData,bleAddress.toUpperCase().replace(":", ""));
                }
            }
        }).start();
    }

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (result != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_Container);
                                if (fragment != null) {
                                    if (fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
                                        if ((result.getDevice().getName() != null) && (result.getDevice().getName().length() > 0) && (result.getDevice().getName().equalsIgnoreCase(getResources().getString(R.string.FIRMWARE_DEVICE_NAME))) && (result.getDevice() != null)) {
                                            if (bleScanHelperInterface != null) {
                                                bleScanHelperInterface.scanDevices(new CustBluetootDevice(result.getDevice().getAddress(), result.getDevice().getName(), result.getDevice(), false), SCAN_TAG);
                                            }
                                        }
                                    }
                                }

                            }
                        });
                    }
                }
            };


    private void startScan() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (SCAN_TAG.equalsIgnoreCase("") || SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STOPED))) {
                    SCAN_TAG = getResources().getString(R.string.SCAN_STARTED);
                    bluetoothLeScanner.startScan(leScanCallback);
                    //bluetoothLeScanner.startScan(null,null,leScanCallback);
                    bleScanHelperInterface.scanStarted(SCAN_TAG);
                }

            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_PERIOD);
    }

    private void stopScan() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                    SCAN_TAG = getResources().getString(R.string.SCAN_STOPED);
                    bluetoothLeScanner.stopScan(leScanCallback);
                    bleScanHelperInterface.scanStoped(SCAN_TAG);
                }

            }
        });
    }

    public void start_stop_scan() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ble_on_off()) {
                if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STOPED)) || (SCAN_TAG.equalsIgnoreCase(""))) {
                    startScan();
                    Log.d(TAG, "start_stop_scan: start scan ");
                } else if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                    /**
                     * Scan already started.
                     */
                    Log.d(TAG, "start_stop_scan: scan already started ");
                }
            }
        }

    }

    public void setUpbeScanHelperInterface_Intialization(BleScanHelper bleScanHelper_loc) {
        bleScanHelperInterface = bleScanHelper_loc;
    }

    public void setUpbledataInterface(DataInterface loc_dataInterface) {
        dataInterface = loc_dataInterface;
    }


    public void setupPassConnectionStatusToFragment(PassConnectionStatusToFragment locpassConnectionStatusToFragment) {
        this.passConnectionStatusToFragment = locpassConnectionStatusToFragment;
    }

    public void setUpDeviceRegistration(DeviceRegistration deviceRegistration_loc) {
        this.deviceRegistrationInterface = deviceRegistration_loc;
    }




    @Override
    public void makeDevieConnecteDisconnect(CustBluetootDevice custBluetootDevices, boolean connect_disconnect) {
        if (connect_disconnect) {
            boolean connectissue = mBluetoothLeService.connect(custBluetootDevices.getBleAddress());
            uiHelper.show_Progress_dialog(getResources().getString(R.string.PROGRESS_DIALOG_TITLE), "");
            if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                stopScan();
            }
        } else {
            mBluetoothLeService.disconnect(custBluetootDevices.getBleAddress());
        }

    }

    public void sendDeviceInfoKeyViaBleToFirmware(String bleAddress,String secretKey){
        mBluetoothLeService.sendDataToBleDevice(bleAddress, set_VerifyDevice_Info(ADD_DEVICE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(secretKey), true));
    }

    public  void addDeviceInfo(String bleAddress) {
        int randomNNumber = ThreadLocalRandom.current().nextInt(100, 1000);
        String timeInMilliSeconds=""+System.currentTimeMillis();
        if(ble_on_off()){
            if(mBluetoothLeService.checkDeviceIsAlreadyConnected(bleAddress)){
               new Thread(new Runnable() {
                    @Override
                    public void run() {
                        roomDBHelperInstance.get_deviceRegistration_dao().setSecretKey(timeInMilliSeconds+""+randomNNumber,bleAddress.toUpperCase().replace(":", ""));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uiHelper.hide_progressDialog();
                                bleAddressInCommunication_MainActivity=bleAddress;
                                replaceFragment(new FragmentUserSetting(), null);
                            }
                        });
                    }
                }).start();
            }else {
                Toast.makeText(getApplicationContext(),"Device is Disconnected",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Bluetooth Is OFF",Toast.LENGTH_SHORT).show();
        }
    }

    private String secretKeyFromFragmentUserSetting="";
    public void firmwareDeviceSetting(String bleAddress,int otpValidity,int otpDisplayTime,int fingerPrintMatchType,int polAuthCycle,String secretKey){
        if(ble_on_off()){
            if(mBluetoothLeService.checkDeviceIsAlreadyConnected(bleAddress)){
                secretKeyFromFragmentUserSetting="";
                secretKeyFromFragmentUserSetting=secretKey;
                mBluetoothLeService.sendDataToBleDevice(bleAddress, getSettingPacketForFirmware(USER_SETTING, (byte) 0X10, getSettingArray(otpValidity,otpDisplayTime,fingerPrintMatchType,polAuthCycle) ,true));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        roomDBHelperInstance.get_deviceRegistration_dao().updateSetting(""+otpValidity,""+otpDisplayTime,""+polAuthCycle,""+fingerPrintMatchType,bleAddress.toUpperCase().replace(":", ""));
                    }
                }).start();
            }else {
                Toast.makeText(getApplicationContext(),"Device is Disconnected",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Bluetooth Is OFF",Toast.LENGTH_SHORT).show();
        }
    }


    public static void deleteuserInfo(String bleAddress,String secretKey) {
        mBluetoothLeService.sendDataToBleDevice(bleAddress, get_delete_user_info(DELETE_DEVIE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(secretKey), true));
        new Thread(new Runnable() {
            @Override
            public void run() {

                roomDBHelperInstance.get_deviceRegistration_dao().deleteByBleAddress(bleAddress.toUpperCase().replace(":", ""));
            }
        }).start();
    }

    public static void startFingerPrintScannig(byte indexNumber, String bleAddress) {
        mBluetoothLeService.sendDataToBleDevice(bleAddress, get_ScanningFingerPrintData(SCAN_FINGER_PRINT_DATA, (byte) 0x01, index_ScanFingerPrintData(indexNumber), true));
        Log.d(TAG, "startFingerPrintScannig: indexNumber= " + indexNumber+" BleAddress= "+bleAddress);
    }

    public static void fetchAllFingerPrintData(String bleAddress) {
        mBluetoothLeService.sendDataToBleDevice(bleAddress, getCompleteFingerPrintData(ALL_FINGER_PRINT_DATA, (byte) 0x01, getAllFingerPrints(), true));
        uiHelper.show_Progress_dialog("Registrering...", "Please wait");
    }

    public  void sendTimeStamp(String bleAddress, int timeStamp) {
        if(ble_on_off()){
            if(mBluetoothLeService.checkDeviceIsAlreadyConnected(bleAddress)){
                mBluetoothLeService.sendDataToBleDevice(bleAddress, setTimeStampToFirmware(SET_TIME, (byte) 0x10, getPresetTime(timeStamp), true));
            }else {
                Toast.makeText(getApplicationContext(),"Device is DisConnected ",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Device is DisConnected ",Toast.LENGTH_SHORT).show();
        }

    }





/*    private void open_CommmnonDialogFragment(byte upcCode, byte ackresponse, String bleAddress) {
        CommonDialog dialogFragment = new CommonDialog(upcCode, ackresponse);
        fragmentTransction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(CommonDialog.class.getSimpleName());
        if (prev != null) {
            fragmentTransction.remove(prev);
        }
        fragmentTransction.addToBackStack(null);
        dialogFragment.show(fragmentTransction, CommonDialog.class.getSimpleName());
        dialogFragment.setUpDialogListner(new CommonDialog.CommonDialogFragmentListner() {
            @Override
            public void positiveButton(byte upCode_loc, byte ack_loc) {
                byte upcode = upCode_loc;
                byte ack = ack_loc;
                switch (upcode) {
                    case VERIFY_DEVICE_INFO: {
                        switch (ack) {
                            case SUCESS: {
                                Bundle bundle = new Bundle();
                                bundle.putString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS), bleAddress);
                                replaceFragment(new FragmentDevieOwner(), bundle);
                                break;
                            }
                            case FAILURE: {
                                replaceFragment(new Registration_Fragment(), null);
                                break;
                            }
                            case UNKNOWN: {
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void cancelButton(byte upCode, byte ack) {
                Log.d(TAG, "cancelButton: Upcode " + upcCode + " ack =" + ack);
            }
        });
    }*/

    private void intializeRoomDataBaseInstance() {
        roomDBHelperInstance = getRoomDBInstance(this);
    }

    private void checkRecords() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DeviceRegistation_Room> deviceRegistation = roomDBHelperInstance.get_deviceRegistration_dao().getDeviceInfo();
                int size = deviceRegistation.size();
                System.out.println("-------------------->" + size);
            }
        }).start();
    }

    private void dialgoShow(String bleAddress, String topic, String subTopic, byte... value) {
        byte upcode = value[0];
        byte ack = value[1];
        builder.setTitle(topic);
        builder.setMessage(subTopic)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (upcode) {
                            case VERIFY_DEVICE_INFO: {
                                switch (ack) {
                                    case SUCESS: {
                                        bleAddressInCommunication_MainActivity=bleAddress;
                                    /*    Bundle bundle = new Bundle();
                                        bundle.putString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS), bleAddress);*/
                                      replaceFragment(new FragmentDevieOwner(), null);
//                                        replaceFragment(new FragmentUserSetting(), null);
                                        break;
                                    }
                                    case FAILURE: {
                                        /**
                                         * Fresh Device its 0x00
                                         */
                                        bleAddressInCommunication_MainActivity=bleAddress;
                                       /* Bundle bundle = new Bundle();
                                        bundle.putString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS), bleAddress);*/
                                        replaceFragment(new FragmentRegistration(), null);
                                        //  replaceFragment(new FragmentDevieOwner(), bundle);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        /**
                                         * Remove it.
                                         * Used for De-Buggin purpose only..
                                         */
                                        /*Bundle bundle = new Bundle();
                                        bundle.putString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS), bleAddress);
                                        replaceFragment(new FragmentDevieOwner(), bundle);*/

                                        break;
                                    }
                                }
                                break;
                            }
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void noAcccountAdded(String topic, String subtopic) {
        builder.setTitle(topic);
        builder.setMessage(subtopic)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void bottomNavigationOnClickListner() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.id_home: {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_Container);
                        if (!fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
                            bottomNavigationView.setSelected(true);
                            replaceFragment(new FragmentScan(), null);
                        }
                        return true;
                    }
                    case R.id.id_account: {
                        bottomNavigationView.setSelected(true);
                        replaceFragment(new FragmentDevicesAdded(), null);
                       /* new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<DeviceRegistation_Room> deviceRegistation = roomDBHelperInstance.get_deviceRegistration_dao().getDeviceInfo();
                                int size = deviceRegistation.size();
                                if (size <= 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                          //  noAcccountAdded("Account InActive", "Device Registration Pending !");
                                            bottomNavigationView.setSelected(false);

                                            return;
                                        }
                                    });

                                } else {
                                    bottomNavigationView.setSelected(true);
                                    replaceFragment(new FragmentDevicesAdded(), null);
                                }
                            }
                        }).start();*/
                        return true;
                    }
                }

                return false;
            }
        });
    }

    public static String generateOTP(String fingerPrintSecurityKey, String fingerPrintData, String hextimeStamp) {
        String OTP = "";
        String hexSecuritKey=convertSecretKeytoHex(fingerPrintSecurityKey);
        String minValueFromTimeStamp = getMinValueFromTimeStamp(hextimeStamp);
        String totoalData = hexSecuritKey + fingerPrintData + hextimeStamp;
        Log.d(TAG, "FW_DEBUG  fingerPrintSecurity Key = " + hexSecuritKey);
        Log.d(TAG, "FW_DEBUG  fingerPrintData Key = " + fingerPrintData);
        Log.d(TAG, "FW_DEBUG   hexTimeStamp = " + hextimeStamp);
        try {
            byte[] validData = getSHA(totoalData);
            byte[] consideredData = {validData[2], validData[1], validData[0]};
//             OTP= getHexStringFromByteArray(consideredData,false)+minValueFromTimeStamp;
            String hexStringFingerPrintData = getHexStringFromByteArray(consideredData, false);

            int otpNumber = Integer.parseInt(hexStringFingerPrintData, 16) % 1000000;
            otpNumber = (otpNumber * 100) + Integer.parseInt(minValueFromTimeStamp);
            OTP = String.format("%06d", otpNumber);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return OTP;
    }

    /**
     *
     * Use ing this method to change from String Value to HexValue.
     */

    public static String convertSecretKeytoHex(String secretKey){
        StringBuffer sb = new StringBuffer();
        char ch[] = secretKey.toCharArray();
        for(int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        return result;
    }




    /*public void verifyDeviceInfo(String bleAddressToCommunicate) {
        mBluetoothLeService.sendDataToBleDevice(bleAddressToCommunicate, get_VerifyDevice_Info(VERIFY_DEVICE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(CONST_TIME_STAMP, CONST_3_DIGIT_NUMBER), true));
    }*/

    private void settingSaved(String topic, String subtopic,String bleaddress) {
        bleAddressInCommunication_MainActivity=bleaddress;
        builder.setTitle(topic);
        builder.setMessage(subtopic)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        replaceFragment(new FragmentDevieOwner(),null);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private String getSecretKeyFromDatabase(String bleAddress){
        final String[] secretKey = {""};
        new Thread(new Runnable() {
            @Override
            public void run() {
                secretKey[0] =roomDBHelperInstance.get_deviceRegistration_dao().getSecretKeyFromDataBase(bleAddress.toUpperCase().replace(":",""));
                if((secretKey[0]!=null)){
                    if(secretKey[0].length()>0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBluetoothLeService.sendDataToBleDevice(bleAddress, get_VerifyDevice_Info(VERIFY_DEVICE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(secretKey[0]), true));
                            }
                        });
                    }

                }else {
                    mBluetoothLeService.sendDataToBleDevice(bleAddress, get_VerifyDevice_Info(VERIFY_DEVICE_INFO, (byte) 0x10, add_verify_delete_DeviceInfo(CONST_TIME_STAMP+CONST_3_DIGIT_NUMBER), true));

                }
                Log.d(TAG, "DataBase Secret Key : "+secretKey[0]);
            }
        }).start();
        return secretKey[0];
    }
}