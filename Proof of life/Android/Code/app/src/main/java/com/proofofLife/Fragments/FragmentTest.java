package com.proofofLife.Fragments;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.proofofLife.BaseFragment.BaseFragmentHelper;
import com.proofofLife.DialogFragment.BottomSheetNextRegistration;
import com.proofofLife.MainActivity;
import com.proofofLife.R;

import java.sql.Timestamp;
import java.util.Date;

import static com.proofofLife.UtilitConversion.ConversionHelper.convert4bytes;

public class FragmentTest extends BaseFragmentHelper {
    View fragmentTest;
    RelativeLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    MainActivity mainActivity;

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTest = inflater.inflate(R.layout.fragment_test, container, false);
        layoutBottomSheet = (RelativeLayout) fragmentTest.findViewById(R.id.bottomsheet_registration);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mainActivity.bottoNavigationVisibility(true);


        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(stamp.getTime());
        // System.out.println("Date_Isssue= Local Time "+date);


        SimpleDateFormat f = new SimpleDateFormat("h:mm a");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(f.format(new Date()));
        String dd = f.format(new Date());
        //System.out.println("Date_Isssue = UTC "+dd);


        //   System.out.println("Date_Issue_trial GMT "+gmttoLocalDate(localToGMT()));
        //    System.out.println("Date_Issue_trial local  "+localToGMT());


        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long now = System.currentTimeMillis() / 1000 - offset;
        // System.out.println("GMT= "+now);


//        trial_2();
        trial_3();

        System.out.println(" MINUTE VALUE= "+getMinValueFromTimeStamp("6024F3F4"));
        return fragmentTest;
    }

    public static Date localToGMT() {
        Timestamp stamp = new Timestamp(1620087390);
        // Date date = new Date(stamp.getTime());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmt = new Date(sdf.format(date));
        return gmt;
    }

    public void trial_2() {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(1623002400 * 1000);
        time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(time.getTimeInMillis()));
//        time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(1623002400));
        Date date = time.getTime();
        time.get(Calendar.MINUTE);
        System.out.println("Jugaad------>" + time.get(Calendar.MINUTE) + " Date= " + date);
    }

    public void trial_3() {
        final Date currentTime = new Date();
        currentTime.setTime(1613034484000L);
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
       Calendar calendar= sdf.getCalendar();
        System.out.println("GMT time: " + sdf.format(currentTime)+" Minus= "+calendar.get(Calendar.MINUTE));
    }


    public static String getMinValueFromTimeStamp(String hexValue) {
        String minutesValue="NA";
        long timeStamp=Long.parseLong(""+convert4bytes(hexValue));
        timeStamp=timeStamp*1000;

        final Date currentTime = new Date();
        currentTime.setTime(timeStamp);
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar= sdf.getCalendar();
        sdf.format(currentTime);
        int minutes=calendar.get(Calendar.MINUTE);


        if(minutes==0){
            minutesValue="00";
        }else {
            minutesValue=""+minutes;
        }
        return minutesValue;
    }

    public static Date gmttoLocalDate(Date date) {

        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    BottomSheetNextRegistration bottomSheetNextRegistration = new BottomSheetNextRegistration();

    @Override
    public void onResume() {
        super.onResume();
       /* if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }*/

        bottomSheetNextRegistration=new BottomSheetNextRegistration();
        bottomSheetNextRegistration.setCancelable(false);
        bottomSheetNextRegistration.show(getFragmentManager(),"Chect");
        bottomSheetNextRegistration.setDialogListener(new BottomSheetNextRegistration.DialogListener() {
            @Override
            public void dialogClosed(byte index) {
                bottomSheetNextRegistration.dismiss();
            }
        });
    }

    private void countDownTimer() {
        new CountDownTimer(9000, 1000) {
            public void onTick(long millisUntilFinished) {
                //  texview_timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                //texview_timer.setText("");
                bottomSheetNextRegistration.dismiss();
            }

        }.start();
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

    private void getActivityReference() {
        mainActivity = (MainActivity) getActivity();
    }


}
