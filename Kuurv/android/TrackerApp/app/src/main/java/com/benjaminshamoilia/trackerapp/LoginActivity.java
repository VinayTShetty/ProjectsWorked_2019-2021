package com.benjaminshamoilia.trackerapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.benjaminshamoilia.trackerapp.db.DBHelper;
import com.benjaminshamoilia.trackerapp.db.DataHolder;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.vo.VoDeviceList;
import com.benjaminshamoilia.trackerapp.vo.VoDeviceListData;
import com.benjaminshamoilia.trackerapp.vo.VoLoginData;
import com.benjaminshamoilia.trackerapp.vo.VoRegisterData;

import com.benjaminshamoilia.trackerapp.vo.Vo_Device_Regstd_from_serv;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.benjaminshamoilia.trackerapp.fragment.FragmentHome.userDeviceListAdapter;


/**
 * Created by Jaydeep on 16-02-2018.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Retrofit mRetrofit;
    private PreferenceHelper mPreferenceHelper;
    Utility mUtility;



    @BindView(R.id.activity_register_checkbox_terms_condition)
    CheckBox mCheckBoxRememberPw;
    @BindView(R.id.activity_login_til_email)
    TextInputLayout mTextInputLayoutEmail;
    @BindView(R.id.activity_login_til_password)
    TextInputLayout mTextInputLayoutPassword;

    @BindView(R.id.activity_login_tv_forgot_password)
    TextView mTextViewForgotPassword;
    @BindView(R.id.activity_login_et_email)
    AppCompatEditText mEditTextEmail;
    @BindView(R.id.activity_login_et_password)
    AppCompatEditText mEditTextPassword;
    @BindView(R.id.activity_login_button_login)
    Button mButtonLogin;
    @BindView(R.id.activity_login_rl_main)
    RelativeLayout mRelativeLayoutMain;
    private String mStringEmail = "";
    private String mStringPassword = "";
    private String mStringDeviceType = "1";
    private String mStringDevicesUIDFCMToken = "mTimer";
    String mStringGuestUserId = "0000";


    private API mApiService;
    @BindView(R.id.frg_contact_us_google) ImageView Google; //Google image

    @BindView(R.id.frg_contact_us_twitter) ImageView Twitter;// Twitter image

    @BindView(R.id.frg_contact_us_fb) ImageView FaceBook;  //FaceBook image.
    DBHelper mDbHelper;

    /**
     * Google Sign in
     */
    GoogleApiClient mGoogleSignInClient;
    public static final int Gooogle_signIn=9001;



    /**
     * Twitter login.
     */
    private TwitterLoginButton twitterLoginButton; // used to get the  call back but its invisible to the user.
    private TwitterAuthClient client;


    /**
     * FaceBook Login
     */
   private LoginButton FacebookLoignButton;
   CallbackManager callbackManager;
  // Context context=null;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);
        mUtility = new Utility(LoginActivity.this);
        mPreferenceHelper = new PreferenceHelper(LoginActivity.this);
        mCheckBoxRememberPw.setTypeface(ResourcesCompat.getFont(this, R.font.century_gothic));
        mDbHelper = DBHelper.getDBHelperInstance(LoginActivity.this);
        // Vinay Code
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClient())
                .client(mUtility.getClientWithAutho())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(API.class);


       //sessionManagement.



        if(checkItsfromRegistrationActivity()){
            //if the user is comming from RegistrationActivity.
            mEditTextEmail.setText(mPreferenceHelper.getUser_email());
            mEditTextPassword.setText(mPreferenceHelper.getUserPassword());
            if(mPreferenceHelper.getIsRememberMe()){
                mCheckBoxRememberPw.setChecked(true);
                mEditTextEmail.setText(mPreferenceHelper.getRememberMeUsername());
                mEditTextPassword.setText(mPreferenceHelper.getRememberMePassword());
            }else{
                mCheckBoxRememberPw.setChecked(false);
                mPreferenceHelper.setRememberMeUsername("");
                mPreferenceHelper.setRememberMepassword("");
            }
        }else{
            if(mPreferenceHelper.getIsRememberMe()){
                mCheckBoxRememberPw.setChecked(true);
                mEditTextEmail.setText(mPreferenceHelper.getRememberMeUsername());
                mEditTextPassword.setText(mPreferenceHelper.getRememberMePassword());
            }else{
                mCheckBoxRememberPw.setChecked(false);
                mEditTextEmail.setText(mPreferenceHelper.getUser_email());
                mEditTextPassword.setText(mPreferenceHelper.getUserPassword());
            }
        }

        mPreferenceHelper.setDeviceToken(mStringDevicesUIDFCMToken);
        mEditTextPassword.setTransformationMethod(new PasswordTransformationMethod());
        mEditTextPassword.setOnEditorActionListener(new AppCompatEditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mUtility.hideKeyboard(LoginActivity.this);
                    return true;
                }
                return false;
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
                new IntentFilter("FcmTokenReceiver"));

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

        //Google Sigin in.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Twitter login
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig("jRIQgwIF6YiZxysq7FV8ByvQi ", "mRc6o7zFR5d2UxrnyVp1x5ELAr3DHgaWtfHds5sSa26kFyDcZR"))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)//enable debug mode
                .build();
        com.twitter.sdk.android.core.Twitter.initialize(config);
        client = new TwitterAuthClient();
        twitterLoginButton = findViewById(R.id.default_twitter_login_button);

        //FaceBookLogin.
      FacebookLoignButton=(LoginButton) findViewById(R.id.facebookloign);
        callbackManager = CallbackManager.Factory.create();
        FacebookLoignButton.setPermissions("email");
        FacebookLoignButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { }
            @Override
            public void onCancel() { }
            @Override
            public void onError(FacebookException error) { }});



        //Used for testing Deelte it if its not working..
        mActivity = (MainActivity) new MainActivity();
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

    @OnClick(R.id.activity_login_button_Sign)
    public void onRegisterClick(View mView) {
        mPreferenceHelper.setfromTemrs(false);

        fromLogintoRegistrationViceVersa();
        /**
         * Animation Part.
         */
        /*startActivity(new Intent(context, RegisterActivity.class));
        Animatoo.animateSpin(context);
        finish();*/
        Intent mIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(mIntent);
        finish();
    }

    @OnClick(R.id.activity_login_tv_forgot_password)
    public void onForgotPasswordClick(View mView)
    {
        mUtility.hideKeyboard(LoginActivity.this);
        Intent mIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(mIntent);

    }

    /**
     * This is used to logout all the social media if log in.
     */
/* public void logout_socialMedia()
    {

     try{
         TwitterCore.getInstance().getSessionManager().clearActiveSession(); //Twitter
         Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(new ResultCallback<Status>() {
             @Override
             public void onResult(@NonNull Status status) { }}); // Gooogle
         LoginManager.getInstance().logOut(); // Facebook.
     }
     catch (Exception e)
     {
         e.printStackTrace();
     }
    }*/

    @OnClick(R.id.frg_contact_us_google)
    public void Google(View mView) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!mUtility.haveInternet()) {
                showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));
                return;
            } else {}

            mUtility.hideKeyboard(LoginActivity.this);
            Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) { }});
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
            startActivityForResult(signInIntent, Gooogle_signIn);
        }else{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }else{
                //showExplanationDialogFragmentAddDevice();
            }
        }

    }



    @OnClick(R.id.frg_contact_us_twitter)
    public void Twitter(View mView) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!mUtility.haveInternet()) {
                showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));
                return;
            } else {}

            mUtility.hideKeyboard(LoginActivity.this);
            TwitterCore.getInstance().getSessionManager().clearActiveSession(); //Twitter
            customLoginTwitter();
        }else{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }else{
                //showExplanationDialogFragmentAddDevice();
            }
        }

    }


    @OnClick(R.id.frg_contact_us_fb)
    public void FaceBook(View mView) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!mUtility.haveInternet()) {
                showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));
                return;
            } else {}
            LoginManager.getInstance().logOut(); // Facebook.
            if (mView == FaceBook) {
                FacebookLoignButton.performClick();
            }
        }else{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }else{
                //showExplanationDialogFragmentAddDevice();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //google
        if (requestCode == Gooogle_signIn)
        {
            System.out.println("Data Request Code Google"+requestCode);
            System.out.println("Data Result Code Google"+resultCode);


            final GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleResult(result);
        }
        //Twitter
        else if(requestCode==140 && resultCode==-1 )
        {
            System.out.println("Data Request Code Twitter"+requestCode);
            System.out.println("Data Result Code Twitter"+resultCode);
            //        Request Code = 140,Result Code = -1 for twitter
            if (client != null)
                client.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result to the login button.
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
        //FaceBook
        else {

           callbackManager.onActivityResult(requestCode,resultCode,data);
        }


    }
    private void GoogleResult(GoogleSignInResult result)
    {
        if(result!=null){
            if(result.isSuccess())
            { GoogleSignInAccount account=result.getSignInAccount();
                System.out.println("Name --->"+account.getDisplayName());
                System.out.println("Email--->"+account.getEmail());
                System.out.println("UID --->"+account.getId());


                String username_given=account.getDisplayName();
                String user_emial=account.getEmail();
                String uid=account.getId();
                call_Social_LoginAPI(user_emial,username_given,uid, "google");

            }
            else
            {
                System.out.println(" Result is failure...");
            }
        }
    }


    @OnClick(R.id.activity_login_button_login)
    public void onLoginClick(View mView) {
        if (!isFinishing()) {
            mTextInputLayoutPassword.setError("");
            mUtility.hideKeyboard(LoginActivity.this);
            mStringEmail = mEditTextEmail.getText().toString().trim();
            mStringPassword = mEditTextPassword.getText().toString().trim();
            mPreferenceHelper.setDeviceToken(mStringDevicesUIDFCMToken);
            if (mStringEmail.equalsIgnoreCase(""))
            {
                mTextInputLayoutEmail.setError("Please enter your Email"); // This is the helper text i.e Red color
                mTextInputLayoutEmail.setHintEnabled(false);
                return;
            }

            if (!isValidEmail(mStringEmail)) {
                mTextInputLayoutEmail.setError("Please enter valid Email address");
                mTextInputLayoutEmail.setHintEnabled(false);
                return;
            }

            if (mStringPassword.equalsIgnoreCase("")) {
                mTextInputLayoutPassword.setError("Please enter your Password");
                mTextInputLayoutPassword.setHintEnabled(false);
                return;
            }

            if (mStringPassword.length() < 6)
            {
                mTextInputLayoutPassword.setError("Incorrect Password");
                mTextInputLayoutPassword.setHintEnabled(false);
                return;
            }


            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (!mUtility.haveInternet()) {
                    showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_no_internet_connection), getResources().getString(R.string.str_ok));
                } else {
                    saveRembermeUsernamePasswordwhilelogin();
                    userLogin();
                }
            }else{

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                    }
                }else{
                    //showExplanationDialogFragmentAddDevice();
                }
            }

        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    private void userLogin() {
        mUtility.hideKeyboard(LoginActivity.this);
        mUtility.ShowProgress("Please Wait..");
        System.out.println("mStringEmail" + mStringEmail);
        System.out.println("mStringPassword" + mStringPassword);
        System.out.println("mStringDeviceToken" + mStringDevicesUIDFCMToken);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mStringEmail); // Fetch Email text
        params.put("password", mStringPassword);
        params.put("device_type", "1");
        params.put("device_token", mStringDevicesUIDFCMToken);
        params.put("is_social_login", "0");
        params.put("social_type", "NA");
        params.put("social_id", "NA");
        Call<VoLoginData> mLogin = mApiService.userLoginAPI(params);
        mLogin.enqueue(new Callback<VoLoginData>() {
            @Override
            public void onResponse(Call<VoLoginData> call, Response<VoLoginData> response) {
                mUtility.HideProgress();
                VoLoginData mLoginData = response.body();
                System.out.println("Login_Activity>>>>>>" + new Gson().toJson(response.body()));
                if (mLoginData != null
                        && mLoginData.getResponse().equalsIgnoreCase("true")
                        && mLoginData.getMessage().equalsIgnoreCase("You are logged in successfully.")) {
                    mPreferenceHelper.setUserId(mLoginData.getData().getId());
                    mPreferenceHelper.setAccountName(mLoginData.getData().getName());
                    mPreferenceHelper.setUserEmail(mLoginData.getData().getEmail());
                    mPreferenceHelper.setAccessToken(mLoginData.getAuth_token());
                    saveRembermeUsernamePasswordwhilelogin();
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(mDbHelper.user_id, mLoginData.getData().getId());
                    mContentValues.put(mDbHelper.name,mLoginData.getData().getName());
                    mContentValues.put(mDbHelper.email, "" +mLoginData.getData().getEmail());
                    int x= mDbHelper.insertRecord(mDbHelper.User_Set_Info, mContentValues);
                    System.out.println("Record Inserted 2 = "+x);

                    new getDevicelistAPIThread().start();

                  //  getDeviceList_API();
                    LoginInfo("NormalLogin");

                    /*Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mPreferenceHelper.setfromSocialLogin(false);
                    mIntent.putExtra("isFromNotification", false);
                    mIntent.putExtra("isFromLogin", true);
                    startActivity(mIntent);
                    finish();*/
                } else if (mLoginData != null
                        && mLoginData.getResponse().equalsIgnoreCase("false")
                        && mLoginData.getMessage().equalsIgnoreCase("Invalid Credentials.")) {

                    mUtility.errorDialogWithCallBack("KUURV","Invalid Login", 3, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {

                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                }

                //Email not verified!!, Plase verify your email address.
                else if (mLoginData != null
                        && mLoginData.getResponse().equalsIgnoreCase("false")
                        && mLoginData.getMessage().equalsIgnoreCase("Email not verified!!, Plase verify your email address.")) {
                    mUtility.errorDialogWithCallBack("KUURV","Email not verified!!,\nPlase verify your email address.", 1, false, new onAlertDialogCallBack() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {

                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {

                        }
                    });
                } else if (mLoginData != null
                        && mLoginData.getResponse().equalsIgnoreCase("false")
                        && mLoginData.getMessage().startsWith("You email is registered with")) {
                    mUtility.errorDialogWithCallBack("KUURV","" + mLoginData.getMessage(), 1, false, new onAlertDialogCallBack() {
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
            public void onFailure(Call<VoLoginData> call, Throwable t) {
                mUtility.HideProgress();
                showMessageRedAlert(mRelativeLayoutMain, getResources().getString(R.string.str_server_error_try_again), getResources().getString(R.string.str_ok));
            }

        });


    }

    private void showMessageRedAlert(View mView, String mStringMessage, String mActionMessage) {
        mUtility.hideKeyboard(LoginActivity.this);
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




    private void call_Social_LoginAPI(final String email, final String name, final String UniqueID, final String Social) {
        mUtility.hideKeyboard(LoginActivity.this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email); // Fetch Email text
        params.put("device_type", "1");
        params.put("device_token", mStringDevicesUIDFCMToken);
        params.put("is_social_login", "1");
        params.put("social_type", Social);
        params.put("social_id", UniqueID);
        params.put("name", name);

        if(Social.toString().equalsIgnoreCase("google"))
        {//Logout  Google
            Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) { }}); }
        else if(Social.toString().equalsIgnoreCase("twitter"))
        { TwitterCore.getInstance().getSessionManager().clearActiveSession(); // twitter
        }
        else { LoginManager.getInstance().logOut(); }


        mUtility.ShowProgress("Please Wait..");

        Call<VoLoginData> mLogin = mApiService.userLoginAPI(params);
        mLogin.enqueue(new Callback<VoLoginData>() {
            @Override
            public void onResponse(Call<VoLoginData> call, Response<VoLoginData> response) {

                VoLoginData mLoginData = response.body();
                System.out.println("Login_Activity>>>Response" + new Gson().toJson(response.body()));
                if (mLoginData != null
                        && mLoginData.getResponse().equalsIgnoreCase("false")
                        && mLoginData.getMessage().equalsIgnoreCase("Your social login details not found, please try again."))
                {
                    mUtility.HideProgress();
                    call_Social_SignUpAPI(email, name, UniqueID, Social);
                }

                if (mLoginData != null
                        && mLoginData.getResponse().equalsIgnoreCase("true")
                        && mLoginData.getMessage().equalsIgnoreCase("You are logged in successfully.")) {
                    mUtility.HideProgress();

                    /**
                     * this is used to remove saved username and Email i.e remember useremail and userpassword.
                     */
                    erasepreferenceDatawhenSocialLogin();

                    mPreferenceHelper.setUserId(mLoginData.getData().getId());
                    mPreferenceHelper.setAccountName(mLoginData.getData().getName());
                    mPreferenceHelper.setUserEmail(mLoginData.getData().getEmail());
                    mPreferenceHelper.setAccessToken(mLoginData.getAuth_token());
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(mDbHelper.user_id, mLoginData.getData().getId());
                    mContentValues.put(mDbHelper.name,mLoginData.getData().getName());
                    mContentValues.put(mDbHelper.email, "" +mLoginData.getData().getEmail());
                    int x= mDbHelper.insertRecord(mDbHelper.User_Set_Info, mContentValues);
                    System.out.println("Record Inserted 3 = "+x);


                    new getDevicelistAPIThread().start();

                   // getDeviceList_API();
                    LoginInfo("You are logged in successfully.");

                   /* Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                    *//**
                     * This is used to hide the Account Setting if the user is log in from social..
                     *//*
                    mPreferenceHelper.setfromSocialLogin(true);
                    *//**
                     * This is used for Rememberme check box issue..
                     *//*
                    mCheckBoxRememberPw.setChecked(false);
                    mPreferenceHelper.setIsRememberMe(false);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    finish();*/
                }

            }

            @Override
            public void onFailure(Call<VoLoginData> call, Throwable t) {
                mUtility.HideProgress();
            }
        });
    }

    private void call_Social_SignUpAPI(String email, String name, String UniqueID, String Social) {
        mUtility.hideKeyboard(LoginActivity.this);


        mUtility.ShowProgress("Registering User");
        Map<String, String> params = new HashMap<String, String>();
        params.put("device_token", mStringDevicesUIDFCMToken);
        params.put("device_type", "1");
        params.put("name", name);
        params.put("email", email); // Fetch Email text
        params.put("is_social_login", "1");
        params.put("social_type", Social);
        params.put("social_id", UniqueID);
        params.put("password", "");
        Call<VoRegisterData> mRegisterData = mApiService.userRegisterAPI(params);
        mRegisterData.enqueue(new Callback<VoRegisterData>() {
            @Override
            public void onResponse(Call<VoRegisterData> call, Response<VoRegisterData> response) {


                System.out.println("Login_Activity>>>Check_FaceBook_SignUp API>>>>" + new Gson().toJson(response.body()));

                final VoRegisterData data_response = response.body();

                if (data_response != null && data_response.getResponse().equalsIgnoreCase("false")
                        && data_response.getMessage().startsWith("user email already registered with social login")) {
                    mUtility.HideProgress();
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
                        //show dialog
                        mUtility.errorDialogWithCallBack("KUURV",Dialog_message, 1, false, new onAlertDialogCallBack() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {

                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {

                            }
                        });
                }


                if (data_response != null && data_response.getResponse().equalsIgnoreCase("true")
                        && data_response.getMessage().equalsIgnoreCase("Thanks! You have successfully signed up")) {
                    mUtility.HideProgress();

                    /**
                     * User normal Registration
                     * Then social login password length will be more,then directly natvigate him to MainActivity..
                     */
                    if(data_response.getData().getPassword().length()>5)
                    {

                        saveRembermeUsernamePasswordwhilelogin();


                        ContentValues mContentValues = new ContentValues();
                        mContentValues.put(mDbHelper.user_id, data_response.getData().getId());
                        mContentValues.put(mDbHelper.name,data_response.getData().getName());
                        mContentValues.put(mDbHelper.email, "" +data_response.getData().getEmail());
                        int x= mDbHelper.insertRecord(mDbHelper.User_Set_Info, mContentValues);
                        System.out.println("Record Inserted 1 ="+x);

                        mPreferenceHelper.setUserId(data_response.getData().getId());
                        mPreferenceHelper.setAccountName(data_response.getData().getName());
                        mPreferenceHelper.setUserEmail(data_response.getData().getEmail());
                        mPreferenceHelper.setAccessToken(data_response.getAuth_token());


                        new getDevicelistAPIThread().start();

//                        getDeviceList_API();
                        LoginInfo("Thanks! You have successfully signed up");

                        /*Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        *//**
                         * This is used to hide the Account Setting if the user is log in from social..
                         *//*
                        mPreferenceHelper.setfromSocialLogin(true);
                        *//**
                         * This is used for Rememberme check box issue..by default make it false
                         *//*
                        mCheckBoxRememberPw.setChecked(false);
                        mPreferenceHelper.setIsRememberMe(false);
                        startActivity(mIntent);
                        finish();*/
                    }

                    /**
                     * if the user login through social login then password length will be 0
                     */
                    else if(data_response.getData().getPassword().equalsIgnoreCase(""))
                    {
                        mUtility.errorDialogWithCallBack("KUURV","You have been registered\n sucessfully", 0, false, new onAlertDialogCallBack() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id)
                            {
                                saveRembermeUsernamePasswordwhilelogin();
                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(mDbHelper.user_id, data_response.getData().getId());
                                mContentValues.put(mDbHelper.name,data_response.getData().getName());
                                mContentValues.put(mDbHelper.email, "" +data_response.getData().getEmail());
                                int x= mDbHelper.insertRecord(mDbHelper.User_Set_Info, mContentValues);
                                System.out.println("Record Inserted 1 ="+x);

                                mPreferenceHelper.setUserId(data_response.getData().getId());
                                mPreferenceHelper.setAccountName(data_response.getData().getName());
                                mPreferenceHelper.setUserEmail(data_response.getData().getEmail());
                                mPreferenceHelper.setAccessToken(data_response.getAuth_token());

                                new getDevicelistAPIThread().start();

//                                getDeviceList_API();
                                LoginInfo("You have been registered sucessfully");

                               /* Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                *//**
                                 * This is used to hide the Account Setting if the user is log in from social..
                                 *//*
                                mPreferenceHelper.setfromSocialLogin(true);
                                *//**
                                 * This is used for Rememberme check box issue..
                                 *//*
                                mCheckBoxRememberPw.setChecked(false);
                                mPreferenceHelper.setIsRememberMe(false);
                                startActivity(mIntent);
                                finish();*/
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {

                            }
                        });

                    }




                }


                if (data_response != null && data_response.getResponse().equalsIgnoreCase("false")
                        && data_response.getMessage().equalsIgnoreCase("Email is required")) {

                    mUtility.HideProgress();
                    mUtility.errorDialogWithCallBack("KUURV",data_response.getMessage(), 1, false, new onAlertDialogCallBack() {
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
            public void onFailure(Call<VoRegisterData> call, Throwable t) {
                mUtility.HideProgress();

            }
        });
    }

    //Google Sigin in CallBack method...
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * twittter login code..
     */
    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return session;
    }
    public void fetchTwitterEmail(final TwitterSession twitterSession) {
        client.requestEmail(twitterSession, new com.twitter.sdk.android.core.Callback<String>() {
            @Override
            public void success(Result<String> result) {
                //here it will give u only email and rest of other information u can get from TwitterSession
                //    userDetailsLabel.setText("User Id : " + twitterSession.getUserId() + "\nScreen Name : " + twitterSession.getUserName() + "\nEmail Id : " + result.data);
                //Add the social login call API here... for twitter.

                System.out.println("user iD= "+twitterSession.getUserId());
                System.out.println("user Name= "+twitterSession.getUserName());
                System.out.println("user UserEmail= "+result.data);


                String userId=""+twitterSession.getUserId();
                String username=""+twitterSession.getUserName();
                String userEmail=""+result.data;
                call_Social_LoginAPI(userEmail, username, userId, "twitter");

            }

            @Override
            public void failure(TwitterException exception)
            {
                Toast.makeText(LoginActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void customLoginTwitter() {

        System.out.println("Twitter image got clicked");
        //check if user is already authenticated or not
        if (getTwitterSession() == null) {
            endAuthorizeInProgress();
            //if user is not authenticated start authenticating
            client.authorize(this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {

                    // Do something with result, which provides a TwitterSession for making API calls
                    TwitterSession twitterSession = result.data;

                    //call fetch email only when permission is granted
                    fetchTwitterEmail(twitterSession);

                    System.out.println("Twitter OncSucess");
                }

                @Override
                public void failure(TwitterException e) {
                    System.out.println("Twitter  failure ");

                }
            });
        } else {
            //if user is already authenticated direct call fetch twitter email api
            //  Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            fetchTwitterEmail(getTwitterSession());
        }
    }

    AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) { if(currentAccessToken==null) {
            System.out.println("User Log out facebook");

        } else {
            loadUserProfile(currentAccessToken);
        } }};
    private  void loadUserProfile(AccessToken accessToken) {
        GraphRequest request=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {

                    final      String fristname=object.getString("first_name");
                    final       String lastname=object.getString("last_name");
                    final       String email=object.getString("email");
                    final       String id=object.getString("id");
                    System.out.println("FaceBook fristname "+fristname);
                    System.out.println("FaceBook lastname"+lastname);
                    System.out.println("FaceBook email"+email);
                    System.out.println("FaceBook id"+id);
                  //  call_Social_LoginAPI(email, fristname+" "+lastname, id, "fb");

                    if (!isFinishing()) {
                        call_Social_LoginAPI(email, fristname+" "+lastname, id, "fb");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters=new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void endAuthorizeInProgress() {
        try {
            Field authStateField = client.getClass().getDeclaredField("authState");
            authStateField.setAccessible(true);
            Object authState = authStateField.get(client);
            Method endAuthorize = authState.getClass().getDeclaredMethod("endAuthorize");
            endAuthorize.invoke(authState);
        } catch (NoSuchFieldException | SecurityException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException e) {
            //logger.e("Couldn't end authorize in progress.", e);
        }
    }




    public void getDeviceList_API() {
        System.out.println("LoginActivity ----> detDeviceListAPI called");
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mPreferenceHelper.getUserId());
        params.put("limit", "20");
        params.put("offset", "0");
        final Call<VoDeviceList> device_list = mApiService.Device_listAPI(params);
        device_list.enqueue(new Callback<VoDeviceList>() {
            @Override
            public void onResponse(Call<VoDeviceList> call, Response<VoDeviceList> response) {
                new Thread( new Runnable() { @Override public void run() {
                    System.out.println("LoginActivity Device List Response = " + new Gson().toJson(response.body()));
                    VoDeviceList voDeviceList = response.body();
                    List<VoDeviceListData> voDeviceListData = voDeviceList.getData();
                    System.out.println("LoginActivity getDeviceList_API Message" + voDeviceList.getMessage().toString());
                    System.out.println("LoginActivity getDeviceList_API Response" + voDeviceList.getResponse());
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
                                System.out.println("LoginActivity RecordInserted");
                                mDbHelper.insertRecord(mDbHelper.Device_Table, mContentValues);
                            } else {
                                System.out.println("LoginActivity RecordUpdated");
                                mDbHelper.updateRecord(mDbHelper.Device_Table, mContentValues, mDbHelper.ble_address + "=?", new String[]{isExistInUserDB});
                            }
                        }

                       /* String result=  mDbHelper.getTableAsString(mDbHelper.getReadableDatabase(),"Device_Table");
                           System.out.println("Login Activity------------->"+result);
                        Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mPreferenceHelper.setfromSocialLogin(false);
                        mIntent.putExtra("isFromNotification", false);
                        mIntent.putExtra("isFromLogin", true);
                        startActivity(mIntent);
                        finish();*/
                    }
                    else if (servermessage.contains("Invalid Token") && serverResponse.equalsIgnoreCase("false")) {
                    }
                } } ).start();
            }

            @Override
            public void onFailure(Call<VoDeviceList> call, Throwable t) {

            }
        });
    }

    public String retutn_tracekrAddress(double latitude, double longitude) {
        System.out.println("MainActivity Address Method Entered");
        String getAddress = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
       //     getAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            getAddress=addresses.get(0).getPremises()+","+addresses.get(0).getSubLocality()+","+addresses.get(0).getLocality()+","+addresses.get(0).getAdminArea()+","+addresses.get(0).getPostalCode();
            System.out.println("Address Printed Here " + getAddress);
            System.out.println("MainActivity Address Method Address" + getAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getAddress;
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



    static MainActivity mActivity;
    public void getDBDeviceListloginScreen() {
        System.out.println("BLE Address----> getDBDeviceLIst called");
        DataHolder mDataHolderLight;
        if (mActivity.mArrayListMyDevices != null) {
            mActivity.mArrayListMyDevices.clear();
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
                    mActivity.mArrayListMyDevices.add(vo_device_regstd_from_serv);
                }
                userDeviceListAdapter.notifyDataSetChanged();
                System.out.println("Size_Issue LoginActivity ArraylistSize=  "+mActivity.mArrayListMyDevices.size());

                try{

                }catch (Exception e){e.printStackTrace();}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //  System.out.println("Main Activity Table Data" + mDbHelper.getTableAsString(mDbHelper.getReadableDatabase(), mDbHelper.Device_Table));

    }

    private  final int LOCATION_REQUEST_CODE=2;

    @TargetApi(Build.VERSION_CODES.M)
    private void showExplanationDialogFragmentAddDevice() {
        boolean deniedfristtime_true_else_false= ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(deniedfristtime_true_else_false){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mUtility.errorDialogWithYesNoCallBack("KUURV", "Please Provide\n Location Acess", "Yes", "No", true, 1, new onAlertDialogCallBack() {
                @Override
                public void PositiveMethod(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                @Override
                public void NegativeMethod(DialogInterface dialog, int id) {

                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        System.out.println("Permission Granted onRequestPermissionsResult");
                        System.out.println("Permission Granted onRequestPermissionsResul permissions array--->"+ Arrays.toString(permissions));
                        System.out.println("Permission Granted onRequestPermissionsResul permissions grantResults--->"+ Arrays.toString(grantResults));
                    }
                }else{

                    System.out.println("Permission Denied onRequestPermissionsResul permissions array--->"+ Arrays.toString(permissions));
                    System.out.println("Permission Denied onRequestPermissionsResul permissions grantResults--->"+ Arrays.toString(grantResults));

                    showExplanationDialogFragmentAddDevice();
                }
                return;
            }
        }

    }



  /*  private boolean checkUsermailPasswordsavedSavedinPreference(){
        boolean result=false;
        if(mPreferenceHelper.getUser_email().length()>0&&mPreferenceHelper.getUserPassword().length()>0){
            result =true;
        }
        return result;
    }*/

   /* private void RememberMeSave(boolean rememberme) {

        if(rememberme){
            if (mCheckBoxRememberPw.isChecked()) {
                mPreferenceHelper.setIsRememberMe(true);
                mPreferenceHelper.setRememberMeUsername(mStringEmail);
                mPreferenceHelper.setRememberMepassword(mStringPassword);
            }
        }else{
            if (!mCheckBoxRememberPw.isChecked()) {
                mPreferenceHelper.setIsRememberMe(false);
                mPreferenceHelper.setRememberMeUsername("");
                mPreferenceHelper.setRememberMepassword("");
            }
        }

    }*/


    private boolean checkItsfromRegistrationActivity(){
        boolean result=false;
        Intent intent = getIntent();
        String dataObtained = intent.getStringExtra("fromRegistration");
        if(dataObtained!=null&&dataObtained.length()>1){
            if(dataObtained.equals("fromRegistration")){
                result=true;
            }
        }
        return result;
    }


    /*private void CheckUserhasgivenRemembermeWhileLogin(){
           saveUsernamePasswordbyDefault();
        if (mCheckBoxRememberPw.isChecked()) {
            mPreferenceHelper.setIsRememberMe(true);
            mPreferenceHelper.setRememberMeUsername(mStringEmail);
            mPreferenceHelper.setRememberMepassword(mStringPassword);

        }else{
            mPreferenceHelper.setIsRememberMe(false);
            mPreferenceHelper.setRememberMeUsername("");
            mPreferenceHelper.setRememberMepassword("");
        }
    }

    private void saveUsernamePasswordbyDefault(){
        mPreferenceHelper.setUserEmail(mStringEmail);
        mPreferenceHelper.setUserPassword(mStringPassword);
    }*/


/*    private void userGoingtoRegistration(){
        if (mCheckBoxRememberPw.isChecked()){
            mPreferenceHelper.setIsRememberMe(true);
            mPreferenceHelper.setRememberMeUsername(mStringEmail);
            mPreferenceHelper.setRememberMepassword(mStringPassword);
        }
        else{
            mPreferenceHelper.setIsRememberMe(false);
            mPreferenceHelper.setRememberMeUsername("");
            mPreferenceHelper.setRememberMepassword("");
        }

        mStringEmail = mEditTextEmail.getText().toString().trim();
        mStringPassword = mEditTextPassword.getText().toString().trim();
        mPreferenceHelper.setUserEmail(mStringEmail);
        mPreferenceHelper.setUserPassword(mStringPassword);
    }*/

    private void fromLogintoRegistrationViceVersa(){
        mStringEmail = mEditTextEmail.getText().toString().trim();
        mStringPassword = mEditTextPassword.getText().toString().trim();
        mPreferenceHelper.setUserEmail(mStringEmail);
        mPreferenceHelper.setUserPassword(mStringPassword);
        if (mCheckBoxRememberPw.isChecked()){
            mPreferenceHelper.setIsRememberMe(true);
            mPreferenceHelper.setRememberMeUsername(mStringEmail);
            mPreferenceHelper.setRememberMepassword(mStringPassword);
        }
        else{
            mPreferenceHelper.setIsRememberMe(false);
            mPreferenceHelper.setRememberMeUsername("");
            mPreferenceHelper.setRememberMepassword("");
        }
    }


    private void saveRembermeUsernamePasswordwhilelogin(){
        commonUsernamePasswordStore();
        mStringEmail = mEditTextEmail.getText().toString().trim();
        mStringPassword = mEditTextPassword.getText().toString().trim();
        if (mCheckBoxRememberPw.isChecked()){
            mPreferenceHelper.setIsRememberMe(true);
            mPreferenceHelper.setRememberMeUsername(mStringEmail);
            mPreferenceHelper.setRememberMepassword(mStringPassword);
        }else{
            mPreferenceHelper.setIsRememberMe(false);
            mPreferenceHelper.setRememberMeUsername("");
            mPreferenceHelper.setRememberMepassword("");
        }
    }


    private void erasepreferenceDatawhenSocialLogin(){
        mPreferenceHelper.setIsRememberMe(false);
        mCheckBoxRememberPw.setChecked(false);
        mPreferenceHelper.setRememberMeUsername("");
        mPreferenceHelper.setRememberMepassword("");
    }

    private void commonUsernamePasswordStore(){
        mStringEmail = mEditTextEmail.getText().toString().trim();
        mStringPassword = mEditTextPassword.getText().toString().trim();
        mPreferenceHelper.setUserEmail(mStringEmail);
        mPreferenceHelper.setUserPassword(mStringPassword);
    }


    private void LoginInfo(String  fromWhereLogin){




        if(fromWhereLogin.equalsIgnoreCase("NormalLogin")){
             Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mPreferenceHelper.setfromSocialLogin(false);
                    mIntent.putExtra("isFromNotification", false);
                    mIntent.putExtra("isFromLogin", true);
                    startActivity(mIntent);
                    finish();
        }else if(fromWhereLogin.equalsIgnoreCase("You are logged in successfully.")){
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
            /**
             * This is used to hide the Account Setting if the user is log in from social..
             */
            mPreferenceHelper.setfromSocialLogin(true);
            /**
             * This is used for Rememberme check box issue..
             */
            mCheckBoxRememberPw.setChecked(false);
            mPreferenceHelper.setIsRememberMe(false);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();
        }else if(fromWhereLogin.equalsIgnoreCase("Thanks! You have successfully signed up")){
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            /**
             * This is used to hide the Account Setting if the user is log in from social..
             */
            mPreferenceHelper.setfromSocialLogin(true);
            /**
             * This is used for Rememberme check box issue..by default make it false
             */
            mCheckBoxRememberPw.setChecked(false);
            mPreferenceHelper.setIsRememberMe(false);
            startActivity(mIntent);
            finish();
        }else{
           // fromWhereLogin  = You have been registered /n sucessfully
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            /**
             * This is used to hide the Account Setting if the user is log in from social..
             */
            mPreferenceHelper.setfromSocialLogin(true);
            /**
             * This is used for Rememberme check box issue..
             */
            mCheckBoxRememberPw.setChecked(false);
            mPreferenceHelper.setIsRememberMe(false);
            startActivity(mIntent);
            finish();
        }
    }


    private class getDevicelistAPIThread extends Thread{
        @Override
        public void run() {
            getDeviceList_API();
        }
    }

}


