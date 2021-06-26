package com.benjaminshamoilia.trackerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.vo.VoForgot_Password;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jaydeep on 16-02-2018.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    private Retrofit mRetrofit;
    private PreferenceHelper mPreferenceHelper;
    Utility mUtility;
    @BindView(R.id.activity_fp_img_back)
    ImageView mImageViewBack;
    @BindView(R.id.activity_fp_et_email)
    AppCompatEditText mEditTextEmail;
    @BindView(R.id.activity_fp_button_submit)
    Button mButtonSubmit;
    @BindView(R.id.activity_fp_rl_main)
    RelativeLayout mRelativeLayoutMain;

    String mStringEmail = "";

    @BindView(R.id.activity_login_til_email)
    TextInputLayout mTextInputLayoutEmail;

    private API mApiService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_forgot_password);



        ButterKnife.bind(ForgotPasswordActivity.this);
        mUtility = new Utility(ForgotPasswordActivity.this);
        mPreferenceHelper = new PreferenceHelper(ForgotPasswordActivity.this);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClient())
                .client(mUtility.getClientWithAutho())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(API.class);


        mRelativeLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUtility.hideKeyboard(ForgotPasswordActivity.this);
            }
        });



        mTextInputLayoutEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputLayoutEmail.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputLayoutEmail.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputLayoutEmail.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorWhite));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    @OnClick(R.id.activity_fp_img_back)
    public void onBackClick(View mView) {
        if (!isFinishing()) {
            onBackPressed();

        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    @OnClick(R.id.activity_fp_button_submit)
    public void onRegisterClick(View mView) {
        if (!isFinishing()) {
            mUtility.hideKeyboard(ForgotPasswordActivity.this);
            mStringEmail = mEditTextEmail.getText().toString().trim();
            if (mStringEmail.equalsIgnoreCase("")) {
                mTextInputLayoutEmail.setError("Please enter your Email"); // This is the helper text i.e Red color
                mTextInputLayoutEmail.setHintEnabled(false);
                return;
            }

            if (!isValidEmail(mStringEmail)) {
                mTextInputLayoutEmail.setError("Please enter valid Email address");
                mTextInputLayoutEmail.setHintEnabled(false);
                return;
            }

            if (!mUtility.haveInternet()) {
                showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));
            } else {
                             forgot_password();
            }
        }
    }

    private void forgot_password()
    {
        mUtility.ShowProgress("Please Wait..");
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mStringEmail); // Fetch Email text
        System.out.println("Email = "+mStringEmail);
        final Call<VoForgot_Password> forgot_password=mApiService.forgot_password(params);
        forgot_password.enqueue(new Callback<VoForgot_Password>() {
            @Override
            public void onResponse(Call<VoForgot_Password> call, Response<VoForgot_Password> response) {
                  String Reponse=  new Gson().toJson(response.body());
                  VoForgot_Password forgot_password=response.body();
                    String message_receieved=forgot_password.getMessage().toString();
                    System.out.println("JSON Response "+Reponse);

                    //New password reset link has been sent to your email address.
                    String Eroor_forgot_password=forgot_password.getMessage().toString();


                if(forgot_password != null
                        && forgot_password.getResponse().equalsIgnoreCase("false")
                        &&forgot_password.getMessage().equalsIgnoreCase("New password reset link has been sent to your email address."))
                {
                    mUtility.HideProgress();
                    mUtility.errorDialogWithCallBack("KUURV",""+forgot_password.getMessage(), 0, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }

                else if(forgot_password != null
                        && forgot_password.getResponse().equalsIgnoreCase("false")
                        &&forgot_password.getMessage().equalsIgnoreCase("This email is not registered with our system."))
                {
                    mUtility.HideProgress();
                    mUtility.errorDialogWithCallBack("KUURV",""+forgot_password.getMessage(), 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {

                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }




                else if(forgot_password != null
                        && forgot_password.getResponse().equalsIgnoreCase("false")
                        &&Eroor_forgot_password.toString().equalsIgnoreCase("You can't forgot password because You are logged in with socialid"))
                {
                    System.out.println("JSON Response Entered the Eroor Dialog Msg here");
                    mUtility.HideProgress();
                    mUtility.errorDialogWithCallBack("KUURV","Forgot Password wont work for Emails registered through Social Login", 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {

                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<VoForgot_Password> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        mIntent.putExtra("is_from_add_account", false);
        startActivity(mIntent);
        finish();
    }

    private void showMessageRedAlert(View mView, String mStringMessage, String mActionMessage) {
        mUtility.hideKeyboard(ForgotPasswordActivity.this);
        Snackbar.make(mView, mStringMessage, 5000)
                .setAction(mActionMessage, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

}
