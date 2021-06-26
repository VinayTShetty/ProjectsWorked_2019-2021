package com.proofofLife.DialogFragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.proofofLife.R;

import static com.proofofLife.ConstUpCode.BLE_UpCode.DELETE_DEVIE_INFO;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FAILURE;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SCAN_FINGER_PRINT_DATA;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SUCESS;
import static com.proofofLife.ConstUpCode.BLE_UpCode.UNKNOWN;
import static com.proofofLife.ConstUpCode.BLE_UpCode.USER_DEFINED;
import static com.proofofLife.ConstUpCode.BLE_UpCode.VERIFY_DEVICE_INFO;

public class CommonDialog extends DialogFragment {
    CommonDialogFragmentListner commonDialogFragmentListner;
    View commonDialogView;
    TextView textView_topic,textView_sub_topic;
    Button button_ok,button_cancel;
    byte upcode;
    byte ack;

    public CommonDialog(byte upcode_loc,byte ack_loc){
        this.upcode=upcode_loc;
        this.ack=ack_loc;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        commonDialogView = inflater.inflate(R.layout.fragment_common_dialog, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        intializeViews();
        onClickListner();
        textView_Ui_mainpulation();
        return commonDialogView;
    }

    private void intializeViews() {
        textView_topic=(TextView)commonDialogView.findViewById(R.id.textView_id_topic);
        textView_sub_topic=(TextView)commonDialogView.findViewById(R.id.textView_id_subtopic);
        button_ok=(Button) commonDialogView.findViewById(R.id.button_id_ok);
        button_cancel=(Button) commonDialogView.findViewById(R.id.button_id_cancel);
    }
    private void textView_Ui_mainpulation(){
        byte loc_upcode=upcode;
        byte loc_ack=ack;
        switch (loc_upcode){
            case VERIFY_DEVICE_INFO:{
                switch (loc_ack){
                    case SUCESS:{
                        textView_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_OWNWER));
                        textView_sub_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_OWNWER));
                        break;
                    }
                    case FAILURE:{
                        textView_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_FRESH_DEVICE));
                        textView_sub_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_FRESH_DEVICE));
                        break;
                    }
                    case UNKNOWN:{
                        textView_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_BELONGS_TO_SOMEONE));
                        textView_sub_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_BELONGS_TO_SOMEONE));
                        break;
                    }
                }
                break;
            }
            case DELETE_DEVIE_INFO:{
                switch (loc_ack){
                    case USER_DEFINED:{
                        textView_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_DELETE_DEVICE));
                        textView_sub_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_SURE_DELTE_DEVICE));
                        break;
                    }
                }
                break;
            }
            case SCAN_FINGER_PRINT_DATA:{
                switch (loc_ack){
                    case SUCESS:{
                        textView_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_TOPIC_HEADER_START_REGSITRATION));
                        textView_sub_topic.setText(getResources().getString(R.string.DIALOG_FRAGMENT_SUB_TOPIC_HEADER_START_REGSITRATION));
                        break;
                    } case FAILURE:{
                        break;
                    } case UNKNOWN:{
                        break;
                    }

                }
                break;
            }
        }

    }
    private void onClickListner(){
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commonDialogFragmentListner!=null){
                    commonDialogFragmentListner.positiveButton(upcode,ack);
                }
                dismiss();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commonDialogFragmentListner!=null){
                    commonDialogFragmentListner.cancelButton(upcode,ack);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom);
        setCancelable(true);
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
        return CommonDialog.class.getSimpleName();
    }

    public interface CommonDialogFragmentListner{
        void positiveButton(byte upCode,byte ack);
        void cancelButton(byte upCode,byte ack);
    }

    public void setUpDialogListner(CommonDialogFragmentListner commonDialogFragmentListner_loc){
        this.commonDialogFragmentListner=commonDialogFragmentListner_loc;
    }
}
