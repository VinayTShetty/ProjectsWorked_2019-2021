package com.benjaminshamoilia.trackerapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.benjaminshamoilia.trackerapp.interfaces.ApiCrushLogout;
import com.benjaminshamoilia.trackerapp.interfaces.DeviceDisconnectionCallback;
import com.benjaminshamoilia.trackerapp.interfaces.PassScannedDevices;
import com.benjaminshamoilia.trackerapp.vo.VoOwnerStatusForDevice;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.benjaminshamoilia.trackerapp.db.DBHelper;
import com.benjaminshamoilia.trackerapp.db.DataHolder;
import com.benjaminshamoilia.trackerapp.fragment.FragmentAboutUs;
import com.benjaminshamoilia.trackerapp.fragment.FragmentAccountSetting;
import com.benjaminshamoilia.trackerapp.fragment.FragmentHelp;
import com.benjaminshamoilia.trackerapp.fragment.FragmentHome;
import com.benjaminshamoilia.trackerapp.helper.BLEUtility;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.CustomDialog;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.DeviceConnectionStatus;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.interfaces.onLocationChangeListner;
import com.benjaminshamoilia.trackerapp.vo.VoAddDeviceData;
import com.benjaminshamoilia.trackerapp.vo.VoBluetoothDevices;
import com.benjaminshamoilia.trackerapp.vo.VoCrushLogOut;
import com.benjaminshamoilia.trackerapp.vo.VoDeviceList;
import com.benjaminshamoilia.trackerapp.vo.VoDeviceListData;
import com.benjaminshamoilia.trackerapp.vo.VoNormalLogout;
import com.benjaminshamoilia.trackerapp.vo.Vo_Device_Regstd_from_serv;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Intent.ACTION_VIEW;
import static com.benjaminshamoilia.trackerapp.fragment.FragmentHome.fragmentHome_instance;
import static com.benjaminshamoilia.trackerapp.helper.BLEUtility.hexToDecimal;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,FragmentHome.DeleteBleAddress
{

    String TAG = MainActivity.class.getSimpleName();
    public Utility mUtility;
    public PreferenceHelper mPreferenceHelper;
    public Retrofit mRetrofit;
    public API mApiService;
    @BindView(R.id.activity_main_rl_main)
    public RelativeLayout mRelativeLayoutMain;
    @BindView(R.id.activity_main_toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.activity_main_navigation_view)
    public NavigationView mNavigationView;
    @BindView(R.id.activity_main_drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_main_appbar_header)
    public AppBarLayout appBarLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;

    public ImageView mImageViewBack;
    public ImageView mImageViewAddDevice;
    public TextView mTextViewTitle;
    public TextView mTextViewAdd;
    public ProgressBar scanningProgressBar;

    FragmentTransaction fragmentTransaction;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private boolean exit = false;

    // BLE


    public ArrayList<VoBluetoothDevices> mLeDevices = new ArrayList<>();
    public ArrayList<VoBluetoothDevices> mLeDevicesAddDevice = new ArrayList<>();



    public static final int BLUETOOTH_ENABLE_REQT = 11;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;



    public onLocationChangeListner mOnLocationChangeListnear;
    public boolean isNeverAskPermissionCheck = false;



    private LocationManager mLocationManager;
    public String userUniqueKey;
    public DBHelper mDbHelper;
    public String latitude = "";
    public String longitude = "";
    LocationManager locationManager;
    public Location mLastLocation;
    //Social Media
    GoogleSignInClient mGoogleSignInClient;
    //Social Media
    // Navigation Header
    TextView username, email;
    //AddDevice API for connect/disconnect.

    MediaPlayer mMediaPlayerAlert;
    public List<Vo_Device_Regstd_from_serv> mArrayListMyDevices = new ArrayList<>();

    /**
     * This Arraylist(mArraylistOwnerstatus) is used to make the flag for OwnerStatus.As i am not able to maintain the flag in the
     * mArrayListMyDevices...as its keep changing again and again..
     */
    public List<VoOwnerStatusForDevice> mArraylistOwnerstatusinfo = new ArrayList<>();

    static Timer mTimerBuzze;
    boolean isStopTimer = false;


    public DeviceConnectionStatus deviceConnectionStatus;
    public DeviceDisconnectionCallback deviceConnectioncallback;
    public ApiCrushLogout userloginfromDifferetnPhone;

    public PassScannedDevices passingScanDevices;
    /**
     * Local Notification Part.
     */

    private static final String CHANNEL_ID = "com.benjaminshamoilia.trackerapp";
    private static final String CHANNEL_NAME = "com.benjaminshamoilia.trackerapp";
    private static final String CHANNEL_DESC = "com.benjaminshamoilia.trackerapp";
    IntentFilter mIntentFilter = new IntentFilter();


    public static boolean mainActivityExecuted=false; //this flag is used to show the load image when the user open the app and we did nt get the data instantly from the DB after Re-loagding.
    /**
     * This flag is used to check any device is added and place that device in TopPosition.
     */
    public static boolean deviceAddedFromfragmentConigureDevice=false;

    /**
     * Alert Setting Part.
     */
    public static MediaPlayer fragmentalertSettingMediaPlayer;


    /**
     *
     * Application Status state for DisconnectionCall.
     */
    private static String applicationShowingtoUser ="NO_STATUS";
    private static boolean userLoggingoutCheck=false;


    private static int conterPassedToArraList=0;
    private static boolean connectionProcessCompletedForDevice=true;
    private static boolean mActivityScanningStarted=false;


    Timer mTimer;
    public final Handler mhandler = new Handler();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        mPreferenceHelper = new PreferenceHelper(MainActivity.this);
        mainActivityExecuted=true;
        mUtility = new Utility(MainActivity.this);
        mLeDevices = new ArrayList<>();
        mLeDevicesAddDevice = new ArrayList<>();
        initToolbar();
        userLoggingoutCheck=false;


        /**
         * Flag used for CustomArraylist.
         */
        mArraylistOwnerstatusinfo = new ArrayList<>();



        /**
         * RX BLE new Logic...
         */
        rxBleClient = RxBleClient.create(this);
        conterPassedToArraList=0;
        connectionProcessCompletedForDevice=true;
        clearScannnedDevices();

        /**
         * Ble Connection library.
         */
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);


        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getClientWithAutho())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .build();
        mApiService = mRetrofit.create(API.class);


        mDbHelper = DBHelper.getDBHelperInstance(MainActivity.this);
        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPreferenceHelper.getUser_email().length() > 18) {
            userUniqueKey = mPreferenceHelper.getUser_email().toString().substring(0, 18);
        } else {
            userUniqueKey = mPreferenceHelper.getUser_email().toString();
        }

        //Social Login
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Social Login

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                mUtility.hideKeyboard(MainActivity.this);
                super.onDrawerOpened(v);
            }
        };
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        showBackButton(false);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectDrawerItem(menuItem);
                    }
                }, 200);
                return true;
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mbluetoothBroadcastReceiever, filter);

      /*  setScrollingBehavior(true);
        removeAllFragmentFromBack();
        MapsInitializer.initialize(this);
        FragmentHome mFragmentHome = new FragmentHome();
        replacesFragment(mFragmentHome, false, null, 1);*/



        setLocationChangeListnear(new onLocationChangeListner() {
            @Override
            public void onLocationChange(Location mLocation) {

            }
        });
        View headerView = mNavigationView.getHeaderView(0);

        username = (TextView) headerView.findViewById(R.id.nav_header_main_tv_titleTest4);
        email = (TextView) headerView.findViewById(R.id.nav_header_main_tv_web);
        email.setText("(" + mPreferenceHelper.getUser_email() + ")");

        String CapitalName = mPreferenceHelper.getAccountName().substring(0, 1).toUpperCase() + mPreferenceHelper.getAccountName().substring(1);
        username.setText(" " + CapitalName);

        /*if (!mUtility.haveInternet()) {
           getDBDeviceList();
        } else {
            getDeviceList_API();
        }*/

        /**
         * Notfication part
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        setDeviceConnectionStatus(new DeviceConnectionStatus() {
            @Override
            public void ConnectionStatus(String BleAddress, String ConnectionStatus,BleDevice bleDevice) {

            }

            @Override
            public void infoReceievedFrombleDevice(String info) {

            }

            @Override
            public void deleteinfoReceievedfromBleDevice(int ackreceieved,BleDevice passedDevice) {

            }

            @Override
            public boolean bluetoothturningOff(boolean bleoff) {
                return false;
            }

            @Override
            public void readbatterystatusFromDevice(int batterystatus) {

            }
        });


        setpassScannedDevicesToFragmentAddDevice(new PassScannedDevices() {
            @Override
            public void sendingResultstoFragment(RxBleDevice BleAddress) {

            }
        });

        mTimer = new Timer(); //Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run() {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {
                            if(getMainActivityBluetoothAdapter()!=null){
                                if(getMainActivityBluetoothAdapter().isEnabled()){
                                    int recordAvaliableCount=getTableCountforRegisteredDevices();
                                    if(recordAvaliableCount>0){
                                        System.out.println("TestingTimer Entered");
                                        if(mArrayListAddDeviceScannedDevices.size()>0){
                                            if(connectionProcessCompletedForDevice){
                                                try{
                                                    for (int counter = 0; counter < mArrayListAddDeviceScannedDevices.size(); counter++) {
                                                        if(mArrayListAddDeviceScannedDevices.get(counter).getMacAddress().equalsIgnoreCase(bleAddressAddCharcter(mArrayListMyDevices.get(conterPassedToArraList).getBle_address()))){
                                                            System.out.println("TestingTimer DeviceAvaliabel inScanning");
                                                            final String bleAdddressCheckingFromOrder=bleAddressAddCharcter(mArrayListMyDevices.get(conterPassedToArraList).getBle_address());
                                                            final BluetoothDevice convertedBleDevice = getMainActivityBluetoothAdapter().getRemoteDevice(bleAdddressCheckingFromOrder);
                                                            if(BleManager.getInstance().isConnected(new BleDevice(convertedBleDevice))){
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        updateGuiinFragmetnHome(convertedBleDevice.getAddress(),"Connected",null);
                                                                    }
                                                                });
                                                                incrementCounterValuePassed_ResetCounterValuePassed();
                                                                UpdateconnectionProcessCompletedForDevice(true);
                                                                break;
                                                            }else{
                                                                UpdateconnectionProcessCompletedForDevice(false);
                                                                ProcessConnectionToDevice(convertedBleDevice.getAddress());
                                                            }
                                                        }else{
                                                            System.out.println("TestingTimer DeviceNot avaliable");
                                                           // clearScannnedDevices();
                                                            incrementCounterValuePassed_ResetCounterValuePassed();
                                                            UpdateconnectionProcessCompletedForDevice(true);
                                                        }
                                                    }
                                                }catch (Exception e){
                                                    clearScannnedDevices();
                                                    incrementCounterValuePassed_ResetCounterValuePassed();
                                                    UpdateconnectionProcessCompletedForDevice(true);
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                },
                0,
                3000);;

        /**
         * Alert Setting Part
         */
        fragmentalertSettingMediaPlayer = MediaPlayer.create(this, R.raw.trackeralertseparation);
        setScrollingBehavior(true);
        removeAllFragmentFromBack();
        MapsInitializer.initialize(this);
        FragmentHome mFragmentHome = new FragmentHome();
        replacesFragment(mFragmentHome, false, null, 1);
    }




    private boolean CheckConnectionStatusFortheObject(int ArrayIndexpassed){
        boolean result=false;
        String bleaddessObtained= mArrayListMyDevices.get(ArrayIndexpassed).getBle_address();
        String ConvertedBleAddress=bleAddressAddCharcter(bleaddessObtained);

        if(getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
            final BluetoothDevice devicematchedfromDatabaseAddress= bluetoothAdapter.getRemoteDevice(ConvertedBleAddress);
            BleDevice ConvertBleDevice=new BleDevice(devicematchedfromDatabaseAddress);
            if(BleManager.getInstance().isConnected(ConvertBleDevice)){
                result=true;
            }
        }
        return result;
    }

    public void setDeviceConnectionStatus(DeviceConnectionStatus LocDeviceConnectionStatus)
    {
        this.deviceConnectionStatus=LocDeviceConnectionStatus;
    }

    public void setDeviceDisconnectionCallback(DeviceDisconnectionCallback LocdeviceConnectioncallback)
    {
        this.deviceConnectioncallback=LocdeviceConnectioncallback;
    }

    public void setUserLoginFromDifferentAccount(ApiCrushLogout LocuserloginfromDifferetnPhone)
    {
        this.userloginfromDifferetnPhone=LocuserloginfromDifferetnPhone;
    }

    public void setLocationChangeListnear(onLocationChangeListner locationChangeListner) {
        this.mOnLocationChangeListnear = locationChangeListner;
    }

    public void setpassScannedDevicesToFragmentAddDevice(PassScannedDevices locpassScannedDevices){
        this.passingScanDevices=locpassScannedDevices;
    }

    /*Show Pregress*/
    public void showProgress(final String mStringProgressTitle, boolean isShowCount) {
        if (!isFinishing()) {
            mUtility.ShowProgress(mStringProgressTitle, isShowCount);
        }
    }

    /*Hide Progress*/
    public void hideProgress() {
        mUtility.HideProgress();
    }


    @Override
    protected void onResume() {
        super.onResume();

        applicationShowingtoUser ="ACTIVE";

        System.gc();
        //   getLocation();  // Here Check the locatio permission is given or not if Given then take the location updates.
        /**
         * Notificaton part.
         */
        activateNotification = false;

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
            if(getMainActivityBluetoothAdapter()!=null){
                BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
                if(bluetoothAdapter.isEnabled()){
                    mainActivityStartScanning();
                }
            }
        }
        // asklocationPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanWhenActivityDestroyed();
        unregisterReceiver(mbluetoothBroadcastReceiever);
        System.out.println("MainActivity onDestroyExecuted");
        DisconnectAllDevices();
        mTimer.cancel();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        View logo = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        mImageViewBack = (ImageView) logo.findViewById(R.id.custom_action_img_back);
        mImageViewAddDevice = (ImageView) logo.findViewById(R.id.custom_actionbar_iv_add);
        mTextViewTitle = (TextView) logo.findViewById(R.id.custom_action_txt_title);
        mTextViewAdd = (TextView) logo.findViewById(R.id.custom_action_txt_add);
        scanningProgressBar= (ProgressBar) logo.findViewById(R.id.scaningProgressbar);
        scanningProgressBar.setVisibility(View.INVISIBLE);

        mTextViewAdd.setVisibility(View.GONE);
        mTextViewTitle.setText(R.string.app_name);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mToolbar.addView(logo);
    }

    public void showBackButton(boolean enable) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {
            // Remove hamburger
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_back);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            mActionBarDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    public void setScrollingBehavior(boolean isMapIndex) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();

        if (isMapIndex) {
            params.setScrollFlags(0);
            appBarLayout.setLayoutParams(appBarLayoutParams);
        } else {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
            appBarLayout.setLayoutParams(appBarLayoutParams);
        }
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_main_content_container);
        switch (menuItem.getItemId()) {
            case R.id.menu_dashboard:

                if (!(mFragment instanceof FragmentHome)) {
                    removeAllFragmentFromBack();
                    replacesFragment(new FragmentHome(), false, null, 1);
                }
                else {
                    if (mUtility.haveInternet()) {
                        APICrushLogout();
                    }
                    showCurentLocationInMap();
                }
                break;
            case R.id.menu_help:
                if (!(mFragment instanceof FragmentHelp)) {
                    removeAllFragmentFromBack();
                    replacesFragment(new FragmentHelp(), false, null, 1);
                }
                break;
            case R.id.menu_accountsetting:
                if (!(mFragment instanceof FragmentAccountSetting)) {
                    removeAllFragmentFromBack();
                    replacesFragment(new FragmentAccountSetting(), false, null, 1);
                }
                break;
            case R.id.menu_about_us:
                if (!(mFragment instanceof FragmentAboutUs)) {
                    removeAllFragmentFromBack();
                    replacesFragment(new FragmentAboutUs(), false, null, 1);
                }
                break;
            case R.id.menu_logout:

                /// onLogoutClick();

                mUtility.errorDialogWithYesNoCallBack("KUURV", "Are you sure want to Logout ?", "Yes", "No", true, 1, new onAlertDialogCallBack() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //            PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).ResetPrefData();
                        if (!isFinishing()) {
                            if (!mUtility.haveInternet()) {
                                userLoggingoutCheck=true;
                                DisconnectAllDevices();
                                clear_tables();
                                PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).ResetPrefData();
                                Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(mIntent);
                                finish();
                            } else {
                                userLoggingoutCheck=true;
                                DisconnectAllDevices();
                                logout();
                            }

                        }

                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });

                break;
            case R.id.menu_buy_now:
                Intent intent = new Intent(ACTION_VIEW);
                intent.setData(Uri.parse("https://www.kuurvtracker.com/buy-now"));
                startActivity(intent);
                break;
            default:
                break;
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void removeAllFragmentFromBack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void replacesFragment(Fragment mFragment, boolean isBackState, Bundle mBundle, int animationType) {





        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (isBackState)
            fragmentTransaction.addToBackStack(null);

        if (mBundle != null)
            mFragment.setArguments(mBundle);
        fragmentTransaction.replace(R.id.activity_main_main_content_container, mFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void replacesFragment(Fragment mOldFragment, Fragment mFragment, boolean isBackState, Bundle mBundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (isBackState)
            fragmentTransaction.addToBackStack(null);
        if (mBundle != null)
            mFragment.setArguments(mBundle);
        // fragmentTransaction.replace(R.id.activity_main_fragment_container,
        // mFragment);
        // fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.hide(mOldFragment);
        fragmentTransaction.add(R.id.activity_main_main_content_container,
                mFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addFragment(Fragment mFragment, boolean isBackState,
                            Bundle mBundle) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (isBackState)
            fragmentTransaction.addToBackStack(null);

        if (mBundle != null)
            mFragment.setArguments(mBundle);

        fragmentTransaction.add(R.id.activity_main_main_content_container,
                mFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        mUtility.hideKeyboard(MainActivity.this);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_main_content_container);
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
        if (count > 0) {
            if (mFragment instanceof FragmentHome) {
                if (mFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                    mFragment.getChildFragmentManager().popBackStack();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            if (mFragment instanceof FragmentHome) {
                if (exit) {
//                    importDB();
                    CustomDialog.ExitDialog(MainActivity.this, getString(R.string.alert_exit));

                } else {
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 2000);
                }

            } else {
                mNavigationView.getMenu().getItem(0).setChecked(true);
                replacesFragment(new FragmentHome(), false, null, 0);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        if (mOnLocationChangeListnear !=null){
            mOnLocationChangeListnear.onLocationChange(location);
        }
        updateLatLong(latitude, longitude);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final long period = 10000;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mPreferenceHelper.getUserId() != null && !mPreferenceHelper.getUserId().isEmpty()) {
                            updateLatLong(latitude, longitude);
                        }
                    }
                }, 0, period);

            }
        });

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Code for Getting the Location.i.e latitude and longitude
     */
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private String CheckRecordExistInUserAccountDB(String values, String table_name, String ColumnValues) {
        DataHolder mDataHolder;
        String url = "select * from " + table_name + " where " + ColumnValues + "= '" + values + "'";
        System.out.println("Main_Activity>>>>>>Response_Querey = " + url);
        mDataHolder = mDbHelper.read(url);
        if (mDataHolder != null) {
            //    System.out.println(" UserList : " + url + " : " + mDataHolder.get_Listholder().size());
            if (mDataHolder != null && mDataHolder.get_Listholder().size() != 0) {
                return mDataHolder.get_Listholder().get(0).get(mDbHelper.ble_address);
            } else {
                return "-1";
            }
        }
        return "-1";
    }

    public void getDBDeviceList() {
        DataHolder mDataHolderLight;
        if (mArrayListMyDevices != null) {
            mArrayListMyDevices.clear();
        }
        try {
            String url = "select * from Device_Table";
            mDataHolderLight = mDbHelper.readData(url);
            if (mDataHolderLight != null) {
                Vo_Device_Regstd_from_serv vo_device_regstd_from_serv;
                for (int i = 0; i < mDataHolderLight.get_Listholder().size(); i++) {
                    vo_device_regstd_from_serv = new Vo_Device_Regstd_from_serv();
                    //   mStringsArrayName.add(mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.id));
                    vo_device_regstd_from_serv.setBle_address(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.ble_address));
                    vo_device_regstd_from_serv.setLatitude(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.latitude));
                    vo_device_regstd_from_serv.setLongitude(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.longitude));
                    vo_device_regstd_from_serv.setPhoto_serverURL(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.photo_server_url));
                    vo_device_regstd_from_serv.setPhoto_localURL(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.photo_local_url));
                    vo_device_regstd_from_serv.setCreated_time(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.created_time));
                    vo_device_regstd_from_serv.setUpdated_time(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.updated_time));
                    vo_device_regstd_from_serv.setDevice_name(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.device_name));
                    vo_device_regstd_from_serv.setId(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.id)));
                    vo_device_regstd_from_serv.setServer_id(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.server_id));
                    vo_device_regstd_from_serv.setIs_silent_mode(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.DeviceFiledSilentMode)));
                    vo_device_regstd_from_serv.setBuzzer_volume(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.DeviceFiledBuzzerVolume)));
                    vo_device_regstd_from_serv.setSeperate_alert(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.DeviceFiledSeperateAlert)));
                    vo_device_regstd_from_serv.setRepeat_alert(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.DeviceFiledRepeatAlert)));
                    vo_device_regstd_from_serv.setCorrection_status(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.correction_status));
                    vo_device_regstd_from_serv.setDevice_type(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.device_type));
                    vo_device_regstd_from_serv.setTracker_device_alert(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.tracker_device_alert));
                    vo_device_regstd_from_serv.setMarked_lost(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.marked_lost));
                    vo_device_regstd_from_serv.setIs_active(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.is_active));
                    vo_device_regstd_from_serv.setContact_name(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.contact_name));
                    vo_device_regstd_from_serv.setContact_email(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.contact_email));
                    vo_device_regstd_from_serv.setContact_mobile(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.contact_mobile));
                    vo_device_regstd_from_serv.setBattery_status(Integer.parseInt(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.DeviceFiledBatteryStatus)));
                    vo_device_regstd_from_serv.setTracker_Address(mDataHolderLight.get_Listholder().get(i).get(mDbHelper.tracker_locAdds));
                    mArrayListMyDevices.add(vo_device_regstd_from_serv);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        stopPlayingsound();
        return super.onKeyDown(keyCode, event);
    }


    private void addDeviceOnConnect(String Ble_adddress, String connection_status, Vo_Device_Regstd_from_serv mVoDevice) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("device_id", mVoDevice.getServer_id());   // Server ID...
        params.put("ble_address", Ble_adddress);
        params.put("device_name", mVoDevice.getDevice_name());
        params.put("correction_status", connection_status);
        //     System.out.println("Fragment_Home>>>Correction Status Fetched From DB and Given to API  " + connection_status);
        params.put("device_type", "1");

        params.put("longitude", getlongitudeFromDB(mPreferenceHelper.getUserId()));
        params.put("latitude", getlatitudeFromDB(mPreferenceHelper.getUserId()));

        if (mVoDevice.getTracker_device_alert().toString() == null || mVoDevice.getTracker_device_alert().toString().isEmpty()) // This logic is used to check when the user login to IOS and the to android.Bcoz in IOS its sending Null for this parameter..
        {
            params.put("tracker_device_alert", "NA");
        } else {
            params.put("tracker_device_alert", mVoDevice.getTracker_device_alert());
        }

        if (mVoDevice.getMarked_lost().toString() == null || mVoDevice.getMarked_lost().toString().isEmpty()) // This logic is used to check when the user login to IOS and the to android.Bcoz in IOS its sending Null for this parameter..
        {
            params.put("marked_lost", "NA");
        } else {
            params.put("marked_lost", mVoDevice.getMarked_lost());
        }

        params.put("is_active", mVoDevice.getIs_active());
        params.put("contact_name", mVoDevice.getContact_name());
        params.put("contact_email", mVoDevice.getContact_email());
        params.put("contact_mobile", mVoDevice.getContact_mobile());
        params.put("device_name", mVoDevice.getDevice_name());
        System.out.println("Param=" + params.toString());
        final Call<VoAddDeviceData> mLogin = mApiService.Add_Device_API_without_image(params);
        mLogin.enqueue(new Callback<VoAddDeviceData>() {
            @Override
            public void onResponse(Call<VoAddDeviceData> call, Response<VoAddDeviceData> response) {
                VoAddDeviceData configure_device = response.body();
                if (configure_device.getMessage().startsWith("Invalid Token")) {
                    APICrushLogout();
                } else {
                    ContentValues mContentValues = new ContentValues();
                    if (configure_device.getData().getUser_id() != null && !configure_device.getData().getUser_id().isEmpty()) {
                        mContentValues.put(mDbHelper.user_id, "" + configure_device.getData().getUser_id());
                    } else {
                        mContentValues.put(mDbHelper.user_id, mPreferenceHelper.getUserId());
                    }
                    mContentValues.put(mDbHelper.ble_address, "" + configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase());
                    mContentValues.put(mDbHelper.device_name, "" + configure_device.getData().getDevice_name());
                    mContentValues.put(mDbHelper.correction_status, "" + configure_device.getData().getCorrection_status());
                    mContentValues.put(mDbHelper.device_type, "" + configure_device.getData().getDevice_type());
                    mContentValues.put(mDbHelper.latitude, "" + configure_device.getData().getLatitude());
                    mContentValues.put(mDbHelper.longitude, "" + configure_device.getData().getLongitude());
                    mContentValues.put(mDbHelper.photo_local_url, configure_device.getData().getDevice_image());
                    mContentValues.put(mDbHelper.tracker_device_alert, "" + configure_device.getData().getTracker_device_alert());
                    mContentValues.put(mDbHelper.marked_lost, "" + configure_device.getData().getMarked_lost());
                    mContentValues.put(mDbHelper.is_active, "" + configure_device.getData().getIs_active());
                    mContentValues.put(mDbHelper.contact_name, "" + configure_device.getData().getContact_name());
                    mContentValues.put(mDbHelper.contact_email, "" + configure_device.getData().getContact_email());
                    mContentValues.put(mDbHelper.contact_mobile, "" + configure_device.getData().getContact_mobile());
                    mContentValues.put(mDbHelper.status, "" + configure_device.getData().getCorrection_status());
                    mContentValues.put(mDbHelper.created_time, "" + configure_device.getData().getCreated_at());
                    mContentValues.put(mDbHelper.updated_time, "" + configure_device.getData().getUpdated_at());
                    mContentValues.put(mDbHelper.server_id, "" + configure_device.getData().getId());
                    mContentValues.put(mDbHelper.identifier, "NA");
                    mContentValues.put(mDbHelper.is_sync, "NA");
                    mContentValues.put(mDbHelper.photo_server_url, configure_device.getData().getDevice_image_path());
                    mContentValues.put(mDbHelper.tracker_locAdds, retutn_tracekrAddress(Double.parseDouble(configure_device.getData().getLatitude()), Double.parseDouble(configure_device.getData().getLongitude())));

                    String isExistInUserDB = CheckRecordExistInUserAccountDB_BLEAddress(configure_device.getData().getBle_address().replace(":", "").trim().toLowerCase(), mDbHelper.Device_Table, mDbHelper.ble_address);
                    if (isExistInUserDB.equalsIgnoreCase("-1")) {
                        mDbHelper.insertRecord(mDbHelper.Device_Table, mContentValues);
                    } else {
                        mDbHelper.updateRecord(mDbHelper.Device_Table, mContentValues, mDbHelper.ble_address + "=?", new String[]{isExistInUserDB});
                    }
                    getDBDeviceList();

                }
            }

            @Override
            public void onFailure(Call<VoAddDeviceData> call, Throwable t) {
                System.out.println("Response Failure" + t.getMessage());
            }
        });
    }

    private String CheckRecordExistInUserAccountDB_BLEAddress(String values, String table_name, String ColumnValues) {
        DataHolder mDataHolder;
        String url = "select * from " + table_name + " where " + ColumnValues + "= '" + values + "'";
        mDataHolder = mDbHelper.read(url);
        if (mDataHolder != null) {
            System.out.println(" UserList : " + url + " : " + mDataHolder.get_Listholder().size());
            if (mDataHolder != null && mDataHolder.get_Listholder().size() != 0) {
                return mDataHolder.get_Listholder().get(0).get(mDbHelper.ble_address);
            } else {
                return "-1";
            }
        }
        return "-1";
    }


    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue) {
        SQLiteDatabase sqldb = mDbHelper.getReadableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + "'" + fieldValue + "'";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    /**
     * Implementing the Crush Logout  in the application..
     */

    @Override
    protected void onRestart() {
        super.onRestart();
        /**
         * Notification flag..
         */
        activateNotification = true;
        new CrushLogout().execute();
    }


    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                System.out.println("Map Clicked");
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    private static String deletingBleAddressPassed="";
    @Override
    public void deletingedBleAddress(String bleAddress) {
        deletingBleAddressPassed=bleAddress;
    }


    // Crush Logout in android..
    class CrushLogout extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            APICrushLogout();
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
        }

    }

    public static boolean crushlogout=false;

    public void APICrushLogout() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("token", mPreferenceHelper.getAccessToken());

        final Call<VoCrushLogOut> markedAsLost = mApiService.crushLogOut(params);
        markedAsLost.enqueue(new Callback<VoCrushLogOut>() {
            @Override
            public void onResponse(Call<VoCrushLogOut> call, Response<VoCrushLogOut> response) {
                VoCrushLogOut voCrushLogOut = response.body();
                if (voCrushLogOut.getMessage().startsWith("Invalid Token")) {
                    DisconnectAllDevices();
                    userLoggingoutCheck=true;
                    crushlogout=true;
                   /* if (userloginfromDifferetnPhone!= null) {
                        userloginfromDifferetnPhone.UserLogoutFromDifferentPhone(true);
                    }*/
                    mUtility.crushlogoutDialog("User logged in from different\ndevice,so automatically \n logging out", 3, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            crushlogout=false;
                            logout();
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });


                } else if (voCrushLogOut.getMessage().equalsIgnoreCase("success!!")) {
                }
            }

            @Override
            public void onFailure(Call<VoCrushLogOut> call, Throwable t) {

            }
        });
    }

    //API Crush logout..
    private void logout() {
        mUtility.ShowProgress("Please Wait");
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getClientWithAutho())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .build();
        API mApiService = mRetrofit.create(API.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("token", mPreferenceHelper.getAccessToken());
        final Call<VoNormalLogout> NormalLogOut = mApiService.NormalLogout(params);
        NormalLogOut.enqueue(new Callback<VoNormalLogout>() {
            @Override
            public void onResponse(Call<VoNormalLogout> call, Response<VoNormalLogout> response) {
                VoNormalLogout voNormalLogout = response.body();
                mUtility.HideProgress();
                if (voNormalLogout.getMessage().equalsIgnoreCase("You are loged out successfully.")) {
                    clear_tables();
                    PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).ResetPrefData();
                    Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(mIntent);
                    finish();
                }

                if (voNormalLogout.getMessage().startsWith("Invalid Token")) {
                    mUtility.HideProgress();
                    PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).ResetPrefData();
                    clear_tables();
                    Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<VoNormalLogout> call, Throwable t) {

            }
        });
    }

    private void clear_tables() {
        mDbHelper.exeQuery("delete from " + mDbHelper.Device_Table);
        mDbHelper.exeQuery("delete from " + mDbHelper.User_Set_Info);
    }


    /**
     * This method is used to fetch the DeviceList API and fill the details to local DB.
     * Call the API,if ble address avaliable then update.else insert
     */
    public void getDeviceList_API() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("limit", "20");
        params.put("offset", "0");
        APICrushLogout();
        final Call<VoDeviceList> device_list = mApiService.Device_listAPI(params);
        device_list.enqueue(new Callback<VoDeviceList>() {
            @Override
            public void onResponse(Call<VoDeviceList> call, Response<VoDeviceList> response) {
                VoDeviceList voDeviceList = response.body();
                List<VoDeviceListData> voDeviceListData = voDeviceList.getData();
                String servermessage = voDeviceList.getMessage().toString();
                String serverResponse = voDeviceList.getResponse();
                if (servermessage.contains("success!!") && serverResponse.equalsIgnoreCase("true")) {
                    for (int i = 0; i < voDeviceListData.size(); i++) {
                        System.out.println("BLE Address = " + voDeviceListData.get(i).getBle_address());
                        ContentValues mContentValues = new ContentValues();
                        mContentValues.put(mDbHelper.user_id, voDeviceListData.get(i).getUser_id());
                        mContentValues.put(mDbHelper.ble_address, voDeviceListData.get(i).getBle_address().toString().toLowerCase().replace(":", ""));
                        mContentValues.put(mDbHelper.device_name, voDeviceListData.get(i).getDevice_name());
                        mContentValues.put(mDbHelper.correction_status, voDeviceListData.get(i).getCorrection_status());
                        mContentValues.put(mDbHelper.device_type, voDeviceListData.get(i).getDevice_type());
                        mContentValues.put(mDbHelper.latitude, voDeviceListData.get(i).getLatitude());
                        mContentValues.put(mDbHelper.longitude, voDeviceListData.get(i).getLongitude());
                        mContentValues.put(mDbHelper.photo_local_url, "NA" + voDeviceListData.get(i).getDevice_type());
                        mContentValues.put(mDbHelper.tracker_device_alert, voDeviceListData.get(i).getTracker_device_alert());
                        mContentValues.put(mDbHelper.marked_lost, voDeviceListData.get(i).getMarked_lost());
                        mContentValues.put(mDbHelper.is_active, voDeviceListData.get(i).getIs_active());
                        mContentValues.put(mDbHelper.contact_name, voDeviceListData.get(i).getDevice_name());
                        mContentValues.put(mDbHelper.contact_email, voDeviceListData.get(i).getContact_email());
                        mContentValues.put(mDbHelper.contact_mobile, voDeviceListData.get(i).getContact_mobile());
                        mContentValues.put(mDbHelper.status, voDeviceListData.get(i).getCorrection_status());
                        mContentValues.put(mDbHelper.created_time, voDeviceListData.get(i).getCreated_at());
                        mContentValues.put(mDbHelper.updated_time, voDeviceListData.get(i).getUpdated_at());
                        mContentValues.put(mDbHelper.server_id, voDeviceListData.get(i).getId());
                        mContentValues.put(mDbHelper.identifier, "NA");
                        mContentValues.put(mDbHelper.is_sync, "NA");
                        mContentValues.put(mDbHelper.photo_server_url, voDeviceListData.get(i).getDevice_image_path());
                        mContentValues.put(mDbHelper.tracker_locAdds, retutn_tracekrAddress(Double.parseDouble(voDeviceListData.get(i).getLatitude()), Double.parseDouble(voDeviceListData.get(i).getLongitude())));

                        String isExistInUserDB = CheckRecordExistInUserAccountDB(voDeviceListData.get(i).getBle_address().toLowerCase().replace(":", "").trim().toString(), mDbHelper.Device_Table, mDbHelper.ble_address);
                        if (isExistInUserDB.equalsIgnoreCase("-1")) {
                            mDbHelper.insertRecord(mDbHelper.Device_Table, mContentValues);
                        } else {
                            mDbHelper.updateRecord(mDbHelper.Device_Table, mContentValues, mDbHelper.ble_address + "=?", new String[]{isExistInUserDB});

                        }
                    }
                    //  getDBDeviceList();
                } else if (servermessage.contains("Invalid Token") && serverResponse.equalsIgnoreCase("false")) {
                    APICrushLogout();
                }
            }

            @Override
            public void onFailure(Call<VoDeviceList> call, Throwable t) {

            }
        });
    }


    private void addDeviceOnConnect_WithoutInternet(String mStringBleAddress, String connection_status) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mDbHelper.updated_time, mUtility.convertTimeNoInternt());
        mContentValues.put(mDbHelper.correction_status, connection_status);
        mDbHelper.updateRecord(mDbHelper.Device_Table, mContentValues, mDbHelper.ble_address + "=?", new String[]{mStringBleAddress});
        getDBDeviceList();

    }


    /**
     * This method will return the Address of the latitude and longitude
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public String retutn_tracekrAddress(double latitude, double longitude) {
        String getAddress = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            getAddress=addresses.get(0).getPremises()+","+addresses.get(0).getSubLocality()+","+addresses.get(0).getLocality()+","+addresses.get(0).getAdminArea()+","+addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getAddress;
    }

    public String getAddressFromDB(String bleAddress) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String result = "";
        String Querey = "SELECT address_tracker FROM Device_Table WHERE ble_address=" + "'" + bleAddress + "'";
        Cursor cursor = db.rawQuery(Querey, null);
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("address_tracker"));
        }
        cursor.close();
        return result;
    }

    public String getDeviceNameFromDB(String bleAddress) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String result = "";
        String Querey = "SELECT device_name FROM Device_Table WHERE ble_address=" + "'" + bleAddress + "'";
        Cursor cursor = db.rawQuery(Querey, null);
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("device_name"));
        }
        cursor.close();
        return result;
    }

    public void updateAddressinDB(String BleAddress, String address) {
        if (address != null && !address.isEmpty()) {
            String Querey = "UPDATE Device_Table SET address_tracker = '" + address + "'" + "where ble_address=" + "'" + BleAddress + "'";
            mDbHelper.exeQuery(Querey);
        }
    }

    /**
     * Notification part..
     */
    public static boolean activateNotification = false;

    private void displayNotification(String bleAddress, int ConnectionStatus) {

        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notificationlayout);
        String DeviceName = "" + getDeviceNameFromDB(bleAddress);
        String message = "";

        if (ConnectionStatus == 0) {
            message = "has been Disconnected or out of Range";
        } else {
            message = "has been Connected";
        }

        notificationView.setTextViewText(R.id.notification_higher_text, DeviceName + "\n" + message);
        NotificationCompat.Builder mbuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Tracker Application")
                        .setAutoCancel(true)
                        .setCustomContentView(notificationView)
                        .setContentText("" + DeviceName + "\n" + message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mbuilder.setContentIntent(contentIntent);


        NotificationManagerCompat notificationmanager = NotificationManagerCompat.from(this);
        notificationmanager.notify(Integer.parseInt(getAlphaNumericString(5)), mbuilder.build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        applicationShowingtoUser="BACKGROUND";
        activateNotification = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationShowingtoUser="BACKGROUND";
    }


    static String getAlphaNumericString(int n) {

        String AlphaNumericString = "0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }


    private void updateLatLong(String latitude, String longitude) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mDbHelper.UserLatitude, latitude);
        mContentValues.put(mDbHelper.UserLongitude, longitude);
        mContentValues.put(mDbHelper.LoginUserID, mPreferenceHelper.getUserId());
        String isExistInUserDB = CheckRecordExistInLatLong(mPreferenceHelper.getUserId(), mDbHelper.User_latlong, mDbHelper.LoginUserID);


        if (isExistInUserDB.equalsIgnoreCase("-1")) {
            mDbHelper.insertRecord(mDbHelper.User_latlong, mContentValues);
        } else {
            mDbHelper.updateRecord(mDbHelper.User_latlong, mContentValues, mDbHelper.LoginUserID + "=?", new String[]{isExistInUserDB});
        }
    }

    public String getlatitudeFromDB(String userId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String result = "";

        String Querey = "SELECT latitude FROM latlong WHERE User_id=" + "'" + userId + "'";
        Cursor cursor = db.rawQuery(Querey, null);
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("latitude"));
        }
        cursor.close();
        return result;
    }

    public String getlongitudeFromDB(String userId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String result = "";

        String Querey = "SELECT longitude FROM latlong WHERE User_id=" + "'" + userId + "'";
        Cursor cursor = db.rawQuery(Querey, null);
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("longitude"));
        }
        cursor.close();
        return result;
    }


    /**
     * It is used for Checking the userid in latlong table..
     */
    private String CheckRecordExistInLatLong(String values, String table_name, String ColumnValues) {
        DataHolder mDataHolder;
        String url = "select * from " + table_name + " where " + ColumnValues + "= '" + values + "'";
        mDataHolder = mDbHelper.read(url);
        if (mDataHolder != null) {
            //    System.out.println(" UserList : " + url + " : " + mDataHolder.get_Listholder().size());
            if (mDataHolder != null && mDataHolder.get_Listholder().size() != 0) {
                return mDataHolder.get_Listholder().get(0).get(mDbHelper.LoginUserID);
            } else {
                return "-1";
            }
        }
        return "-1";
    }


    private Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void showCurentLocationInMap()
    {
        String latitudeFromDb = getlatitudeFromDB(mPreferenceHelper.getUserId());
        String longitudeFromDb =getlongitudeFromDB(mPreferenceHelper.getUserId());
        if(mDbHelper.tableIsEmpty(mDbHelper.Device_Table)) // If table is empty means no device is added...
        {
            if (latitudeFromDb != null && !latitudeFromDb.isEmpty() && longitudeFromDb != null && !longitudeFromDb.isEmpty())
            {
                LatLng latLng = new LatLng(Double.parseDouble(latitudeFromDb), Double.parseDouble(longitudeFromDb));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                markerOptions.position(latLng);
                if(mMap==null)
                {
                    mMap=FragmentHome.mMap;
                }
                else{
                    mMap=FragmentHome.mMap;
                }
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            }
            else
            {
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if(mMap==null)
                { mMap=FragmentHome.mMap;
                }else{mMap=FragmentHome.mMap;}
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            }
        }
    }


    public String bleAddressAddCharcter(String bleAddress)
    {
        String address=bleAddress;
        String address_1=addChar(address,':',2).toUpperCase();
        String address_2=addChar(address_1,':',5).toUpperCase();
        String address_3=addChar(address_2,':',8).toUpperCase();
        String address_4=addChar(address_3,':',11).toUpperCase();
        String finalModifiedAddress=addChar(address_4,':',14).toUpperCase();

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


    /**
     * Get the Postion of the Object in the Arraylist Using Ble Address
     */

    public  int getPositionofBleObjectInArrayList(String bleAddress)
    {
        int postion=-1;
        for (int counter = 0; counter <mArrayListMyDevices.size(); counter++) {
            if(mArrayListMyDevices.get(counter).getBle_address().equalsIgnoreCase(bleAddress.replace(":","").toLowerCase()))
            {
                postion=counter;
            }
        }
        return postion;
    }

    /**
     * Ble part in MainActivity.
     * @param macAddress
     */
    public void ProcessConnectionToDevice(String macAddress)
    {
        if(!checkLocationPermissionGiven()){
            return;
        }

        clearScannnedDevices();
        if(getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
                BleManager.getInstance().connect(new BleDevice(device), new BleGattCallback() {
                    @Override
                    public void onStartConnect() {


                    }
                    @Override
                    public void onConnectFail(final BleDevice bleDevice, BleException e) {

                        //    startScanningforDevicesWhenConnectionFails_ConnecitonCompletes();
                        incrementCounterValuePassed_ResetCounterValuePassed();
                        UpdateconnectionProcessCompletedForDevice(true);
                    }
                    @Override
                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                        /**
                         * after i get the callback of onConnectSuccess i will make the scan happen again.
                         */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateGuiinFragmetnHome(bleDevice.getMac(),"Connected",null);
                            }
                        });

                        System.out.println("TestingTimer onConnectSuccess "+"= "+device.getAddress());
                        notifyDataWriteOnCharctersticChanged(bleDevice);

                    }

                    @Override
                    public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i)
                    {
                        System.out.println("Common MainActivity  Device Disconnected ="+bleDevice.getMac());
                        if(deletingBleAddressPassed.equalsIgnoreCase(bleDevice.getMac())){
                            deletingBleAddressPassed="";
                        }else{
                            if(mDbHelper.CheckRecordAvaliableinDB(mDbHelper.Device_Table, mDbHelper.ble_address,device.getAddress().replace(":", "").toLowerCase())){

                                if(userLoggingoutCheck==true){
                                }else{
                                    if(applicationShowingtoUser.equalsIgnoreCase("ACTIVE")){
                                        disconnectCallMethodWhenApplicationisRunning(bleDevice);
                                        if (deviceConnectioncallback != null) {
                                            deviceConnectioncallback.callbackWhenDeviceIsActuallyDisconnected(bleDevice.getMac(), "Disconnected",bleDevice);
                                        }
                                    }else if(applicationShowingtoUser.equalsIgnoreCase("BACKGROUND")){
                                        disconnectCallMethodWhenApplicationisinForegraound(bleDevice);
                                    }else{
                                        disconnectCallMethodWhenUserLogOut(bleDevice);
                                    }
                                }
                            }
                        }
                    }
                });
            }else{
                giveCustomToastBluetoothNotEnabled();
            }
        }else{
            giveCustomToastBluetoothNotEnabled();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                writeAuthenicationToDevice(bleDevice);
                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //     startScanningforDevicesWhenConnectionFails_ConnecitonCompletes();
                                incrementCounterValuePassed_ResetCounterValuePassed();
                                UpdateconnectionProcessCompletedForDevice(true);
                            }
                        });
                    }

                    @Override
                    public void onCharacteristicChanged(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Here i will get the datanotify on the Device...
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    String hex =HexUtil.encodeHexStr(data);

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


                                       int postionIntheArrayList= getPositionofBleObjectInArrayList(bleDevice.getMac());

                                        int mIntVerifyDeviceInfo = BLEUtility.hexToDecimal(hex.substring(4, 6));
                                        System.out.println("Verify Owner Info = "+mIntVerifyDeviceInfo);


                                        VoOwnerStatusForDevice voOwnerStatusForDevice=new VoOwnerStatusForDevice();
                                        voOwnerStatusForDevice.setBleAddress(bleDevice.getMac());
                                        voOwnerStatusForDevice.setOwnerstatus(true);
                                        mArraylistOwnerstatusinfo.add(voOwnerStatusForDevice);


                                        if(mIntVerifyDeviceInfo==1)
                                        {
                                            if (deviceConnectionStatus != null) {
                                                deviceConnectionStatus.ConnectionStatus(bleDevice.getMac(), "Connected",bleDevice);
                                            }

                                            if(applicationShowingtoUser.equalsIgnoreCase("BACKGROUND")){
                                                displayNotification(bleDevice.getMac().replace(":", "").toLowerCase(),1);
                                            }

                                            if(!mUtility.haveInternet()){
                                                addDeviceOnConnect_WithoutInternet(removeSymbolAndConvCaseBleAddress(bleDevice.getMac(),"lowercase"),"1");
                                            }else{
                                                addDeviceOnConnect(removeSymbolAndConvCaseBleAddress(bleDevice.getMac(),"lowercase"), "1", mArrayListMyDevices.get(getPositionofBleObjectInArrayList(bleDevice.getMac())));
                                            }
                                        }

                                        //Readbattery Percentage from the Device.
                                        readBatteryValueFromDevice(bleDevice);

                                    }


                                    else if(hex.toUpperCase().startsWith("01"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerName = hex.substring(4, (4 + msgLength));
                                        strOwnerName = BLEUtility.convertHexToString(strOwnerName);


                                        /**this logic i have used to Delete the Device by miss if
                                         *  the user trying to delete the device and its not Deleted.
                                         */

                                        if(strOwnerName.length()>1){
                                            deleteOwnerInfoFromDevice(bleDevice);
                                        }

                                    }


                                    else if(hex.toUpperCase().startsWith("02"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerPhoneNo = hex.substring(4, (4 + msgLength));
                                        strOwnerPhoneNo = BLEUtility.convertHexToString(strOwnerPhoneNo);

                                    }

                                    else if(hex.toUpperCase().startsWith("03"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerEmail = hex.substring(4, (4 + msgLength));
                                        strOwnerEmail = BLEUtility.convertHexToString(strOwnerEmail);
                                    }

                                    else if(hex.toUpperCase().startsWith("04"))
                                    {
                                        int msgLength = (BLEUtility.hexToDecimal(hex.substring(2, 4)) * 2);
                                        String strOwnerEmail1 = hex.substring(4, (4 + msgLength));
                                        strOwnerEmail1 = BLEUtility.convertHexToString(strOwnerEmail1);

                                    }

                                    else if(hex.toUpperCase().startsWith("09")){
                                        int mIntDeleteDeviceInfo = BLEUtility.hexToDecimal(hex.substring(4, 6));
                                        if (deviceConnectionStatus != null) {
                                            deviceConnectionStatus.deleteinfoReceievedfromBleDevice(mIntDeleteDeviceInfo,bleDevice);
                                        }

                                    }

                                    else if(hex.toUpperCase().startsWith("0D")){
                                        //Getting the Battery Value here..

                                        int mIntBattery = BLEUtility.hexToDecimal(hex.substring(4, 6));
                                        System.out.println("Battery=" + mIntBattery);
                                        //Update battery Status in DB.
                                        ContentValues mContentValues = new ContentValues();
                                        mContentValues.put(mDbHelper.DeviceFiledBatteryStatus, mIntBattery);
                                        mDbHelper.updateRecord(mDbHelper.Device_Table, mContentValues, mDbHelper.id + "=?", new String[]{mArrayListMyDevices.get(getPositionofBleObjectInArrayList(bleDevice.getMac())).getId() + ""});
                                        //Update battery status in Array.
                                        mArrayListMyDevices.get(getPositionofBleObjectInArrayList(bleDevice.getMac())).setBattery_status(mIntBattery);

                                        if (deviceConnectionStatus != null) {
                                            deviceConnectionStatus.readbatterystatusFromDevice(mIntBattery);
                                        }
                                        updatebatteryPercentageinDB(""+mIntBattery,bleDevice.getMac().replace(":", "").toLowerCase());
                                        readBuzzerVolume(bleDevice);
                                    }

                                    else if(hex.toUpperCase().startsWith("10") || hex.toUpperCase().startsWith("11"))   // 11 right...
                                    {
                                        int mBuzzerVolume = BLEUtility.hexToDecimal(hex.substring(4, 6));
                                        System.out.println("BuzzerVolume= " + mBuzzerVolume+" "+bleDevice.getMac());
                                        updateBuzervolumeinDb(""+mBuzzerVolume,bleDevice.getMac().replace(":", "").toLowerCase());
                                        readSilentModeStatusFromDevice(bleDevice);
                                    }


                                    else if(hex.toUpperCase().startsWith("0F")){
                                        int silentModeOnOff = BLEUtility.hexToDecimal(hex.substring(4, 6));
                                        System.out.println("SilentModeStatus " + silentModeOnOff+" "+bleDevice.getMac());

                                        if(Integer.toString(silentModeOnOff).equalsIgnoreCase("1")){
                                            updateSilentModeStatusinDB("1",bleDevice.getMac().toLowerCase().replace(":", ""));

                                        }else{
                                            updateSilentModeStatusinDB("0",bleDevice.getMac().toLowerCase().replace(":", ""));
                                        }

                                        //  startScanningforDevicesWhenConnectionFails_ConnecitonCompletes();
                                        incrementCounterValuePassed_ResetCounterValuePassed();
                                        UpdateconnectionProcessCompletedForDevice(true);

                                    }
                                    System.out.println("Data Parsing --------> "+hex);

                                }

                            }
                        });
                    }
                });

    }

    private void updateBuzervolumeinDb( String HighorLow,String bleAddress){
        if(HighorLow.equalsIgnoreCase("1")){
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(mDbHelper.DeviceFiledBuzzerVolume,"1" );
            UpdateRecordinTable(mContentValues,bleAddress);
        }else{
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(mDbHelper.DeviceFiledBuzzerVolume,"0" );
            UpdateRecordinTable(mContentValues,bleAddress);
        }
    }

    private void updateSilentModeStatusinDB(String DataPassed,String bleaddress){
        if(DataPassed.equalsIgnoreCase("1")){
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(mDbHelper.DeviceFiledSilentMode,"1" );
            UpdateRecordinTable(mContentValues,bleaddress);
        }else{
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(mDbHelper.DeviceFiledSilentMode,"0" );
            UpdateRecordinTable(mContentValues,bleaddress);
        }
    }


    private void updatebatteryPercentageinDB(String battreyPerecentage,String bleAddress){
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mDbHelper.DeviceFiledBatteryStatus, battreyPerecentage);
        UpdateRecordinTable(mContentValues,bleAddress);
    }

    private void UpdateRecordinTable(ContentValues mContentValues,String BleAddress){
        mDbHelper.updateRecord(mDbHelper.Device_Table, mContentValues, "ble_address = ?",new String[]{BleAddress});
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
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
        short mDataLength = (short) 0x04;
        byte byte_value[] = new byte[6];
        System.out.println("auth_value=" + value);
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
                verifyDeviceInfoToDevice(userUniqueKey,device);
                //sometimes after verifying the info also color won t change so i am changing here...
                if (deviceConnectionStatus != null) {
                    deviceConnectionStatus.ConnectionStatus(device.getMac(), "Connected",device);
                }

            }
        }, 1000);
    }
    //Verify infotoDevice.
    public static short OPCODE_VERIFY_DEVICE_INFO = 0x0B;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void verifyDeviceInfoToDevice(String userUniqueKey,BleDevice device) {
        byte[] byte_user_key = userUniqueKey.getBytes(StandardCharsets.US_ASCII);
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
        writedatatoBleDevice(device, byte_value);

    }

    /**
     * delete (device)owner info from target device
     */
    public void deleteOwnerInfoFromDevice(BleDevice device) {
        byte[] byte_user_key = userUniqueKey.getBytes(StandardCharsets.US_ASCII);
        byte value_index = 0, name_index;
        short mDataLength = (short) byte_user_key.length;
        byte byte_value[] = new byte[2 + byte_user_key.length];
        byte_value[0] = (byte) (Constant.OPCODE_DELETE_OWNER_INFO & 0xFF);
        value_index++;
        byte_value[1] = (byte) (mDataLength & 0xFF);
        value_index++;
        for (name_index = 0; name_index < byte_user_key.length; name_index++) {
            byte_value[value_index++] = byte_user_key[name_index];
        }
        writedatatoBleDevice(device, byte_value);
    }



    public void readOwnerInfoFromDevice(BleDevice bleDevice) {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_READ_OWNER_INFO & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
        // byte_value = null;
    }



    public void readBatteryValueFromDevice(BleDevice bleDevice) {
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_READ_BATTERY_VALUE & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
        // byte_value = null;
    }



    /**
     * read buzzer volume on target device disconnect
     */
    public void readBuzzerVolume(BleDevice bleDevice) {
        // OnOff=  0- turn off, 1-turn on
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_READ_BUZZER_VOLUME & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
        // byte_value = null;
    }


    public void readSilentModeStatusFromDevice(BleDevice bleDevice) {
        // OnOff=  0- turn off, 1-turn on
        short mDataLength = (short) 0x00;
        byte byte_value[] = new byte[2];
        byte_value[0] = (byte) (Constant.OPCODE_READ_SILENT_MODE_ON_OFF & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
        // byte_value = null;
    }


    /**
     * write buzzer volume on target device disconnect
     */
    public void writeBuzzerVolumeHighorLow(boolean isHighVolume,BleDevice bleDevice) {
        // OnOff=  0- turn off, 1-turn on
        short mDataLength = (short) 0x01;
        byte byte_value[] = new byte[3];
        short mDataOnOff = (short) 0x00;
        if (isHighVolume) {
            mDataOnOff = (short) 0x01;
        }
        byte_value[0] = (byte) (Constant.OPCODE_WRITE_BUZZER_VOLUME & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        byte_value[2] = (byte) (mDataOnOff & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
        //   byte_value = null;
    }



    short msilentModedata;
    public void writeSilentModeOFFSwitchisOFF(BleDevice bleDevice){
        short mDataLength = (short) 0x01;
        byte byte_value[] = new byte[3];
        short mDataLengthx = (short) 0x01;     //  00 Switch is ON
        byte_value[0] = (byte) (Constant.OPCODE_SILENTMODE_ON_OFF & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        byte_value[2] = (byte) (mDataLengthx & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
    }


    public void writeSilentModeOnSwitchisON(BleDevice bleDevice){
        short mDataLength = (short) 0x01;
        byte byte_value[] = new byte[3];
        short mDataLengthx = (short) 0x00;     //  00 Switch is ON
        byte_value[0] = (byte) (Constant.OPCODE_SILENTMODE_ON_OFF & 0xFF);
        byte_value[1] = (byte) (mDataLength & 0xFF);
        byte_value[2] = (byte) (mDataLengthx & 0xFF);
        writedatatoBleDevice(bleDevice, byte_value);
    }


    public String removeSymbolAndConvCaseBleAddress( String bleaddress,String Case)
    {
        if(Case.equalsIgnoreCase("lowercase"))
        { return bleaddress.toLowerCase().replace(":",""); }
        else{return bleaddress.toUpperCase().replace(":","");}

    }

    public BluetoothAdapter getMainActivityBluetoothAdapter()
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


    public boolean checkLocationPermissionGiven(){
        boolean result=false;
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            result=true;
        }
        return result;
    }

    public void giveCustomToastBluetoothNotEnabled(){
        Drawable icon = getResources().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp);
        Toasty.normal(MainActivity.this, "Turn On Bluetooth",20, icon).show();

    }


    /**
     * BroadcastReceiever for Bluetoooth.
     */
    private final BroadcastReceiver mbluetoothBroadcastReceiever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (deviceConnectionStatus != null) {
                            deviceConnectionStatus.bluetoothturningOff(true);
                        }
                        stopScanningwhenBleTurningOff();
                        clearScannnedDevices();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        mainActivityStartScanning();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        incrementCounterValuePassed_ResetCounterValuePassed();
                        UpdateconnectionProcessCompletedForDevice(true);
                        break;
                }
            }
        }
    };


    private int getTableCountforRegisteredDevices(){
        int result=-1;
        result=mDbHelper.getTableCount(mDbHelper.Device_Table);
        if(result>0){
            result=mDbHelper.getTableCount(mDbHelper.Device_Table);
        }else{
            result=-1;
        }
        return  result;
    }



    public void diconnectDevice(String bleAddress){
        if(getMainActivityBluetoothAdapter()!=null){
            BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
            if(bluetoothAdapter.isEnabled()){
                final BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice device=new BleDevice(bleDevice);
                if(BleManager.getInstance().isConnected(device)){
                    BleManager.getInstance().disconnect(device);
                }
            }
        }
    }

    private  String fetchedSilentModeValue="-1";
    private  String fetchedSeparationAlertValue="-1";
    private  String fetchedRepeatAlertValue="-1";
    public void startPlayingSound(final boolean repeatalerton){
        if(repeatalerton){
            fragmentalertSettingMediaPlayer.setLooping(true);
        }else{
            fragmentalertSettingMediaPlayer.setLooping(false);
        }
        fragmentalertSettingMediaPlayer.start();
        fragmentalertSettingMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                fragmentalertSettingMediaPlayer.reset();
                fragmentalertSettingMediaPlayer =MediaPlayer.create(getApplicationContext(), R.raw.trackeralertseparation);
            }
        });
    }

    public  void stopPlayingsound(){
        if(fragmentalertSettingMediaPlayer.isPlaying()){
            fragmentalertSettingMediaPlayer.stop();
        }
        fragmentalertSettingMediaPlayer=MediaPlayer.create(this, R.raw.trackeralertseparation);
    }


    private void getValuesofAlertSetting(String bleaddress){
        Map<String,String> getUSer_info=  mDbHelper.getBLE_Set_Info(mDbHelper.getReadableDatabase(),
                mDbHelper.Device_Table,
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
                System.out.println("Fragment_Configure_Device>>>>>Its NUll");
            } else
            {
                fetchedSilentModeValue=(String) getUSer_info.get("is_silent_mode");
                fetchedSeparationAlertValue=(String) getUSer_info.get("seperate_alert");
                fetchedRepeatAlertValue=(String) getUSer_info.get("repeat_alert");
            }
        }
    }


    private void processValuestoBuzz(String bleAddress){
        if(fetchedSilentModeValue.equalsIgnoreCase("1")){
            //play music.
            if(fetchedSeparationAlertValue.equalsIgnoreCase("1")&&fetchedRepeatAlertValue.equalsIgnoreCase("1")){
                return;
            }

            if(fetchedSeparationAlertValue.equalsIgnoreCase("0")&&fetchedRepeatAlertValue.equalsIgnoreCase("0")){
                startPlayingSound(true);
                final Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopPlayingsound();
                    }
                }, 5000);
            }else{
                startPlayingSound(false);
            }

        }else{
            //Don t Play.
        }


        fetchedSilentModeValue="-1";
        fetchedSeparationAlertValue="-1";
        fetchedRepeatAlertValue="-1";

    }

    private void processValuestoBuzzWhenapplicationBackground(String bleAddress){
        if(fetchedSilentModeValue.equalsIgnoreCase("1")){
            if(fetchedSeparationAlertValue.equalsIgnoreCase("0")&&fetchedRepeatAlertValue.equalsIgnoreCase("0")){
                startPlayingSound(false);
            }
            if(fetchedSeparationAlertValue.equalsIgnoreCase("0")&&fetchedRepeatAlertValue.equalsIgnoreCase("1")){
                startPlayingSound(false);
            }
        }else{

        }
    }



    private void disconnectCallMethodWhenApplicationisRunning(BleDevice device){
        getValuesofAlertSetting(device.getMac().replace(":", "").toLowerCase());
        RingtoneThreadApplicationisRunnning threadApplicationisRunnning=new RingtoneThreadApplicationisRunnning(device);
        threadApplicationisRunnning.start();
        if (deviceConnectionStatus != null) {
            deviceConnectionStatus.ConnectionStatus(device.getMac(), "Disconnected",device);
        }
        if(!mUtility.haveInternet()){
            addDeviceOnConnect_WithoutInternet(device.getMac().replace(":", "").toLowerCase(),"0");
        }else{
            addDeviceOnConnect(removeSymbolAndConvCaseBleAddress(device.getMac(),"lowercase"), "0", mArrayListMyDevices.get(getPositionofBleObjectInArrayList(device.getMac().replace(":", "").toLowerCase())));
        }

    }
    private void disconnectCallMethodWhenApplicationisinForegraound(BleDevice device){
        getValuesofAlertSetting(device.getMac().replace(":", "").toLowerCase());
        if(!mUtility.haveInternet()){
            addDeviceOnConnect_WithoutInternet(device.getMac().replace(":", "").toLowerCase(),"0");
        }else{
            addDeviceOnConnect(removeSymbolAndConvCaseBleAddress(device.getMac(),"lowercase"), "0", mArrayListMyDevices.get(getPositionofBleObjectInArrayList(device.getMac().replace(":","" ).toLowerCase())));
        }
        // processValuestoBuzzWhenapplicationBackground(device.getMac().replace(":", "").toLowerCase());
        RingtoneThreadApplicationisBackGround ringtoneThreadApplicationisBackGround=new RingtoneThreadApplicationisBackGround(device);
        ringtoneThreadApplicationisBackGround.start();
        displayNotification(device.getMac().replace(":", "").toLowerCase(),0);
    }

    private void  disconnectCallMethodWhenUserLogOut(BleDevice device){
        if(mArrayListMyDevices.size()>0){
            if(!mUtility.haveInternet()){
                addDeviceOnConnect_WithoutInternet(device.getMac().replace(":", "").toLowerCase(),"0");
            }else{
                //    addDeviceOnConnect(removeSymbolAndConvCaseBleAddress(device.getMac(),"lowercase"), "0", mArrayListMyDevices.get(getPositionofBleObjectInArrayList(device.getMac().replace(":","" ).toLowerCase())));
            }
        }

    }

    private void DisconnectAllDevices(){
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }


    private void updateGuiinFragmetnHome(final String bleAddres,final String ConnectionStatus,final BleDevice bledevice){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                System.out.println("TestingTimer updateGuiinFragmetnHome bleAddres "+bleAddres+" status = "+ConnectionStatus);
                if(ConnectionStatus.equalsIgnoreCase("Connected")){
                    if(fragmentHome_instance!=null){
                        if(fragmentHome_instance.bleAddressonTopLayout!=null){
                            if(fragmentHome_instance.bleAddressonTopLayout.length()>1){
                                if(fragmentHome_instance.fragmetn_homeDestroyed==false){
                                    if(fragmentHome_instance.bleAddressonTopLayout.equalsIgnoreCase(bleAddres)){
                                        if(getPositionofBleObjectInArrayList(bleAddres)<=mArrayListMyDevices.size()-1){
                                            fragmentHome_instance.topLayoutColorChnageOnConnection(true,getPositionofBleObjectInArrayList(bleAddres));
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                if(ConnectionStatus.equalsIgnoreCase("Disconnected")){
                    if(fragmentHome_instance!=null){
                        if(fragmentHome_instance.bleAddressonTopLayout!=null){
                            if(fragmentHome_instance.bleAddressonTopLayout.length()>1){

                                if(fragmentHome_instance.fragmetn_homeDestroyed==false){
                                    if(fragmentHome_instance.bleAddressonTopLayout.equalsIgnoreCase(bleAddres)){
                                        if(getPositionofBleObjectInArrayList(bleAddres)<=mArrayListMyDevices.size()-1){
                                            fragmentHome_instance.topLayoutColorChnageOnConnection(false,getPositionofBleObjectInArrayList(bleAddres));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try{
                    if (deviceConnectionStatus != null) {
                        deviceConnectionStatus.ConnectionStatus(bleAddres, ConnectionStatus,bledevice);

                    }
                }catch (Exception e){}finally {
                    if (deviceConnectionStatus != null) {
                        if(bledevice!=null){
                            deviceConnectionStatus.ConnectionStatus(bleAddres, ConnectionStatus,bledevice);
                        }
                    }

                }

            }
        });
    }


    private    class RingtoneThreadApplicationisRunnning extends  Thread{
        BleDevice bleDevice;
        RingtoneThreadApplicationisRunnning(BleDevice bleDevice){
            this.bleDevice=bleDevice;
        }
        @Override
        public void run() {
            processValuestoBuzz(bleDevice.getMac().replace(":", "").toLowerCase());
        }
    }


    private    class RingtoneThreadApplicationisBackGround extends  Thread{
        BleDevice bleDevice;
        RingtoneThreadApplicationisBackGround(BleDevice bledevice){
            this.bleDevice=bledevice;
        }
        @Override
        public void run() {
            processValuestoBuzzWhenapplicationBackground(bleDevice.getMac().replace(":", "").toLowerCase());
        }
    }


    RxBleClient rxBleClient;
    Disposable mainActivityScanningInstance;
    private void mainActivityStartScanning(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(getMainActivityBluetoothAdapter()!=null){
                System.out.println("TestingTimer onScanFinished getMainActivityBluetoothAdapter()!=null ");
                BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
                if(bluetoothAdapter.isEnabled()){
                    mActivityScanningStarted=true;
                    try{
                        Disposable scanDisposable = rxBleClient.scanBleDevices(
                                new com.polidea.rxandroidble2.scan.ScanSettings.Builder()
                                        .setScanMode(com.polidea.rxandroidble2.scan.ScanSettings.SCAN_MODE_BALANCED)
                                        .build(),
                                new com.polidea.rxandroidble2.scan.ScanFilter.Builder()
                                        .setServiceUuid(null)
                                        .build() // Optional, more than one ScanFilter may be passed as varargs.
                        )

                                .subscribe(scanResult -> {
                                    //  System.out.println("ConnectionLogic scanResults"+scanResult.getBleDevice().getMacAddress()+" Name = "+scanResult.getBleDevice().getName());
                                    if(scanResult!=null){
                                        if(scanResult.getBleDevice().getName()!=null){
                                            if(scanResult.getBleDevice().getName().contains("KUURV")){

                                                if( passingScanDevices!= null) {
                                                    passingScanDevices.sendingResultstoFragment(scanResult.getBleDevice());
                                                }

                                                addScanDevices(scanResult.getBleDevice());
                                            }
                                        }
                                    }
                                }, throwable -> {
                                    // Handle an error here.
                                });
                        mainActivityScanningInstance=scanDisposable;
                    }catch (Exception e){
                        e.printStackTrace();}
                }else{
                }
            }else{
            }
        }
    }

    ArrayList<RxBleDevice> mArrayListAddDeviceScannedDevices=new ArrayList();
    private void addScanDevices(RxBleDevice bleDevice){
        if(!mArrayListAddDeviceScannedDevices.contains(bleDevice)){
            mArrayListAddDeviceScannedDevices.add(bleDevice);
            System.out.println("Testing Timer Devices Added in Scanning = "+bleDevice.getMacAddress());
        }
    }

    private void clearScannnedDevices(){
        mArrayListAddDeviceScannedDevices.clear();
    }


    private void incrementCounterValuePassed_ResetCounterValuePassed(){
        conterPassedToArraList++;
        int recordAvaliableCount=getTableCountforRegisteredDevices();
        if(recordAvaliableCount>0){
            if(conterPassedToArraList>=recordAvaliableCount){
                conterPassedToArraList=0;
            }
        }else{
            conterPassedToArraList=0;
        }
    }

    private void UpdateconnectionProcessCompletedForDevice(boolean passedValue){
        this.connectionProcessCompletedForDevice=passedValue;
        System.out.println("TestingTimer UpdateconnectionProcessCompletedForDevice "+this.connectionProcessCompletedForDevice);
    }


    private void stopScanWhenActivityDestroyed(){
        if(mActivityScanningStarted){
            if(!mainActivityScanningInstance.isDisposed()){
                mainActivityScanningInstance.dispose();
            }
        }
    }

    private void stopScanningwhenBleTurningOff(){
        if(mActivityScanningStarted){
            if(!mainActivityScanningInstance.isDisposed()){
                mainActivityScanningInstance.dispose();
            }
        }
    }

  /*  private void stopScanningWhenDevieGoingForConnection(){
        if(mActivityScanningStarted==true){ // As its made to false when the Device is Going for connection.
            mActivityScanningStarted=false;
            if(!mainActivityScanningInstance.isDisposed()){
                mainActivityScanningInstance.dispose();
            }
        }
    }*/

 /* private void startScanningforDevicesWhenConnectionFails_ConnecitonCompletes(){
      if(mActivityScanningStarted==false){
          if(!mainActivityScanningInstance.isDisposed()){
              mainActivityStartScanning();
          }
      }
  }*/

  /*private void stopScaaningIfAllDeviceAreConnected(){
      if(getMainActivityBluetoothAdapter()!=null){
          BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
          if(bluetoothAdapter.isEnabled()){
              final  List<BleDevice> ConnectedDevicelist = BleManager.getInstance().getAllConnectedDevice();
              if(ConnectedDevicelist.size()==getTableCountforRegisteredDevices()){
                  if(mActivityScanningStarted==true){
                      mActivityScanningStarted=false;
                      if(!mainActivityScanningInstance.isDisposed()){
                          mainActivityScanningInstance.dispose();
                          clearScannedDevicesIfAllDevicesAreConnected();
                      }
                  }
              }
          }
      }
  }*/

  /*private void startScanningIfAnyOneDeviceIsDisconnected(){
      if(getMainActivityBluetoothAdapter()!=null){
          BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
          if(bluetoothAdapter.isEnabled()){
              final  List<BleDevice> ConnectedDevicelist = BleManager.getInstance().getAllConnectedDevice();
              if(ConnectedDevicelist.size()<getTableCountforRegisteredDevices()){
                  if(mActivityScanningStarted==false){
                      if(mainActivityScanningInstance.isDisposed()){  // mainActivityScanningInstance.isDisposed() Return false when the Device is Scanning...
                          mainActivityStartScanning();
                      }
                  }
              }
          }
      }
  }*/

  /*private void stopScanningAndStartScanningIfDeviceNotAvaliableForLongTime(){
      if(getMainActivityBluetoothAdapter()!=null){
          BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
          if(bluetoothAdapter.isEnabled()){
              if(numberofTimesTimerExecuted>30){
                  if(mActivityScanningStarted==true){
                      if(!mainActivityScanningInstance.isDisposed()){
                          mainActivityScanningInstance.dispose();
                          clearScaanedDevicesAfterSpecificInterval();
                      }
                  }
              }
          }
      }
  }*/

  /*private void exceptionOccuredStartScanning(){
      if(getMainActivityBluetoothAdapter()!=null){
          BluetoothAdapter bluetoothAdapter=getMainActivityBluetoothAdapter();
          if(bluetoothAdapter.isEnabled()){
              if(mActivityScanningStarted==false){
                  if(mainActivityScanningInstance.isDisposed()){
                      mainActivityStartScanning();
                  }
              }else{
                  if(mActivityScanningStarted==true){
                      if(!mainActivityScanningInstance.isDisposed()){
                          mainActivityStartScanning();
                      }
                  }
              }
          }
      }
  }*/

 /* private void clearScaanedDevicesAfterSpecificInterval(){
      mArrayListAddDeviceScannedDevices.clear();
}*/

/* private void clearScannedDevicesIfAllDevicesAreConnected(){
     mArrayListAddDeviceScannedDevices.clear();
 }*/


}



// mainActivityScanningInstance.isDisposed() ------------>while scannign it gives false....
