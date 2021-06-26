package com.proofofLife.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.proofofLife.R;

import java.lang.reflect.Field;

import believe.cht.fadeintextview.TextView;
import believe.cht.fadeintextview.TextViewListener;
import static com.proofofLife.ConstUpCode.BLE_UpCode.*;
public class BottomSheetNextRegistration extends BottomSheetDialogFragment {
    View bottomSheetRegistrationView;
    TextView texview_timer,texView_sucessCount,textView_instrction_details_1,textView_instrction_details_2;
    ProgressBar progressbar_sucessProgress;
    byte indexPassed;
    public BottomSheetNextRegistration(byte indexPassed) {
        super();
        this.indexPassed=indexPassed;
    }

    public BottomSheetNextRegistration( ) {
        super();
    }
    private DialogListener dialogListener;

    public static BottomSheetNextRegistration newInstance() {
        return new BottomSheetNextRegistration();
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        bottomSheetDialog.setContentView(R.layout.bottomsheet_nextregistration);
        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING){
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomSheetRegistrationView=inflater.inflate(R.layout.bottomsheet_nextregistration, container, false);
        intializeView();
        textViewAnimationInstrcution_1();
        showUIChangesinBottomSheetFragmet();
        return bottomSheetRegistrationView;
    }

    private void showUIChangesinBottomSheetFragmet() {
        switch (indexPassed){
            case FRIST_TIME:{
                progressbar_sucessProgress.setProgress(10);
                texView_sucessCount.setText("10/100");
                break;
            }
            case SECOND_TIME:{
                progressbar_sucessProgress.setProgress(20);
                texView_sucessCount.setText("20/100");
                break;
            }
            case THIRD_TIME:{
                progressbar_sucessProgress.setProgress(30);
                texView_sucessCount.setText("30/100");
                break;
            }
            case FOURTH_TIME:{
                progressbar_sucessProgress.setProgress(40);
                texView_sucessCount.setText("40/100");
                break;
            }
            case FIFTH_TIME:{
                progressbar_sucessProgress.setProgress(50);
                texView_sucessCount.setText("50/100");
                break;
            }
            case SIXTH_TIME:{
                progressbar_sucessProgress.setProgress(60);
                texView_sucessCount.setText("60/100");
                break;
            }
            case SEVENTH_TIME:{
                progressbar_sucessProgress.setProgress(70);
                texView_sucessCount.setText("70/100");
                break;
            }
            case EIGHT_TIME:{
                progressbar_sucessProgress.setProgress(80);
                texView_sucessCount.setText("80/100");
                break;
            }
            case NINETH_TIME:{
                progressbar_sucessProgress.setProgress(90);
                texView_sucessCount.setText("90/100");
                break;
            }
        }
    }

    private void textViewAnimationInstrcution_1() {
        textView_instrction_details_1.setListener(new TextViewListener() {
            @Override
            public void onTextStart() {
            }

            @Override
            public void onTextFinish() {
                textViewAnimationInstrcution_2();
            }
        });
        textView_instrction_details_1.setLetterDuration(70); // sets letter duration programmatically
        textView_instrction_details_1.setText("1) Remove Finger From Scanner");
    }
    private void textViewAnimationInstrcution_2() {
        textView_instrction_details_2.setListener(new TextViewListener() {
            @Override
            public void onTextStart() {
            }

            @Override
            public void onTextFinish() {
                countDownTimer();
            }
        });
        textView_instrction_details_2.setLetterDuration(70); // sets letter duration programmatically
        textView_instrction_details_2.setText("2) Place the Finger on the Scanner again before the TimeOut");
    }


    private void countDownTimer() {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
                texview_timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                texview_timer.setText("");
                if(dialogListener!=null){
                    dialogListener.dialogClosed(indexPassed);
                }
            }

        }.start();
    }

    private void intializeView() {
        texview_timer=(TextView)bottomSheetRegistrationView.findViewById(R.id.textView_timer_id);
        textView_instrction_details_1=(TextView)bottomSheetRegistrationView.findViewById(R.id.textView_instrction_details_1_id);
        textView_instrction_details_2=(TextView)bottomSheetRegistrationView.findViewById(R.id.textView_instrction_details_2_id);
        texView_sucessCount=(TextView)bottomSheetRegistrationView.findViewById(R.id.texView_sucessCount_id);
        progressbar_sucessProgress=(ProgressBar)bottomSheetRegistrationView.findViewById(R.id.progressbar_sucessProgress_id);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }

    public interface DialogListener {
        void dialogClosed(byte indexValuePassed);
    }
    public void setDialogListener(DialogListener dialogListener_loc) {
        this.dialogListener = dialogListener_loc;
    }
}
