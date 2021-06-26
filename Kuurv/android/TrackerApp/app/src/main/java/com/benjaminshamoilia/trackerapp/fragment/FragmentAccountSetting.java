package com.benjaminshamoilia.trackerapp.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentAccountSetting extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    @BindView(R.id.frg_about_us_rl_change_password)
    RelativeLayout mRelativeLayoutChangePassword;
    @BindView(R.id.frg_about_us_rl_shared_info)
    RelativeLayout mRelativeLayoutSharedInfo;
    private PreferenceHelper mPreferenceHelper;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_account_setting, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_account_setting);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.showBackButton(false);
        mPreferenceHelper = new PreferenceHelper(getActivity());



        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
        mRelativeLayoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.APICrushLogout();
                mActivity.replacesFragment(new FragmentChangePassword(),true,null,0);
            }
        });


        /**
         * Make the layout invisible/invisible to the user if they login through social Login
         */
        if(mPreferenceHelper.getFromSocailLogin())
        { mRelativeLayoutChangePassword.setVisibility(View.GONE); }
        else{mRelativeLayoutChangePassword.setVisibility(View.VISIBLE);}

        mRelativeLayoutSharedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.APICrushLogout();
                mActivity.replacesFragment(new FragmentSharedInfo(),true,null,0);
            }
        });


        return mViewRoot;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewBack.setVisibility(View.GONE);
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
