package com.benjaminshamoilia.trackerapp.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.benjaminshamoilia.trackerapp.vo.VoOwnerStatusForDevice;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.db.DataHolder;
import com.benjaminshamoilia.trackerapp.helper.BLEUtility;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.vo.VoAddDeviceData;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;

import static com.benjaminshamoilia.trackerapp.MainActivity.deviceAddedFromfragmentConigureDevice;

public class FragmentConfigureDevice extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    @BindView(R.id.frg_config_device_et_device_name)
    AppCompatEditText mEditTextDeviceName;
    @BindView(R.id.frg_config_device_et_name)

    AppCompatEditText mEditTextOwnerName;
    @BindView(R.id.frg_config_device_et_email)
    AppCompatEditText mEditTextOwnerEmail;
    @BindView(R.id.frg_config_device_et_mobile_no)
    AppCompatEditText mEditTextOwnerPhoneNo;

    @BindView(R.id.frg_config_device_btn_finish)
    AppCompatButton mButtonFinish;
    String mStringDeviceName;
    String mStringOwnerName;
    String mStringOwnerEmail;
    String mStringOwnerContactNo;
    startDeviceOwnershipTimer mStartDeviceOwnershipTimer;
    int deviceAddStatus = 0;


    //Vinay Code
    @BindView(R.id.frg_add_device_iv_scan)
    ImageView SelectImage;

    private PreferenceHelper mPreferenceHelper;
    private Bitmap ObtainedImage;
    private final int CAMERA_REQUEST_CODE = 20;
    private boolean image_selected = false;
    private File filepath;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    /**
     * Rest API calls intialization
     */
    private API mApiService;
    Utility mUtility;
    private Retrofit mRetrofit;
    String connectedBleAddress = ""; //Pass the Connected BLE address here.The Device which needs to be added to the Cloud..
    boolean deviceConnectionStatus=false;

    RelativeLayout layout;
    boolean isNeverAskPermissionCheck = false;
    TextView chooseimage;

    @BindView(R.id.fragment_device_aliasname)
    TextInputLayout mTextInputlayoutDeviceAliasname;

    @BindView(R.id.frg_config_device_et_name_l)
    TextInputLayout mTextInputlayoutname;


    @BindView(R.id.frg_config_device_et_email_l)
    TextInputLayout mTextInputlayoutemail;


    @BindView(R.id.frg_config_device_et_mobile_no_l)
    TextInputLayout mTextInputlayoutmobile;

    BleDevice bleDevice;



    /**
     *
     * Timer used for Checking the Connection is Sucess if not Sucess then Show Dialog and then navigate back...
     */
    DeviceConnectionTimer mStartDeviceConnectionTimer;
    private boolean fragmentConfigureDeviceSucess=false;
    private boolean fragmentConfigureDeviceAlive=false;

    //Vinay Code
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_configure_device, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_configure_device);

        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);

        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);
        layout =(RelativeLayout)mViewRoot.findViewById(R.id.imagebackgorund_layout);
        chooseimage =(TextView)mViewRoot.findViewById(R.id.change_image_text);
        chooseimage.setTextColor(Color.WHITE);

        Bundle b = getArguments();
        connectedBleAddress =b.getString("ble_address");

        if(getFragmentFragmentConfigureBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=getFragmentFragmentConfigureBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(connectedBleAddress);
                bleDevice=new BleDevice(getBleDevice);
                final BluetoothDevice device_Conn = bluetoothAdapter.getRemoteDevice(connectedBleAddress);
                if (BleManager.getInstance().isConnected(new BleDevice(device_Conn))) {
                    deviceConnectionStatus=true;
                }
            }else{
                mActivity.giveCustomToastBluetoothNotEnabled();
            }
        }else{
            mActivity.giveCustomToastBluetoothNotEnabled();
        }


        get_UserInfo();
        mStartDeviceOwnershipTimer = new startDeviceOwnershipTimer(10000, 1000);

        //Vinay Code

        /**
         * Rest API calls code....
         */

//
//        connectedBleAddress = mSelectedVoBluetoothDevices.getDeviceAddress().toLowerCase().replace(":", "");


        mPreferenceHelper = new PreferenceHelper(getActivity());
        mUtility = new Utility(getActivity());
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getClientWithAutho())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .build();
        mApiService = mRetrofit.create(API.class);
        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        mTextInputlayoutDeviceAliasname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutDeviceAliasname.setErrorEnabled(false);
                mTextInputlayoutDeviceAliasname.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutDeviceAliasname.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mTextInputlayoutname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutname.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayoutname.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutname.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mTextInputlayoutemail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutemail.setErrorEnabled(false);
                mTextInputlayoutemail.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutemail.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mTextInputlayoutmobile.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutmobile.setErrorEnabled(false);
                mTextInputlayoutmobile.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutmobile.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if(mActivity.getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter =mActivity.getMainActivityBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(connectedBleAddress);
                boolean connectionStatus=BleManager.getInstance().isConnected(connectedBleAddress);
                if(connectionStatus){
                    notifyDataWriteOnCharctersticChanged(new BleDevice(device));
                }else{
                    mActivity.mUtility.errorDialogWithCallBack("KUURV","Device is disconnected. Please try again to connect.", 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            mActivity.onBackPressed();
                        }
                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {
                        }
                    });
                }
            }
        }

        mStartDeviceConnectionTimer = new DeviceConnectionTimer(15000, 1000);
        fragmentConfigureDeviceAlive=true;
        return mViewRoot;
    }


    @OnClick(R.id.frg_config_device_btn_finish)
    public void onButtonFinishClick(View mView) {
        if (isAdded()) {
            mTextInputlayoutDeviceAliasname.setError("");
            mTextInputlayoutname.setError("");
            mTextInputlayoutemail.setError("");
            mTextInputlayoutmobile.setError("");
            mStringDeviceName = mEditTextDeviceName.getText().toString().trim();
            mStringOwnerName = mEditTextOwnerName.getText().toString().trim();
            mStringOwnerEmail = mEditTextOwnerEmail.getText().toString().trim();
            mStringOwnerContactNo = mEditTextOwnerPhoneNo.getText().toString().trim();

            if (mStringDeviceName.equalsIgnoreCase("")) {
                mTextInputlayoutDeviceAliasname.setError("Please enter device name"); // This is the helper text i.e Red color
                mTextInputlayoutDeviceAliasname.setHintEnabled(false);
                return;
            }


            if (mStringOwnerName.equalsIgnoreCase("")) {
                mTextInputlayoutname.setError("Please enter your name"); // This is the helper text i.e Red color
                mTextInputlayoutname.setHintEnabled(false);

                return;
            }
            if (mStringOwnerName.length() > 18) {
                mTextInputlayoutname.setError(getResources().getString(R.string.str_configure_device_enter_valid_device_owner_name)); // This is the helper text i.e Red color
                mTextInputlayoutname.setHintEnabled(false);

                return;
            }
            if (mStringOwnerEmail.equalsIgnoreCase("")) {
                mTextInputlayoutemail.setError("Please enter your Email"); // This is the helper text i.e Red color
                mTextInputlayoutemail.setHintEnabled(false);
                return;
            }
           /* if (mStringOwnerEmail.length()>18)
            {
                mTextInputlayoutemail.setError("Email Length should be lesss than 18 Charcters."); // This is the helper text i.e Red color
                mTextInputlayoutemail.setHintEnabled(false);
                return;
            }*/

            //commented for checking email...Uncomment it later...
           /* if (!isValidEmail(mStringOwnerEmail)) {
                mTextInputlayoutemail.setError("Please enter valid email address");
                mTextInputlayoutemail.setHintEnabled(false);
                return;
            }*/
            if (mStringOwnerContactNo.equalsIgnoreCase("")) {
                mStringOwnerContactNo = "NA";
            }
         /*   if (!image_selected) {
                System.out.println("Fragment_Configure_Device>>>>>AddDevice API called");
                Add_Device_API_without_image();
            } else {
                Add_Device_API_with_image();
            }*/
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                mActivity.mUtility.errorDialog("KUURV","Bluetooth is not Supported",3 , true);
                return;
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
                // mActivity.mUtility.errorDialog("Please Turn on Bluetooth and Try again Later", 1, true);

                mUtility.errorDialogWithCallBack("KUURV","Please Turn on Bluetooth and Try again Later", 1, false, new onAlertDialogCallBack() {
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
            if(!mActivity.mUtility.haveInternet())
            {
                mUtility.errorDialogWithCallBack("KUURV",getResources().getString(R.string.str_no_internet_connection), 1, false, new onAlertDialogCallBack() {
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
            if (deviceConnectionStatus)
            {
                if (!mActivity.mUtility.haveInternet()) {
                    mActivity.mUtility.errorDialog("KUURV",getResources().getString(R.string.str_no_internet_connection), 1, true);
                } else
                {
                    if(image_selected)
                    {
                        createDirectoryAndSaveFile(ObtainedImage, connectedBleAddress + ".jpg");}

                    mActivity.mUtility.ShowProgress("Saving Device ", false);
                    if (mStartDeviceOwnershipTimer != null)
                        mStartDeviceOwnershipTimer.cancel();
                    if (mStartDeviceOwnershipTimer != null)
                        mStartDeviceOwnershipTimer.start();


                    if(mStartDeviceConnectionTimer != null){
                        if(fragmentConfigureDeviceSucess==false){
                            mStartDeviceConnectionTimer.start();
                        }

                    }

                }

                // Vinay Code
                // save_user_info(mStringOwnerName,mStringOwnerEmail,mStringOwnerContactNo);
                //Vinay Code


            } else {
                mActivity.mUtility.errorDialogWithCallBack("KUURV","Device is disconnected. Please try again to connect.", 1, false, new onAlertDialogCallBack() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        mActivity.onBackPressed();
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
            }
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    /*Get ALL Device Setting value*/
    private class startDeviceOwnershipTimer extends CountDownTimer {
        public startDeviceOwnershipTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            if (millisUntilFinished <= 1000) {
             //   System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer readOwnerInfoFromDevice");
                readOwnerInfoFromDevice(bleDevice);
            }
            else if(millisUntilFinished <= 500) {
              //  System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer readOwnerInfoFromDevice");
                verifyDeviceInfoToDevice(mActivity.userUniqueKey,bleDevice);
            }
            else if (millisUntilFinished <= 2000 && millisUntilFinished > 1000) {
                System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer setOwnerEmailToDevice");

                if(mStringOwnerEmail.length()<=18){
                    setOwnerEmailToDevicelengthEqualto18(mStringOwnerEmail,bleDevice);
                }else{
                    setOwnerEmailToDevice(mStringOwnerEmail,bleDevice);
                }
            }
            else if (millisUntilFinished <= 3000 && millisUntilFinished > 2000) {
             //   System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer setOwnerPhoneNoToDevice");
                setOwnerPhoneNoToDevice(mStringOwnerContactNo,bleDevice);
            }
            else if (millisUntilFinished <= 4000 && millisUntilFinished > 3000) {
               // System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer setOwnerNameToDevice");

                if(checkConnectionStatusDevice()){
                    setOwnerNameToDevice(mStringOwnerName,bleDevice);
                }else{
                    mActivity.hideProgress();
                    mActivity.mUtility.errorDialogWithCallBack("KUURV","Something Went Wrong \n please try again later", 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            mActivity.onBackPressed();
                        }
                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }
            }
            else if (millisUntilFinished <= 5000 && millisUntilFinished > 4000) {
              //  System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer addDevice");
                addDevice(mActivity.userUniqueKey,bleDevice);
            }
            else if(millisUntilFinished <= 6000 && millisUntilFinished > 3000)
            {
               // System.out.println("FastBle_DeBug---->FCD startDeviceOwnershipTimer notifyDataWriteOnCharctersticChanged");
            }
        }

        @Override
        public void onFinish() {

           // System.out.println("FastBle_DeBug---->FCD onFinish");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentConfigureDeviceAlive=false;
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        if(!fragmentConfigureDeviceSucess){
            if(BleManager.getInstance().isConnected(bleDevice)){
                BleManager.getInstance().disconnect(bleDevice);
              //  System.out.println("Fragment_Configure_Device onBackPressed Device Disconnected = "+bleDevice.getMac());
            }
            // mActivity.onBackPressed();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private String CheckRecordExistInUserAccountDB(String values, String table_name, String ColumnValues) {
        DataHolder mDataHolder;
        String url = "select * from " + table_name + " where " + ColumnValues + "= '" + values + "'";

    //    System.out.println("Fragment_configure_Device>>>>>>CheckRecordExistInUserAccountDB \n" + url);

        mDataHolder = mActivity.mDbHelper.read(url);
        if (mDataHolder != null) {
            System.out.println(" UserList : " + url + " : " + mDataHolder.get_Listholder().size());
            if (mDataHolder != null && mDataHolder.get_Listholder().size() != 0) {
                return mDataHolder.get_Listholder().get(0).get(mActivity.mDbHelper.ble_address);
            } else {
                return "-1";
            }
        }
        return "-1";
    }

    // saving the image file to the local Storage
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/Tracker");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Tracker/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Tracker/"), fileName);


        filepath = file;
        //System.out.println("FragmentConfigureDevice ImagePath" + file.getPath());
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CAMERA == requestCode && resultCode == RESULT_OK) {
            image_selected = true;   //Here i am making the image selection is selecetd or not...
            ObtainedImage = (Bitmap) data.getExtras().get("data");
            SelectImage.setImageBitmap(ObtainedImage);
            chooseimage.setText("");
        }
        else if (GALLERY == requestCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                ObtainedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                SelectImage.setImageBitmap(ObtainedImage);
                image_selected = true;
                chooseimage.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentConfigureDeviceAlive=true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void callMarshMallowParmession() {
        if (Build.VERSION.SDK_INT < 23) {
            showPictureDialog();
        } else {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showPictureDialog();
            } else {
                List<String> permissionsNeeded = new ArrayList<String>();
                final List<String> permissionsList = new ArrayList<String>();
                if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    permissionsNeeded.add("Write External Storage");
                if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                    permissionsNeeded.add("Read External Storage");
                if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                    permissionsNeeded.add("Acess Camera");
                if (permissionsList.size() > 0) {
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    return;
                }
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (mActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @OnClick(R.id.frg_add_device_iv_scan)
    public void selectImage(View mView) {
        if (isAdded()) {
           /* Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(photoCaptureIntent, CAMERA_REQUEST_CODE);*/
            callMarshMallowParmession();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Map<String, Integer> perms = new HashMap<String, Integer>();
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                    //denied
//                            Log.e("denied", permission);
                } else {
                    if (ActivityCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
//                                Log.e("allowed", permission);
                    } else {
                        //set to never ask again
//                                Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                if (!isNeverAskPermissionCheck) {
                    isNeverAskPermissionCheck = true;
                    mActivity.mUtility.errorDialogWithCallBack("KUURV","You have forcefully denied some of the required permissions. Please open settings, go to permissions and allow them.", 1, true, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", mActivity.getPackageName(), null));
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, 103);
                            isNeverAskPermissionCheck = false;
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {
                            isNeverAskPermissionCheck = false;
                        }
                    });
                }
            }
        } else {
            showPictureDialog();
        }
    }

    private int GALLERY = 1, CAMERA = 2, CANCEL = 3;

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera",
                "Cancel"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                            case 2:
                                removeselectedImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void removeselectedImage() {
        SelectImage.setImageBitmap(null);
        image_selected = false;
        layout.setVisibility(View.VISIBLE);
        SelectImage.setImageResource(R.drawable.circle_shape);
        chooseimage.setText(" Choose \n  Image");
    }

    public void choosePhotoFromGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

// Vinay Code

    /**
     * This method is used for adding the device  when image is not selected from the user
     */
    private void Add_Device_API_without_image() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("ble_address", connectedBleAddress.replace(":","").toLowerCase());
        params.put("device_name", mStringDeviceName);
        params.put("correction_status", "1");   // connected 1 ,disconneceted 0
        params.put("device_type", "1");
        params.put("longitude",mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId()));
        params.put("latitude",mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId()));
        params.put("tracker_device_alert", "1"); // no used.... take from BLE not from server...
        params.put("marked_lost", "NO");// when adding the device...
        params.put("is_active", "1"); // Default is one...by miss if not delted in the local db..use this..(is_active 1 Frist time adding)(is_active 0 not already added)
        params.put("contact_name", mStringOwnerName);
        params.put("contact_email", mStringOwnerEmail);
        params.put("contact_mobile", mStringOwnerContactNo);
        final Call<VoAddDeviceData> mLogin = mApiService.Add_Device_API_without_image(params);
        mLogin.enqueue(new Callback<VoAddDeviceData>() {
            @Override
            public void onResponse(Call<VoAddDeviceData> call, Response<VoAddDeviceData> response) {

                VoAddDeviceData configure_device = response.body();
                if (configure_device.getMessage().startsWith("Invalid Token")) {
                    mActivity.APICrushLogout();
                } else {
               //     System.out.println("FragmentCOnfigureDevice AddDeviceWithoutImage" + new Gson().toJson(response.body()));
                //    System.out.println("FragmentCOnfigureDevice AddDeviceWithoutImage response" +configure_device.getResponse() );
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(mActivity.mDbHelper.user_id, "" + configure_device.getData().getUser_id());
                    mContentValues.put(mActivity.mDbHelper.ble_address, "" + configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase());
                    mContentValues.put(mActivity.mDbHelper.device_name, "" + configure_device.getData().getDevice_name());
                    mContentValues.put(mActivity.mDbHelper.correction_status, "" + configure_device.getData().getCorrection_status());
                    mContentValues.put(mActivity.mDbHelper.device_type, "" + configure_device.getData().getDevice_type());
                    mContentValues.put(mActivity.mDbHelper.latitude, "" + configure_device.getData().getLatitude());
                    mContentValues.put(mActivity.mDbHelper.longitude, "" + configure_device.getData().getLongitude());
                    mContentValues.put(mActivity.mDbHelper.photo_local_url,configure_device.getData().getDevice_image());
                    mContentValues.put(mActivity.mDbHelper.tracker_device_alert, "" + configure_device.getData().getTracker_device_alert());
                    mContentValues.put(mActivity.mDbHelper.marked_lost, "" + configure_device.getData().getMarked_lost());
                    mContentValues.put(mActivity.mDbHelper.is_active, "" + configure_device.getData().getIs_active());
                    mContentValues.put(mActivity.mDbHelper.contact_name, "" + configure_device.getData().getContact_name());
                    mContentValues.put(mActivity.mDbHelper.contact_email, "" + configure_device.getData().getContact_email());
                    mContentValues.put(mActivity.mDbHelper.contact_mobile, "" + configure_device.getData().getContact_mobile());
                    mContentValues.put(mActivity.mDbHelper.status, "" + configure_device.getData().getCorrection_status());
                    mContentValues.put(mActivity.mDbHelper.created_time, "" + configure_device.getData().getCreated_at());
                    mContentValues.put(mActivity.mDbHelper.updated_time, "" + configure_device.getData().getUpdated_at());
                    mContentValues.put(mActivity.mDbHelper.server_id, "" + configure_device.getData().getId());
                    mContentValues.put(mActivity.mDbHelper.identifier, "NA");
                    mContentValues.put(mActivity.mDbHelper.is_sync, "NA");
                    mContentValues.put(mActivity.mDbHelper.photo_server_url, "");
                    mContentValues.put(mActivity.mDbHelper.tracker_locAdds, mActivity.retutn_tracekrAddress(Double.parseDouble(configure_device.getData().getLatitude()),Double.parseDouble(configure_device.getData().getLongitude())));
                    String isExistInUserDB = CheckRecordExistInUserAccountDB(configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase(), mActivity.mDbHelper.Device_Table, mActivity.mDbHelper.ble_address);
                  //  System.out.println("Fragment_configure_Device>>>>>> isExistInUserDB =" + isExistInUserDB);
                    if (isExistInUserDB.equalsIgnoreCase("-1")) {
                        //  mDbHelper.insertRecord(mDbHelper.mTableUserAccount, mContentValuesUser);
                        int result = mActivity.mDbHelper.insertRecord(mActivity.mDbHelper.Device_Table, mContentValues);
                      //  System.out.println("Fragment_configure_Device>>>>>>Response_Data_Inserted");
                    } else {
                        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, mActivity.mDbHelper.user_id + "=?", new String[]{isExistInUserDB});

                      //  System.out.println("Fragment_configure_Device>>>>>>Response_Data_Updated");

                    }




                    chooseimage.setText("kuurv");
                    chooseimage.setTextSize(25);
                    chooseimage.setTextColor(Color.BLACK);
                    Update_userSetInfo(configure_device.getData().getContact_name(),
                            configure_device.getData().getContact_email(),
                            configure_device.getData().getContact_mobile(),
                            configure_device.getData().getUser_id());
                    mActivity.getDBDeviceList();
                    mActivity.hideProgress();
                    mActivity.mUtility.errorDialogWithCallBack("KUURV","Device Added Successfully", 0, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            // mActivity.onBackPressed();
                            deviceAddedFromfragmentConigureDevice=true;
                            mActivity.mPreferenceHelper.setDeviecPostion("");
                            mActivity.replacesFragment(new FragmentHome(), true, null, 0);
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }


            }

            @Override
            public void onFailure(Call<VoAddDeviceData> call, Throwable t) {

            }


        });

    }

    /**
     * This method is used for adding the device  when image is selected from the user
     */

    File compressedImgFile;

    private void Add_Device_API_with_image() {

        File file = new File(filepath.getPath());
       // System.out.println("Fragment_configure_Device>>>>>>Response_Add_Device_API_with_image =" + file);

        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), mPreferenceHelper.getUserId());
        RequestBody ble_address = RequestBody.create(MediaType.parse("multipart/form-data"), connectedBleAddress.replace(":","").toLowerCase());
        RequestBody device_name = RequestBody.create(MediaType.parse("multipart/form-data"), mStringDeviceName);
        RequestBody correction_status = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody device_type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"),mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId()));
        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId()));
        RequestBody tracker_device_alert = RequestBody.create(MediaType.parse("multipart/form-data"), "ON");
        RequestBody marked_lost = RequestBody.create(MediaType.parse("multipart/form-data"), "NO");
        RequestBody is_active = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody contact_name = RequestBody.create(MediaType.parse("multipart/form-data"), mStringOwnerName);
        RequestBody contact_email = RequestBody.create(MediaType.parse("multipart/form-data"), mStringOwnerEmail);
        RequestBody contact_mobile = RequestBody.create(MediaType.parse("multipart/form-data"), mStringOwnerContactNo);
        MultipartBody.Part device_image = null;

        try {
            compressedImgFile = new Compressor(getActivity()).compressToFile(file);
           // System.out.println("FragmentConfigureDevice Compressed ImagePath" + compressedImgFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }


        RequestBody request_file = RequestBody.create(MediaType.parse("multipart/form-data"), compressedImgFile);
///    System.out.println("Fragment_configure_Device>>>>>>File_Name "+file.getName()+"file ="+file+"Request File = "+request_file);
        // System.out.println("Fragment_configure_Device>>>>>>File_Name " + compressedImgFile.getAbsolutePath() + "file =" + compressedImgFile + "Request File = " + request_file);
        //   System.out.println("Path = " + compressedImgFile.getAbsolutePath() + "file Size = =" +Integer.parseInt(String.valueOf(compressedImgFile.length()/1024)));


        device_image = MultipartBody.Part.createFormData("device_image", compressedImgFile.getName(), request_file);

        Call<VoAddDeviceData> call = mApiService.Add_Device_API_with_image(
                user_id,
                ble_address,
                device_name,
                correction_status,
                device_type,
                longitude,
                latitude,
                tracker_device_alert,
                marked_lost,
                is_active,
                contact_name,
                contact_email,
                contact_mobile,
                device_image);

        call.enqueue(new Callback<VoAddDeviceData>() {
            @Override
            public void onResponse(Call<VoAddDeviceData> call, Response<VoAddDeviceData> response) {

                VoAddDeviceData configure_device = response.body();

                if (configure_device.getMessage().startsWith("Invalid Token")) {
                    mActivity.APICrushLogout();
                } else {
                   // System.out.println("FragmentCOnfigureDevice AddDeviceWithImage" + new Gson().toJson(response.body()));
                  //  System.out.println("FragmentCOnfigureDevice AddDeviceWithImage response" +configure_device.getResponse() );
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(mActivity.mDbHelper.user_id, "" + configure_device.getData().getUser_id());
                    mContentValues.put(mActivity.mDbHelper.ble_address, "" + configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase());
                    mContentValues.put(mActivity.mDbHelper.device_name, "" + configure_device.getData().getDevice_name());
                    mContentValues.put(mActivity.mDbHelper.device_type, "" + configure_device.getData().getDevice_type());
                    mContentValues.put(mActivity.mDbHelper.latitude, "" + configure_device.getData().getLatitude());
                    mContentValues.put(mActivity.mDbHelper.longitude, "" + configure_device.getData().getLongitude());
                    mContentValues.put(mActivity.mDbHelper.tracker_device_alert, "" + configure_device.getData().getTracker_device_alert());
                    mContentValues.put(mActivity.mDbHelper.marked_lost, "" + configure_device.getData().getMarked_lost());
                    mContentValues.put(mActivity.mDbHelper.is_active, "" + configure_device.getData().getIs_active());
                    mContentValues.put(mActivity.mDbHelper.contact_name, "" + configure_device.getData().getContact_name());
                    mContentValues.put(mActivity.mDbHelper.contact_email, "" + configure_device.getData().getContact_email());
                    mContentValues.put(mActivity.mDbHelper.contact_mobile, "" + configure_device.getData().getContact_mobile());
                    mContentValues.put(mActivity.mDbHelper.photo_local_url, "" + filepath);
                    mContentValues.put(mActivity.mDbHelper.photo_server_url, "" + configure_device.getData().getDevice_image_path());
                    mContentValues.put(mActivity.mDbHelper.identifier, "NA");
                    mContentValues.put(mActivity.mDbHelper.created_time, "" + configure_device.getData().getCreated_at());
                    mContentValues.put(mActivity.mDbHelper.updated_time, "" + configure_device.getData().getUpdated_at());
                    mContentValues.put(mActivity.mDbHelper.status, "" + configure_device.getData().getCorrection_status());
                    mContentValues.put(mActivity.mDbHelper.is_sync, "NA");
                    mContentValues.put(mActivity.mDbHelper.server_id, "" + configure_device.getData().getId());
                    mContentValues.put(mActivity.mDbHelper.tracker_locAdds,mActivity.retutn_tracekrAddress(Double.parseDouble(configure_device.getData().getLatitude()),Double.parseDouble(configure_device.getData().getLongitude())));
                    String isExistInUserDB = CheckRecordExistInUserAccountDB(configure_device.getData().getBle_address(), mActivity.mDbHelper.Device_Table, mActivity.mDbHelper.ble_address);
                //    System.out.println("Fragment_configure_Device>>>>>> isExistInUserDB =" + isExistInUserDB);
                    if (isExistInUserDB.equalsIgnoreCase("-1")) {
                        //  mDbHelper.insertRecord(mDbHelper.mTableUserAccount, mContentValuesUser);
                        int result = mActivity.mDbHelper.insertRecord(mActivity.mDbHelper.Device_Table, mContentValues);
                     //   System.out.println("Fragment_configure_Device>>>>>>Response_Data_Inserted. Result = " + result);
                    } else {
                        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, mActivity.mDbHelper.user_id + "=?", new String[]{isExistInUserDB});
                        //    mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, "user_id=", null);
                      //  System.out.println("Fragment_configure_Device>>>>>>Response_Data_Updated");
                    }

                    Update_userSetInfo(configure_device.getData().getContact_name(),
                            configure_device.getData().getContact_email(),
                            configure_device.getData().getContact_mobile(),
                            configure_device.getData().getUser_id());


                    //  System.out.println("Device DataTable Printed withImage "+mActivity.mDbHelper.getTableAsString(mActivity.mDbHelper.getReadableDatabase(), mActivity.mDbHelper.Device_Table));
                    mActivity.getDBDeviceList();
                    mActivity.hideProgress();
                    mActivity.mUtility.errorDialogWithCallBack("KUURV","Device Added Successfully", 0, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            //  mActivity.onBackPressed();

                            mActivity.replacesFragment(new FragmentHome(), true, null, 0);
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }



               /* String result=  mActivity.mDbHelper.getTableAsString(mActivity.mDbHelper.getReadableDatabase(),"Device_Table");
                System.out.println("Tabel Data Printed Here "+result);*/
            }

            @Override
            public void onFailure(Call<VoAddDeviceData> call, Throwable t) {

            }
        });




    }


    private void Update_userSetInfo(String name, String Email, String Number, String userid) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.name, name);
        mContentValues.put(mActivity.mDbHelper.email, Email);
        mContentValues.put(mActivity.mDbHelper.mobile, Number);
        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.User_Set_Info, mContentValues, mActivity.mDbHelper.user_id + "=?", new String[]{userid});

        // System.out.println("User_set_info values "+mActivity.mDbHelper.getTableAsString(mActivity.mDbHelper.getReadableDatabase(), mActivity.mDbHelper.User_Set_Info));
    }


    /**
     * This method is used to Fetch the shared info the user and prefilled in
     * the textview of the FragmentConfigure_Device.
     */

    String use_stored_name;
    String use_stored_email;
    String user_stored_mobile;

    private void get_UserInfo() {
        DataHolder mDataHolderLight;
        String querey = "select * from User_Set_Info";
        mDataHolderLight = mActivity.mDbHelper.readData(querey);
        for (int i = 0; i < mDataHolderLight.get_Listholder().size(); i++) {
            use_stored_name = mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.name);
            use_stored_email = mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.email);
            user_stored_mobile = mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.mobile);

        }
        mEditTextOwnerName.setText(use_stored_name);
        // mEditTextOwnerName.setText(""+getAlphaNumericString(4));////// Used for De-Bugging mode Remove it...
        mEditTextOwnerEmail.setText(use_stored_email);

        mEditTextDeviceName.setText(connectedBleAddress.replace(":","").toLowerCase()); // Used for De-Bugging mode Remove it...




        if (user_stored_mobile == null || user_stored_mobile.isEmpty() || user_stored_mobile.equalsIgnoreCase("NA")) {
            mEditTextOwnerPhoneNo.setText("");
        } else {
            mEditTextOwnerPhoneNo.setText(user_stored_mobile);
        }


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditTextOwnerPhoneNo.setText(""+generateRandomNumber(10)); //// Used for De-Bugging mode Remove it...
        }*/

    }

    //BLE part used in this fragment...

    public void addDevice(String userUniqueKey,final BleDevice bleDevice) {

        byte[] byte_user_key = userUniqueKey.getBytes(StandardCharsets.US_ASCII);
    //    System.out.println("Length=" + byte_user_key.length);
        byte value_index = 0, name_index;
        short mDataLength = (short) byte_user_key.length;
        final byte byte_value[] = new byte[2 + byte_user_key.length];
        byte_value[0] = (byte) (Constant.OPCODE_DEVICE_ADD & 0xFF);
        value_index++;
        byte_value[1] = (byte) (mDataLength & 0xFF);
        value_index++;

        /*Add device owner name*/
        for (name_index = 0; name_index < byte_user_key.length; name_index++) {
            byte_value[value_index++] = byte_user_key[name_index];
        }
        /*for (int i = 0; i < byte_value.length; i++) {
            System.out.println("Byte[" + i + "]=" + byte_value[i]);
        }*/
        writedatatoBleDevice(bleDevice,byte_value);
    }


    /**
     * Writes the owner name on the target device
     *
     * @param ownerName write the owner name.
     */
    public void setOwnerNameToDevice(String ownerName,BleDevice bleDevice) {
        byte[] byte_name = ownerName.getBytes(StandardCharsets.US_ASCII);
      //  System.out.println("Length=" + byte_name.length);
        byte value_index = 0, name_index;
        short mDataLength = (short) byte_name.length;
        byte byte_value[] = new byte[2 + byte_name.length];
        byte_value[0] = (byte) (Constant.OPCODE_WRITE_OWNER_NAME & 0xFF);
        value_index++;
        byte_value[1] = (byte) (mDataLength & 0xFF);
        value_index++;

        /*Add device owner name*/
        for (name_index = 0; name_index < byte_name.length; name_index++) {
            byte_value[value_index++] = byte_name[name_index];
        }
       /* for (int i = 0; i < byte_value.length; i++) {
            System.out.println("Byte[" + i + "]=" + byte_value[i]);
        }*/
        writedatatoBleDevice(bleDevice,byte_value);
    }



    /**
     * Writes the owner contact no on the target device
     *
     * @param ownerPhone write the owner contact no.
     */
    public void setOwnerPhoneNoToDevice(String ownerPhone,BleDevice bleDevice) {
        byte[] byte_contact = ownerPhone.getBytes(StandardCharsets.US_ASCII);
       // System.out.println("Length=" + byte_contact.length);
        byte value_index = 0, name_index;
        short mDataLength = (short) byte_contact.length;
        byte byte_value[] = new byte[2 + byte_contact.length];
        byte_value[0] = (byte) (Constant.OPCODE_WRITE_OWNER_PHONE_NO & 0xFF);
        value_index++;
        byte_value[1] = (byte) (mDataLength & 0xFF);
        value_index++;

        /*Add device owner name*/
        for (name_index = 0; name_index < byte_contact.length; name_index++) {
            byte_value[value_index++] = byte_contact[name_index];
        }
        /*for (int i = 0; i < byte_value.length; i++) {
            System.out.println("Byte[" + i + "]=" + byte_value[i]);
        }*/
        writedatatoBleDevice(bleDevice,byte_value);
    }

    /**
     * Writes the owner email on the target device
     *
     * @param ownerEmail write the owner email.
     */
    public void setOwnerEmailToDevice(String ownerEmail,final BleDevice bleDevice) {
        final byte[] byte_email = ownerEmail.getBytes(StandardCharsets.US_ASCII);
       // System.out.println("Length=" + byte_email.length);

        if (byte_email.length > 18) {
            setOwnerEmailPart1ToDevice(Arrays.copyOfRange(byte_email, 0, 18), true,bleDevice);

            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setOwnerEmailPart1ToDevice(Arrays.copyOfRange(byte_email, 18, byte_email.length), false,bleDevice);
                }
            }, 400);
        } else {
            setOwnerEmailPart1ToDevice(byte_email, true,bleDevice);
        }
    }


    public void setOwnerEmailToDevicelengthEqualto18(String ownerEmail,final BleDevice bleDevice){
        final byte[] byte_email = ownerEmail.getBytes(StandardCharsets.US_ASCII);

        final byte[] byte_emailSecondPart="NA".getBytes(StandardCharsets.US_ASCII);
     //   System.out.println("Length=" + byte_email.length);

        if (true) {
            setOwnerEmailPart1ToDevice(Arrays.copyOfRange(byte_email, 0, 18), true,bleDevice);

            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setOwnerEmailPart1ToDevice(Arrays.copyOfRange(byte_emailSecondPart,0 , byte_emailSecondPart.length), false,bleDevice);
                }
            }, 400);
        }
    }

    /**
     * Writes the owner email part 1 on the target device
     *
     * @param byte_email write the owner email part 1.
     */
    public void setOwnerEmailPart1ToDevice(byte[] byte_email, boolean isPart1,BleDevice device) {
        byte value_index = 0, name_index;
        short mDataLength = (short) byte_email.length;
        byte byte_value[] = new byte[2 + byte_email.length];
        if (isPart1) {
            byte_value[0] = (byte) (Constant.OPCODE_WRITE_OWNER_EMAIL_1 & 0xFF);
        } else {
            byte_value[0] = (byte) (Constant.OPCODE_WRITE_OWNER_EMAIL_2 & 0xFF);
        }
        value_index++;
        byte_value[1] = (byte) (mDataLength & 0xFF);
        value_index++;

        /*Add device owner name*/
        for (name_index = 0; name_index < byte_email.length; name_index++) {
            byte_value[value_index++] = byte_email[name_index];
        }
       /* for (int i = 0; i < byte_value.length; i++) {
            System.out.println("Byte[" + i + "]=" + byte_value[i]);
        }*/
        writedatatoBleDevice(device,byte_value);
       // byte_value = null;

    }


    /**
     * read owner info from target device
     */
    public void readOwnerInfoFromDevice(BleDevice bleDevice) {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_READ_OWNER_INFO & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        writedatatoBleDevice(bleDevice,byte_value);
        byte_value = null;
    }



    public static short OPCODE_VERIFY_DEVICE_INFO = 0x0B;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void verifyDeviceInfoToDevice(String userUniqueKey,BleDevice device) {
        byte[] byte_user_key = userUniqueKey.getBytes(StandardCharsets.US_ASCII);
        System.out.println("Length=" + byte_user_key.length);
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



    public void writedatatoBleDevice(final BleDevice bleDevice,byte [] datasent)
    {
        BleManager.getInstance().write(
                bleDevice,
                "0000ab00-0143-0800-0008-e5f9b34fb000",
                "0000ab01-0143-0800-0008-e5f9b34fb000",
                datasent,
                new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

                        if(fragmentConfigureDeviceAlive==true){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("FastBle_DeBug---->FCD onWriteSuccess");
                                    // notifyDataWriteOnCharctersticChanged(bleDevice);

                                }
                            });
                        }
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {

                        if(fragmentConfigureDeviceAlive==true){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("FastBle_DeBug---->FCD onWriteFailure");
                                }
                            });
                        }

                    }
                });
    }

    String  OwnerNameObtainedFromDevice="";
    String  OwnerEmailObtainedFromDevice="";
    String  OwnerNumnerObtainedFromDevice="";
    int numberOfTriestoWrite=0;
    public void notifyDataWriteOnCharctersticChanged(final BleDevice bleDevice)
    {

        BleManager.getInstance().notify(
                bleDevice,
                "0000ab00-0143-0800-0008-e5f9b34fb000",
                "0000ab01-0143-0800-0008-e5f9b34fb000",
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {

                        if(fragmentConfigureDeviceAlive==true){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("FastBle_DeBug---->FCD notifyDataWriteOnCharctersticChanged onNotifySuccess");
                                }
                            });
                        }
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {


                        if(fragmentConfigureDeviceAlive==true){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("FastBle_DeBug---->FCD notifyDataWriteOnCharctersticChanged onNotifyFailure");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(final byte[] data) {

                        if(fragmentConfigureDeviceAlive==true){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                        String hex = HexUtil.encodeHexStr(data);
                                        StringBuilder output = new StringBuilder();
                                        for (int i = 0; i < hex.length(); i+=2) {
                                            String str = hex.substring(i, i+2);
                                            output.append((char)Integer.parseInt(str, 16));
                                        }
                                        if(hex.toUpperCase().startsWith("01"))
                                        {
                                            int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                            String strOwnerName = hex.substring(4, (4 + msgLength));
                                            strOwnerName = BLEUtility.convertHexToString(strOwnerName);
                                            System.out.println("FastBle_DeBug---->FCD onCharacteristicChanged strOwnerName  = "+strOwnerName);
                                            OwnerNameObtainedFromDevice=strOwnerName;
                                        }


                                        else if(hex.toUpperCase().startsWith("02"))
                                        {
                                            int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                            String strOwnerPhoneNo = hex.substring(4, (4 + msgLength));
                                            strOwnerPhoneNo = BLEUtility.convertHexToString(strOwnerPhoneNo);
                                            System.out.println("FastBle_DeBug---->FCD onCharacteristicChanged strOwnerPhoneNo = "+strOwnerPhoneNo);
                                            OwnerNumnerObtainedFromDevice=strOwnerPhoneNo;
                                        }

                                        else if(hex.toUpperCase().startsWith("03"))
                                        {
                                            int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                            String strOwnerEmail = hex.substring(4, (4 + msgLength));
//                                        System.out.println("strOwnerEmail=" + strOwnerEmail);
                                            strOwnerEmail = BLEUtility.convertHexToString(strOwnerEmail);
                                            System.out.println("FastBle_DeBug---->FCD onCharacteristicChanged strOwnerEmail 1 = "+strOwnerEmail);
                                            OwnerEmailObtainedFromDevice=strOwnerEmail;
                                            if(checkOwnerDetailsfromDevice(OwnerNameObtainedFromDevice,OwnerNumnerObtainedFromDevice,OwnerEmailObtainedFromDevice))
                                            {
                                                /**
                                                 * This is used to add the device in the arraylist to make the owner status of the device as true.
                                                 */
                                                VoOwnerStatusForDevice voOwnerStatusForDevice=new VoOwnerStatusForDevice();
                                                voOwnerStatusForDevice.setBleAddress(bleDevice.getMac());
                                                voOwnerStatusForDevice.setOwnerstatus(true);
                                                mActivity.mArraylistOwnerstatusinfo.add(voOwnerStatusForDevice);

                                                fragmentConfigureDeviceSucess=true;
                                                if (!image_selected) {
                                                    System.out.println("FastBle_DeBug---->FCD onCharacteristicChanged strOwnerEmail 1 = "+strOwnerEmail);
                                                    Add_Device_API_without_image();
                                                }
                                                else {
                                                    Add_Device_API_with_image();
                                                }
                                            }
                                            else {
                                                numberOfTriestoWrite++;
                                                if(numberOfTriestoWrite<3)
                                                {
                                                    if (mStartDeviceOwnershipTimer != null)
                                                        mStartDeviceOwnershipTimer.cancel();
                                                    if (mStartDeviceOwnershipTimer != null)
                                                        mStartDeviceOwnershipTimer.start();
                                                }else
                                                {
                                                    System.out.println("FastBle_DeBug---->FCD treid 3 times then disconnected "+bleDevice.getMac());
                                                    BleManager.getInstance().getBleBluetooth(bleDevice).disconnect();
                                                }
                                            }
                                        }

                                        else if(hex.toUpperCase().startsWith("04"))
                                        {
                                            int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                            String strOwnerEmail1 = hex.substring(4, (4 + msgLength));
//                                        System.out.println("strOwnerEmail1=" + strOwnerEmail1);
                                            strOwnerEmail1 = BLEUtility.convertHexToString(strOwnerEmail1);
                                            System.out.println("FastBle_DeBug---->FCD onCharacteristicChanged strOwnerEmail 2 = "+strOwnerEmail1);
                                        }
                                        System.out.println("FastBle_DeBug---->FCD---->Values "+hex);

                                    }

                                }
                            });
                        }
                    }
                });

    }



   /* //Randon Number..
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private long generateRandomNumber(int n) {
        long min = (long) Math.pow(10, n - 1);
        return ThreadLocalRandom.current().nextLong(min, min * 10);
    }*/

    /*static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }*/

    private boolean checkOwnerDetailsfromDevice(String nameObtainedfromDevice,String phoneNoObtainedfromDevice,String emailObtainedfromDevice)
    {
        System.out.println("FastBle_DeBug NameChecking "+nameObtainedfromDevice+" "+phoneNoObtainedfromDevice+" "+emailObtainedfromDevice);
        boolean result=false;
        if(mStringOwnerEmail.length()>18){
            if(nameObtainedfromDevice.equalsIgnoreCase(mStringOwnerName) &&phoneNoObtainedfromDevice.equalsIgnoreCase(mStringOwnerContactNo)) {
                if(emailObtainedfromDevice.equalsIgnoreCase(mStringOwnerEmail.substring(0,18))){
                    result =true;
                }
            }
        }else{
            if(nameObtainedfromDevice.equalsIgnoreCase(mStringOwnerName)&&phoneNoObtainedfromDevice.equalsIgnoreCase(mStringOwnerContactNo)&&
                    emailObtainedfromDevice.substring(0, mStringOwnerEmail.length()).equalsIgnoreCase(mStringOwnerEmail)){
                result =true;
            }
        }
        return result;
    }

    public BluetoothAdapter getFragmentFragmentConfigureBluetoothAdapter()
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
    private boolean checkConnectionStatusDevice(){
        boolean result=false;
        if(getFragmentFragmentConfigureBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=getFragmentFragmentConfigureBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(connectedBleAddress);
                bleDevice=new BleDevice(getBleDevice);
                final BluetoothDevice device_Conn = bluetoothAdapter.getRemoteDevice(connectedBleAddress);
                if (BleManager.getInstance().isConnected(new BleDevice(device_Conn))) {
                    result=true;
                }else{
                    result=false;
                }
            }else{
                result=false;
                mActivity.giveCustomToastBluetoothNotEnabled();
            }
        }else{
            result=false;
            mActivity.giveCustomToastBluetoothNotEnabled();
        }
        return result;
    }


    private class DeviceConnectionTimer extends CountDownTimer {
        public DeviceConnectionTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(fragmentConfigureDeviceSucess==true){
                System.out.println("FragmentConfigureDevice Timer Canncelled");
                mStartDeviceConnectionTimer.cancel();
            }
        }

        @Override
        public void onFinish() {
            mActivity.mUtility.HideProgress();
            if(fragmentConfigureDeviceSucess==false){

                if(fragmentConfigureDeviceAlive==true){
                    if(connectedBleAddress.length()>3){
                        mActivity.diconnectDevice(connectedBleAddress);
                    }
                    mActivity.mUtility.errorDialogWithCallBack("KUURV","Something went wrong.\nPlease try again later.", 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {

                            mActivity.onBackPressed();

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
