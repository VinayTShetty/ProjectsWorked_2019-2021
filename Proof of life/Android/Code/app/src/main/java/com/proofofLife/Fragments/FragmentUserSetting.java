package com.proofofLife.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.proofofLife.BaseFragment.BaseFragmentHelper;
import com.proofofLife.DataBaseRoomDAO.DeviceRegistation_Room;
import com.proofofLife.MainActivity;
import com.proofofLife.R;

import java.util.List;

import static com.proofofLife.MainActivity.bleAddressInCommunication_MainActivity;

public class FragmentUserSetting extends BaseFragmentHelper {
    public static final String TAG=FragmentUserSetting.class.getSimpleName();
    MainActivity mainActivity;
    View userSettingFragmentView;
    String bleAddressToCommunicate;
    EditText tokenKey_EditText,otpValidity_EditText,timeSync_EditText,OTP_Display_EditText,skew_EditText, tokenValidity_EditText,branding_EditText,FingerPrintMatch_EditText;
    Button submitt_button;
    AlertDialog.Builder builder;
    DeviceRegistation_Room deviceRegistation_room_Setting;
    ImageView imageView_toolbar_cancel;;
    String secretKey="";

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityReference();
        bleAddressToCommunicate=bleAddressInCommunication_MainActivity;
        checkRecords();
     //   getDataFromBundle();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userSettingFragmentView = inflater.inflate(R.layout.fragment_user_setting, container, false);
        intializeViews();

        bottomNavigationInvisible(true);
        onclickLIstner();

        return userSettingFragmentView;
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
        return FragmentUserSetting.class.getSimpleName();
    }
    private void getActivityReference() {
        mainActivity = (MainActivity) getActivity();
    }
   /* private void getDataFromBundle() {
        final  Bundle bundle=this.getArguments();
        if(bundle!=null){
            bleAddressToCommunicate=bundle.getString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS));
        }

        System.out.println(" "+bleAddressToCommunicate);
    }*/
    private void bottomNavigationInvisible(boolean invisible_true_false) {
        mainActivity.bottoNavigationVisibility(invisible_true_false);
    }


    private void intializeViews() {
        builder = new AlertDialog.Builder(getActivity());
        tokenKey_EditText=(EditText)userSettingFragmentView.findViewById(R.id.tokenKey_EditText_id);
        tokenKey_EditText.setEnabled(false);
        otpValidity_EditText=(EditText)userSettingFragmentView.findViewById(R.id.otpValidity_EditText_id);
        timeSync_EditText=(EditText)userSettingFragmentView.findViewById(R.id.timeSync_EditText_id);
        OTP_Display_EditText=(EditText)userSettingFragmentView.findViewById(R.id.OTP_Display_EditText_id);
        skew_EditText=(EditText)userSettingFragmentView.findViewById(R.id.skew_EditText_id);
        tokenValidity_EditText =(EditText)userSettingFragmentView.findViewById(R.id.tokenValidity_EditText_id);
        branding_EditText=(EditText)userSettingFragmentView.findViewById(R.id.branding_EditText_id);
        FingerPrintMatch_EditText=(EditText)userSettingFragmentView.findViewById(R.id.FingerPrintMatch_EditText_id);
        submitt_button=(Button)userSettingFragmentView.findViewById(R.id.submitt_button_id);
        imageView_toolbar_cancel=(ImageView) userSettingFragmentView.findViewById(R.id.imageView_toolbar_cancel_id);
    }

    private void onclickLIstner() {
        submitt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSettingValues();
            }
        });
        imageView_toolbar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
    }

    private void getSettingValues() {
        int otpValidity=5*60;// otpValidity*60
        int fingerPrintMatching=1;
        int tokenVal =24;
        int OtpDisplay=10;

        if(!otpValidity_EditText.getText().toString().equalsIgnoreCase("")){
            otpValidity=Integer.parseInt(otpValidity_EditText.getText().toString());
            otpValidity=otpValidity*60;
        }

        if(!FingerPrintMatch_EditText.getText().toString().equalsIgnoreCase("")){
            fingerPrintMatching=Integer.parseInt(FingerPrintMatch_EditText.getText().toString());
        }

        if(!tokenValidity_EditText.getText().toString().equalsIgnoreCase("")){
            tokenVal =Integer.parseInt(tokenValidity_EditText.getText().toString());
        }
        if(!OTP_Display_EditText.getText().toString().equalsIgnoreCase("")){
            OtpDisplay=Integer.parseInt(OTP_Display_EditText.getText().toString());
        }
        if(otpValidity>65535){
            showDialog("Wrong Input","OTP validdity*60 should not be greater than 65535");
            return;
        }if(OtpDisplay>255){
            showDialog("Wrong Input","OTP Display should not be greater than 255");
            return;
        } if(tokenVal >65535){
            showDialog("Wrong Input","Pool  should not be greater than 65535");
            return;
        }if(fingerPrintMatching>5){
            showDialog("Wrong Input","Finger print Match should not be greater than 5");
            return;
        }
        mainActivity.firmwareDeviceSetting(bleAddressToCommunicate,otpValidity,OtpDisplay,fingerPrintMatching, tokenVal,secretKey);

    }

    private void showDialog(String header,String message) {
        builder.setTitle(header);
        builder.setMessage(message)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void checkRecords() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DeviceRegistation_Room> deviceRegistation = mainActivity.roomDBHelperInstance.get_deviceRegistration_dao().getDeviceInfo();
                for (DeviceRegistation_Room each:deviceRegistation) {
                    if(each.getBleAddress().equalsIgnoreCase(bleAddressToCommunicate.toUpperCase().replace(":", ""))){
                        deviceRegistation_room_Setting=each;
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(deviceRegistation_room_Setting!=null){
                                    secretKey="";
                                    secretKey=deviceRegistation_room_Setting.getSecretkey();
                                   tokenKey_EditText.setText(deviceRegistation_room_Setting.getSecretkey());
                                    otpValidity_EditText.setText("5");
                                    FingerPrintMatch_EditText.setText("1");
                                    tokenValidity_EditText.setText("24");
                                    OTP_Display_EditText.setText("10");
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
