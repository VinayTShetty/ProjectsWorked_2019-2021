package com.proofofLife.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.proofofLife.R;
import java.lang.reflect.Field;
import believe.cht.fadeintextview.TextViewListener;
public class BottomSheetFristRegistration extends BottomSheetDialogFragment {
    View bottomSheetView;
    TextView texview_timer,textView_instrction_details_1,textView_instrction_details_2;
    Button button_ok;
    public BottomSheetFristRegistration() {
        super();
    }
    private DialogListener dialogListener;
    public static BottomSheetFristRegistration newInstance() {
        return new BottomSheetFristRegistration();
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        bottomSheetDialog.setContentView(R.layout.registration_process_start);
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

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomSheetView =inflater.inflate(R.layout.registration_process_start, container, false);
        intializeView();
        onClickListner();
       // textViewAnimationInstrcution_1();
        return bottomSheetView;
    }





   /* private void textViewAnimationInstrcution_1() {
        textView_instrction_details_1.setListener(new TextViewListener() {
            @Override
            public void onTextStart() {
            }

            @Override
            public void onTextFinish() {
                countDownTimer();
            }
        });
        textView_instrction_details_1.setLetterDuration(70); // sets letter duration programmatically
        textView_instrction_details_1.setText("1) Place the Finger in the Scanner ");
    }*/
  /*  private void textViewAnimationInstrcution_2() {
        textView_instrction_details_2.setListener(new TextViewListener() {
            @Override
            public void onTextStart() {
            }

            @Override
            public void onTextFinish() {
                button_ok.setVisibility(View.VISIBLE);
            }
        });
        textView_instrction_details_2.setLetterDuration(70); // sets letter duration programmatically
        textView_instrction_details_2.setText("2) Press 'OK' Button");
    }*/



    /*private void countDownTimer() {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
                texview_timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                texview_timer.setText("");
                textViewAnimationInstrcution_2();
            }

        }.start();
    }*/
    private void intializeView() {
        texview_timer=(TextView) bottomSheetView.findViewById(R.id.textView_timer_id);
        textView_instrction_details_1=(TextView) bottomSheetView.findViewById(R.id.textView_instrction_details_1_id);
        textView_instrction_details_2=(TextView) bottomSheetView.findViewById(R.id.textView_instrction_details_2_id);
        button_ok=(Button) bottomSheetView.findViewById(R.id.button_ok_id);
    }
    private void onClickListner() {
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogListener!=null){
                    dialogListener.dialogClosed();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  bottomSheetView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(5); // Remove this line to hide a dark background if you manually hide the dialog.
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public String toString() {
        return BottomSheetFristRegistration.class.getSimpleName();
    }

    public interface DialogListener {
        void dialogClosed();
    }
    public void setDialogListener(DialogListener dialogListener_loc) {
        this.dialogListener = dialogListener_loc;
    }
}
