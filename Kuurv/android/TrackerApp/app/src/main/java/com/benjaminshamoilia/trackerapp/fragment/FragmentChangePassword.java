package com.benjaminshamoilia.trackerapp.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.vo.VoForgot_Password;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FragmentChangePassword extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    private PreferenceHelper mPreferenceHelper;
    private API mApiService;
    Utility mUtility;
    private Retrofit mRetrofit;

    @BindView(R.id.frg_change_password_et_old_password)
    AppCompatEditText mTextViewOldpassword;
    String oldpassword;
    @BindView(R.id.frg_change_password_til_old_password)
    TextInputLayout mTextInputlayoutOldpassword;

    @BindView(R.id.frg_change_password_et_new_password) AppCompatEditText mTextViewNewpassword;
    String newpassword;
    @BindView(R.id.frg_change_password_til_new_password)
    TextInputLayout mTextInputlayoutNewpassword;

    @BindView(R.id.frg_change_password_et_confirm_password) AppCompatEditText mTextViewConfermpassword;
    String confermpassword;
    @BindView(R.id.frg_change_password_til_confirm_password)
    TextInputLayout mTextInputlayoutConfermpassword;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_change_password, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_change_password);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER );
        mActivity.mTextViewTitle.setTextSize(20);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);

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
                mActivity.onBackPressed();
            }
        });

        mTextViewOldpassword.setTransformationMethod(new PasswordTransformationMethod());
        mTextViewNewpassword.setTransformationMethod(new PasswordTransformationMethod());
        mTextViewConfermpassword.setTransformationMethod(new PasswordTransformationMethod());


        mTextInputlayoutOldpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutOldpassword.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayoutOldpassword.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutOldpassword.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTextInputlayoutNewpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutNewpassword.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayoutNewpassword.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutNewpassword.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTextInputlayoutConfermpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayoutConfermpassword.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayoutConfermpassword.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayoutConfermpassword.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        return mViewRoot;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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

    @OnClick(R.id.frg_change_password_btn_save)
    public void change_password()
    {
        if(isAdded())
        {
            mTextInputlayoutOldpassword.setError("");
            mTextInputlayoutNewpassword.setError("");
            mTextInputlayoutConfermpassword.setError("");
            mActivity.mUtility.hideKeyboard(getActivity());
            oldpassword=mTextViewOldpassword.getText().toString().trim();
            newpassword=mTextViewNewpassword.getText().toString().trim();
            confermpassword=mTextViewConfermpassword.getText().toString().trim();

            if (oldpassword.equalsIgnoreCase(""))
            {
               // mTextInputlayoutOldpassword.setError(getResources().getString(R.string.old_password_length));
             //   mTextInputlayoutOldpassword.setHintEnabled(false);

                mTextInputlayoutOldpassword.setError("Please enter your old password"); // This is the helper text i.e Red color
                mTextInputlayoutOldpassword.setHintEnabled(false);


                return;
            }

            if (newpassword.length() < 6)
            {
              //  mTextInputlayoutNewpassword.setError(getResources().getString(R.string.new_password_length));
               // mTextInputlayoutNewpassword.setHintEnabled(false);


                mTextInputlayoutNewpassword.setError("Password must be minimum 6 characters"); // This is the helper text i.e Red color
                mTextInputlayoutNewpassword.setHintEnabled(false);

                return;
            }

            if (confermpassword.length() < 6)
            {
              //  mTextInputlayoutConfermpassword.setError(getResources().getString(R.string.conferm_password_length));
               // mTextInputlayoutConfermpassword.setHintEnabled(false);

                mTextInputlayoutConfermpassword.setError("Confirm Password must be minimum 6 characters"); // This is the helper text i.e Red color
                mTextInputlayoutConfermpassword.setHintEnabled(false);
                return;
            }

            if (!newpassword.equalsIgnoreCase(confermpassword)) {
             //   mTextInputlayoutConfermpassword.setError(getResources().getString(R.string.password_not_same));
             //   mTextInputlayoutConfermpassword.setHintEnabled(false);


                mTextInputlayoutConfermpassword.setError(getResources().getString(R.string.password_not_same)); // This is the helper text i.e Red color
                mTextInputlayoutConfermpassword.setHintEnabled(false);

                return;
            }

            else
            {
                call_change_passwordAPI();
            }



        }

    }


    private void call_change_passwordAPI()
    {
        mUtility.hideKeyboard(getActivity());
        mUtility.ShowProgress("Please wait", true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("current_pass",oldpassword );
        params.put("new_pass",newpassword );

        Call<VoForgot_Password> mchangePassword = mApiService.ChangePassword(params);
        mchangePassword.enqueue(new Callback<VoForgot_Password>() {
            @Override
            public void onResponse(Call<VoForgot_Password> call, Response<VoForgot_Password> response) {
                mUtility.HideProgress();
                VoForgot_Password voForgot_password=response.body();
                System.out.println("Fragment Change Paassword" + new Gson().toJson(response.body()));
              //  VoNotification voForgot_password = response.body();
                if (voForgot_password.getMessage().startsWith("Invalid Token")) {
                    mActivity.APICrushLogout();
                } else
                {
                    String message=voForgot_password.getMessage().toString();
                    System.out.println("message   ="+message);
                    String sucess=voForgot_password.getResponse().toString();
                    if(message.toString().equalsIgnoreCase("Password updated successfully.")   &&
                            (sucess.equalsIgnoreCase("true")))
                    {
                        mActivity.mUtility.errorDialogWithCallBack("KUURV","Password updated \n successfully.", 0, false, new onAlertDialogCallBack() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                                mActivity.onBackPressed();
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {

                            }
                        });

                    }
                    else if(message.toString().equalsIgnoreCase("Invalid Current Password.")&&
                            (sucess.equalsIgnoreCase("false"))
                    )
                    {
                        mActivity.mUtility.errorDialogWithCallBack("KUURV","Invalid Current Password", 3, false, new onAlertDialogCallBack() {
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

            @Override
            public void onFailure(Call<VoForgot_Password> call, Throwable t) {

            }
        });

    }

}
