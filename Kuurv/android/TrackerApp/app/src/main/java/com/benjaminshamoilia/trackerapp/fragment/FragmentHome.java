package com.benjaminshamoilia.trackerapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benjaminshamoilia.trackerapp.db.DataHolder;
import com.benjaminshamoilia.trackerapp.interfaces.DeviceDisconnectionCallback;
import com.benjaminshamoilia.trackerapp.vo.Vo_Device_Regstd_from_serv;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.exception.BleException;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.DeviceConnectionStatus;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.interfaces.onLocationChangeListner;
import com.benjaminshamoilia.trackerapp.vo.VoDeleteDevice;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libRG.CustomTextView;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.benjaminshamoilia.trackerapp.MainActivity.deviceAddedFromfragmentConigureDevice;
import static com.benjaminshamoilia.trackerapp.MainActivity.mainActivityExecuted;

//import static com.benjaminshamoilia.trackerapp.MainActivity.devicePostion;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentHome extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    static MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;

    @BindView(R.id.fragment_home_device)
    RelativeLayout mRelativeLayoutDevice;

    @BindView(R.id.frg_home_tv_locate)
    CustomTextView mCustomTextViewLocate;
    @BindView(R.id.frg_home_btn_add_device)
    FloatingActionButton mFloatingActionButtonAddDevice;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    static BottomSheetFragment bottomSheetFragment;

    //Getting the current Location in Map...  // vinay code
    static public GoogleMap mMap;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    //Getting the current Location in Map...  // vinay code
//    public ArrayList<Vo_Device_Regstd_from_serv> vo_device_regst_array_list = new ArrayList<Vo_Device_Regstd_from_serv>();
    RequestOptions requestOptions;

    private PreferenceHelper mPreferenceHelper;
    private API mApiService;
    Utility mUtility;
    private Retrofit mRetrofit;

    //Delete Device Variable

    MarkerOptions markerOptions;
    static Marker marker;

    //ble adreess to send pass in bundle.
    public static String bleAddressonTopLayout;
    /**
     * RecycleView Design...
     */

    @BindView(R.id.deviicelist_recycleview)
    RecyclerView mdevicelistRecycleView;

  /*  @BindView(R.id.load_image)
    AppCompatImageView loadImageViewIcon;*/


    @BindView(R.id.frg_home_iv_navigation)
    AppCompatImageView navigationIcon;
    /**
     * Top layout Design
     */

    /**
     * Top layout Design Changes
     *
     * @param savedInstanceState
     */
    @BindView(R.id.raw_add_device_item_tv_device_name)
    TextView mcustomDevicename;

    @BindView(R.id.raw_add_device_item_tv_last_seen)
    TextView mcustomtextDeviceLastseen;



    @BindView(R.id.raw_add_device_iv_device)
    CircleImageView devicebackGroundonTopLayout;

    @BindView(R.id.raw_add_device_item_iv_battery)
    ImageView batteryImageViewonToplayout;

    @BindView(R.id.raw_add_device_item_tv_battery_percentage)
    TextView mcustomTextViewDevicebatteryPercentage;

    public static UserDeviceListAdapter userDeviceListAdapter;

    public static FragmentHome fragmentHome_instance = null;

    private int infoReceivedafterDeleting=-1;
    private static int fragmentHomeExecutedOnce=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        fragmentHome_instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.activity_title_menu);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setTextSize(40);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.NORMAL);
        mActivity.mTextViewTitle.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        mActivity.mImageViewAddDevice.setVisibility(View.VISIBLE);
        mActivity.showBackButton(false);
        mActivity.mPreferenceHelper.setIsDeviceSync(false);
        /**
         * RecycleView Design
         */
        FragmentHomegetDBDeviceList();
         //mActivity.getDBDeviceList();

        userDeviceListAdapter = new UserDeviceListAdapter();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mdevicelistRecycleView.setLayoutManager(mLayoutManager);
        mdevicelistRecycleView.setAdapter(userDeviceListAdapter);

       // FragmentHomegetDBDeviceList();

        System.out.println("The Size of the list= "+mActivity.mArrayListMyDevices.size());

        mPreferenceHelper = new PreferenceHelper(getActivity());
        mUtility = new Utility(getActivity());
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getClientWithAutho())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .build();
        mApiService = mRetrofit.create(API.class);
        bottomSheetFragment = new BottomSheetFragment();


// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_default_logo);
        requestOptions.error(R.drawable.ic_default_logo);
        requestOptions.centerCrop();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
        requestOptions.skipMemoryCache(false);
        requestOptions.dontAnimate();
        requestOptions.dontTransform();
        requestOptions.priority(Priority.HIGH);

        mActivity.mImageViewAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    /**
                     * RecycleView Design Flags
                     */
                    mdevicelistRecycleView.setVisibility(mViewRoot.INVISIBLE);
                    showRecycleView = true;

                    bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
                }
            }
        });

        mActivity.setLocationChangeListnear(new onLocationChangeListner() {
            @Override
            public void onLocationChange(Location mLocation) {
                if (mActivity.mArrayListMyDevices.size() == 0) {
                    showCurrentLocationMarker();
                }
            }
        });


            if (mActivity.mArrayListMyDevices.size() > 0) {


                mRelativeLayoutDevice.setVisibility(View.VISIBLE);
                navigationIcon.setVisibility(View.VISIBLE);
                if (mActivity.mPreferenceHelper.getDevicePostion().equalsIgnoreCase("")) {
                    setDetailsonTopLayout(0, null, null);
                    String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(0).getBle_address());

                    if(getFragmentHomeBluetoothAdapter()!=null){
                        BluetoothAdapter bluetoothAdapter=getFragmentHomeBluetoothAdapter();
                        if(bluetoothAdapter.isEnabled()){
                            boolean connectionStatus = BleManager.getInstance().isConnected(getBleAddresswithUppercaseAndSymbol);
                            topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                        }else{
                            topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                        }
                    }else{
                        topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                        mActivity. giveCustomToastBluetoothNotEnabled();
                    }
                } else {

                    if(mActivity.mArrayListMyDevices.size()>0){
                        //Here it might Crash when sometest case so check it out..
                        int savedpostion=Integer.parseInt(mActivity.mPreferenceHelper.getDevicePostion());
                         int sizeofthelist=mActivity.mArrayListMyDevices.size()-1;
                         /**
 * This logic i have applied to check wheather the sharedPreferencevalue saved is valid or not..Coz sometimes its not giving proper response.
 */
                        if(savedpostion<=sizeofthelist){
                            System.out.println("-------------------->"+mActivity.mPreferenceHelper.getDevicePostion());
                            setDetailsonTopLayout(Integer.parseInt(mActivity.mPreferenceHelper.getDevicePostion()), null, null);
                            String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(Integer.parseInt(mActivity.mPreferenceHelper.getDevicePostion())).getBle_address());

                            if(getFragmentHomeBluetoothAdapter()!=null){
                                BluetoothAdapter bluetoothAdapter =getFragmentHomeBluetoothAdapter();
                                if(bluetoothAdapter.isEnabled()){
                                    boolean connectionStatus = BleManager.getInstance().isConnected(getBleAddresswithUppercaseAndSymbol);
                                    topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                }else{
                                    topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                    mActivity. giveCustomToastBluetoothNotEnabled();
                                }
                            }else{
                                topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                mActivity. giveCustomToastBluetoothNotEnabled();
                            }
                        }else{
                            setDetailsonTopLayout(0, null, null);
                            String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(0).getBle_address());

                            if(getFragmentHomeBluetoothAdapter()!=null){
                                BluetoothAdapter bluetoothAdapter=getFragmentHomeBluetoothAdapter();
                                if(bluetoothAdapter.isEnabled()){
                                    boolean connectionStatus = BleManager.getInstance().isConnected(getBleAddresswithUppercaseAndSymbol);
                                    topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                }else{
                                    topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                }
                            }else{
                                topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                mActivity. giveCustomToastBluetoothNotEnabled();
                            }
                        }
                    }

                }
            }

        mdevicelistRecycleView.setVisibility(mViewRoot.INVISIBLE);


        mActivity.setDeviceConnectionStatus(new DeviceConnectionStatus() {
            @Override
            public void ConnectionStatus(String BleAddress, String ConnectionStatus, BleDevice bleDevice) {
             //   System.out.println("MainActivity_Test ConnectionStatus Enter "+BleAddress +"-->"+ConnectionStatus );
                if(bleAddressonTopLayout!=null){
                    if(bleAddressonTopLayout.length()>2){
                        if(bleAddressonTopLayout.replace(":", "").toLowerCase().equalsIgnoreCase(BleAddress.replace(":", "").toLowerCase())){
                            int postionObtained= mActivity.getPositionofBleObjectInArrayList(BleAddress);
                            if(postionObtained!=-1){
                                if(ConnectionStatus.equalsIgnoreCase("Connected")){
                                    topLayoutColorChnageOnConnection(true, postionObtained);
                                }else {
                                    topLayoutColorChnageOnConnection(false, postionObtained);
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void infoReceievedFrombleDevice(String info) {

            }

            @Override
            public void deleteinfoReceievedfromBleDevice(int ackreceieved,BleDevice passedDevice) {
                if(ackreceieved==1){
                    System.out.println("Deleting_Tracker MainActivity ACK Receieved = "+passedDevice.getMac()+" ACK = "+ackreceieved);
                    deleteOwnerInfoTimer.cancel();
                    BleManager.getInstance().disconnect(passedDevice);
                    Delete_Device_ble_API(bleAddressonTopLayout.toLowerCase().replace(":",""));
                    delteImage_From_Phone(bleAddressonTopLayout.toLowerCase().replace(":",""));
                }
            }
            @Override
            public boolean bluetoothturningOff(boolean bleoff) {
                if(bleoff){
                            if(mActivity.mArrayListMyDevices.size()>0){
                                if(bleAddressonTopLayout!=null){
                                   if(bleAddressonTopLayout.length()>2){
                                       topLayoutColorChnageOnConnection(false,mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout));
                                   }
                                }
                            }
                }
                return false;
            }

            @Override
            public void readbatterystatusFromDevice(int batterystatus) {
                // Here Read the Battery status from the Device and Update the UI.
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



        deleteOwnerInfoTimer=new DeleteOwnerInfoTimer(7000, 1000);
        fristtimeProgressbarTimer = new FristtimeProgressbar(10000, 1000); // fristtimeProgressbarTimer = new FristtimeProgressbar(14000, 1000);
        fragmetn_homeDestroyed=false;
        return mViewRoot;
    }


    @OnClick(R.id.frg_home_iv_navigation)
    public void onNavigationClick(View mView)
    {
        if (isAdded()) {

            if (mActivity.mArrayListMyDevices.size() > 0) {
                String uri = "";
                String userlatitudeposition = mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId()).substring(0, 7);;
                String userlongitudeposition = mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId()).substring(0, 7);;
                String deviceLatitude = mActivity.mArrayListMyDevices.get(mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout)).getLatitude().substring(0, 7);
                String deviceLongitude =mActivity.mArrayListMyDevices.get(mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout)).getLongitude().substring(0, 7);
                System.out.println("userlatitudeposition =" + userlatitudeposition);
                System.out.println("userlongitudeposition =" + userlongitudeposition);
                System.out.println("deviceLatitude =" + deviceLatitude);
                System.out.println("deviceLongitude = " + deviceLongitude);

                /**
                 * This logic is used,if the same latlong
                 */
                if (userlatitudeposition.equalsIgnoreCase(deviceLatitude)
                        &&
                        userlongitudeposition.equalsIgnoreCase(deviceLongitude)) {
                    uri = "http://maps.google.com/maps?q=loc:" + userlatitudeposition + "," + userlongitudeposition;
                } else {
                    uri = "http://maps.google.com/maps?saddr=" + mActivity.mArrayListMyDevices.get(mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout)).getLatitude() + "," + mActivity.mArrayListMyDevices.get(mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout)).getLongitude() + "&daddr=" + mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId()) + "," + mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId());

                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.frg_home_btn_add_device)
    public void onFloatingAddButtonClick(View mView) {
        if (isAdded()) {
            FragmentAddDevice mFragmentAddDevice = new FragmentAddDevice();
            mActivity.replacesFragment(mFragmentAddDevice, true, null, 0);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.showBackButton(false);
        mActivity.mImageViewBack.setVisibility(View.GONE);
       // mActivity.getDBDeviceList();
        if (mActivity.mArrayListMyDevices.size() > 1) {
            if (bleAddressonTopLayout != null) {
                if (bleAddressonTopLayout.length() > 1) {
                      if(getFragmentHomeBluetoothAdapter()!=null){
                          BluetoothAdapter bluetoothAdapter=getFragmentHomeBluetoothAdapter();
                          if(bluetoothAdapter.isEnabled()){
                              boolean connectionStatus = BleManager.getInstance().isConnected(bleAddressonTopLayout);
                              topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout));
                          }else{
                              topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout));
                              mActivity. giveCustomToastBluetoothNotEnabled();
                          }
                      }else{
                          topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout));
                          mActivity. giveCustomToastBluetoothNotEnabled();
                      }
                }
            }
        }else{
         //   mActivity.getDBDeviceList();
        }

      //  final Handler handler = new Handler();
       mActivity.mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mainActivityExecuted){
                    mainActivityExecuted=false;
                    int recordAvaliableCount=getTableCountforRegisteredDevices();
                    System.out.println("Size_Issue FragmentHome = "+recordAvaliableCount);
                    FragmentHomegetDBDeviceList();
                    if(recordAvaliableCount>0||mActivity.mArrayListMyDevices.size()>0){
                        mRelativeLayoutDevice.setVisibility(View.VISIBLE);
                        navigationIcon.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                fristtimeProgressbarTimer.start();
                                System.out.println("Design_Issue  Timer ProgressBar Started ");
                            }
                        }).start();
                        String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(0).getBle_address());
                        setDetailsonTopLayout(0, null, null);
                        if(getFragmentHomeBluetoothAdapter()!=null){
                            BluetoothAdapter bluetoothAdapter=getFragmentHomeBluetoothAdapter();
                            if(bluetoothAdapter.isEnabled()){
                                boolean connectionStatus = BleManager.getInstance().isConnected(getBleAddresswithUppercaseAndSymbol);
                                topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                            }else{
                                topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                mActivity. giveCustomToastBluetoothNotEnabled();
                            }
                        }else{
                            topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                            mActivity. giveCustomToastBluetoothNotEnabled();
                        }
                    }else{

                    }
                }else{
                    System.out.println("Design_Issue  Timer mainActivityExecuted "+mainActivityExecuted);
                }

            }
        }, 1000);



    }
    FristtimeProgressbar fristtimeProgressbarTimer;
    private class FristtimeProgressbar extends CountDownTimer {
        public FristtimeProgressbar(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(mActivity.crushlogout==true){
                mActivity.crushlogout=false;
                mActivity.mUtility.HideProgress();
                fristtimeProgressbarTimer.cancel();
                fristtimeProgressbarTimer.onFinish();
            }

            else{
                mActivity.mUtility.ShowProgress("Acessing Tracker", false);
                if(getFragmentHomeBluetoothAdapter()!=null){
                    BluetoothAdapter bluetoothAdapter=getFragmentHomeBluetoothAdapter();
                    if(bluetoothAdapter.isEnabled()){
                        if(mActivity.mArrayListMyDevices.size()>0){
                            String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(0).getBle_address());
                            boolean connectionStatus = BleManager.getInstance().isConnected(getBleAddresswithUppercaseAndSymbol);
                            if(connectionStatus){
                                fristtimeProgressbarTimer.cancel();
                                fristtimeProgressbarTimer.onFinish();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onFinish() {
            mActivity.mUtility.HideProgress();
        }
    }

    public static boolean fragmetn_homeDestroyed = false;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mCurrentLocation = null;
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        fragmetn_homeDestroyed = true;
        /**
         * Here i am saving the Device postion so that the user when switches to different screen.
         * i will show the same Device,by fetching the position from the ArrayList...
         */
        if (selected_devicePostion != -1) {
            mActivity.mPreferenceHelper.setDeviecPostion(Integer.toString(selected_devicePostion));
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        System.out.println("ONMap ready called");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                initMap();
            }
        } else {
            initMap();
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mActivity.mArrayListMyDevices.size()<0){
                    System.out.println("Maap clicked");
                }
            }
        });

        setmarkerofDeviceToppostion();

    }


    private void initMap() {
        buildGoogleApiClient();
        System.out.println("JD=MAP initMap");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        System.out.println("JD=MAP initMap size=" + mActivity.mArrayListMyDevices.size());
        if (mActivity.mArrayListMyDevices.size() == 0) {
            showCurrentLocationMarker();
        }
        String latitudeFromDb = mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId());
        String longitudeFromDb = mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId());
        if (mActivity.mDbHelper.tableIsEmpty(mActivity.mDbHelper.Device_Table)) // If table is empty means no device is added...
        {

            if (latitudeFromDb != null && !latitudeFromDb.isEmpty() && longitudeFromDb != null && !longitudeFromDb.isEmpty()) {

                markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(latitudeFromDb), Double.parseDouble(longitudeFromDb)));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                mMap.clear();
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15.0f));
                marker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitudeFromDb), Double.parseDouble(longitudeFromDb)), 15.0f));

            } else {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    mMap.clear();
                    //                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15.0f));
                    marker = mMap.addMarker(markerOptions);
                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mActivity.mLastLocation.getLatitude(), mActivity.mLastLocation.getLongitude()), 15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
                }
            }
        }


    }

    public void showCurrentLocationMarker() {
        mMap.setMyLocationEnabled(true);
        LatLng mLatLng = null;

        String latitudeFromDb = mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId());
        String longitudeFromDb = mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId());

        try {

            if (latitudeFromDb != null && !latitudeFromDb.isEmpty() && longitudeFromDb != null && !longitudeFromDb.isEmpty()) {
                mLatLng = new LatLng(Double.parseDouble(mActivity.getlatitudeFromDB(mPreferenceHelper.getUserId())), Double.parseDouble(mActivity.getlongitudeFromDB(mPreferenceHelper.getUserId())));
            } else {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    mLatLng = new LatLng(latitude, longitude);
                /*    System.out.println("latitude internet=" + location.getLongitude());
                    System.out.println("Longitude internet=" + location.getLongitude());
                    System.out.println("Got Latitude from Internet");*/
                }

            }


        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (mLatLng == null) {
            System.out.println("latlong is null");
            return;
        }
        if (mCurrentLocation == null) {
            mCurrentLocation = new Location("Loc");
            mCurrentLocation.setLatitude(mLatLng.latitude);
            mCurrentLocation.setLongitude(mLatLng.longitude);
            markerOptions = new MarkerOptions().position(mLatLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            mMap.clear();
            marker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15.0f));

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode){
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    }
                }else{
                    showExplanationDialogFragmentHome();
                }
                return;
            }
        }


        //buildGoogleApiClient();   this method implementation should be completed when the location permission has been granted..
    }

    public static class BottomSheetFragment extends BottomSheetDialogFragment {
        public BottomSheetFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View mView = inflater.inflate(R.layout.bottom_sheet_menu, container, false);
            AppCompatButton mButtonPhoneAlert = (AppCompatButton) mView.findViewById(R.id.frg_bottom_sheet_btn_phone_alert);
            final AppCompatButton mButtonMarkAsLost = (AppCompatButton) mView.findViewById(R.id.frg_bottom_sheet_btn_mar_as_lost);
            AppCompatButton mButtonChangeName = (AppCompatButton) mView.findViewById(R.id.frg_bottom_sheet_btn_change_name);
            AppCompatButton mButtonDeleteDevice = (AppCompatButton) mView.findViewById(R.id.frg_bottom_sheet_btn_delete_device);

            mButtonPhoneAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetFragment.dismiss();

                    if(mActivity.mArrayListMyDevices.size() > 0){
                        Bundle bundle = new Bundle();
                        bundle.putString("connectedBleAddress", bleAddressonTopLayout);
                        mActivity.replacesFragment(new FragmentPhoneAlertSetting(), true, bundle, 0);
                    }else{
                        mActivity.replacesFragment(new FragmentPhoneAlertSettingNoDevice(), true, null, 0);
                    }

                }
            });


            mButtonChangeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetFragment.dismiss();
                    //Chnage Name and Image..Here if the user is addded any device then open this fragment else no device added.

                    if (mActivity.mArrayListMyDevices.size() > 0) {
                        String getBleAddres = bleAddressonTopLayout.replace(":", "").toLowerCase();
                        Bundle bundle = new Bundle();
                        bundle.putString("ble_address", getBleAddres);
                        mActivity.replacesFragment(new FragmentChangeName_Image(), true, bundle, 0);

                    } else {
                        mActivity.mUtility.errorDialog("KUURV","There is no device found", 3, true);
                    }


                }
            });
            mButtonDeleteDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetFragment.dismiss();

                  if(fragmentHome_instance.getFragmentHomeBluetoothAdapter()==null){
                      mActivity.giveCustomToastBluetoothNotEnabled();
                  }
                  if(fragmentHome_instance.getFragmentHomeBluetoothAdapter()!=null){
                   BluetoothAdapter FragmentHomeBluetoothAdapter=fragmentHome_instance.getFragmentHomeBluetoothAdapter();
                   if(!FragmentHomeBluetoothAdapter.isEnabled()){
                       mActivity.giveCustomToastBluetoothNotEnabled();
                       return;
                   }
                  }

                    if (mActivity.mArrayListMyDevices.size() == 0) {
                        mActivity.mUtility.errorDialog("KUURV","There is no device to delete", 3, true);
                        return;
                    }
                    mActivity.mUtility.errorDialogWithYesNoCallBack("KUURV", "Are you sure want to delete \n this device?", "Yes", "No", true, 1, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            if (!mActivity.mUtility.haveInternet()) {
                                mActivity.mUtility.errorDialog("KUURV","Please check your internet \n connection and try again.", 3, true);
                                return;
                            }else{
                                // Here Update the new Counter logic as per the changes after doing some Testing...



                                mActivity.showProgress("Removing Tracker", false);


                                if(mActivity.mUtility.haveInternet()){
                                    mActivity.mPreferenceHelper.setDeviecPostion("");
                                    if(bleAddressonTopLayout!=null&&bleAddressonTopLayout.length()>1){
                                        fragmentHome_instance.deleteBleAddress.deletingedBleAddress(bleAddressonTopLayout);
                                        if(mActivity.getMainActivityBluetoothAdapter()!=null){
                                            final BluetoothDevice deletingDevice = mActivity.getMainActivityBluetoothAdapter().getRemoteDevice(bleAddressonTopLayout);
                                            BleDevice convertedgDeletingDevice=new BleDevice(deletingDevice);
                                            if(BleManager.getInstance().isConnected(convertedgDeletingDevice)){
                                                fragmentHome_instance.deleteDevicethroughBle(convertedgDeletingDevice);
                                               // System.out.println("Deleting_Tracker MethodCalled deleteDevicethroughBle");
                                            }else{
                                                Delete_Device_ble_API(bleAddressonTopLayout.toLowerCase().replace(":",""));
                                                delteImage_From_Phone(bleAddressonTopLayout.toLowerCase().replace(":",""));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }
            });

            return mView;
        }
    }


    private void drawMarker(LatLng point, String Device_name, String url, String bleAddress) {
        mMap.clear();
        markerOptions = new MarkerOptions().position(point);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15.0f));
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        String LocationAddressofTracker = mActivity.getAddressFromDB(bleAddress);
        System.out.println("Obtained Address ==" + LocationAddressofTracker);
        if (LocationAddressofTracker != null && !LocationAddressofTracker.isEmpty()) {
            LocationAddressofTracker = "Near " + LocationAddressofTracker.replace("null", "");
        } else {
            LocationAddressofTracker = "";
            /**
             * Here i am double check if the address is null.
             * if we have not obtained the address..Then again i am trying to get the address and update in DB.
             * and Fetch it..If by chance the address is again null displaying like that only...
             */
            if (mUtility.haveInternet()) {
                String address = mActivity.retutn_tracekrAddress(point.latitude, point.longitude);
                mActivity.updateAddressinDB(bleAddress, address);
            }

        }

        if(url!=null){
            if(url.length()>5){
                url.replace("http://kuurvtrackerapp.com/uploads//", "http://kuurvtrackerapp.com/uploads/");
            }
        }


        final GlideInfoWindowAdapter markerInfoWindowAdapter = new GlideInfoWindowAdapter(mActivity, Device_name, url, LocationAddressofTracker); // mActivity.getAddressFromDB(bleAddress) Fetching the address always from local DB.
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter);
        markerInfoWindowAdapter.showInfoWindow(marker);


//        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
//            @Override
//            public void onInfoWindowClose(Marker marker) {
//                markerInfoWindowAdapter.showInfoWindow(marker);
//            }
//        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    markerInfoWindowAdapter.showInfoWindow(marker);
                }
            }
        });
    }

    class GlideInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final Map<Marker, BitmapDrawable> images = new HashMap<>();
        private final Map<Marker, BaseTarget<BitmapDrawable>> targets = new HashMap<>();
        private Context context;
        private String strDeviceName;
        private String strDeviceAddress;
        private String strDeviceImageURL;
        RequestOptions requestOptions;

        public GlideInfoWindowAdapter(Context context, String deviceName, String deviceImageUrl, String deviceAddress) {
            strDeviceName = deviceName;
            strDeviceAddress = deviceAddress;
            strDeviceImageURL = deviceImageUrl;
            requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_default_logo);
            requestOptions.error(R.drawable.ic_default_logo);
            requestOptions.centerCrop();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.dontAnimate();
            requestOptions.dontTransform();
            requestOptions.priority(Priority.HIGH);
            this.context = context.getApplicationContext();
        }

        public void showInfoWindow(Marker marker) {
            marker.showInfoWindow();
        }

        public void remove(Marker marker) {
            images.remove(marker);
            marker.remove();
        }

        @Override
        public View getInfoContents(final Marker marker) {
            BitmapDrawable image = images.get(marker);
            if (image == null) {

             getActivity().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     try{
                         Glide.with(context)
                                 .applyDefaultRequestOptions(requestOptions)
                                 .load(strDeviceImageURL)
                                 .override(600, 200)
                                 .diskCacheStrategy(DiskCacheStrategy.DATA)
                                 .into(getTarget(marker));
                     }catch (Exception e){e.printStackTrace();}
                 }
             });


                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View markerView = inflater.inflate(R.layout.custom_marker_layout, null);
                TextView dev_name = (TextView) markerView.findViewById(R.id.device_name);
                TextView dev_address = (TextView) markerView.findViewById(R.id.marker_tv_device_last_address);
                CircleImageView Marker_Device_image = (CircleImageView) markerView.findViewById(R.id.device_image);
                dev_name.setText(strDeviceName);

                dev_address.setText(strDeviceAddress);
                //  System.out.println("Address Printed Here ="+strDeviceAddress);
                Marker_Device_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_default_logo));
                return markerView;
            } else {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View markerView = inflater.inflate(R.layout.custom_marker_layout, null);
                TextView dev_name = (TextView) markerView.findViewById(R.id.device_name);
                TextView dev_address = (TextView) markerView.findViewById(R.id.marker_tv_device_last_address);
                CircleImageView Marker_Device_image = (CircleImageView) markerView.findViewById(R.id.device_image);
                dev_name.setText(strDeviceName);
                dev_address.setText(strDeviceAddress);

                Marker_Device_image.setImageDrawable(image);
                return markerView;
            }
        }

        @Override // GoogleMap.InfoWindowAdapter
        public View getInfoWindow(Marker marker) {
            return null;
        }

        private BaseTarget getTarget(Marker marker) {
            //BaseTarget<BitmapDrawable>
            BaseTarget target = targets.get(marker);
            if (target == null) {
                target = new InfoTarget(marker);
                targets.put(marker, target);
            }
            return target;
        }

        private class InfoTarget extends SimpleTarget<BitmapDrawable> {
            Marker marker;

            InfoTarget(Marker marker) {
                super(100, 100);
                this.marker = marker;
            }

            @Override
            public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition<? super BitmapDrawable> transition) {
                images.put(marker, resource);
                marker.showInfoWindow();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                images.remove(marker);
            }
        }
    }


    /**
     * This Method will Delete the Device from Server,BLE,LocalDB.when ever we call this method.
     * The Top Item on the spinner will be Delted.
     */
    /**
     * This Method will Delete the Device from Server,BLE,LocalDB.when ever we call this method.
     * The Top Item on the spinner will be Delted.
     */
    private static void Delete_Device_ble_API(final String Delete_Ble_Address) {
     //   System.out.println("Fragment_Home>>>>>>>Response_ BLE Address = " + Delete_Ble_Address);
        String Querey = "select server_id from Device_Table where ble_address=" + "'" + Delete_Ble_Address + "'";
        String server_id = "";
        Cursor cursor = mActivity.mDbHelper.Query(Querey);
        if (cursor.moveToFirst()) {
            do {
                server_id = cursor.getString(cursor.getColumnIndex("server_id"));
                //   String ble_address = cursor.getString(cursor.getColumnIndex("ble_address"));
                // do what ever you want here
            } while (cursor.moveToNext());
        }
        cursor.close();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mActivity.mPreferenceHelper.getUserId());
        params.put("device_id", server_id);
        params.put("ble_address", Delete_Ble_Address);

        Call<VoDeleteDevice> call = mActivity.mApiService.Delete_device(params);
        call.enqueue(new Callback<VoDeleteDevice>() {
            @Override
            public void onResponse(Call<VoDeleteDevice> call, Response<VoDeleteDevice> response) {

                VoDeleteDevice deleteDevice=response.body();
                if(deleteDevice.getMessage().equalsIgnoreCase("Device deleted successfully.")&&
                   deleteDevice.getResponse().equalsIgnoreCase("true")){
                    mActivity.mDbHelper.exeQuery("DELETE FROM" + " " + mActivity.mDbHelper.Device_Table + " " + "WHERE" + " " + mActivity.mDbHelper.ble_address + "=" + "'" + Delete_Ble_Address + "'");
                    mActivity.mPreferenceHelper.setDeviecPostion("");
                    mActivity.getDBDeviceList();
                    userDeviceListAdapter.notifyDataSetChanged();
                    if(fragmetn_homeDestroyed == false){
                        if(mActivity.mArrayListMyDevices.size()>0){
                            fragmentHome_instance.setDetailsonTopLayout(0,null,null);

                            if(fragmentHome_instance.getFragmentHomeBluetoothAdapter()!=null){

                                BluetoothAdapter fragmentHomeBluetoothadapter=fragmentHome_instance.getFragmentHomeBluetoothAdapter();

                                if(fragmentHomeBluetoothadapter.isEnabled())
                                {
                                    String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(0).getBle_address());
                                    BluetoothAdapter bluetoothAdapter=fragmentHome_instance.getFragmentHomeBluetoothAdapter();
                                    BluetoothDevice bleDevice =bluetoothAdapter.getRemoteDevice(getBleAddresswithUppercaseAndSymbol);
                                    if(BleManager.getInstance().isConnected(new BleDevice(bleDevice))){
                                        fragmentHome_instance.topLayoutColorChnageOnConnection(true, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                    }else{
                                        fragmentHome_instance.topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                                    }

                                }
                            }else{
                                mActivity.giveCustomToastBluetoothNotEnabled();
                            }
                        }else{

                            if (mActivity.mArrayListMyDevices.size() == 0) {
                                if (marker != null) {
                                    marker.remove();
                                    marker = null;
                                    mMap.setInfoWindowAdapter(null);
                                }

                                try{fragmentHome_instance.showCurrentLocationMarker();}
                                catch (Exception e){e.printStackTrace();}
                                finally {
                                    fragmentHome_instance.showCurrentLocationMarker();
                                }

                            }
                            fragmentHome_instance.mRelativeLayoutDevice.setVisibility(View.INVISIBLE);
                            fragmentHome_instance.navigationIcon.setVisibility(View.INVISIBLE);
                        }

                        mActivity.mArraylistOwnerstatusinfo.remove(getpostionoftheDeviceinList_OwnerInfoList(Delete_Ble_Address));
                        mActivity.mUtility.errorDialog("KUURV","Deleted Successfully.", 0, true);
                    }

                    //Delete Device Via BLE Call only...

                }

               mActivity.diconnectDevice(fragmentHome_instance.bleAddressAddCharcter(Delete_Ble_Address));

                mActivity.hideProgress();
            }

            @Override
            public void onFailure(Call<VoDeleteDevice> call, Throwable t) {
                mActivity.hideProgress();
            }
        });

    }


    /**
     * This method is used to delete the image from the phone...i.e SD card and cache...
     *
     * @param bleAddress
     */
    private static void delteImage_From_Phone(String bleAddress) {

        // Delete from Sdcard...
        File from_sdcard = new File("/sdcard/Tracker/" + bleAddress.toString().replace(":", "").trim().toLowerCase() + ".jpg");
        if (from_sdcard.exists()) {
            if (from_sdcard.delete()) {
            } else {
            }
        }
        // Delete from cache...
        File from_cache = new File("/data/user/0/com.benjaminshamoilia.trackerapp/cache/images/" + bleAddress.toString().replace(":", "").trim().toLowerCase() + ".jpg");
        if (from_cache.exists()) {
            if (from_cache.delete()) {
            } else {
            }
        }
    }


    /**
     * RecycleView adapter Design Code here....
     */

    /**
     * Taking this selected_devicePostion=50 value so the default value won t come into exeistance  i.e defaultvalue=0;
     */
    int selected_devicePostion = -1;

    //Trying with RecycleView for Devicelist.
    public class UserDeviceListAdapter extends RecyclerView.Adapter<UserDeviceListAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.homefragmentdevicelist, parent, false);
            return new UserDeviceListAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.mtextViewDeiceName.setText(mActivity.mArrayListMyDevices.get(position).getDevice_name().toString());
            Glide.with(holder.deviceImageRecycleView)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(mActivity.mArrayListMyDevices.get(position).getPhoto_serverURL())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.deviceImageRecycleView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdevicelistRecycleView.setVisibility(View.INVISIBLE);
                    showRecycleView = true; // falg used again if the user is trying to click tha layout in the RecycleView.
                    selected_devicePostion = position;
                    setDetailsonTopLayout(selected_devicePostion, null, null);
                    String getBleAddresswithUppercaseAndSymbol = mActivity.bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(selected_devicePostion).getBle_address());

                    if(getFragmentHomeBluetoothAdapter()!=null){
                        BluetoothAdapter bluetoothAdapter=getFragmentHomeBluetoothAdapter();
                        if(bluetoothAdapter.isEnabled()){
                            boolean connectionStatus = BleManager.getInstance().isConnected(getBleAddresswithUppercaseAndSymbol);
                            if(connectionStatus){
                                mCustomTextViewLocate.setText("Locate");
                            }
                            topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                        }else{
                            topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                        }
                    } else{
                        topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(getBleAddresswithUppercaseAndSymbol));
                        mActivity. giveCustomToastBluetoothNotEnabled();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {

            System.out.println("RecycleView mActivity.mArrayListMyDevices size = " + mActivity.mArrayListMyDevices.size());
            return (mActivity.mArrayListMyDevices == null) ? 0 : mActivity.mArrayListMyDevices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.raw_add_device_item_tv_device_name)
            TextView mtextViewDeiceName;

            @BindView(R.id.raw_add_device_iv_device)
            CircleImageView deviceImageRecycleView;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    /**
     * RecycleView Visible/InVisible operation and flags.
     */
    boolean showRecycleView = true;

    @OnClick(R.id.fragment_home_device)
    public void DeviceShowListLayoutClick(View mview) {
        mdevicelistRecycleView.setVisibility(mview.VISIBLE);

        if (showRecycleView) {
            mdevicelistRecycleView.setVisibility(View.VISIBLE);
            showRecycleView = false;
        } else {
            mdevicelistRecycleView.setVisibility(View.INVISIBLE);
            showRecycleView = true;
        }
    }

    /**
     * This method will put the user selected device into the top position of the layout.
     */
    public void setDetailsonTopLayout(int position, String conectionStatus, String bleaddress) {
        /**
         * BLE Part.
         */
        bleAddressonTopLayout = bleAddressAddCharcter(mActivity.mArrayListMyDevices.get(position).getBle_address());
        String device_name = mActivity.mArrayListMyDevices.get(position).getDevice_name();
        mcustomDevicename.setText(device_name);
        Glide.with(devicebackGroundonTopLayout.getContext())
                .applyDefaultRequestOptions(requestOptions)
                .load(mActivity.mArrayListMyDevices.get(position).getPhoto_serverURL())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(devicebackGroundonTopLayout);

        /**
         * image background,lastseen,
         */
        LatLng point = new LatLng(Double.parseDouble(mActivity.mArrayListMyDevices.get(position).getLatitude()), Double.parseDouble(mActivity.mArrayListMyDevices.get(position).getLongitude()));
        if (mMap != null) {
            drawMarker(point, mActivity.mArrayListMyDevices.get(position).getDevice_name(), mActivity.mArrayListMyDevices.get(position).getPhoto_serverURL(), mActivity.mArrayListMyDevices.get(position).getBle_address().toLowerCase().replace(":", ""));
        }

    }

    /**
     * sometimes the marker won t be executed as nMap object will be null.
     * so when ever the nMap Object is not null i am calling this method in onMapReady and passing the value to the setDetailsonTopLayout();
     */
    private void setmarkerofDeviceToppostion() {
        if (mActivity.mArrayListMyDevices.size() > 0) {
            if (mActivity.mPreferenceHelper.getDevicePostion().equalsIgnoreCase("")) {
                if (deviceAddedFromfragmentConigureDevice) {
                    /**
                     * This logic is used to put the Devie to the top position in the FragmentHome,
                     * when the user is added new Device from FragmentConfigureDevice.
                     */
                    setDetailsonTopLayout(mActivity.mArrayListMyDevices.size() - 1, null, null);

                } else {
                    setDetailsonTopLayout(0, null, null);

                }
            } else {
                if(mActivity.mArrayListMyDevices.size()>0){

                    /**
                     * This logic i have applied to check wheather the sharedPreferencevalue saved is valid or not..Coz sometimes its not giving proper response.
                     */
                    int savedpostion=Integer.parseInt(mActivity.mPreferenceHelper.getDevicePostion());
                    int sizeofthelist=mActivity.mArrayListMyDevices.size()-1;

                    if(savedpostion<=sizeofthelist){
                        setDetailsonTopLayout(Integer.parseInt(mActivity.mPreferenceHelper.getDevicePostion()), null, null);
                    }else{
                        setDetailsonTopLayout(0, null, null);
                    }
                }

            }
        }
    }


    /**
     * This method is used to convert the BLE address comming from server to uppercase and adding : in the midddle.
     *
     * @param bleAddress (as455efvf78er)
     * @return(AS:45:5E:FV:F7:8E)
     */
    private String bleAddressAddCharcter(String bleAddress) {
        String address = bleAddress;
        String address_1 = addChar(address, ':', 2).toUpperCase();
        String address_2 = addChar(address_1, ':', 5).toUpperCase();
        String address_3 = addChar(address_2, ':', 8).toUpperCase();
        String address_4 = addChar(address_3, ':', 11).toUpperCase();
        String finalModifiedAddress = addChar(address_4, ':', 14).toUpperCase();
        return finalModifiedAddress;
    }

    /**
     * Adding : to the ble address
     */
    public static String addChar(String str, char ch, int position) {
        int len = str.length();
        char[] updatedArr = new char[len + 1];
        str.getChars(0, position, updatedArr, 0);
        updatedArr[position] = ch;
        str.getChars(position, len, updatedArr, position + 1);
        return new String(updatedArr);
    }


    public void enableBeepBuzzer(BleDevice bleDevice) {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[1];
        byte_value[0] = (byte) (Constant.OPCODE_BEEP_BUZZER & 0xFF);
        mActivity.writedatatoBleDevice(bleDevice, byte_value);
    }

    public void disableBeepBuzzer(BleDevice bleDevice) {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_STOP_BUZZER & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        mActivity.writedatatoBleDevice(bleDevice, byte_value);
    }


    @OnClick(R.id.frg_home_tv_locate)
    public void onLocateButtonClick(View mView) {
        if (isAdded()) {
            if (BluetoothAdapter.getDefaultAdapter() == null) {
                // Device does not support Bluetooth
                mActivity.mUtility.errorDialog("KUURV","Bluetooth is not Supported", 1, true);
                return;
            }
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                mUtility.errorDialogWithCallBack("KUURV","Please turn ON your \n bluetooth and then try \n locating your device", 3, false, new onAlertDialogCallBack() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {
                    }
                });
                return;
            }

            if (mActivity.mArrayListMyDevices.size() == 0) {
                mActivity.mUtility.errorDialog("KUURV","Add device to locate it.", 3, true);
                return;
            }


                if (getFragmentHomeBluetoothAdapter() != null) {
                    System.out.println("Fragment_Home sending data to BleAddress = "+bleAddressonTopLayout);
                    boolean connectionStatus = BleManager.getInstance().isConnected(bleAddressonTopLayout);
                    if (connectionStatus) {
                        if(checkOwnerStatusForTheBleAddress(bleAddressonTopLayout)){
                            topLayoutColorChnageOnConnection(connectionStatus, mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout));
                            final BluetoothDevice device = getFragmentHomeBluetoothAdapter().getRemoteDevice(bleAddressonTopLayout);
                            if (mCustomTextViewLocate.getText().toString().equalsIgnoreCase("Locate")) {
                                mCustomTextViewLocate.setText("Stop");
                                enableBeepBuzzer(new BleDevice(device));
                            } else {
                                mCustomTextViewLocate.setText("Locate");
                                disableBeepBuzzer(new BleDevice(device));
                            }
                        }else{
                            System.out.println("OwnerStatus is not Confirmed");
                        }
                    } else {
                        mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n locate.", 1, true);
                        topLayoutColorChnageOnConnection(false, mActivity.getPositionofBleObjectInArrayList(bleAddressonTopLayout));
                    }
                }else{
                    mActivity.mUtility.errorDialog("KUURV","Turn on Bluetooth", 1, true);
                }

        }
    }


    public void topLayoutColorChnageOnConnection(boolean connectionStatus, int postion) {
        if (mcustomtextDeviceLastseen != null && devicebackGroundonTopLayout != null && batteryImageViewonToplayout != null && mcustomTextViewDevicebatteryPercentage != null && batteryImageViewonToplayout != null && mcustomTextViewDevicebatteryPercentage != null) {
            if(mActivity.mArrayListMyDevices!=null){
                if(mActivity.mArrayListMyDevices.size()>0){
                    if (connectionStatus) {
                        mcustomtextDeviceLastseen.setVisibility(View.INVISIBLE);
                        //Connected Design. BorderColor,BatterySymbol,Batttey Visible,
                        devicebackGroundonTopLayout.setBorderColor(getResources().getColor(R.color.colorGreen));
                        mcustomTextViewDevicebatteryPercentage.setVisibility(View.VISIBLE);
                        batteryImageViewonToplayout.setVisibility(View.VISIBLE);
                        mcustomTextViewDevicebatteryPercentage.setText(""+mActivity.mArrayListMyDevices.get(postion).getBattery_status()+"%"); // batteryPercentage here.
                    } else {
                        mCustomTextViewLocate.setText("Locate");
                        mcustomTextViewDevicebatteryPercentage.setVisibility(View.INVISIBLE);
                        batteryImageViewonToplayout.setVisibility(View.INVISIBLE);
                        mcustomtextDeviceLastseen.setVisibility(View.VISIBLE);
                        devicebackGroundonTopLayout.setBorderColor(getResources().getColor(R.color.colorRed));
                        mcustomtextDeviceLastseen.setText("Last Seen :"+mActivity.mUtility.convert_time(mActivity.mArrayListMyDevices.get(postion).getUpdated_time()));  // updated time here...
                    }
                }
            }
        }

    }


    public BluetoothAdapter getFragmentHomeBluetoothAdapter()
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


    private  final int LOCATION_REQUEST_CODE=1;
    public void fragmentHomeLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        if(getFragmentHomeBluetoothAdapter()!=null){
                    if(!getFragmentHomeBluetoothAdapter().isEnabled()){
                        mActivity. giveCustomToastBluetoothNotEnabled();
                    }
        }else{
            mActivity. giveCustomToastBluetoothNotEnabled();
        }

    }
    private void showExplanationDialogFragmentHome() {

        boolean deniedfristtime_true_else_false=ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(deniedfristtime_true_else_false){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mUtility.errorDialogWithYesNoCallBack("KUURV", "Please Provide\n locatio Acess to Scan for Devices.", "Yes", "No", true, 1, new onAlertDialogCallBack() {
                @Override
                public void PositiveMethod(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                @Override
                public void NegativeMethod(DialogInterface dialog, int id) {

                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=(Activity)context;
       try{
           deleteBleAddress=(DeleteBleAddress)activity;
       }catch(ClassCastException e){
           throw new ClassCastException(activity.toString()+"Must Override DeleteBleAddress interace");
       }

    }

    DeleteBleAddress deleteBleAddress;
    public interface DeleteBleAddress{
        public void  deletingedBleAddress(String bleAddress);
    }



    private DeleteOwnerInfoTimer deleteOwnerInfoTimer;

    private class DeleteOwnerInfoTimer extends CountDownTimer {
        public DeleteOwnerInfoTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            if (millisUntilFinished <= 1000) {
                  Delete_Device_ble_API(bleAddressonTopLayout.toLowerCase().replace(":",""));
                  delteImage_From_Phone(bleAddressonTopLayout.toLowerCase().replace(":",""));
            } else if (millisUntilFinished <= 2000 && millisUntilFinished > 1000) {
            } else if (millisUntilFinished <= 3000 && millisUntilFinished > 2000) {
                if(bleDeviceDeleting!=null){
                    mActivity.readOwnerInfoFromDevice(bleDeviceDeleting);
                }
            } else if (millisUntilFinished <= 4000 && millisUntilFinished > 3000) {
            } else if (millisUntilFinished <= 5000 && millisUntilFinished > 4000) {

            }
        }

        @Override
        public void onFinish() {
        }
    }

    private BleDevice bleDeviceDeleting=null;
    private void deleteDevicethroughBle(BleDevice bleDevice){
        deleteOwnerInfoTimer.start();
        mActivity. deleteOwnerInfoFromDevice(bleDevice);
        bleDeviceDeleting=bleDevice;
    }


    private int getTableCountforRegisteredDevices(){
        int result=-1;
        result=mActivity.mDbHelper.getTableCount(mActivity.mDbHelper.Device_Table);
        if(result>0){
            result=mActivity.mDbHelper.getTableCount(mActivity.mDbHelper.Device_Table);
        }else{
            result=-1;
        }
        return  result;
    }




    public void FragmentHomegetDBDeviceList() {
        DataHolder mDataHolderLight;
        if (mActivity.mArrayListMyDevices != null) {
            mActivity.mArrayListMyDevices.clear();
        }
        try {
            String url = "select * from Device_Table";
            mDataHolderLight = mActivity.mDbHelper.readData(url);
            if (mDataHolderLight != null) {
                Vo_Device_Regstd_from_serv vo_device_regstd_from_serv;
                for (int i = 0; i < mDataHolderLight.get_Listholder().size(); i++) {
                    vo_device_regstd_from_serv = new Vo_Device_Regstd_from_serv();
                    //   mStringsArrayName.add(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.id));
                    vo_device_regstd_from_serv.setBle_address(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.ble_address));
                    vo_device_regstd_from_serv.setLatitude(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.latitude));
                    vo_device_regstd_from_serv.setLongitude(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.longitude));
                    vo_device_regstd_from_serv.setPhoto_serverURL(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.photo_server_url));
                    vo_device_regstd_from_serv.setPhoto_localURL(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.photo_local_url));
                    vo_device_regstd_from_serv.setCreated_time(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.created_time));
                    vo_device_regstd_from_serv.setUpdated_time(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.updated_time));
                    vo_device_regstd_from_serv.setDevice_name(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.device_name));
                    vo_device_regstd_from_serv.setId(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.id)));
                    vo_device_regstd_from_serv.setServer_id(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.server_id));
                    vo_device_regstd_from_serv.setIs_silent_mode(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.DeviceFiledSilentMode)));
                    vo_device_regstd_from_serv.setBuzzer_volume(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.DeviceFiledBuzzerVolume)));
                    vo_device_regstd_from_serv.setSeperate_alert(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.DeviceFiledSeperateAlert)));
                    vo_device_regstd_from_serv.setRepeat_alert(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.DeviceFiledRepeatAlert)));
                    vo_device_regstd_from_serv.setCorrection_status(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.correction_status));
                    vo_device_regstd_from_serv.setDevice_type(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.device_type));
                    vo_device_regstd_from_serv.setTracker_device_alert(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.tracker_device_alert));
                    vo_device_regstd_from_serv.setMarked_lost(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.marked_lost));
                    vo_device_regstd_from_serv.setIs_active(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.is_active));
                    vo_device_regstd_from_serv.setContact_name(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.contact_name));
                    vo_device_regstd_from_serv.setContact_email(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.contact_email));
                    vo_device_regstd_from_serv.setContact_mobile(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.contact_mobile));
                    vo_device_regstd_from_serv.setBattery_status(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.DeviceFiledBatteryStatus)));
                    vo_device_regstd_from_serv.setTracker_Address(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.tracker_locAdds));
                    mActivity.mArrayListMyDevices.add(vo_device_regstd_from_serv);
                }
                userDeviceListAdapter.notifyDataSetChanged();
            }else{
                System.out.println("DataHolder Object is Null");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FragmentHome Exception Occured ");
        }

    }

    private boolean checkOwnerStatusForTheBleAddress(String bleaddres){
        boolean result=false;
        int position=-1;
        for (int counter = 0; counter < mActivity.mArraylistOwnerstatusinfo.size(); counter++) {
            if(mActivity.mArraylistOwnerstatusinfo.get(counter).getBleAddress().equalsIgnoreCase(bleaddres)){
                position=counter;
            }
        }

        if(position!=-1){
            if(mActivity.mArraylistOwnerstatusinfo.get(position).isOwnerstatus()){
                result=true;
            }
        }

        return result;
    }

    private static int getpostionoftheDeviceinList_OwnerInfoList(String bleaddress){
        int postion=-1;
        for (int counter = 0; counter < mActivity.mArraylistOwnerstatusinfo.size(); counter++) {
            if(mActivity.mArraylistOwnerstatusinfo.get(counter).getBleAddress().equalsIgnoreCase(fragmentHome_instance.bleAddressAddCharcter(bleaddress))){
                postion=counter;
            }
        }
        return postion;
    }

}
