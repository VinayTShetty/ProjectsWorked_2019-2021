package com.proofofLife.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proofofLife.Adapters.FragmentAcccountsAdapter;
import com.proofofLife.Adapters.FragmentScanAdapter;
import com.proofofLife.BaseFragment.BaseFragmentHelper;
import com.proofofLife.CustomObjects.CustBluetootDevice;
import com.proofofLife.DataBaseRoomDAO.DeviceRegistation_Room;
import com.proofofLife.DataBaseRoomEntity.DeviceRegistration_DAO;
import com.proofofLife.MainActivity;
import com.proofofLife.R;

import java.util.ArrayList;
import java.util.List;

import static com.proofofLife.MainActivity.bleAddressInCommunication_MainActivity;
import static com.proofofLife.MainActivity.tokenNumber_MainActivity;

public class FragmentDevicesAdded extends BaseFragmentHelper {
    MainActivity mainActivity;
    View deviceAddedFragmetView;
    RecyclerView fragmentdeviceAdded_RecyclerView;
    private ArrayList<CustBluetootDevice> customBluetoothDeviceList;
    FragmentAcccountsAdapter fragmentAcccountsAdapter;
    ImageView imageView_toolbar_cancel;
    TextView noDeviceFound_TextView;

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
        deviceAddedFragmetView = inflater.inflate(R.layout.fragment_accounts, container, false);
        intializeViews();
        setUpRecycleView();
        onClickListner();
        fetchDataFromDataBase();
        scanItemOnclickInterface_Adapter();
        return deviceAddedFragmetView;
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
        return FragmentDevicesAdded.class.getSimpleName();
    }

    private void getActivityReference() {
        mainActivity = (MainActivity) getActivity();
    }

    private void setUpRecycleView() {
        customBluetoothDeviceList = new ArrayList<CustBluetootDevice>();
        fragmentAcccountsAdapter = new FragmentAcccountsAdapter(customBluetoothDeviceList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentdeviceAdded_RecyclerView.setLayoutManager(mLayoutManager);
        fragmentdeviceAdded_RecyclerView.setAdapter(fragmentAcccountsAdapter);
    }

    private void intializeViews() {
        fragmentdeviceAdded_RecyclerView = (RecyclerView) deviceAddedFragmetView.findViewById(R.id.scan_item_recycleView_id);
        imageView_toolbar_cancel = (ImageView) deviceAddedFragmetView.findViewById(R.id.imageView_toolbar_cancel_id);
        noDeviceFound_TextView = (TextView) deviceAddedFragmetView.findViewById(R.id.noDeviceFound_TextView_Id);
    }

    private void onClickListner() {
        imageView_toolbar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
    }

    private void fetchDataFromDataBase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DeviceRegistation_Room> deviceRegistration_daos = mainActivity.roomDBHelperInstance.get_deviceRegistration_dao().getDeviceInfo();
                if(deviceRegistration_daos.size()>0){
                    for (DeviceRegistation_Room deviceRegistation_room : deviceRegistration_daos) {
                        customBluetoothDeviceList.add(new CustBluetootDevice(deviceRegistation_room.getBleData(), null, null, false,""+deviceRegistation_room.getId()));
                    }
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentAcccountsAdapter.notifyDataSetChanged();
                        }
                    });
                }else {
                    fragmentdeviceAdded_RecyclerView.setVisibility(View.INVISIBLE);
                    noDeviceFound_TextView.setVisibility(View.VISIBLE);
                }
            }
        }).start();
    }

    private void scanItemOnclickInterface_Adapter() {
        fragmentAcccountsAdapter.setupOnItemClicklistnerInterface(new FragmentAcccountsAdapter.AccountsItemInterface() {
            @Override
            public void accountsItemClick(CustBluetootDevice customBluetoothObject, int ItemSlected) {
                bleAddressInCommunication_MainActivity=customBluetoothObject.getBleAddress();
                mainActivity.replaceFragment(new FragmentDevieOwner(), null);
            }
        });
    }
}
