package com.benjaminshamoilia.trackerapp;

import android.content.*;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.vo.VoRegisterData;
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

public class RegisterActivity extends AppCompatActivity {


    private PreferenceHelper mPreferenceHelper;
    Utility mUtility;
    @BindView(R.id.activity_register_til_username)
    TextInputLayout mTextInputLayoutUsername;

    @BindView(R.id.activity_register_til_email)
    TextInputLayout mTextInputLayoutEmail;

    @BindView(R.id.activity_register_til_password)
    TextInputLayout mTextInputLayoutPassword;

    @BindView(R.id.activity_register_til_confirm_password)
    TextInputLayout mTextInputLayoutConfirmPassword;



    @BindView(R.id.activity_register_et_username) AppCompatEditText mEditTextUsername;
    @BindView(R.id.activity_register_et_password) AppCompatEditText mEditTextPassword;
    @BindView(R.id.activity_register_et_confirm_password) AppCompatEditText mEditTextConfirmPassword;
    @BindView(R.id.activity_register_et_email) AppCompatEditText mEditTextEmail;

    @BindView(R.id.activity_register_button_register)
    Button mButtonRegister;
    @BindView(R.id.activity_register_checkbox_terms_condition)
    CheckBox mCheckBoxTermNCondition;
    @BindView(R.id.activity_register_textview_terms_n_condition)
    TextView mTextViewTermNCondition;
    @BindView(R.id.activity_register_rl_main)
    RelativeLayout mRelativeLayoutMain;

    String mStringUsername = "";
    String mStringEmail = "";
    String mStringPassword = "";
    String mStringConfirmPassword = "";
    String mStringDevicesUIDFCMToken = "mTimer";
    String mStringGuestUserId = "0000";

    //Vinay Code
    private API mApiService;
    private Retrofit mRetrofit;
    //Vinay Code

    /**
     *This is used to save the prefrence whern the user is filling the form and navigationg to Terms and conditon.
     */
    public  SharedPreferences Reegister_Prefere;
    public static final String Register_activity = "register_Activity";
    public static final String Entered_name = "nameKey";
    public static final String Entered_email = "emailKey";
    public static final String Entered_password = "passkey";
    public static final String Entered_confermPassword = "conferm_pass";
    public static boolean from_Terms_flag=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(RegisterActivity.this);
        mCheckBoxTermNCondition.setTypeface(ResourcesCompat.getFont(this, R.font.century_gothic));
        mUtility = new Utility(RegisterActivity.this);
        mPreferenceHelper = new PreferenceHelper(RegisterActivity.this);

          //Vinay Code

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPreferenceHelper.setDeviceToken(mStringDevicesUIDFCMToken);
        mApiService = mRetrofit.create(API.class);

          //Vinay Code



       mEditTextConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());

        mPreferenceHelper.setDeviceToken(mStringDevicesUIDFCMToken);
        mEditTextPassword.setTransformationMethod(new PasswordTransformationMethod());
        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
                new IntentFilter("FcmTokenReceiver"));
        mRelativeLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUtility.hideKeyboard(RegisterActivity.this);
            }
        });



        //used for saving the preference text when the user navigates to terms and condtion.
        Reegister_Prefere = getSharedPreferences("register_Activity",
                Context.MODE_PRIVATE);


        if (mPreferenceHelper.getFromTerms())   // Here add the preference file boolean value..
        {   mEditTextUsername.setText(Reegister_Prefere.getString(Entered_name, ""));
            mEditTextEmail.setText(Reegister_Prefere.getString(Entered_email, ""));
            mEditTextPassword.setText(Reegister_Prefere.getString(Entered_password, ""));
            mEditTextConfirmPassword.setText(Reegister_Prefere.getString(Entered_confermPassword, "")); }
        else {
            Clear_regActivity_SharedPref();
            mPreferenceHelper.setfromTemrs(false);
        }


        mTextInputLayoutUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Username color chnaged line.");

                mTextInputLayoutUsername.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputLayoutUsername.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputLayoutUsername.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorWhite));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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
        mTextInputLayoutPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputLayoutPassword.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputLayoutPassword.setHintEnabled(true);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputLayoutPassword.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorWhite));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTextInputLayoutConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputLayoutConfirmPassword.setErrorEnabled(false);
                mTextInputLayoutConfirmPassword.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputLayoutConfirmPassword.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorWhite));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra("fcm_token");
            if (token != null) {
                mStringDevicesUIDFCMToken = token;
            }
        }
    };

    @OnClick(R.id.activity_register_textview_already_account)
    public void onSignInClick(View mView) {
        Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        mIntent.putExtra("From", "fromRegistration");
        startActivity(mIntent);
        finish();
    }


    @OnClick(R.id.activity_register_textview_already_login)
    public void SignInClick(View mView) {
        Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        mIntent.putExtra("From", "fromRegistration");
        startActivity(mIntent);
        finish();
    }


    @OnClick(R.id.activity_register_textview_terms_n_condition)
    public void onTermsAndConditionClick(View mView) {

        String User_Entered_name = mEditTextUsername.getText().toString();
        String User_Entered_email = mEditTextEmail.getText().toString();
        String User_Entered_password = mEditTextPassword.getText().toString();
        String User_Entered_confermPassword = mEditTextConfirmPassword.getText().toString();
        SharedPreferences.Editor editor = Reegister_Prefere.edit();
        editor.putString(Entered_name, User_Entered_name);
        editor.putString(Entered_email, User_Entered_email);
        editor.putString(Entered_password, User_Entered_password);
        editor.putString(Entered_confermPassword, User_Entered_confermPassword);
        editor.commit();
        startActivity(new Intent(RegisterActivity.this, TermsAndConditionActivity.class));
    }

    @OnClick(R.id.activity_register_button_register)
    public void onRegisterClick(View mView) {
        if (!isFinishing()) {
            mTextInputLayoutUsername.setError("");
            mTextInputLayoutEmail.setError("");
            mTextInputLayoutPassword.setError("");
            mTextInputLayoutConfirmPassword.setError("");
            mUtility.hideKeyboard(RegisterActivity.this);
            mStringUsername = mEditTextUsername.getText().toString().trim();
            mStringEmail = mEditTextEmail.getText().toString().trim();
            mStringPassword = mEditTextPassword.getText().toString().trim();
            mStringConfirmPassword = mEditTextConfirmPassword.getText().toString().trim();
            mPreferenceHelper.setDeviceToken(mStringDevicesUIDFCMToken);

            if (mStringUsername.equalsIgnoreCase("")) {
                mTextInputLayoutUsername.setError("Please enter your Name");
                mTextInputLayoutUsername.setHintEnabled(false);
                return;
            }
            if (mStringEmail.equalsIgnoreCase("")) {
                mTextInputLayoutEmail.setError("Please enter your Email");
                mTextInputLayoutEmail.setHintEnabled(false);
                return;
            }
            if (!mUtility.isValidEmail(mStringEmail)) {
                mTextInputLayoutEmail.setError("Please enter valid Email");
                mTextInputLayoutEmail.setHintEnabled(false);
                return;
            }
            if (mStringPassword.equalsIgnoreCase("")) {
                mTextInputLayoutPassword.setError("Please enter your Password");
                mTextInputLayoutPassword.setHintEnabled(false);
                return;
            }
            if (mStringPassword.length() < 6) {
                mTextInputLayoutPassword.setError("Password should be atleast 6 characters");
                mTextInputLayoutPassword.setHintEnabled(false);
                return;
            }
            if (mStringConfirmPassword.equalsIgnoreCase("")) {
                mTextInputLayoutConfirmPassword.setError("Please Confirm Password");
                mTextInputLayoutConfirmPassword.setHintEnabled(false);
                return;
            }
//            if (mStringConfirmPassword.length() < 6) {
//                mTextInputLayoutConfirmPassword.setError(getResources().getString(R.string.str_sign_up_enter_valid_password));
//                mTextInputLayoutConfirmPassword.setHint("");
//                return;
//            }
            if (!mStringPassword.equalsIgnoreCase(mStringConfirmPassword)) {
                mTextInputLayoutConfirmPassword.setError("Password & Confirm Password should match");
                mTextInputLayoutConfirmPassword.setHintEnabled(false);
                return;
            }
            if (!mCheckBoxTermNCondition.isChecked()) {

                mUtility.errorDialog("KUURV","Please agree to terms \n and conditions", 3, true);

                return;
            }
            if (mStringDevicesUIDFCMToken == null || mStringDevicesUIDFCMToken.equalsIgnoreCase("null") || mStringDevicesUIDFCMToken.equalsIgnoreCase("")) {
                mStringDevicesUIDFCMToken = System.currentTimeMillis() + "";
            }

            if (!mUtility.haveInternet()) {
                showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));
            } else {
                         User_Registration();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        mIntent.putExtra("From", "fromRegistration");
        startActivity(mIntent);
        finish();
    }

    private void showMessageRedAlert(View mView, String mStringMessage, String mActionMessage) {
        mUtility.hideKeyboard(RegisterActivity.this);
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



    private void User_Registration()
    {
        mUtility.hideKeyboard(RegisterActivity.this);
        mUtility.ShowProgress("Please Wait..",true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("device_token", mStringDevicesUIDFCMToken);
        params.put("device_type", "1");
        params.put("name",mStringUsername);
        params.put("email", mStringEmail); // Fetch Email text
        params.put("is_social_login", "0");
        params.put("social_type", "NA");
        params.put("social_id", "NA");
        params.put("password", mStringPassword);
        Call<VoRegisterData> mRegisterData=mApiService.userRegisterAPI(params);
        mRegisterData.enqueue(new Callback<VoRegisterData>() {
            @Override
            public void onResponse(Call<VoRegisterData> call, Response<VoRegisterData> response)
            {
                //mUtility.ShowProgress("Registration Complete",true);
                mUtility.HideProgress();
              System.out.println("Registration_Activity>>>>>>"+new Gson().toJson(response.body()));

                VoRegisterData data_response=response.body();
                if(data_response != null && data_response.getResponse().equalsIgnoreCase("true"))
                {
                    mUtility.ShowProgress("Registration Complete",true);
                    mUtility.HideProgress();
                    //  mUtility.AlertDialog(""+response.body().getMessage().toString());
                //    mUtility.errorDialog(response.body().getMessage().toString(),0,true);

                    System.out.println("Registration complete please verify your email account.......");
                    mUtility.errorDialogWithCallBack("KUURV","Registration successful.\nPlease check your Email\n account and click on \"Verify\"\nlink inside", 0, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id)
                        {
                            System.out.println("user Email = "+mStringEmail);
                            System.out.println("user Password = "+mStringPassword);
                            userAddednewAccount();
                            Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            mIntent.putExtra("From", "fromRegistration");
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mIntent);
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });



                }

                else if(data_response != null &&
                        data_response.getResponse().equalsIgnoreCase("false")&&
                        data_response.getMessage().startsWith("user email already registered with social login  ")
                       )
                {

                    String Response_message=data_response.getMessage();
                    String modify_result=Response_message.replace("user email already registered with social login","");
                    System.out.println("-------->"+modify_result);
                    if(modify_result.startsWith("  fb."))
                    {
                        modify_result="FaceBook";
                    }
                    else if(modify_result.startsWith("  twitter."))
                    {
                        modify_result="Twitter";
                    }
                    else
                    {
                        modify_result="Google";
                    }
                    String Dialog_message="This email address has already\n registered with us using \n"+modify_result+" Login";

                    mUtility.errorDialogWithCallBack("KUURV",Dialog_message, 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {


                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });


                }


                else if(data_response != null &&
                        data_response.getResponse().equalsIgnoreCase("false")&&
                        data_response.getMessage().startsWith("user already registered with this email adddres.")
                )
                {
                    mTextInputLayoutEmail.setError("This email address has already registered with us");
                    mTextInputLayoutEmail.setHintEnabled(false);



                    mPreferenceHelper.setUserEmail(mStringEmail);
                    mPreferenceHelper.setUserPassword(mStringPassword);

                    /*mUtility.errorDialogWithCallBack("User already registered with this email adddres.", 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {

                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });*/


                }

                else{}






            }

            @Override
            public void onFailure(Call<VoRegisterData> call, Throwable t) {

            }
        });

    }


    /**
     * It is used for clearing the preference data when the user comming back to RegisterActivity..
     */
    private void Clear_regActivity_SharedPref()
    {
        SharedPreferences.Editor editor = Reegister_Prefere.edit();
        editor.putString(Entered_name, "");
        editor.putString(Entered_email, "");
        editor.putString(Entered_password, "");
        editor.putString(Entered_password, "");
        editor.clear();
        editor.commit();
    }


    private void userAddednewAccount(){
        mPreferenceHelper.setUserEmail(mStringEmail);
        mPreferenceHelper.setUserPassword(mStringPassword);
        if(mPreferenceHelper.getIsRememberMe()){
            mPreferenceHelper.setRememberMeUsername(mStringEmail);
            mPreferenceHelper.setRememberMepassword(mStringPassword);
        }else{
            mPreferenceHelper.setRememberMeUsername("");
            mPreferenceHelper.setRememberMepassword("");
        }
    }


}
