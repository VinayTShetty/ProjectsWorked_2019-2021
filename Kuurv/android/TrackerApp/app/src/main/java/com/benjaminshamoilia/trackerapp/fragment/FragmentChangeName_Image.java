package com.benjaminshamoilia.trackerapp.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.db.DataHolder;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.vo.VoAddDeviceData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static android.app.Activity.RESULT_OK;
import static com.benjaminshamoilia.trackerapp.MyApplication.getAppContext;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentChangeName_Image extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    @BindView(R.id.frg_config_device_et_device_name) AppCompatEditText mEditTextDeviceName;
    @BindView(R.id.frg_config_device_btn_finish) AppCompatButton mButtonFinish;

    @BindView(R.id.fragment_change_device_name)
    TextInputLayout mTextInputlayoutDeviceAliasname;

    private boolean deviceNameChanged=false;
    private boolean deviceImageChnaged=false;
    String mStringDeviceName;
    int deviceAddStatus = 0;
    //Vinay Code
    @BindView(R.id.frg_add_device_iv_scan)
    ImageView SelectImage=null;
    private PreferenceHelper mPreferenceHelper;
    private Bitmap ObtainedImage; //Camera...

    private final int CAMERA_REQUEST_CODE = 20;
    private boolean image_selected=false;
    private File filepath;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    /**
     * Rest API calls intialization
     */
    private API mApiService;
    Utility mUtility;
    private Retrofit mRetrofit;

    URL ImageUrl = null;
    InputStream is = null;
    Bitmap bmImg = null;



    //Vinay Code

    String Connected_ble_address;
    boolean isNeverAskPermissionCheck = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_change_nameimage, container, false);
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


        //Vinay Code

        /**
         * Rest API calls code....
         */

        mPreferenceHelper= new PreferenceHelper(getActivity());
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
                mActivity.onBackPressed();
            }
        });


        mTextInputlayoutDeviceAliasname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutDeviceAliasname.setErrorEnabled(false);
                //  textInputEmail.setError("");
                deviceNameChanged=true;
                mTextInputlayoutDeviceAliasname.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutDeviceAliasname.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Bundle b = getArguments();

        Connected_ble_address=b.getString("ble_address");
        get_user_details( b.getString("ble_address"));   // no image   //  kjrjrcdjk6cc           // image gorbg1ts66eh


      /*  if(mActivity.isDevicesConnected)
        {
            db_latitude=mActivity.latitude;
            db_longitude=mActivity.longitude;

            System.out.println("Fragment_change_name_latitude ="+mActivity.latitude);
            System.out.println("Fragment_change_name_longitude ="+mActivity.longitude);
        }*/

        System.out.println(" Photo server URL ="+db_photo_serverURL);

       /* if(str != null && !str.isEmpty()){}*/


        if(db_photo_serverURL != null && !db_photo_serverURL.isEmpty()&&db_photo_serverURL.toString().length()>4)
        {
            Glide.with(SelectImage.getContext())
                    .load(db_photo_serverURL)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(SelectImage);

        }else { System.out.println("Here we Obtain No image");
        SelectImage.setImageResource(R.drawable.ic_default_logo);
        }

       /*     if(db_photo_serverURL.toString().length()>4)
        {
            Glide.with(SelectImage.getContext())
                    .load(db_photo_serverURL)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(SelectImage);
        }
        else
        {
            System.out.println("Here we Obtain No image");
            SelectImage.setImageResource(R.drawable.ic_default_logo);

        }*/
        mEditTextDeviceName.setText(db_device_name);
        return mViewRoot;
    }



    @OnClick(R.id.frg_config_device_btn_finish)
    public void onButtonFinishClick(View mView) {
        if (isAdded()) {
            System.out.println("Finsih Button Clickeddd");
            mStringDeviceName = mEditTextDeviceName.getText().toString().trim();
            if (mStringDeviceName.equalsIgnoreCase("")) {
                mTextInputlayoutDeviceAliasname.setError("Please enter device name"); // This is the helper text i.e Red color
                mTextInputlayoutDeviceAliasname.setHintEnabled(false);
                return;
            }

            if(mStringDeviceName.equalsIgnoreCase(db_device_name))
            { deviceNameChanged=false; }

            System.out.println("deviceNameChanged="+deviceNameChanged);
            System.out.println("deviceImageChnaged="+deviceImageChnaged);
            if(!deviceNameChanged&&!deviceImageChnaged)
            {
                mActivity.onBackPressed();
                return;
            }


            if (!mUtility.haveInternet()) {
                mUtility.errorDialogWithCallBack("KUURV","Device has not been \n updated on web due to no \n internet connection", 3, true, new onAlertDialogCallBack() {
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

            if(image_selected)
            { createDirectoryAndSaveFile(ObtainedImage, Connected_ble_address + ".jpg");}
            if(!image_selected){
                System.out.println("FragmentChangeName_Image>>>>>AddDevice API called");
                Add_Device_API_without_image();
            }else{Add_Device_API_with_image();}
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mUtility.ShowProgress("Please Wait", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.mUtility.HideProgress();
            }
        }, 1000);
    }



    @Override
    public void onPause() {
        super.onPause();
    }




    private String CheckRecordExistInUserAccountDB(String values,String table_name,String ColumnValues) {
        DataHolder mDataHolder;
        String url = "select * from " +table_name + " where " + ColumnValues + "= '" + values + "'";

        System.out.println("Fragment_configure_Device>>>>>>CheckRecordExistInUserAccountDB \n"+url);

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


    /* To check wheather the User Info is stored in the SQL ite or not...
    if avaliable then don t show the Edit text gmail and password and contact details
    if not avaliabel the show all the fieds */





    // saving the image file to the local Storage
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/Tracker");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Tracker/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Tracker/"), fileName);
        filepath=file;
        System.out.println("Fragment_configure_Device>>>>>>Response_file_path ="+file.getPath());
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
       /* if(CAMERA_REQUEST_CODE == requestCode && resultCode == RESULT_OK){
            image_selected=true;   //Here i am making the image selection is selecetd or not...
            bitmap = (Bitmap)data.getExtras().get("data");
            SelectImage.setImageBitmap(bitmap);
            Calendar cal = Calendar.getInstance();
            createDirectoryAndSaveFile(bitmap, db_ble_address+".jpg");
        }*/


        if (CAMERA == requestCode && resultCode == RESULT_OK) {
        deviceImageChnaged=true;
            System.out.println("Check Chnage camera");
            image_selected = true;   //Here i am making the image selection is selecetd or not...
            ObtainedImage = (Bitmap) data.getExtras().get("data");
            SelectImage.setImageBitmap(ObtainedImage);
          //  createDirectoryAndSaveFile(bitmap, connectedBleAddress + ".jpg");
        }


        else if(GALLERY==requestCode&& resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            deviceImageChnaged=true;
            System.out.println("Check Chnage Gallery");
            Uri uri = data.getData();
            try {
                ObtainedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                SelectImage.setImageBitmap(ObtainedImage);
                image_selected = true;
            //    createDirectoryAndSaveFile(ObtainedImage, connectedBleAddress + ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Log.d(TAG, String.valueOf(bitmap));

        }


    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (isAdded())
        {
            /*Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(photoCaptureIntent, CAMERA_REQUEST_CODE);*/

            callMarshMallowParmession();
        }
    }


// Vinay Code
    /**
     * This method is used for adding the device  when image is not selected from the user
     */


    String db_user_id="";
    String db_ble_address="";
    String db_device_name="";
    String db_device_type="";
    String db_latitude="";
    String db_longitude="";
    String db_tracker_device_alert="";
    String db_marked_lost="";
    String db_is_active="";
    String db_contact_name="";
    String db_contact_email="";
    String db_contact_mobile="";
    String db_photo_localURL="";
    String db_photo_serverURL="";
    String db_correction_status="";
    String db_updated_time="";
    String db_created_time="";


    private void get_user_details(String Ble_adddress)
    {

        Map<String,String> getUSer_info=  mActivity.mDbHelper.getBLE_Set_Info(mActivity.mDbHelper.getReadableDatabase(),
                mActivity.mDbHelper.Device_Table,
                "ble_address",
                Ble_adddress
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
                db_user_id = (String)getUSer_info.get("user_id");
                db_ble_address= (String)getUSer_info.get("ble_address");
                db_device_name=(String) getUSer_info.get("device_name");
                db_device_type=(String) getUSer_info.get("device_type");
                db_latitude=(String) getUSer_info.get("latitude");
                db_longitude=(String) getUSer_info.get("longitude");
                db_tracker_device_alert=(String) getUSer_info.get("tracker_device_alert");
                db_marked_lost=(String) getUSer_info.get("marked_lost");
                db_is_active=(String) getUSer_info.get("is_active");
                db_contact_name=(String) getUSer_info.get("contact_name");
                db_contact_email=(String) getUSer_info.get("contact_email");
                db_contact_mobile=(String) getUSer_info.get("contact_mobile");
                db_photo_localURL=(String) getUSer_info.get("photo_localURL");
                db_correction_status=(String) getUSer_info.get("correction_status");
                db_photo_serverURL=(String) getUSer_info.get("photo_serverURL");

            }
        }


    }




    private void Add_Device_API_without_image()
    {
        mUtility.hideKeyboard(getActivity());
        mUtility.ShowProgress("Please Wait", true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id",mPreferenceHelper.getUserId() );
        params.put("ble_address", db_ble_address);
        params.put("device_name",mStringDeviceName );
        params.put("correction_status",db_correction_status);   //
        params.put("device_type","1" );
        params.put("longitude",mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId()));
        params.put("latitude",mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId()) );
        params.put("tracker_device_alert",db_tracker_device_alert);
        params.put("marked_lost","NA" );
        params.put("is_active","1" );
        params.put("contact_name",db_contact_name);
        params.put("contact_email",db_contact_email );
        params.put("contact_mobile",db_contact_mobile);
        final Call<VoAddDeviceData> mLogin = mApiService.Add_Device_API_without_image(params);
        mLogin.enqueue(new Callback<VoAddDeviceData>() {
            @Override
            public void onResponse(Call<VoAddDeviceData> call, Response<VoAddDeviceData> response) {
                mUtility.HideProgress();
                VoAddDeviceData configure_device=response.body();

                if(configure_device.getMessage().startsWith("Invalid Token")) { mActivity.APICrushLogout(); }
                else
                {
                    System.out.println("FragmentChangenameImage without_Image "+new Gson().toJson(response.body()));
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(mActivity.mDbHelper.user_id, ""+configure_device.getData().getUser_id());
                    mContentValues.put(mActivity.mDbHelper.ble_address, ""+configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase());
                    mContentValues.put(mActivity.mDbHelper.device_name, ""+configure_device.getData().getDevice_name());
                    mContentValues.put(mActivity.mDbHelper.device_type, ""+configure_device.getData().getDevice_type());
                    mContentValues.put(mActivity.mDbHelper.latitude, ""+configure_device.getData().getLatitude());
                    mContentValues.put(mActivity.mDbHelper.longitude, ""+configure_device.getData().getLongitude());
                    mContentValues.put(mActivity.mDbHelper.tracker_device_alert, ""+configure_device.getData().getTracker_device_alert());
                    mContentValues.put(mActivity.mDbHelper.marked_lost, ""+configure_device.getData().getMarked_lost());
                    mContentValues.put(mActivity.mDbHelper.is_active, ""+configure_device.getData().getIs_active());
                    mContentValues.put(mActivity.mDbHelper.contact_name, ""+configure_device.getData().getContact_name());
                    mContentValues.put(mActivity.mDbHelper.contact_email, ""+configure_device.getData().getContact_email());
                    mContentValues.put(mActivity.mDbHelper.contact_mobile, ""+configure_device.getData().getContact_mobile());
                    mContentValues.put(mActivity.mDbHelper.photo_local_url, ""+filepath);
                    mContentValues.put(mActivity.mDbHelper.photo_server_url, ""+configure_device.getData().getDevice_image_path());
                    mContentValues.put(mActivity.mDbHelper.identifier, "NA");
                    mContentValues.put(mActivity.mDbHelper.created_time, ""+configure_device.getData().getCreated_at());
                    mContentValues.put(mActivity.mDbHelper.updated_time, ""+configure_device.getData().getUpdated_at());
                    mContentValues.put(mActivity.mDbHelper.status, ""+configure_device.getData().getCorrection_status());
                    mContentValues.put(mActivity.mDbHelper.is_sync, "NA");
                    mContentValues.put(mActivity.mDbHelper.server_id, ""+configure_device.getData().getId());
                    mContentValues.put(mActivity.mDbHelper.tracker_locAdds, mActivity.retutn_tracekrAddress(Double.parseDouble(configure_device.getData().getLatitude()),Double.parseDouble(configure_device.getData().getLongitude())));

                    String isExistInUserDB=CheckRecordExistInUserAccountDB(configure_device.getData().getBle_address(),mActivity.mDbHelper.Device_Table,mActivity.mDbHelper.ble_address);
                           System.out.println("Fragment_configure_Device>>>>>> isExistInUserDB ="+isExistInUserDB);
                    if (isExistInUserDB.equalsIgnoreCase("-1")) {
                        //  mDbHelper.insertRecord(mDbHelper.mTableUserAccount, mContentValuesUser);
                        int result=mActivity.mDbHelper.insertRecord(mActivity.mDbHelper.Device_Table, mContentValues);
                               System.out.println("Fragment_configure_Device>>>>>>Response_Data_Inserted. Result = "+result);
                    } else {
                        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, mActivity.mDbHelper.ble_address + "=?", new String[]{isExistInUserDB});
                        //    mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, "user_id=", null);
                               System.out.println("Fragment_configure_Device>>>>>>Response_Data_Updated");
                    }
                    mActivity.getDBDeviceList();

                    if (configure_device != null && configure_device.getResponse().equalsIgnoreCase("true")
                            && configure_device.getMessage().startsWith("Device updated successfully."))
                    {


                        mActivity.mUtility.errorDialogWithCallBack("KUURV",configure_device.getMessage().toString(), 0, false, new onAlertDialogCallBack() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                                mActivity.onBackPressed();
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {

                            }
                        });

                    }}
            }

            @Override
            public void onFailure(Call<VoAddDeviceData> call, Throwable t)
            {

            }


        });

    }


    /**
     * This method is used for adding the device  when image is selected from the user
     */
    File compressedImgFile;
    private  void Add_Device_API_with_image()
    {
        mUtility.ShowProgress("Please Wait", true);
        File file = new File(filepath.getPath());
        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"),mPreferenceHelper.getUserId() );
        RequestBody ble_address = RequestBody.create(MediaType.parse("multipart/form-data"), db_ble_address);
        RequestBody device_name = RequestBody.create(MediaType.parse("multipart/form-data"), mStringDeviceName );
        RequestBody correction_status = RequestBody.create(MediaType.parse("multipart/form-data"), db_correction_status);
        RequestBody device_type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"),mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId()) );
        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId()) );
        RequestBody tracker_device_alert = RequestBody.create(MediaType.parse("multipart/form-data"),db_tracker_device_alert);
        RequestBody marked_lost = RequestBody.create(MediaType.parse("multipart/form-data"),db_marked_lost );
        RequestBody is_active = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody contact_name = RequestBody.create(MediaType.parse("multipart/form-data"),db_contact_name );
        RequestBody contact_email = RequestBody.create(MediaType.parse("multipart/form-data"), db_contact_email );
        RequestBody  contact_mobile = RequestBody.create(MediaType.parse("multipart/form-data"),db_contact_mobile );

        MultipartBody.Part device_image =null;



        try {
            compressedImgFile = new Compressor(getActivity()).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        RequestBody request_file = RequestBody.create(MediaType.parse("multipart/form-data"), compressedImgFile);
//   System.out.println("Path = " + compressedImgFile.getAbsolutePath() + "file Size = =" +Integer.parseInt(String.valueOf(compressedImgFile.length()/1024)));

        device_image = MultipartBody.Part.createFormData("device_image", compressedImgFile.getName(), request_file);



        Call<VoAddDeviceData> call=mApiService.Add_Device_API_with_image(
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
                mUtility.HideProgress();
                VoAddDeviceData configure_device=response.body();

                if(configure_device.getMessage().startsWith("Invalid Token")) { mActivity.APICrushLogout(); }
                else {
                    System.out.println("FragmentChangenameImage with_Image "+new Gson().toJson(response.body()));
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(mActivity.mDbHelper.user_id, ""+configure_device.getData().getUser_id());
                    mContentValues.put(mActivity.mDbHelper.ble_address, ""+configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase());
                    mContentValues.put(mActivity.mDbHelper.device_name, ""+configure_device.getData().getDevice_name());
                    mContentValues.put(mActivity.mDbHelper.device_type, ""+configure_device.getData().getDevice_type());
                    mContentValues.put(mActivity.mDbHelper.latitude, ""+configure_device.getData().getLatitude());
                    mContentValues.put(mActivity.mDbHelper.longitude, ""+configure_device.getData().getLongitude());
                    mContentValues.put(mActivity.mDbHelper.tracker_device_alert, ""+configure_device.getData().getTracker_device_alert());
                    mContentValues.put(mActivity.mDbHelper.marked_lost, ""+configure_device.getData().getMarked_lost());
                    mContentValues.put(mActivity.mDbHelper.is_active, ""+configure_device.getData().getIs_active());
                    mContentValues.put(mActivity.mDbHelper.contact_name, ""+configure_device.getData().getContact_name());
                    mContentValues.put(mActivity.mDbHelper.contact_email, ""+configure_device.getData().getContact_email());
                    mContentValues.put(mActivity.mDbHelper.contact_mobile, ""+configure_device.getData().getContact_mobile());
                    mContentValues.put(mActivity.mDbHelper.photo_local_url, ""+filepath);
                    mContentValues.put(mActivity.mDbHelper.photo_server_url, ""+configure_device.getData().getDevice_image_path());
                    mContentValues.put(mActivity.mDbHelper.identifier, "NA");
                    mContentValues.put(mActivity.mDbHelper.created_time, ""+configure_device.getData().getCreated_at());
                    mContentValues.put(mActivity.mDbHelper.updated_time, ""+configure_device.getData().getUpdated_at());
                    mContentValues.put(mActivity.mDbHelper.status, ""+configure_device.getData().getCorrection_status());
                    mContentValues.put(mActivity.mDbHelper.is_sync, "NA");
                    mContentValues.put(mActivity.mDbHelper.server_id, ""+configure_device.getData().getId());
                    mContentValues.put(mActivity.mDbHelper.tracker_locAdds,mActivity.retutn_tracekrAddress(Double.parseDouble(configure_device.getData().getLatitude()),Double.parseDouble(configure_device.getData().getLongitude())));
                    String isExistInUserDB=CheckRecordExistInUserAccountDB(configure_device.getData().getBle_address(),mActivity.mDbHelper.Device_Table,mActivity.mDbHelper.ble_address);
                    //  System.out.println("Fragment_configure_Device>>>>>> isExistInUserDB ="+isExistInUserDB);
                    if (isExistInUserDB.equalsIgnoreCase("-1")) {
                        //  mDbHelper.insertRecord(mDbHelper.mTableUserAccount, mContentValuesUser);
                        int result=mActivity.mDbHelper.insertRecord(mActivity.mDbHelper.Device_Table, mContentValues);
                             System.out.println("Fragment_configure_Device>>>>>>Response_Data_Inserted. Result = "+result);
                    } else {
                        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, mActivity.mDbHelper.ble_address + "=?", new String[]{isExistInUserDB});
                        //    mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.Device_Table, mContentValues, "user_id=", null);
                              System.out.println("Fragment_configure_Device>>>>>>Response_Data_Updated");
                    }

                    mActivity.getDBDeviceList();
                    if (configure_device != null && configure_device.getResponse().equalsIgnoreCase("true")
                            && configure_device.getMessage().startsWith("Device updated successfully."))
                    {


                        mActivity.mUtility.errorDialogWithCallBack("KUURV",configure_device.getMessage().toString(), 0, false, new onAlertDialogCallBack() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                              mActivity.onBackPressed();

                              //  get_user_details("12345678");
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onFailure(Call<VoAddDeviceData> call, Throwable t) {

            }
        });

    }




    private class AsyncTaskExample extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {

                ImageUrl = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bmImg = BitmapFactory.decodeStream(is, null, options);



            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(SelectImage!=null) {

                SelectImage.setImageBitmap(bitmap);
            }else {

            }
        }
    }



    private int GALLERY = 1, CAMERA = 2,CANCEL=3;
    private void showPictureDialog(){
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
    public void removeselectedImage()
    {

    }

    private void persistImage(Bitmap bitmap, String name) {
        File filesDir = getAppContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");
        filepath=imageFile;
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(FragmentChangeName_Image.class.getSimpleName(), "Error writing bitmap", e);
        }
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
}
