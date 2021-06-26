package com.benjaminshamoilia.trackerapp.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.benjaminshamoilia.trackerapp.BuildConfig;
import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.MyApplication;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.interfaces.onAlertDialogCallBack;
import com.benjaminshamoilia.trackerapp.views.ProgressHUD;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jaydeep on 13-03-2018.
 */
public class Utility {

    Activity mActivity;

    ProgressHUD mProgressHUD;
    Dialog mAlertDialogYesNo;
    Dialog mAlertDialogCallBack;
    Dialog mAlertDialog;


    public static long DOORBELL_APP_ID = 10300;
    public static String DOORBELL_API_KEY = "LvEVqieHMmu6iHxXskQqYLXK9qRaigSqQH7bDkILbLey8WMDshuRIuIQPneQHtMa";
    public Utility(Activity mActivity) {
        this.mActivity = mActivity;
    }


    public void errorDialog(final String title,final String message, final int isSuccess, final boolean isCancelable) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialog != null && mAlertDialog.isShowing()) {

                    } else {
                        mAlertDialog = new Dialog(mActivity);
                        mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialog.setContentView(R.layout.dialog_success);
                        mAlertDialog.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialog.getWindow().setBackgroundDrawable(inset);
                        TextView mTextViewTitle = (TextView) mAlertDialog.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialog.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialog.findViewById(R.id.tv_ok);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialog.findViewById(R.id.dialog_success_img_icon);
                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        } else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_error_black_24dp);
                        }
                        mTextViewMessage.setText(message);
                        mTextViewTitle.setText(title);
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mAlertDialog.show();
                        mAlertDialog.getWindow().setAttributes(lp);



                    }
                }
            });
        }
    }
    public void errorDialog(final String message, final int isSuccess, final boolean isCancelable,final boolean remove) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialog != null && mAlertDialog.isShowing()) {

                    } else {
                        mAlertDialog = new Dialog(mActivity);
                        mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialog.setContentView(R.layout.dialog_success);
                        mAlertDialog.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialog.getWindow().setBackgroundDrawable(inset);
                        TextView mTextViewTitle = (TextView) mAlertDialog.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialog.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialog.findViewById(R.id.tv_ok);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialog.findViewById(R.id.dialog_success_img_icon);
                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        }

                        else if (isSuccess == 3) {
                            mCircleImageView.setVisibility(View.GONE);
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                          //  mTextViewTitle.setTextColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                        }

                        else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_error_black_24dp);
                        }
                        mTextViewMessage.setText(message);
                        mTextViewTitle.setText("Device Disconnected");
                        mTextViewTitle.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mAlertDialog.show();
                        mAlertDialog.getWindow().setAttributes(lp);

                   /*     if(remove)
                        {

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms

                                    mAlertDialog.dismiss();
                                }
                            }, 3000);

                        }*/


                    }
                }
            });
        }
    }



    public void crushlogoutDialog(final String message, final int isSuccess, final boolean isCancelable, final onAlertDialogCallBack mCallBack) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialogCallBack != null && mAlertDialogCallBack.isShowing()) {

                    } else {
                        mAlertDialogCallBack = new Dialog(mActivity);
                        mAlertDialogCallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialogCallBack.setContentView(R.layout.dialog_success);
                        mAlertDialogCallBack.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialogCallBack.getWindow().setBackgroundDrawable(inset);

                        TextView mTextViewTitle = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_ok);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialogCallBack.findViewById(R.id.dialog_success_img_icon);

                        mTextViewMessage.setText(message);
                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        } else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_warning);
                        }
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogCallBack.dismiss();
                                mCallBack.PositiveMethod(mAlertDialogCallBack, 0);
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialogCallBack.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        try{
                            mAlertDialogCallBack.show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        mAlertDialogCallBack.getWindow().setAttributes(lp);
                    }
                }
            });
        }
    }



    public void errorDialogWithCallBack(final String title,final String message, final int isSuccess, final boolean isCancelable, final onAlertDialogCallBack mCallBack) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialogCallBack != null && mAlertDialogCallBack.isShowing()) {

                    } else {
                        mAlertDialogCallBack = new Dialog(mActivity);
                        mAlertDialogCallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialogCallBack.setContentView(R.layout.dialog_success);
                        mAlertDialogCallBack.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialogCallBack.getWindow().setBackgroundDrawable(inset);

                        TextView mTextViewTitle = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_ok);
                         CircleImageView mCircleImageView = (CircleImageView) mAlertDialogCallBack.findViewById(R.id.dialog_success_img_icon);

                        mTextViewMessage.setText(message);
                        mTextViewTitle.setText(title);
                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        } else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_warning);
                        }
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogCallBack.dismiss();
                                mCallBack.PositiveMethod(mAlertDialogCallBack, 0);
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialogCallBack.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mAlertDialogCallBack.show();
                        mAlertDialogCallBack.getWindow().setAttributes(lp);
                    }
                }
            });
        }
    }

    public void errorDialogWithCallBack_connect_disconnectBLE(Activity activity,final String message, final int isSuccess, final boolean isCancelable, final onAlertDialogCallBack mCallBack) {

        if(!activity.isFinishing()){
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAlertDialogCallBack != null && mAlertDialogCallBack.isShowing()) {
                            mAlertDialogCallBack.dismiss();
                            errorDialogWithCallBack_connect_disconnectBLE(activity,message,isSuccess,isCancelable,mCallBack);
//                            errorDialogWithCallBack_connect_disconnectBLE_2(activity,message,isSuccess,isCancelable,mCallBack);
                        } else {
                            mAlertDialogCallBack = new Dialog(mActivity);
                            mAlertDialogCallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            mAlertDialogCallBack.setContentView(R.layout.dialog_success);
                            mAlertDialogCallBack.setCancelable(isCancelable);

                            ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                            InsetDrawable inset = new InsetDrawable(back, 0);
                            mAlertDialogCallBack.getWindow().setBackgroundDrawable(inset);

                            TextView mTextViewTitle = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_title);
                            TextView mTextViewMessage = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_message);
                            TextView mTextViewOk = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_ok);
                            CircleImageView mCircleImageView = (CircleImageView) mAlertDialogCallBack.findViewById(R.id.dialog_success_img_icon);

                            mTextViewMessage.setText(message);
                            if (isSuccess == 0) {
                                mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                                mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                            } else if (isSuccess == 1) {
                                mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                                mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                            } else if (isSuccess == 2) {
                                mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                                mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                            } else {
                                mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                                mCircleImageView.setImageResource(R.drawable.ic_warning);
                            }
                            mTextViewOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialogCallBack.dismiss();
                                    mCallBack.PositiveMethod(mAlertDialogCallBack, 0);
                                }
                            });
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(mAlertDialogCallBack.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                            try{
                            if(!activity.isFinishing()){
                                mAlertDialogCallBack.show();
                            }

                            }catch (Exception e)
                            {e.printStackTrace();
                                System.out.println("MainActivity soundissue Dialog Windows Exception Occured 2");
                            }



                            mAlertDialogCallBack.getWindow().setAttributes(lp);
                        }
                    }
                });
            }else{
                System.out.println("MainActivity soundissue Dialog_not_Executed");
            }
        }else{
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

    }


    public void errorDialogWithCallBack_connect_disconnectBLE_2(Activity activity, final String message, final int isSuccess, final boolean isCancelable, final onAlertDialogCallBack mCallBack) {
        if (activity != null&&!activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialogCallBack != null && mAlertDialogCallBack.isShowing()) {
                        mAlertDialogCallBack.dismiss();
                        errorDialogWithCallBack_connect_disconnectBLE(activity,message,isSuccess,isCancelable,mCallBack);
                    } else {
                        mAlertDialogCallBack = new Dialog(mActivity);
                        mAlertDialogCallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialogCallBack.setContentView(R.layout.dialog_success);
                        mAlertDialogCallBack.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialogCallBack.getWindow().setBackgroundDrawable(inset);

                        TextView mTextViewTitle = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_ok);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialogCallBack.findViewById(R.id.dialog_success_img_icon);

                        mTextViewMessage.setText(message);
                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        } else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_warning);
                        }
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogCallBack.dismiss();
                                mCallBack.PositiveMethod(mAlertDialogCallBack, 0);
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialogCallBack.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        try{
                            if(!activity.isFinishing()){
                                mAlertDialogCallBack.show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("MainActivity soundissue Dialog Windows Exception Occured 1 ");
                        }
                        mAlertDialogCallBack.getWindow().setAttributes(lp);
                    }
                }
            });
        }
    }




    public void errorDialogWithCallBack_Connect_Disconnect_1(final String message, final int isSuccess, final boolean isCancelable, final onAlertDialogCallBack mCallBack) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialogCallBack != null && mAlertDialogCallBack.isShowing()) {
                        mAlertDialogCallBack.dismiss();
                        errorDialogWithCallBack_Connect_Disconnect_2(message,isSuccess,isCancelable,mCallBack);
                    } else {
                        mAlertDialogCallBack = new Dialog(mActivity);
                        mAlertDialogCallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialogCallBack.setContentView(R.layout.dialog_success);
                        mAlertDialogCallBack.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialogCallBack.getWindow().setBackgroundDrawable(inset);

                        TextView mTextViewTitle = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_ok);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialogCallBack.findViewById(R.id.dialog_success_img_icon);

                        mTextViewMessage.setText(message);


                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        } else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_warning);
                        }
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogCallBack.dismiss();
                                mCallBack.PositiveMethod(mAlertDialogCallBack, 0);
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialogCallBack.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mAlertDialogCallBack.show();
                        mAlertDialogCallBack.getWindow().setAttributes(lp);
                    }
                }
            });
        }
    }

    public void errorDialogWithCallBack_Connect_Disconnect_2(final String message, final int isSuccess, final boolean isCancelable, final onAlertDialogCallBack mCallBack) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialogCallBack != null && mAlertDialogCallBack.isShowing()) {
                        mAlertDialogCallBack.dismiss();
                        errorDialogWithCallBack_Connect_Disconnect_1(message,isSuccess,isCancelable,mCallBack);
                    } else {
                        mAlertDialogCallBack = new Dialog(mActivity);
                        mAlertDialogCallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialogCallBack.setContentView(R.layout.dialog_success);
                        mAlertDialogCallBack.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialogCallBack.getWindow().setBackgroundDrawable(inset);

                        TextView mTextViewTitle = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_message);
                        TextView mTextViewOk = (TextView) mAlertDialogCallBack.findViewById(R.id.tv_ok);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialogCallBack.findViewById(R.id.dialog_success_img_icon);

                        mTextViewMessage.setText(message);
                        if (isSuccess == 0) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.dialog_sucess_img_icon);
                        } else if (isSuccess == 1) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_failer_vector);
                        } else if (isSuccess == 2) {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_delete_forever_black);
                        } else {
                            mTextViewOk.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                            mCircleImageView.setImageResource(R.drawable.ic_warning);
                        }
                        mTextViewOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogCallBack.dismiss();
                                mCallBack.PositiveMethod(mAlertDialogCallBack, 0);
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialogCallBack.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mAlertDialogCallBack.show();
                        mAlertDialogCallBack.getWindow().setAttributes(lp);
                    }
                }
            });
        }
    }




    public void errorDialogWithYesNoCallBack(final String dialogTitle, final String dialogMessage, final String positiveBtnCaption, final String negativeBtnCaption, final boolean isCancelable, final int isSuccess, final onAlertDialogCallBack mCallBack) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialogYesNo != null && mAlertDialogYesNo.isShowing()) {

                    } else {
                        mAlertDialogYesNo = new Dialog(mActivity);
                        mAlertDialogYesNo.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mAlertDialogYesNo.setContentView(R.layout.dialog_success);
                        mAlertDialogYesNo.setCancelable(isCancelable);

                        ColorDrawable back = new ColorDrawable(mActivity.getResources().getColor(R.color.colorTransparent));
                        InsetDrawable inset = new InsetDrawable(back, 0);
                        mAlertDialogYesNo.getWindow().setBackgroundDrawable(inset);

                        TextView mTextViewTitle = (TextView) mAlertDialogYesNo.findViewById(R.id.tv_title);
                        TextView mTextViewMessage = (TextView) mAlertDialogYesNo.findViewById(R.id.tv_message);
                        TextView mTextViewYes = (TextView) mAlertDialogYesNo.findViewById(R.id.tv_ok);
                        TextView mTextViewNo = (TextView) mAlertDialogYesNo.findViewById(R.id.tv_no);
                        CircleImageView mCircleImageView = (CircleImageView) mAlertDialogYesNo.findViewById(R.id.dialog_success_img_icon);
                        mTextViewTitle.setText(dialogTitle);
                        mTextViewMessage.setText(dialogMessage);
                        mTextViewYes.setText(positiveBtnCaption);
                        mTextViewYes.setTextColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                        mTextViewNo.setText(negativeBtnCaption);
                        mTextViewNo.setVisibility(View.VISIBLE);
                        mTextViewYes.setBackgroundColor(mActivity.getResources().getColor(R.color.colorBackground));
                        mTextViewNo.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogSuccessBackgroundColor));
                        if (isSuccess == 0) {
                            mCircleImageView.setImageResource(R.drawable.ic_success);
                        } else if
                        (isSuccess == 1) {
                            mCircleImageView.setImageResource(R.drawable.ic_cancel_black_24dp);
                        } else if (isSuccess == 2) {
                            mCircleImageView.setImageResource(R.drawable.ic_delete_alert);
                        } else {
                            mCircleImageView.setImageResource(R.drawable.ic_warning);
                            mTextViewYes.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogInfoBackgroundColor));
                            mTextViewNo.setBackgroundColor(mActivity.getResources().getColor(R.color.dialogNoticeBackgroundColor));
                        }
                        mTextViewYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogYesNo.dismiss();
                                mCallBack.PositiveMethod(mAlertDialogYesNo, 0);
                            }
                        });
                        mTextViewNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialogYesNo.dismiss();
                                mCallBack.NegativeMethod(mAlertDialogYesNo, 0);
                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(mAlertDialogYesNo.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mAlertDialogYesNo.show();
                        mAlertDialogYesNo.getWindow().setAttributes(lp);
                    }
                }
            });
        }

    }

    public boolean haveInternet() {

        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }
        return true;
    }

    public void hideKeyboard(Activity activity) {
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public final boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void ShowProgress(final String mStringTitle, final boolean isShowProgressCount) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressHUD != null && mProgressHUD.isShowing()) {

                    } else {
                        mProgressHUD = ProgressHUD.showDialog(mActivity, mStringTitle, true, isShowProgressCount, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                            }
                        });
                    }
                }
            });
        }
    }

    public void HideProgress() {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mActivity.isDestroyed()) {
                        return;
                    }
                    if (mProgressHUD != null) {
                        mProgressHUD.dismiss();
                    }
                }
            });
        }
    }

    public void AlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            View item;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public OkHttpClient getSimpleClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        return client;
    }

    public String toCamelCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        String[] parts = s.split(" ");
        String camelCaseString = "";
        for (String part : parts) {
            if (part.length() > 1)
                camelCaseString = camelCaseString + toProperCase(part) + " ";
            else if (part.equalsIgnoreCase(""))
                camelCaseString = camelCaseString + part.toUpperCase();
            else
                camelCaseString = camelCaseString + part.toUpperCase() + " ";
        }
        return camelCaseString;
    }

    public String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }


    //Vinay Code
    public OkHttpClient getClientWithAutho() {
        //  final String encoding = Base64.encodeToString((mPreferenceHelper.getUserName() + ":" + mPreferenceHelper.getUserPassword()).getBytes(), Base64.DEFAULT);        System.out.println("encoding-"+encoding);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.connectTimeout(1, TimeUnit.MINUTES);
        httpClient.readTimeout(2, TimeUnit.MINUTES);
        httpClient.writeTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                //  String authToken = Credentials.basic("aaz@gmail.com", "123456");

                //  System.out.println("auto token ="+authToken);
//                String credentials = "j@gmail.com" + ":" + "123456";
//                final String basic =
//                        "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
                System.out.println("TokenA" + PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).getAccessToken());

///                        .header("token",PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).getAccessToken() )

                Request request = original.newBuilder()
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("token", PreferenceHelper.getPreferenceInstance(MyApplication.getAppContext()).getAccessToken())
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }

        });

        OkHttpClient client = httpClient.build();

        System.out.println("client" + client);
        return client;
    }


    public void ShowProgress(final String mStringTitle) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressHUD != null && mProgressHUD.isShowing()) {

                    } else {
                        mProgressHUD = ProgressHUD.showDialog(mActivity, mStringTitle, true, false, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                            }
                        });
                    }
                }
            });
        }
    }


    //Vinay Code


    public String convert_time(String ConvertTime) {
        String Converted_Result = "";
        try {
            String GiveResult = ConvertTime;
            String mothname = new DateFormatSymbols().getMonths()[Integer.parseInt((GiveResult.substring(5, 7))) - 1].substring(0, 3);
            String dateStr = GiveResult.substring(11, 19);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);
            String _24HourTime = formattedDate;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            Converted_Result = mothname + " " + GiveResult.substring(8, 10) + " " + GiveResult.substring(0, 4) + " at " + _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Converted_Result;
    }

    public String convertTime(String ConvertTime) {
        String Converted_Result = "";
        try {
            Date date;
            SimpleDateFormat mDateFormatServer = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            mDateFormatServer.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat mDateFormatLocalDate = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
            SimpleDateFormat mDateFormatLocalTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            if (ConvertTime != null && !ConvertTime.equals("")) {
                date = mDateFormatServer.parse(ConvertTime);
                Converted_Result = mDateFormatLocalDate.format(date) + " at " + mDateFormatLocalTime.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Converted_Result;
    }


    /**
     *This is used to conver the time if the internet is not avaliable..
     * Take the application time.Convert to UTC and then to 24 hours format...
     */
    public  String convertTimeNoInternt()
    {
        String result="";
        /**
         *Get the phone application time.
         */
        Date currentTime = Calendar.getInstance().getTime();
        /**
         * Convert the application time to i.e locale to UTC/GMT
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.ENGLISH);
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));


        /**
         * Convert to 24 hours fromatt for the UTC time..
         */
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        //Desired format: 24 hour format: Change the pattern as per the need
        // DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Date date = null;
        String output = null;
        try{
            //Converting the input String to Date
            date= df.parse(outputFmt.format(time));
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
            System.out.println("Time Stamp final Result = "+output);
            result=output;
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return  result;

    }








}
