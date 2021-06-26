package com.proofofLife.Fragments;
import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.proofofLife.Adapters.FragmentScanAdapter;
import com.proofofLife.BaseFragment.BaseFragmentHelper;
import com.proofofLife.CustomObjects.CustBluetootDevice;
import com.proofofLife.DialogHelper.AppDialogHelper;
import com.proofofLife.InterfaceActivityToFragment.BleScanHelper;
import com.proofofLife.InterfaceActivityToFragment.DataInterface;
import com.proofofLife.InterfaceActivityToFragment.PassConnectionStatusToFragment;
import com.proofofLife.InterfaceFragmentToActivity.DeviceConnectDisconnect;
import com.proofofLife.MainActivity;
import com.proofofLife.R;
import java.util.ArrayList;
import java.util.List;

import static com.proofofLife.BleUtility.BleUtilityHelper.ble_on_off;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FAILURE;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SUCESS;
import static com.proofofLife.ConstUpCode.BLE_UpCode.UNKNOWN;
public class FragmentScan extends BaseFragmentHelper {
    /**
     * Views
     */
    ProgressBar progressBar;
    private final int LocationPermissionRequestCode = 100;
    MainActivity mainActivity;
    View scanFragmetView;
    RecyclerView fragmentScan_RecyclerView;
    private static final String TAG = FragmentScan.class.getSimpleName();
    private ArrayList<CustBluetootDevice> customBluetoothDeviceList;
    FragmentScanAdapter fragmentScanAdapter;
    ImageView toolbar_fragmet_scan_refresh_image;
    DeviceConnectDisconnect deviceConnectDisconnect;

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scanFragmetView = inflater.inflate(R.layout.fragment_scan, container, false);
        intializeViews();
        onClickListner();
        checkPermissionGiven();
        checkBluetoothIsOn();
        setUpRecycleView();
        interface_ImplementationCallback();
        scanItemOnclickInterface_Adapter();
        interfaceIntialization();
        bottomNavigationInvisible(false);
        getListOfConnectedDevices();
        return scanFragmetView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public String toString() {
        return FragmentScan.class.getSimpleName();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LocationPermissionRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mainActivity.start_stop_scan();
                } else {
                    askPermission();
                }
        }
    }

    private void intializeViews() {
        fragmentScan_RecyclerView = (RecyclerView) scanFragmetView.findViewById(R.id.scan_item_recycleView_id);
        toolbar_fragmet_scan_refresh_image = (ImageView) scanFragmetView.findViewById(R.id.fragmet_scan_refresh_image_id);
        progressBar=(ProgressBar)scanFragmetView.findViewById(R.id.fragment_scan_progress_bar_id);
    }
    private void onClickListner() {
        toolbar_fragmet_scan_refresh_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ble_on_off()){
                    clearScanDevices_ClearList();
                   mainActivity.start_stop_scan();
                }else {
                    toolbar_fragmet_scan_refresh_image.setBackgroundResource(R.drawable.ic_bluetooth_off);
                }
            }
        });


    }

    private void getActivityReference() {
        mainActivity = (MainActivity) getActivity();
    }

    private void setUpRecycleView() {
        customBluetoothDeviceList = new ArrayList<CustBluetootDevice>();
        fragmentScanAdapter = new FragmentScanAdapter(customBluetoothDeviceList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentScan_RecyclerView.setLayoutManager(mLayoutManager);
        fragmentScan_RecyclerView.setAdapter(fragmentScanAdapter);
    }
    private void clearScanDevices_ClearList(){
        customBluetoothDeviceList.clear();
        fragmentScanAdapter.notifyDataSetChanged();
    }

    private void checkPermissionGiven() {
        if (isAdded()) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mainActivity.start_stop_scan();
                progressBar.setVisibility(View.VISIBLE);
                toolbar_fragmet_scan_refresh_image.setBackgroundResource(R.drawable.ic_bluetooth_scan_started);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationPermissionRequestCode);
            }
        }
    }

    private void askPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getActivity(),getResources().getString(R.string.LOCATION_OFF),Toast.LENGTH_SHORT).show();
        } else {
            location_allow_setting();
        }
    }

    private void checkBluetoothIsOn() {
        if (!ble_on_off()) {
            Toast.makeText(getActivity(),getResources().getString(R.string.BLUETOOTH_OFF),Toast.LENGTH_SHORT).show();
        }
    }

    private void location_allow_setting() {
        new AppDialogHelper().showPermissionDialog(getActivity(), "Permission Denied For Scanning");
    }

    private void interface_ImplementationCallback() {
        mainActivity.setUpbeScanHelperInterface_Intialization(new BleScanHelper() {
            @Override
            public void scanDevices(CustBluetootDevice custBluetootDevice, String TAG) {
                if (!customBluetoothDeviceList.contains(custBluetootDevice)) {
                    customBluetoothDeviceList.add(custBluetootDevice);
                    fragmentScanAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void scanStarted(String SCAN_TAG) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        toolbar_fragmet_scan_refresh_image.setBackgroundResource(R.drawable.ic_bluetooth_scan_started);
                    }
                });

            }

            @Override
            public void scanStoped(String TAG) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        toolbar_fragmet_scan_refresh_image.setBackgroundResource(R.drawable.ic_bluetooth_scan_stoped);
                    }
                });
            }
        });


        mainActivity.setupPassConnectionStatusToFragment(new PassConnectionStatusToFragment() {
            @Override
            public void connectDisconnect(String bleAddress, boolean connected_disconnected) {
                if (connected_disconnected) {
                    CustBluetootDevice custBluetootDevices = new CustBluetootDevice();
                    custBluetootDevices.setBleAddress(bleAddress);

                    if (customBluetoothDeviceList.contains(custBluetootDevices)) {
                        int postion = customBluetoothDeviceList.indexOf(custBluetootDevices);
                        CustBluetootDevice custBluetootDevices1 = customBluetoothDeviceList.get(postion);
                        custBluetootDevices1.setConnected(true);
                        fragmentScanAdapter.notifyItemChanged(postion);
                    }
                } else {
                    CustBluetootDevice custBluetootDevices = new CustBluetootDevice();
                    custBluetootDevices.setBleAddress(bleAddress);
                    if (customBluetoothDeviceList.contains(custBluetootDevices)) {
                        int postion = customBluetoothDeviceList.indexOf(custBluetootDevices);
                        CustBluetootDevice custBluetootDevices1 = customBluetoothDeviceList.get(postion);
                        custBluetootDevices1.setConnected(false);
                        fragmentScanAdapter.notifyItemChanged(postion);

                    }
                }
            }
        });

        mainActivity.setUpbledataInterface(new DataInterface() {
            @Override
            public void sendData(String datahex) {

            }

            @Override
            public void ownerInfo(byte data) {
                if(data==SUCESS){

                }else if(data==FAILURE){
                    /**
                     * Fresh Device
                     */

                }else if(data==UNKNOWN){
                    /**
                     * belongs to someone else.
                     */

                }
            }
        });


    }

    private void interfaceIntialization(){
        deviceConnectDisconnect = (DeviceConnectDisconnect) getActivity();
    }

    private void scanItemOnclickInterface_Adapter() {
        fragmentScanAdapter.setOnItemClickListner(new FragmentScanAdapter.ScanOnItemClickInterface() {
            @Override
            public void ConnectionStatusClick(CustBluetootDevice customBluetoothObject, int ItemSlected) {
                    if(deviceConnectDisconnect!=null){
                        if(customBluetoothObject.isConnected()){
                            deviceConnectDisconnect.makeDevieConnecteDisconnect(customBluetoothObject,false);
                        }else if(!customBluetoothObject.isConnected()){
                            deviceConnectDisconnect.makeDevieConnecteDisconnect(customBluetoothObject,true);
                        }
                    }
            }
        });
    }

    private void bottomNavigationInvisible(boolean invisible_true_false) {
        mainActivity.bottoNavigationVisibility(invisible_true_false);
    }

    private void getListOfConnectedDevices() {
        if (ble_on_off()) {
            if (mainActivity.mBluetoothLeService != null) {
                List<BluetoothDevice> connectedDevicesList = mainActivity.mBluetoothLeService.getListOfConnectedDevices();
                if ((connectedDevicesList != null) && (connectedDevicesList.size() > 0)) {
                    for (BluetoothDevice bluetoothDevice : connectedDevicesList) {
                        if ((bluetoothDevice != null) && (bluetoothDevice.getName() != null) && (bluetoothDevice.getName().startsWith("ProofofLife"))) {
                            CustBluetootDevice custBluetootDevices = new CustBluetootDevice();
                            custBluetootDevices.setBleAddress(bluetoothDevice.getAddress());
                            custBluetootDevices.setDataObtained("");
                            custBluetootDevices.setConnected(true);
                            if (bluetoothDevice.getName() != null) {
                                custBluetootDevices.setDeviceName(bluetoothDevice.getName());
                            } else {
                                custBluetootDevices.setDeviceName("NA");
                            }
                            customBluetoothDeviceList.add(custBluetootDevices);
                            fragmentScanAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        } else {
            customBluetoothDeviceList.clear();
            fragmentScanAdapter.notifyDataSetChanged();
          Toast.makeText(getActivity(),"Turn on Bluetooth",Toast.LENGTH_SHORT).show();
        }
    }
}
