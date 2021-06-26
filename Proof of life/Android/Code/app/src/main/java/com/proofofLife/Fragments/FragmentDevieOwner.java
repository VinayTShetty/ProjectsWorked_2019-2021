package com.proofofLife.Fragments;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.proofofLife.BaseFragment.BaseFragmentHelper;
import com.proofofLife.DataBaseRoomDAO.DeviceRegistation_Room;
import com.proofofLife.MainActivity;
import com.proofofLife.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.proofofLife.ConstUpCode.BLE_UpCode.FINGER_PRINT_SECURITY_KEY;
import static com.proofofLife.MainActivity.bleAddressInCommunication_MainActivity;
import static com.proofofLife.MainActivity.convertSecretKeytoHex;
import static com.proofofLife.UtilitConversion.ConversionHelper.convert_TimeStampTo_4bytes;
import static com.proofofLife.UtilitConversion.ConversionHelper.getHexStringFromByteArray;

public class FragmentDevieOwner extends BaseFragmentHelper {
    public static final String TAG=FragmentDevieOwner.class.getSimpleName();
    MainActivity mainActivity;
    View ownerFragmentView;
    String bleAddressToCommunicate;
    LinearLayout generate_otp_layout,delete_layout,setting_layout;
    AlertDialog.Builder builder;
    ImageView imageView_toolbar_cancel;
    TextView date_time_textView;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private int present_mYear, present_mMonth, present_mDay, present_mHour, present_mMinute;
    String timeStamp;
    Button button_otp_generation;
    List<DeviceRegistation_Room> deviceRegistation;
    String fingerPrintData="";
    String securityKey="";
    int otpValidity;
    TextView present_time_TextView,presentDate_TextView,toolbar_textViewheader,logsDisplayTextView;
    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityReference();
        bleAddressToCommunicate=bleAddressInCommunication_MainActivity;
       // getDataFromBundle();
        getTimeStamp();
    }

    private void getTimeStamp() {
//        timeStamp=""+System.currentTimeMillis()/1000;
        long SystemtimeStamp=System.currentTimeMillis();
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(SystemtimeStamp);
        calendar.set(Calendar.SECOND,0);
        timeStamp=""+calendar.getTimeInMillis()/1000;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ownerFragmentView = inflater.inflate(R.layout.fragment_owner, container, false);
        intializeViews();
        onClickListner();
        Log.d(TAG, "onCreateView: Ble Address= "+bleAddressToCommunicate);
        getCurrentDate_Time_From_Mobile();
        bottomNavigationInvisible(true);
        System.out.println("BLE Address= "+bleAddressToCommunicate);
        getPresentDateTime();
        mainActivity.sendTimeStamp(bleAddressToCommunicate,Integer.parseInt(timeStamp));
        return ownerFragmentView;
    }

//  val-(TimeStamp%val)  Formula for calculating timer.

    private void getPresentDateTime() {
        present_time_TextView.setText(present_time_TextView.getText());

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        presentDate_TextView.setText("Current TimeStamp: "+formattedDate+" ::");
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
       generateOtpForPresentTime();

    }

    private void generateOtpForPresentTime() {
     //   final String[] fingerprintData = {""};
        final String[] otp = {""};
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceRegistation =mainActivity.roomDBHelperInstance.get_deviceRegistration_dao().getDeviceInfo();
                for (DeviceRegistation_Room deviceRegistation_room:deviceRegistation) {
                    if(deviceRegistation_room.getBleAddress().replace(":","").equalsIgnoreCase(bleAddressToCommunicate.replace(":",""))){
                        fingerPrintData="";
                        fingerPrintData=  deviceRegistation_room.getFingerPrintData();
                        securityKey="";
                        securityKey=deviceRegistation_room.getSecretkey();
                        otpValidity=Integer.parseInt(deviceRegistation_room.getOtpValidity());
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toolbar_textViewheader.setText("Token "+deviceRegistation_room.getId());
                            }
                        });
                    }
                }
                int timeStampConverted=Integer.parseInt(timeStamp);
                int modulusTimeStamp=timeStampConverted-(timeStampConverted%otpValidity);
                otp[0] =mainActivity.generateOTP(securityKey,fingerPrintData,getHexStringFromByteArray(convert_TimeStampTo_4bytes(modulusTimeStamp),false));
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logsDisplayTextView.setText("");
                        int timeStampConverted=Integer.parseInt(timeStamp);

                        int modulusTimeStamp=timeStampConverted-(timeStampConverted%otpValidity);
                        logsDisplayTextView.append("Security Key:- "+convertSecretKeytoHex(securityKey)+" \n"+" FingerPrint Data= "+fingerPrintData+" "+"\n"+"TS= "+getHexStringFromByteArray(convert_TimeStampTo_4bytes(modulusTimeStamp),false));
                        button_otp_generation.setText(otp[0]);
                        date_time_textView.setText("Time Stamp: "+getCurrentDate_Time_From_Mobile());
                    }
                });
            }

        }).start();
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
        return FragmentDevieOwner.class.getSimpleName();
    }
    private void getActivityReference() {
        mainActivity = (MainActivity) getActivity();
    }
   /* private void getDataFromBundle() {
        final  Bundle bundle=this.getArguments();
        if(bundle!=null){
            bleAddressToCommunicate=bundle.getString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS));
        }
    }*/

    private void intializeViews() {
        generate_otp_layout=(LinearLayout) ownerFragmentView.findViewById(R.id.generate_otp_layout_id);
        delete_layout=(LinearLayout) ownerFragmentView.findViewById(R.id.delete_layout_id);
        setting_layout=(LinearLayout) ownerFragmentView.findViewById(R.id.setting_layout_id);
        imageView_toolbar_cancel=(ImageView) ownerFragmentView.findViewById(R.id.imageView_toolbar_cancel_id);
        button_otp_generation=(Button) ownerFragmentView.findViewById(R.id.button_otp_generation_id);
        date_time_textView=(TextView) ownerFragmentView.findViewById(R.id.date_time_textView_id);
        present_time_TextView=(TextView) ownerFragmentView.findViewById(R.id.present_time_TextView_id);
        presentDate_TextView=(TextView) ownerFragmentView.findViewById(R.id.presentDate_TextView_id);
        toolbar_textViewheader=(TextView) ownerFragmentView.findViewById(R.id.toolbar_textViewheader_id);
        logsDisplayTextView=(TextView) ownerFragmentView.findViewById(R.id.logsDisplayTextView_id);
        logsDisplayTextView.setMovementMethod(new ScrollingMovementMethod());
        builder = new AlertDialog.Builder(getActivity());
    }

    private void onClickListner(){
        generate_otp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showAlertDialog(getString(R.string.FRAGMENT_OWNER_SET_OTP_TOPIC),getString(R.string.FRAGMENT_OWNER_SET_OTP_SUB_TOPIC));
                showDatePickerDialog();
            }
            private void showDatePickerDialog() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;
                        show_TimePickerDialog();
                    }
                }, present_mYear, present_mMonth, present_mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
            private void show_TimePickerDialog() {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        /**
                         * new logic implementation for checking the past time.
                         */
                        Calendar datetime = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minutes);
                        convert_SelectedDate_Time_TimeStamp(selectedYear, selectedMonth, selectedDay, hourOfDay, minutes);
                    }
                }, present_mHour, present_mMinute, false);
                timePickerDialog.show();
            }
            private void convert_SelectedDate_Time_TimeStamp(int year_input, int month_input, int date_input, int hourOfDay, int minutes) {
                Calendar calendarLocal = new GregorianCalendar(TimeZone.getDefault());
                calendarLocal.set(year_input, month_input, date_input);
                calendarLocal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarLocal.set(Calendar.MINUTE, minutes);
                calendarLocal.set(Calendar.SECOND, 0);
                int timeStamp = (int) (calendarLocal.getTimeInMillis() / 1000);
                mainActivity.sendTimeStamp(bleAddressToCommunicate,timeStamp);
                Log.d(TAG, "convert_SelectedDate_Time_TimeStamp: test demo "+timeStamp);
                logsDisplayTextView.setText("");
                int modulusTimeStamp=timeStamp-(timeStamp%otpValidity);
                String otp =mainActivity.generateOTP(securityKey,fingerPrintData,getHexStringFromByteArray(convert_TimeStampTo_4bytes(modulusTimeStamp),false));
                logsDisplayTextView.append("Security Key:- "+convertSecretKeytoHex(securityKey)+" \n"+" FingerPrint Data= "+fingerPrintData+" "+"\n"+"TS= "+getHexStringFromByteArray(convert_TimeStampTo_4bytes(modulusTimeStamp),false));




                button_otp_generation.setText(otp);
                /**
                 * Getting the Date from the Calendar.
                 */
                final Date date = calendarLocal.getTime();
                String dateForm = new SimpleDateFormat("dd/MM/yyyy").format(date);
                date_time_textView.setText("TimeStamp: "+dateForm+"::"+String.format("%02d",calendarLocal.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d",calendarLocal.get(Calendar.MINUTE)));

            }
        });
        delete_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(getString(R.string.FRAGMENT_OWNER_DELETE_TOPIC),getString(R.string.FRAGMENT_OWNER_DELETE_SUB_TOPIC));
            }
        });

        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS), bleAddressToCommunicate);
                mainActivity.replaceFragment(new FragmentUserSetting(), bundle);
            }
        });
        imageView_toolbar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
    }
    private void showAlertDialog(String topic,String subTopic,byte ...data){
        byte upcode=-1;;
        if(data.length>0){
            upcode=data[0];
        }
        builder.setTitle(topic);
        builder.setMessage(subTopic)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivity.deleteuserInfo(bleAddressToCommunicate,securityKey);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    private String getCurrentDate_Time_From_Mobile() {
        String time="";
        Calendar c = Calendar.getInstance();
        present_mYear = c.get(Calendar.YEAR);
        present_mMonth = c.get(Calendar.MONTH);
        present_mDay = c.get(Calendar.DAY_OF_MONTH);
        /**
         * Picking up time from android.
         */
        present_mHour = c.get(Calendar.HOUR_OF_DAY);// 24 HOURS formatt--->HOUR_OF_DAY
        present_mMinute = c.get(Calendar.MINUTE);


        /**
         * Getting the Date from the Calendar.
         */
        final Date date = c.getTime();
        String dateForm = new SimpleDateFormat("dd/MM/yyyy").format(date);
//        System.out.println("Fragment Registration= Time= "+full+" TIme= "+present_mHour+":"+present_mMinute);
        time=dateForm+"::"+String.format("%02d",present_mHour)+":"+String.format("%02d",present_mMinute);
        return time;
    }

    private void bottomNavigationInvisible(boolean invisible_true_false) {
        mainActivity.bottoNavigationVisibility(invisible_true_false);
    }


}
