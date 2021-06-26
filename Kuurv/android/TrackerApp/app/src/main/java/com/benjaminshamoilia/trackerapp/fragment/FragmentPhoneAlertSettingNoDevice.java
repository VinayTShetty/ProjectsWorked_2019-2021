package com.benjaminshamoilia.trackerapp.fragment;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;

import butterknife.BindView;
 import butterknife.ButterKnife;

import butterknife.Unbinder;


public class FragmentPhoneAlertSettingNoDevice extends Fragment {
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_phone_alert_setting_no_device, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_phone_alert);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTextSize(20);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);
        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });


        mSwitchCompatSilentMode.setClickable(false);
        mSwitchCompatSilentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchCompatSilentMode.setChecked(false);
                mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n change settings.", 3, true);
            }
        });



        mSwitchCompatSeparatedAlert.setClickable(false);
        mSwitchCompatSeparatedAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchCompatSeparatedAlert.setChecked(false);
                mActivity.mUtility.errorDialog("KUURV","Please add atleast one\ndevice to change Alert\nSettings", 3, true);
            }
        });

        mSwitchCompatRepeatAlert.setClickable(false);
        mSwitchCompatRepeatAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchCompatRepeatAlert.setChecked(false);
                mActivity.mUtility.errorDialog("KUURV","Please add atleast one\ndevice to change Alert\nSettings", 3, true);
            }
        });


        mRadioButtonVolumeHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadioButtonVolumeHigh.setChecked(false);
                mRadioButtonVolumeLow.setChecked(true);
                mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n change settings.", 3, true);
            }
        });
        mRadioButtonVolumeLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadioButtonVolumeHigh.setChecked(false);
                mRadioButtonVolumeLow.setChecked(true);
                mActivity.mUtility.errorDialog("KUURV","Tracker is disconnected.\nPlease connect in order to\n change settings.", 3, true);
            }
        });




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



















}




