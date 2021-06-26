package com.benjaminshamoilia.trackerapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.doorbell.DoorbellApi;
import com.benjaminshamoilia.trackerapp.doorbell.RestCallback;
import com.benjaminshamoilia.trackerapp.doorbell.RestErrorCallback;
import com.benjaminshamoilia.trackerapp.helper.Utility;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentAboutUs extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;


    private DoorbellApi mApi;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_about_us, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_about_us);

        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.showBackButton(false);

        /*mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });*/
        return mViewRoot;
    }

    @OnClick(R.id.frg_about_us_rl_privacy_policy)
    public void onMenuPrivacyPolicyClick(View mView) {
        if (isAdded()) {
          /*  FragmentWebView mFragmentWebView = new FragmentWebView();
            Bundle mBundle = new Bundle();
            mBundle.putString("intent_title", "Privacy Policy");
            mBundle.putString("intent_url", getResources().getString(R.string.frg_contact_terms_n_condition_link));
            mActivity.replacesFragment(mFragmentWebView, true, mBundle, 0);*/
            FragmentViewPDF mFragmentViewInstallGuide = new FragmentViewPDF();
            Bundle mBundle = new Bundle();
            mBundle.putString("intent_title","Privacy Policy");
            mBundle.putString("intent_file_url", "PrivacyPolicy.pdf");
            mBundle.putBoolean("intent_is_landscape", false);
            mActivity.replacesFragment(mFragmentViewInstallGuide, true, mBundle, 1);
        }
    }

    @OnClick(R.id.frg_about_us_rl_term_of_use)
    public void onMenuTermsOfUseClick(View mView) {
        if (isAdded()) {
            //TERMSandCond.pdf
           /* FragmentWebView mFragmentWebView = new FragmentWebView();
            Bundle mBundle = new Bundle();
            mBundle.putString("intent_title", "Terms of Use");
            mBundle.putString("intent_url", getResources().getString(R.string.frg_contact_terms_n_condition_link));
            mActivity.replacesFragment(mFragmentWebView, true, mBundle, 0);*/
            FragmentViewPDF mFragmentViewInstallGuide = new FragmentViewPDF();
            Bundle mBundle = new Bundle();
            mBundle.putString("intent_title","Terms & Condition");
            mBundle.putString("intent_file_url", "TERMSandCond.pdf");
            mBundle.putBoolean("intent_is_landscape", false);
            mActivity.replacesFragment(mFragmentViewInstallGuide, true, mBundle, 1);

        }
    }

    @OnClick(R.id.frg_about_us_rl_feedback)
    public void onMenuUpdateClick(View mView) {
        if (isAdded()) {
            FeedbackDialog();

        }
    }

    private void FeedbackDialog()
    {
        final Dialog myDialog = new Dialog(mActivity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.feedbackdialog);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSemiTransparentWhite)));
        Button mButtonSend = (Button) myDialog
                .findViewById(R.id.frg_feedback_button_send);
        Button mButtonCancel = (Button) myDialog
                .findViewById(R.id.popup_feedback_button_cancel);
        final View mView = (View) myDialog.findViewById(R.id.popup_feedback_main_layout);
        final AppCompatEditText mEditTextMsg = (AppCompatEditText) myDialog.findViewById(R.id.frg_feedback_et_msg);
        final AppCompatEditText mEditTextEmail = (AppCompatEditText) myDialog.findViewById(R.id.frg_feedback_et_email);
        mEditTextEmail.setText(mActivity.mPreferenceHelper.getUser_email());

        mApi = new DoorbellApi(mActivity);
        mApi.setAppId(Utility.DOORBELL_APP_ID);
        mApi.setApiKey(Utility.DOORBELL_API_KEY);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String mStringMsg = mEditTextMsg.getText().toString().trim();
                String mStringEmail = mEditTextEmail.getText().toString().trim();
                mActivity.mUtility.hideKeyboard(mActivity);
                InputMethodManager im = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(mEditTextMsg.getWindowToken(), 0);
                InputMethodManager imm1 = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(mEditTextEmail.getWindowToken(), 0);

                if (mStringMsg.equalsIgnoreCase("")) {
                    showMessageRedAlert(mView, getResources().getString(R.string.str_settings_feedback_enter_feedback), getResources().getString(R.string.str_ok));


                    return;
                }
                if (mStringEmail.equalsIgnoreCase("")) {
                    showMessageRedAlert(mView, getResources().getString(R.string.str_sign_up_enter_email_address), getResources().getString(R.string.str_ok));

                    return;
                }
                if (!mActivity.mUtility.isValidEmail(mStringEmail)) {
                    showMessageRedAlert(mView, getResources().getString(R.string.str_sign_up_enter_valid_email_address), getResources().getString(R.string.str_ok));

                    return;
                }
                if (mActivity.mUtility.haveInternet()) {
                    myDialog.dismiss();
                    mApi.setLoadingMessage(mActivity.getString(R.string.doorbell_sending));
                    mApi.sendFeedback(mStringMsg, mStringEmail);
                    mApi.setCallback(new RestCallback() {
                        @Override
                        public void success(Object obj) {
                            mActivity.mUtility.errorDialog("KUURV",obj.toString(), 0, true);
                        }
                    });
                    mApi.setErrorCallback(new RestErrorCallback() {
                        @Override
                        public void error(String message) {
                            mActivity.mUtility.errorDialog("KUURV",message, 1, true);
                        }
                    });
                } else {
                    showMessageRedAlert(mView, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));

                }
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mActivity.mUtility.hideKeyboard(mActivity);
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextMsg.getWindowToken(), 0);
                InputMethodManager imm1 = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(mEditTextEmail.getWindowToken(), 0);
                myDialog.dismiss();
            }
        });
        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
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


    private void showMessageRedAlert(View mView, String mStringMessage, String mActionMessage) {
        mActivity.mUtility.hideKeyboard(mActivity);
        Snackbar mSnackBar = Snackbar.make(mView, mStringMessage, 5000);
        mSnackBar.setAction(mActionMessage, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mSnackBar.setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
        mSnackBar.getView().setBackgroundColor(getResources().getColor(R.color.colorInActiveMenu));
        mSnackBar.show();
    }

}
