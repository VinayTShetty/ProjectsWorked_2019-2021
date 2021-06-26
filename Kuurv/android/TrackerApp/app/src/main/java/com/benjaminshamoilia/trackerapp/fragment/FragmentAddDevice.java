package com.benjaminshamoilia.trackerapp.fragment;

import android.Manifest;
import android.app.Dialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.*;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.helper.BLEUtility;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.PassScannedDevices;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.benjaminshamoilia.trackerapp.helper.BLEUtility.hexToDecimal;
import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentAddDevice extends Fragment {
    MainActivity mActivity;

    View mViewRoot;
    private Unbinder unbinder;

    @BindView(R.id.frg_add_device_rv)
    RecyclerView mRecyclerView;


    @BindView(R.id.frg_add_device_ll_scanning)
    LinearLayout mLinearLayoutScanning;
    @BindView(R.id.fragment_setting_bridge_conn_swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    DeviceListAdapter mDeviceListAdapter;


    private PreferenceHelper mPreferenceHelper;
    private API mApiService;
    Utility mUtility;
    private Retrofit mRetrofit;


    String email;
    String emailPart2;
    String mobileNumber;
    String ownername;



    private boolean fragmentAddDeviceShowing=false;

    /**
     * Timer to check the DeviceConnection timer.
     * @param savedInstanceState
     */
    private DeviceConnectionTimer mStartDeviceConnectionTimer;

    /**
     * Timer used to Check the DeviceConfiguration is sucess if fails means disconnect the device and show dialog.
     */
    private boolean deviceConfigurationSucess=false;
    ArrayList<RxBleDevice> mArrayListAddDeviceScannedDevices=new ArrayList();

    private boolean deviceDisconnectingInAdddeviceFragment=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_add_device, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_add_device);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.VISIBLE);
        mActivity.mImageViewAddDevice.setImageResource(R.drawable.ic_scan);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDeviceListAdapter = new DeviceListAdapter();
        mRecyclerView.setAdapter(mDeviceListAdapter);



        /**
         * Here calling the Crush Logout for Add Device new Requirements
         */
        mPreferenceHelper = new PreferenceHelper(getActivity());
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mActivity.mUtility.getClientWithAutho())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = mRetrofit.create(API.class);

        if (mActivity.mUtility.haveInternet()) {
            mActivity.APICrushLogout();
        }

        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceDisconnectingInAdddeviceFragment=false;
                mActivity.onBackPressed();
            }
        });
        mActivity.mImageViewAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(getFragmentAddDeviceBluetoothAdapter()!=null){
                            BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                            if(fragmentHomeBluetoothadapter.isEnabled()){
                                clearScannedDevices();
                                layoutVisibility();
                                AddScanResultsFromMainActivity();

                            }else{
                                mActivity.giveCustomToastBluetoothNotEnabled();
                            }
                        }else{
                            mActivity.giveCustomToastBluetoothNotEnabled();
                        }
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                if (isAdded()) {
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(getFragmentAddDeviceBluetoothAdapter()!=null){
                            BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                            if(fragmentHomeBluetoothadapter.isEnabled()){
                                if(mArrayListAddDeviceScannedDevices.size()>0){
                                    clearScannedDevices();
                                    layoutVisibility();
                                    AddScanResultsFromMainActivity();
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }

                }
            }
        });

        fragmentAddDeviceLocationPermission();

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(getFragmentAddDeviceBluetoothAdapter()!=null){
                BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                if(fragmentHomeBluetoothadapter.isEnabled()){
                    clearScannedDevices();
                    AddScanResultsFromMainActivity();
                }else{
                    mActivity.giveCustomToastBluetoothNotEnabled();
                    layoutVisibility();;
                }

            }else{
                mActivity.giveCustomToastBluetoothNotEnabled();
                layoutVisibility();
            }

        }
        /**
         * DeviceconnectionTimer
         */
        mStartDeviceConnectionTimer = new DeviceConnectionTimer(20000, 1000);
        return mViewRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.scanningProgressBar.setVisibility(View.INVISIBLE);
        fragmentAddDeviceShowing=false;
        deviceDisconnectingInAdddeviceFragment=false;
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setImageResource(R.drawable.ic_dots_vertical);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        fragmentAddDeviceDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentAddDeviceShowing=true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private String macAddressGoingforConnection="";
    /*Device list adapter*/
    public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_add_device_list_item, parent, false);
            return new DeviceListAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mTextViewDeviceBleAddress.setText(mArrayListAddDeviceScannedDevices.get(position).getMacAddress());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (isAdded())
                    {

                        if(getFragmentAddDeviceBluetoothAdapter()==null){
                            mActivity.giveCustomToastBluetoothNotEnabled();
                            return;
                        }
                        deviceDisconnectingInAdddeviceFragment=true;
                        if (mActivity.mDbHelper.CheckRecordAvaliableinDB(mActivity.mDbHelper.Device_Table, mActivity.mDbHelper.ble_address, mArrayListAddDeviceScannedDevices.get(position).getMacAddress().toLowerCase().replace(":", ""))) {
                            mActivity.mUtility.errorDialogWithCallBack("KUURV","Device already Added by you", 0, false, new onAlertDialogCallBack() {
                                @Override
                                public void PositiveMethod(DialogInterface dialog, int id) {
                                    mActivity.onBackPressed();
                                }
                                @Override
                                public void NegativeMethod(DialogInterface dialog, int id) {
                                }
                            });
                            return;
                        }

                        macAddressGoingforConnection=mArrayListAddDeviceScannedDevices.get(position).getMacAddress().toString();
                        mStartDeviceConnectionTimer.start();
                        ProcessConnectionToDevice(mArrayListAddDeviceScannedDevices.get(position).getMacAddress().toString());
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mArrayListAddDeviceScannedDevices.size() ;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.raw_add_device_item_tv_device_ble_address)
            TextView mTextViewDeviceBleAddress;
            @BindView(R.id.raw_add_device_iv_device)
            ImageView mImageViewDevice;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }

    /**
     * Owner information Dialog with phonenumner and mail
     * contact and Ignore
     */
    public void lostDeviceOwnerInformationDialog(final String OwnerName, final String OwnerEmail, final String phoneNUmber) {
        final Dialog myDialog = new Dialog(mActivity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.popup_add_device_owner_details);
        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSemiTransparentWhite)));
        final View mView = (View) myDialog.findViewById(R.id.popup_add_device_rl_main_layout);
        Button Contact = (Button) myDialog.findViewById(R.id.popup_add_device_btn_mail);

        Button mButtonIgnore = (Button) myDialog
                .findViewById(R.id.popup_add_device_btn_ignore);

        TextView mTextViewName = (TextView) myDialog
                .findViewById(R.id.popup_add_device_tv_name);
        mTextViewName.setText("" + OwnerName);

        TextView mTextViewEmail = (TextView) myDialog
                .findViewById(R.id.popup_add_device_tv_email);
        mTextViewEmail.setText("" + OwnerEmail);

        TextView mTextViewContactNo = (TextView) myDialog
                .findViewById(R.id.popup_add_device_tv_mobile_no);
        mTextViewContactNo.setText("" + phoneNUmber);


        mButtonIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.mUtility.hideKeyboard(mActivity);
                myDialog.dismiss();
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if(getFragmentAddDeviceBluetoothAdapter()!=null){
                        BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                        if(fragmentHomeBluetoothadapter.isEnabled()){
                                 clearScannedDevices();
                                 layoutVisibility();
                            AddScanResultsFromMainActivity();
                        }else{
                            mActivity.giveCustomToastBluetoothNotEnabled();
                        }

                    }else{
                        mActivity.giveCustomToastBluetoothNotEnabled();
                    }

                }
            }
        });

        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.mUtility.hideKeyboard(mActivity);
                myDialog.dismiss();
                showContactDialogHere(OwnerName, OwnerEmail, phoneNUmber);

            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }


    /**
     * call or mail dialog with cancel on Top.
     */
    private void showContactDialogHere(final String OwnerName, final String OwnerEmail, final String phoneNUmber) {

        final Dialog myDialog = new Dialog(mActivity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_showcontact);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSemiTransparentWhite)));
        final View mView = (View) myDialog.findViewById(R.id.contact_layout);
        TextView makeCall = (TextView) myDialog.findViewById(R.id.dialog_contact_tv_call);
        TextView sendEmail = (TextView) myDialog.findViewById(R.id.dialog_contact_tv_mail);
        ImageView cancel = (ImageView) myDialog.findViewById(R.id.dialog_contact_iv_close);

        if (phoneNUmber == null && phoneNUmber.equals("")) {
            makeCall.setVisibility(View.INVISIBLE);
        }

        makeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNUmber != null && !phoneNUmber.equals("")) {
                    myDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNUmber, null));
                    startActivity(intent);
                } else {
                    mActivity.mUtility.errorDialog("KUURV","Phone is not found.", 1, false);
                }
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (OwnerEmail != null && !OwnerEmail.equals("")) {
                    myDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{OwnerEmail});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Tracker Lost Report");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hi Your Tracker Has been found Please contact my Email Id");
                    startActivity(Intent.createChooser(intent, ""));
                } else {
                    mActivity.mUtility.errorDialog("KUURV","Email address is not found.", 1, false);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                lostDeviceOwnerInformationDialog(OwnerName, OwnerEmail, phoneNUmber);
            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }


    private boolean deviceBelongstoSomeoneelse=false;
    /**
     * Info and cancel window...
     */
    private void showInfoDialog(final String OwnerName, final String OwnerEmail, final String phoneNUmber,BleDevice bleDevice) {
        deviceBelongstoSomeoneelse=true;
        final Dialog myDialog = new Dialog(mActivity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.info_window);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSemiTransparentWhite)));
        final View mView = (View) myDialog.findViewById(R.id.popup_add_device_info_window);

        TextView info = (TextView) myDialog.findViewById(R.id.tv_ok);
        TextView cancel = (TextView) myDialog.findViewById(R.id.tv_cancel);


        BleManager.getInstance().disconnect(bleDevice);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                lostDeviceOwnerInformationDialog(OwnerName, OwnerEmail, phoneNUmber);
                deviceBelongstoSomeoneelse=false;

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
              //  System.out.println("Cancel Dialog Executed");
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if(getFragmentAddDeviceBluetoothAdapter()!=null){
                        BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                        if(fragmentHomeBluetoothadapter.isEnabled()){
                            deviceBelongstoSomeoneelse=false;
                            clearScannedDevices();
                            layoutVisibility();
                            AddScanResultsFromMainActivity();

                        }else{
                            mActivity.giveCustomToastBluetoothNotEnabled();
                        }

                    }else{
                        mActivity.giveCustomToastBluetoothNotEnabled();
                    }

                }
            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    //-----------------------BLE Part--------------------------------

    public void ProcessConnectionToDevice(String macAddress)
    {

        mActivity.mUtility.ShowProgress("Saving Device", false);
        if(getFragmentAddDeviceBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=getFragmentAddDeviceBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
                BleManager.getInstance().connect(new BleDevice(device), new BleGattCallback() {
                    @Override
                    public void onStartConnect()
                    {
           //             System.out.println("Ble_API_De_Bug onStartConnect = "+device.getAddress());
                    }

                    @Override
                    public void onConnectFail(BleDevice bleDevice, BleException e)
                    {
                        if(fragmentAddDeviceShowing){
                            mActivity.mUtility.HideProgress();
                      //      System.out.println("Ble_API_De_Bug onConnectFail = "+device.getAddress());
                            mActivity.mUtility.errorDialogWithCallBack("KUURV","Something went wrong.\nPlease try again later.", 1, false, new onAlertDialogCallBack() {
                                @Override
                                public void PositiveMethod(DialogInterface dialog, int id) {

                                    BluetoothAdapter bluetoothAdapter=mActivity.getMainActivityBluetoothAdapter();
                                    if(bluetoothAdapter!=null){
                                        if(bluetoothAdapter.isEnabled()){
                                            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                                                if(getFragmentAddDeviceBluetoothAdapter()!=null){
                                                    BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                                                    if(fragmentHomeBluetoothadapter.isEnabled()){
                                                    }

                                                }else{

                                                }

                                            }

                                        }else{
                                            mActivity.mUtility.errorDialog("KUURV","Please Turn on Bluetooth", 1, true);
                                        }
                                    }else{
                                        mActivity.mUtility.errorDialog("KUURV","Please Turn on Bluetooth", 1, true);
                                    }

                                    return;

                                }

                                @Override
                                public void NegativeMethod(DialogInterface dialog, int id) {

                                }
                            });
                        }

                    }
                    @Override
                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                      //  System.out.println("Ble_API_De_Bug onConnectSuccess = "+bleDevice.getMac());
                        notifyDataWriteOnCharctersticChanged(bleDevice);

                    }

                    @Override
                    public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                        System.out.println("Common FragmentAddDevice  Device Disconnected ="+bleDevice.getMac());
                        boolean same=macAddressGoingforConnection==bleDevice.getMac();

                        if(fragmentAddDeviceShowing){
                            if(same)
                            {
                                if(deviceBelongstoSomeoneelse==false){
                                    if(deviceDisconnectingInAdddeviceFragment==true){
                                        mActivity.mUtility.errorDialogWithCallBack("KUURV","Something went wrong.\nPlease try again later.", 1, false, new onAlertDialogCallBack() {
                                            @Override
                                            public void PositiveMethod(DialogInterface dialog, int id) {
                                                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                                if (mBluetoothAdapter == null) {
                                                    // Device does not support Bluetooth
                                                    mActivity.mUtility.errorDialog("KUURV","Bluetooth is not Supported", 1, true);
                                                    return;
                                                }
                                                if (!mBluetoothAdapter.isEnabled()) {
                                                    mActivity.mUtility.errorDialogWithCallBack("KUURV","Please Turn ON Bluetooth connection\nto enjoy all the features of the KUURV\n app.", 3, false, new onAlertDialogCallBack() {
                                                        @Override
                                                        public void PositiveMethod(DialogInterface dialog, int id) {
                                                            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                                                                if(getFragmentAddDeviceBluetoothAdapter()!=null){
                                                                    BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                                                                    if(fragmentHomeBluetoothadapter.isEnabled()){
                                                                        //  System.out.println("AddDevice Fragment onCreate ShowProgress");

                                                                    }else{
                                                                        mActivity.giveCustomToastBluetoothNotEnabled();
                                                                        layoutVisibility();;
                                                                    }

                                                                }else{
                                                                    mActivity.giveCustomToastBluetoothNotEnabled();
                                                                    layoutVisibility();
                                                                }

                                                            }

                                                        }

                                                        @Override
                                                        public void NegativeMethod(DialogInterface dialog, int id) {

                                                        }
                                                    });
                                                    return;
                                                }

                                                //Going for scanning again....
                                                mActivity.mUtility.HideProgress();

                                                return;

                                            }

                                            @Override
                                            public void NegativeMethod(DialogInterface dialog, int id) {

                                            }
                                        });

                                    }
                                }

                            }
                        }
                    }
                });
            }else{
                mActivity.giveCustomToastBluetoothNotEnabled();
            }
        }else{
            mActivity.giveCustomToastBluetoothNotEnabled();
        }
    }
    public void notifyDataWriteOnCharctersticChanged(final BleDevice bleDevice)
    {
        BleManager.getInstance().notify(
                bleDevice,
                "0000ab00-0143-0800-0008-e5f9b34fb000",
                "0000ab01-0143-0800-0008-e5f9b34fb000",
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              //  System.out.println("Ble_API_De_Bug notifyDataWriteOnCharctersticChanged onNotifySuccess = "+bleDevice.getMac());
                                writeAuthenicationToDevice(bleDevice);
                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(final byte[] data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Here i will get the datanotify on the Device...
                               // System.out.println("Ble_API_De_Bug notifyDataWriteOnCharctersticChanged onCharacteristicChanged = "+bleDevice.getMac());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    String hex = HexUtil.encodeHexStr(data);
                                    StringBuilder output = new StringBuilder();
                                    for (int i = 0; i < hex.length(); i+=2) {
                                        String str = hex.substring(i, i+2);
                                        output.append((char)Integer.parseInt(str, 16));
                                    }
                                    if (hex.toUpperCase().startsWith("05"))
                                    {

                                        int msgLength = (hexToDecimal(hex.substring(2, 4)) * 2);
                                        String authvalue=hex.substring(4,(4 + msgLength));
                                        int auth_key = hexToDecimal(authvalue);
                                        int auth_key_calculate = ((((auth_key * 55) + 3391) * 16) - ((721 * auth_key) + 452));
                                        setAuthAckResponseToDevice(auth_key_calculate,bleDevice);
                                    }
                                    else if(hex.toUpperCase().startsWith("0B"))
                                    {
                                        int mIntVerifyDeviceInfo = BLEUtility.hexToDecimal(hex.substring(4, 6));
                                     //   System.out.println("mIntVerifyDeviceInfo=" + mIntVerifyDeviceInfo);
                                     //   System.out.println("Ble_API_De_Bug onCharacteristicChanged infoDevice = "+mIntVerifyDeviceInfo);
                                        if(mIntVerifyDeviceInfo==0)
                                        {
                                        //    System.out.println("FastBle_DeBug Status 0 someone is the owner");
                                            //someone is the Owner of the Device.
                                        //    System.out.println("Ble_API_De_Bug ReadingOwnerInfo = "+mIntVerifyDeviceInfo);
                                            readOwnerInfoFromDevice(bleDevice);
                                        }

                                        if(mIntVerifyDeviceInfo==2)
                                        {
                                            /**
                                             * Timer used to Check the DeviceConfiguration is sucess if fails means disconnect the device and show dialog.
                                             */

                                            deviceConfigurationSucess=true;
                                            deviceDisconnectingInAdddeviceFragment=false;
                                          //  System.out.println("FastBle_DeBug Status 2 Device not registered new Device");
                                            mActivity.mUtility.HideProgress();
                                            //Device not Registered to anyone,Fresh Device...
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ble_address", bleDevice.getMac());
                                            FragmentConfigureDevice mFragmentConfigureDevice = new FragmentConfigureDevice();
                                            mActivity.replacesFragment(mFragmentConfigureDevice, true, bundle, 0);
                                        }


                                        if(mIntVerifyDeviceInfo==1)
                                        {
                                            /**
                                             * Timer used to Check the DeviceConfiguration is sucess if fails means disconnect the device and show dialog.
                                             */
                                            deviceConfigurationSucess=true;
                                            deviceDisconnectingInAdddeviceFragment=false;
                                        //    System.out.println("FastBle_DeBug Status 1 Device alreadyAdded by you");
                                            mActivity.mUtility.HideProgress();
                                            //Device already added to you.
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ble_address", bleDevice.getMac());
                                            FragmentConfigureDevice mFragmentConfigureDevice = new FragmentConfigureDevice();
                                            mActivity.replacesFragment(mFragmentConfigureDevice, true, bundle, 0);
                                        }

                                    }


                                    else if(hex.toUpperCase().startsWith("01"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerName = hex.substring(4, (4 + msgLength));
                                        strOwnerName = BLEUtility.convertHexToString(strOwnerName);
                                        //  System.out.println("OwnerInfo_Checking Name FragmentAddDevice"+strOwnerName);
                                        ownername=strOwnerName;
                                    }


                                    else if(hex.toUpperCase().startsWith("02"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerPhoneNo = hex.substring(4, (4 + msgLength));
                                        strOwnerPhoneNo = BLEUtility.convertHexToString(strOwnerPhoneNo);
                                        //  System.out.println("OwnerInfo_Checking PhoneNumber FragmentAddDevice"+strOwnerPhoneNo);
                                        mobileNumber=strOwnerPhoneNo;
                                    }

                                    else if(hex.toUpperCase().startsWith("03"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerEmail = hex.substring(4, (4 + msgLength));
//                                        System.out.println("strOwnerEmail=" + strOwnerEmail);
                                        strOwnerEmail = BLEUtility.convertHexToString(strOwnerEmail);
                                        //   System.out.println("OwnerInfo_Checking Email FragmentAddDevice"+strOwnerEmail);
                                        email=strOwnerEmail;
                                    }

                                    else if(hex.toUpperCase().startsWith("04"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerEmail1 = hex.substring(4, (4 + msgLength));
//                                        System.out.println("strOwnerEmail1=" + strOwnerEmail1);
                                        strOwnerEmail1 = BLEUtility.convertHexToString(strOwnerEmail1);
                                        //   System.out.println("OwnerInfo_Checking Email_part2 FragmentAddDevice"+strOwnerEmail1);
                                        email=email+strOwnerEmail1;
                                        //   System.out.println("email obtained from part 1= "+email);
                                        email.replace("NA", "");
                                        mActivity.mUtility.HideProgress();
                                        /**
                                         * Timer used to Check the DeviceConfiguration is sucess if fails means disconnect the device and show dialog.
                                         */
                                        deviceConfigurationSucess=true;

                                        showInfoDialog(ownername,email,mobileNumber,bleDevice);
                                    }
                                    System.out.println("FastBle_DeBug---->Values "+hex);
                                    if(hex.length()>8){
                                        if(hex.equalsIgnoreCase("0000000000000000000000000000000000000000")){
                                            /**
                                             * Timer used to Check the DeviceConfiguration is sucess if fails means disconnect the device and show dialog.
                                             */
                                            deviceConfigurationSucess=true;
                                            //             System.out.println("Ble_API_De_Bug Giving some Junk values "+hex);
                                            BleManager.getInstance().disconnect(bleDevice);
                                            //              System.out.println("Ble_API_De_Bug Giving Device Disconnected Due to Junk values = "+bleDevice.getMac());
                                        }
                                    }

                                }

                            }
                        });
                    }
                });

    }
    /**
     * First Step after Connection.
     * Need to write the Opcode 05. // Here i am writing it in readAuthValueFromDevice()
     * @param device
     */
    public void writeAuthenicationToDevice(final BleDevice device)
    {

        BleManager.getInstance().write(
                device,
                "0000ab00-0143-0800-0008-e5f9b34fb000",
                "0000ab01-0143-0800-0008-e5f9b34fb000",
                writeAuthValuetoDevice_05(),
                new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                    }
                });
    }
    public void writedatatoBleDevice(BleDevice bleDevice,byte [] datasent)
    {
        BleManager.getInstance().write(
                bleDevice,
                "0000ab00-0143-0800-0008-e5f9b34fb000",
                "0000ab01-0143-0800-0008-e5f9b34fb000",
                datasent,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                    }
                });
    }
    //Utility methods of BLE Communication.
    public static short OPCODE_READ_AUTH_NUMBER = 0x05;
    public byte [] writeAuthValuetoDevice_05() {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (OPCODE_READ_AUTH_NUMBER & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        return byte_value;
    }
    public static short OPCODE_WRITE_AUTH_VALUE = 0x06;
    public void setAuthAckResponseToDevice(int value,final BleDevice device) {
        final short mDataLength = (short) 0x04;
        byte byte_value[] = new byte[6];
        //  System.out.println("auth_value=" + value);
        byte_value[0] = (byte) (OPCODE_WRITE_AUTH_VALUE & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        byte_value[2] = (byte) (value & 0xFF);
        byte_value[3] = (byte) ((value >> 8) & 0xFF);
        byte_value[4] = (byte) ((value >> 16) & 0xFF);
        byte_value[5] = (byte) ((value >> 24) & 0xFF);
        writedatatoBleDevice(device, byte_value);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyDeviceInfoToDevice(mActivity.userUniqueKey,device);  //test@test.com
                //   System.out.println("Ble_API_De_Bug setAuthAckResponseToDevice "+device.getMac());
            }
        }, 1000);
    }
    //Verify infotoDevice.
    public static short OPCODE_VERIFY_DEVICE_INFO = 0x0B;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void verifyDeviceInfoToDevice(String userUniqueKey,BleDevice device) {
        byte[] byte_user_key = userUniqueKey.getBytes(StandardCharsets.US_ASCII);
        //    System.out.println("Length=" + byte_user_key.length);
        byte value_index = 0, name_index;
        short mDataLength = (short) byte_user_key.length;
        byte byte_value[] = new byte[2 + byte_user_key.length];
        byte_value[0] = (byte) (OPCODE_VERIFY_DEVICE_INFO & 0xFF);
        value_index++;
        byte_value[1] = (byte) (mDataLength & 0xFF);
        value_index++;

        /*Add device owner name*/
        for (name_index = 0; name_index < byte_user_key.length; name_index++) {
            byte_value[value_index++] = byte_user_key[name_index];
        }
       /* for (int i = 0; i < byte_value.length; i++) {
            System.out.println("Byte[" + i + "]=" + byte_value[i]);
        }*/
        writedatatoBleDevice(device, byte_value);

    }
    public void readOwnerInfoFromDevice(BleDevice bleDevice) {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_READ_OWNER_INFO & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
      //  byte_value = null;
    }

    //-----------------------BLE Part---------------------------------


    public BluetoothAdapter getFragmentAddDeviceBluetoothAdapter()
    {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            return null;
        }

        if (!bluetoothAdapter.isEnabled()) {
            return null;
        }

        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        }
        return  bluetoothAdapter;
    }


    private  final int LOCATION_REQUEST_CODE=2;
    private void showExplanationDialogFragmentAddDevice() {

        boolean deniedfristtime_true_else_false= ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(deniedfristtime_true_else_false){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mUtility.errorDialogWithYesNoCallBack("KUURV", "Please Provide\n locatio Acess to Scan for Devices.", "Yes", "No", true, 1, new onAlertDialogCallBack() {
                @Override
                public void PositiveMethod(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                @Override
                public void NegativeMethod(DialogInterface dialog, int id) {

                }
            });
        }
    }

    public void fragmentAddDeviceLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        if(getFragmentAddDeviceBluetoothAdapter()!=null){
            if(!getFragmentAddDeviceBluetoothAdapter().isEnabled()){
                mActivity. giveCustomToastBluetoothNotEnabled();
            }
        }else{
            mActivity. giveCustomToastBluetoothNotEnabled();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                     }
                }else{
                                showExplanationDialogFragmentAddDevice();
                }
                return;
            }
        }

    }


    private void layoutVisibility(){
        if (mArrayListAddDeviceScannedDevices.size() > 0) {
            mLinearLayoutScanning.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            //    System.out.println("Test Greater than zero");
        } else {
            mLinearLayoutScanning.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            //    System.out.println("Test Less than Zero");
        }
    }


    private class DeviceConnectionTimer extends CountDownTimer {
        public DeviceConnectionTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // System.out.println("Check" + millisUntilFinished);
            // Note : Increase timer time while fetch more setting
            if(deviceConfigurationSucess==true){
                mStartDeviceConnectionTimer.cancel();
            }
        }

        @Override
        public void onFinish() {
            if(deviceConfigurationSucess==false){
                mActivity.mUtility.HideProgress();
                if(macAddressGoingforConnection.length()>3){
                    //         System.out.println("Device Disconnected");
                    mActivity.diconnectDevice(macAddressGoingforConnection);
                }
                mActivity.mUtility.errorDialogWithCallBack("KUURV","Something went wrong.\nPlease try again later.", 1, false, new onAlertDialogCallBack() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {

                        BluetoothAdapter bluetoothAdapter=mActivity.getMainActivityBluetoothAdapter();
                        if(bluetoothAdapter!=null){
                            if(bluetoothAdapter.isEnabled()){
                                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                                    if(getFragmentAddDeviceBluetoothAdapter()!=null){
                                        BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                                        if(fragmentHomeBluetoothadapter.isEnabled()){

                                            mDeviceListAdapter.notifyDataSetChanged();
                                            layoutVisibility();
                                            //                  System.out.println("AddDevice Fragment onCreate ShowProgress");


                                        }else{
                                            mActivity.giveCustomToastBluetoothNotEnabled();

                                            mDeviceListAdapter.notifyDataSetChanged();
                                            layoutVisibility();;
                                            //          System.out.println("AddDevice Fragment onCreate ShowProgress");
                                        }

                                    }else{
                                        mActivity.giveCustomToastBluetoothNotEnabled();

                                        mDeviceListAdapter.notifyDataSetChanged();
                                        layoutVisibility();;
                                    }

                                }

                            }else{
                                mActivity.mUtility.errorDialog("KUURV","Please Turn on Bluetooth", 1, true);
                            }
                        }else{
                            mActivity.mUtility.errorDialog("KUURV","Please Turn on Bluetooth", 1, true);
                        }

                        return;

                    }
                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });

            }

        }
    }


    private void notifychangestoAdapter(){
        mDeviceListAdapter.notifyDataSetChanged();
    }



    private void fragmentAddDeviceDestroyView(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(getFragmentAddDeviceBluetoothAdapter()!=null){
                BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                if(fragmentHomeBluetoothadapter.isEnabled()){
                      clearScannedDevices();
                }
            }
        }
        mActivity.scanningProgressBar.setVisibility(View.INVISIBLE);
    }


    private void AddScanResultsFromMainActivity(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(getFragmentAddDeviceBluetoothAdapter()!=null){
                BluetoothAdapter fragmentHomeBluetoothadapter=getFragmentAddDeviceBluetoothAdapter();
                if(fragmentHomeBluetoothadapter.isEnabled()){
                    mActivity.setpassScannedDevicesToFragmentAddDevice(new PassScannedDevices() {
                        @Override
                        public void sendingResultstoFragment(RxBleDevice bleDevice) {
                            if(!mArrayListAddDeviceScannedDevices.contains(bleDevice)){
                                mArrayListAddDeviceScannedDevices.add(bleDevice);
                            }
                            notifychangestoAdapter();
                            layoutVisibility();
                        }
                    });
                }else{
                    mActivity.giveCustomToastBluetoothNotEnabled();
                }
            }else{
                mActivity.giveCustomToastBluetoothNotEnabled();
            }
        }
    }


    private void clearScannedDevices(){
        mArrayListAddDeviceScannedDevices.clear();
    }
}


    /*private void startAnimating(){
        mActivity.mImageViewAddDevice.setVisibility(View.INVISIBLE);// Image
        mActivity.scanningProgressBar.setVisibility(View.VISIBLE);//  Progressbar
    }

    private void stopAnimating(){
        mActivity.mImageViewAddDevice.setVisibility(View.VISIBLE);//   Image
        mActivity.scanningProgressBar.setVisibility(View.INVISIBLE);// Progressbar
    }*/