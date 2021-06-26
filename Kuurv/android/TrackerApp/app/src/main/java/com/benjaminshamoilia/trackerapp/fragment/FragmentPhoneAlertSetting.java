package com.benjaminshamoilia.trackerapp.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.interfaces.DeviceDisconnectionCallback;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentPhoneAlertSetting extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;

    @BindView(R.id.frg_phone_alert_sw_silent_mode)
    Switch mSwitchCompatSilentMode;

    @BindView(R.id.frg_phone_alert_sw_separated_alert)
    Switch mSwitchCompatSeparatedAlert;

    @BindView(R.id.frg_phone_alert_sw_repeat_alert)
    Switch mSwitchCompatRepeatAlert;


    @BindView(R.id.frg_phone_alert_rg_buzzer_volume)
    RadioGroup mRadioGroupVolume;

    @BindView(R.id.frg_phone_alert_rb_volume_high)
    RadioButton mRadioButtonVolumeHigh;
    @BindView(R.id.frg_phone_alert_rb_volume_low)
    RadioButton mRadioButtonVolumeLow;


    String bleAddresspassed="";
    String convertBleAddres="";

    String silentmode="-1";
    String buzzervolume="-1";
    String separationAlert="-1";
    String repeatAlert="-1";






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_phone_alert_setting, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_phone_alert);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTextSize(20);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);


        Bundle b = getArguments();
        bleAddresspassed=b.getString("connectedBleAddress");
        convertBleAddres=bleAddresspassed.replace(":", "").toLowerCase();


        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });

        getDetailsfromDB(bleAddresspassed.replace(":", "").toLowerCase());


        //  updateUIinFragment();

        mSwitchCompatSilentMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(checkDeviceConnectionStatusEverytime()){
                    if (isChecked) {
                        silentModeOn();

                    } else {
                        silentModeOff();
                    }
                }else{
                    updateUIFragmentIfDisconnected();
                    mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n change settings.", 3, true);
                }
            }
        });






        mSwitchCompatSilentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  System.out.println("silentmode clicked");
            }
        });

        mSwitchCompatSeparatedAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    separationAlertOn();

                } else {
                    separationAlertoff();

                }
            }
        });


        mSwitchCompatRepeatAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    repeatAlerrton();
                }
                else{
                    repeatAlertOff();
                }
            }
        });


        mRadioButtonVolumeHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkDeviceConnectionStatusEverytime()){
                    updateBuzzerVolumeHighinDB();

                    if(mActivity.getMainActivityBluetoothAdapter()!=null){
                        BluetoothAdapter bluetoothAdapter=mActivity.getMainActivityBluetoothAdapter();
                        if(bluetoothAdapter.isEnabled()){
                            final BluetoothDevice bluetoothDevice= bluetoothAdapter.getRemoteDevice(bleAddresspassed);
                            mActivity.writeBuzzerVolumeHighorLow(true,new BleDevice(bluetoothDevice));
                        }
                    }



                }else {
                    updateUIFragmentIfDisconnected();
                    mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n change settings.", 3, true);
                }


            }
        });
        mRadioButtonVolumeLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDeviceConnectionStatusEverytime()){
                    updateBuzzerVolumeLowinDB();
                    if(mActivity.getMainActivityBluetoothAdapter()!=null){
                        BluetoothAdapter bluetoothAdapter=mActivity.getMainActivityBluetoothAdapter();
                        if(bluetoothAdapter.isEnabled()){
                            final BluetoothDevice bluetoothDevice= bluetoothAdapter.getRemoteDevice(bleAddresspassed);
                            mActivity.writeBuzzerVolumeHighorLow(false,new BleDevice(bluetoothDevice));
                        }
                    }
                }else{
                    updateUIFragmentIfDisconnected();
                    mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n change settings.", 3, true);
                }

            }
        });


        mActivity.setDeviceDisconnectionCallback(new DeviceDisconnectionCallback() {
            @Override
            public void callbackWhenDeviceIsActuallyDisconnected(String BleAddress, String ConnectionStatus, BleDevice bleDevice) {
                mActivity.mUtility.errorDialogWithCallBack_Connect_Disconnect_1(mActivity.getDeviceNameFromDB(bleDevice.getMac().replace(":", "").toLowerCase().toString()) + " has been Disconnected or out of range", 3, false, new onAlertDialogCallBack() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        mActivity.stopPlayingsound();
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
            }
        });

        updateUIinFragment();

        return mViewRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.getDBDeviceList();
        unbinder.unbind();
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mUtility.ShowProgress("Saving Setting", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.mUtility.HideProgress();
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    private void silentModeOn(){

        mSwitchCompatSeparatedAlert.setChecked(false);
        mSwitchCompatRepeatAlert.setChecked(false);

        mSwitchCompatSeparatedAlert.setClickable(false);
        mSwitchCompatRepeatAlert.setClickable(false);

        mSwitchCompatSilentMode.setChecked(true);
        updatesilentmodeOnStatusinDb();
    }




    private void silentModeOff(){

        mSwitchCompatSeparatedAlert.setChecked(true);
        mSwitchCompatRepeatAlert.setChecked(true);
        mSwitchCompatSeparatedAlert.setClickable(true);
        mSwitchCompatRepeatAlert.setClickable(true);
        mSwitchCompatSilentMode.setChecked(false);
        updatesilentmodeOffStatusinDb();
    }

    private void separationAlertoff(){
        mSwitchCompatRepeatAlert.setChecked(false);
        mSwitchCompatRepeatAlert.setClickable(false);
        mSwitchCompatSeparatedAlert.setChecked(false);
        updateSeprationAlertOffStatusinDB();
    }

    private void separationAlertOn(){
        mSwitchCompatRepeatAlert.setClickable(true);
        mSwitchCompatSeparatedAlert.setChecked(true);
        updateSeprationAlertOnStatusinDB();
    }


    private void repeatAlerrton(){
        mSwitchCompatRepeatAlert.setChecked(true);
        updateRepeatAlertOnStatusinDB();
    }

    private void repeatAlertOff(){
        mSwitchCompatRepeatAlert.setChecked(false);
        updateRepeatAlertOffStatusinDB();
    }


    private void buzzerVolumeHigh(){
        mRadioButtonVolumeLow.setChecked(false);
        mRadioButtonVolumeHigh.setChecked(true);
        updateBuzzerVolumeHighinDB();
    }

    private void buzzervolumeLow(){
        mRadioButtonVolumeLow.setChecked(true);
        mRadioButtonVolumeHigh.setChecked(false);
        updateBuzzerVolumeLowinDB();
    }

    private void getDetailsfromDB(String bleaddress){
        Map<String,String> getUSer_info=  mActivity.mDbHelper.getBLE_Set_Info(mActivity.mDbHelper.getReadableDatabase(),
                mActivity.mDbHelper.Device_Table,
                "ble_address",
                bleaddress
        );
        Iterator<Map.Entry<String, String>> itr = getUSer_info.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, String> entry = itr.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            if(entry.getValue()==null)
            {
//                  System.out.println("Fragment_Configure_Device>>>>>Its NUll");
            } else
            {
                silentmode=(String) getUSer_info.get("is_silent_mode");
                buzzervolume=(String) getUSer_info.get("buzzer_volume");
                separationAlert=(String) getUSer_info.get("seperate_alert");
                repeatAlert=(String) getUSer_info.get("repeat_alert");
            }
        }
    }

    private void updateUIinFragment(){
        if(silentmode.equalsIgnoreCase("1")){
            silentModeOff();
        }else{
            silentModeOn();
        }

        //----------silent mode---------------

        if(buzzervolume.equalsIgnoreCase("1")){
            buzzerVolumeHigh();
        }else{
            buzzervolumeLow();
        }
        //---------------Buzzer Volume------------------------

        if(!silentmode.equalsIgnoreCase("0")){
            if(separationAlert.equalsIgnoreCase("0")){
                mSwitchCompatSeparatedAlert.setChecked(true);
            }else{
                mSwitchCompatSeparatedAlert.setChecked(false);
            }

            //---------------Separation Alert------------------------

            if(repeatAlert.equalsIgnoreCase("0")){
                mSwitchCompatRepeatAlert.setChecked(true);
            }else{
                mSwitchCompatRepeatAlert.setChecked(false);
            }
        }


        //----------------Repeat Alert-------------------------------
    }


    private void updatesilentmodeOnStatusinDb(){
        // System.out.println("Alert_Setting updatesilentmodeOnStatusinDb ");
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledSilentMode, "0");                                                                       // getPositionofBleObjectInArrayList
        mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"1" );
        mContentValues.put(mActivity.mDbHelper.DeviceFiledRepeatAlert, "1");    // "_id = ?"
        UpdateRecordinTable(mContentValues);
        sendSilentModeOntoBleDevice("0");
    }

    private void updatesilentmodeOffStatusinDb(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledSilentMode, "1");
        if(mSwitchCompatSeparatedAlert.isChecked()){
            mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"0" );
        }else{
            mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"1" );
        }
        if(mSwitchCompatSeparatedAlert.isChecked()){
            mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"0" );
        }else{
            mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"1" );
        }
        UpdateRecordinTable(mContentValues);
        sendSilentModeOfftoBleDevice("1");
    }


    //Separation Alert part.
    private void updateSeprationAlertOnStatusinDB(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"0" );
        UpdateRecordinTable(mContentValues);
    }
    private void updateSeprationAlertOffStatusinDB(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledSeperateAlert,"1" );
        mContentValues.put(mActivity.mDbHelper.DeviceFiledRepeatAlert,"1" );
        UpdateRecordinTable(mContentValues);
    }

    //--Repeat Alert Part.
    private void updateRepeatAlertOnStatusinDB(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledRepeatAlert,"0" );
        UpdateRecordinTable(mContentValues);
    }
    private void updateRepeatAlertOffStatusinDB(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledRepeatAlert,"1" );
        UpdateRecordinTable(mContentValues);
    }


    //--Buzzer Volume part
    private void updateBuzzerVolumeHighinDB(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledBuzzerVolume,"1" );
        UpdateRecordinTable(mContentValues);
    }
    private void updateBuzzerVolumeLowinDB(){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.DeviceFiledBuzzerVolume,"0" );
        UpdateRecordinTable(mContentValues);
    }

    private void UpdateRecordinTable(ContentValues mContentValues){
        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, "ble_address = ?",new String[]{convertBleAddres});
        String result=  mActivity.mDbHelper.getTableAsString(mActivity.mDbHelper.getReadableDatabase(),"Device_Table");
        System.out.println(result);
    }



    private String silentmodeonDisconnect="-1";
    private String buzzervolumeonDiscoonect="-1";



    private void  updateUIFragmentIfDisconnected(){
        getDetailsfromDBwhenDisconnected(bleAddresspassed.replace(":", "").toLowerCase());
        if(silentmodeonDisconnect.equalsIgnoreCase("1")){
            silentModeOff();
        }else{
            silentModeOn();
        }

        if(buzzervolumeonDiscoonect.equalsIgnoreCase("1")){
            buzzerVolumeHigh();
        }else{
            buzzervolumeLow();
        }
    }

    private void getDetailsfromDBwhenDisconnected(String bleaddress){
        Map<String,String> getUSer_info=  mActivity.mDbHelper.getBLE_Set_Info(mActivity.mDbHelper.getReadableDatabase(),
                mActivity.mDbHelper.Device_Table,
                "ble_address",
                bleaddress
        );
        Iterator<Map.Entry<String, String>> itr = getUSer_info.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, String> entry = itr.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            if(entry.getValue()==null)
            {
//                  System.out.println("Fragment_Configure_Device>>>>>Its NUll");
            } else
            {
                silentmodeonDisconnect=(String) getUSer_info.get("is_silent_mode");
                buzzervolumeonDiscoonect=(String) getUSer_info.get("buzzer_volume");
            }
        }
    }

    private boolean checkDeviceConnectionStatusEverytime(){
        boolean result=false;
        if(mActivity.getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapterAlertSetting=mActivity.getMainActivityBluetoothAdapter();
            if(bluetoothAdapterAlertSetting.isEnabled()){
                boolean connectionStatus = BleManager.getInstance().isConnected(bleAddresspassed);
                if(connectionStatus){
                    result=true;
                }else{
                    result=false;
                }
            }else{
                result=false;
            }

        }else{
            result=false;
        }
        return  result;
    }

    private void sendSilentModeOntoBleDevice(String silentmode){
        if(mActivity.getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=mActivity.getMainActivityBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice bluetoothDevice= bluetoothAdapter.getRemoteDevice(bleAddresspassed);
                mActivity.writeSilentModeOnSwitchisON(new BleDevice(bluetoothDevice));
            }
        }
    }

    private void sendSilentModeOfftoBleDevice(String silentmode){
        if(mActivity.getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=mActivity.getMainActivityBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice bluetoothDevice= bluetoothAdapter.getRemoteDevice(bleAddresspassed);
                mActivity.writeSilentModeOFFSwitchisOFF(new BleDevice(bluetoothDevice));
            }
        }
    }
}



