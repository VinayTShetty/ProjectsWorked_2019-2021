package com.proofofLife.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.proofofLife.BaseFragment.BaseFragmentHelper;
import com.proofofLife.DialogFragment.BottomSheetFristRegistration;
import com.proofofLife.DialogFragment.BottomSheetNextRegistration;
import com.proofofLife.InterfaceActivityToFragment.DeviceRegistration;
import com.proofofLife.MainActivity;
import com.proofofLife.R;

import java.util.Arrays;

import static com.proofofLife.ConstUpCode.BLE_UpCode.DATA_ENDING;
import static com.proofofLife.ConstUpCode.BLE_UpCode.DATA_STARTING;
import static com.proofofLife.ConstUpCode.BLE_UpCode.EIGHT_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FAILURE;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FIFTH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FOURTH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.FRIST_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.NINETH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SCAN_FINGER_PRINT_DATA;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SECOND_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SEVENTH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SIXTH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.SUCESS;
import static com.proofofLife.ConstUpCode.BLE_UpCode.TENTH_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.THIRD_TIME;
import static com.proofofLife.ConstUpCode.BLE_UpCode.UNKNOWN;
import static com.proofofLife.MainActivity.bleAddressInCommunication_MainActivity;

public class FragmentRegistration extends BaseFragmentHelper {
    public static final String TAG = FragmentRegistration.class.getSimpleName();
    View registrationFragmentView;
    ProgressBar progressBarScanCompleted, progress_bar_scanInProgress;
    TextView textview_progress_status;
    ImageView imageView_toolbar_cancel;
    MainActivity mainActivity;
    AlertDialog.Builder builder;
    Toolbar toolbarRegistration;
    BottomSheetFristRegistration bottomSheetFristRegistration;
   // BottomSheetNextRegistration bottomSheetNextRegistration;
    String bleAddressToCommunicate;
  //  TextView textViewLogs;;

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityReference();
       // getDataFromBundle();
    }

   /* private void getDataFromBundle() {
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            bleAddressToCommunicate = bundle.getString(getResources().getString(R.string.CUSTOM_BLE_ADDRESS));
        }
    }*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registrationFragmentView = inflater.inflate(R.layout.fragment_registration, container, false);
        intializeView();
        interfaceImplementationCallback();
        onClickListner();
        bottomNavigationInvisible(true);
        bleAddressToCommunicate=bleAddressInCommunication_MainActivity;
        Log.d(TAG, "onCreateView: BleAddress to Comunicate= "+bleAddressToCommunicate+ " MainActivity= "+bleAddressInCommunication_MainActivity);
        showFristInstructionBottomSheet();
        return registrationFragmentView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void showFristInstructionBottomSheet() {
        bottomSheetFristRegistration.setCancelable(false);
        bottomSheetFristRegistration.show(getFragmentManager(), bottomSheetFristRegistration.toString());
        bottomSheetFristRegistration.setDialogListener(new BottomSheetFristRegistration.DialogListener() {
            @Override
            public void dialogClosed() {
                bottomSheetFristRegistration.setCancelable(true);
                bottomSheetFristRegistration.dismiss();
                mainActivity.startFingerPrintScannig(FRIST_TIME, bleAddressToCommunicate);
                scanningRegistrationStart();
            }
        });
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
        return FragmentRegistration.class.getSimpleName();
    }


    private void intializeView() {
        progressBarScanCompleted = (ProgressBar) registrationFragmentView.findViewById(R.id.progress_circular_id);
        progress_bar_scanInProgress = (ProgressBar) registrationFragmentView.findViewById(R.id.progress_bar_scanInProgress_id);
        textview_progress_status = (TextView) registrationFragmentView.findViewById(R.id.textview_progress_status_id);
        imageView_toolbar_cancel = (ImageView) registrationFragmentView.findViewById(R.id.imageView_toolbar_cancel_id);
        builder = new AlertDialog.Builder(getActivity());
        toolbarRegistration = (Toolbar) registrationFragmentView.findViewById(R.id.toolbarRegistration_id);
        toolbarRegistration.inflateMenu(R.menu.fragment_registration);
        toolbarRegistration.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reset_id: {
                        showResetDialog();
                        break;
                    }
                    case R.id.test2: {
                        // Toast.makeText(getActivity(),"Test 2",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return false;
            }

            private void showResetDialog() {
                builder.setTitle("Reset Device ?");
                builder.setMessage("Are you sure ")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        bottomSheetFristRegistration = new BottomSheetFristRegistration();
      //  bottomSheetNextRegistration = new BottomSheetNextRegistration();
    }

    private void getActivityReference() {
        mainActivity = (MainActivity) getActivity();
    }

    private void scanningRegistrationStart() {
        progressBarScanCompleted.setProgress(0);
        textview_progress_status.setText("0 %");
        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
    }

    private void interfaceImplementationCallback() {
        mainActivity.setUpDeviceRegistration(new DeviceRegistration() {
            @Override
            public void registrationDetails(byte[] registrationDeviceData) {
                byte upCode = registrationDeviceData[0];
                byte length = registrationDeviceData[1];
                byte[] dataArray = new byte[16];// Contains only data.
                dataArray = Arrays.copyOfRange(registrationDeviceData, DATA_STARTING, DATA_ENDING);
                byte indexCount = dataArray[0];
                byte ack = dataArray[1];
                switch (upCode) {
                    case SCAN_FINGER_PRINT_DATA: {
                        switch (indexCount) {
                            case FRIST_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(FRIST_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), FRIST_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), FRIST_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case SECOND_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(SECOND_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), SECOND_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), SECOND_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case THIRD_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(THIRD_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), THIRD_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), THIRD_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case FOURTH_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(FOURTH_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), FOURTH_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), FOURTH_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case FIFTH_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(FIFTH_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), FIFTH_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), FIFTH_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case SIXTH_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(SIXTH_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), SIXTH_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), SIXTH_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case SEVENTH_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(SEVENTH_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), SEVENTH_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), SEVENTH_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case EIGHT_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(EIGHT_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), EIGHT_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), EIGHT_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case NINETH_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(NINETH_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), NINETH_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), NINETH_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                            case TENTH_TIME: {
                                switch (ack) {
                                    case SUCESS: {
                                        scanFingerPrintDataCompleted(TENTH_TIME);
                                        break;
                                    }
                                    case FAILURE: {
                                        dialgoShow(getResources().getString(R.string.FINGER_PRINT_WRONG_TOPIC), getResources().getString(R.string.FINGER_PRINT_WRONG_SUBTOPIC), TENTH_TIME);
                                        break;
                                    }
                                    case UNKNOWN: {
                                        dialgoShow(getResources().getString(R.string.FINGER_NOT_PLACED_TOPIC), getResources().getString(R.string.FINGER_NOT_PLACED_SUB_TOPIC), TENTH_TIME);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });

    }

    private void scanFingerPrintDataCompleted(byte indeCount) {
        switch (indeCount) {
            case FRIST_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(10);
                textview_progress_status.setText("10 %");
                //  askForNextFingerPrintDataSample(indeCount);
          //      showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
        //        progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
                break;
            }
            case SECOND_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(20);
                textview_progress_status.setText("20 %");
                //  askForNextFingerPrintDataSample(indeCount);
            //    showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
          //      progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
                break;
            }
            case THIRD_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(30);
                textview_progress_status.setText("30 %");
               // askForNextFingerPrintDataSample(indeCount);
              //    showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
           //     progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
                break;
            }
            case FOURTH_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(40);
                textview_progress_status.setText("40 %");
              //  askForNextFingerPrintDataSample(indeCount);
             //   showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
          //      progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
                break;
            }
            case FIFTH_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(50);
                textview_progress_status.setText("50 %");
               // askForNextFingerPrintDataSample(indeCount);
             //   showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
        //        progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
                break;
            }
            case SIXTH_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(60);
                textview_progress_status.setText("60 %");
                progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
             //   askForNextFingerPrintDataSample(indeCount);
            //       showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
                break;
            }
            case SEVENTH_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(70);
                textview_progress_status.setText("70 %");
                progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
              //  askForNextFingerPrintDataSample(indeCount);
            //    showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
                break;
            }
            case EIGHT_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(80);
                textview_progress_status.setText("80 %");
                progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
               // askForNextFingerPrintDataSample(indeCount);
            //    showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);
                break;
            }
            case NINETH_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(90);
                textview_progress_status.setText("90 %");
                progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
               // askForNextFingerPrintDataSample(indeCount);
             //    showBottomSheetNextRegistration(indeCount);
                sendNextFingerPrintRequest(indeCount);

                break;
            }
            case TENTH_TIME: {
                vibrateOnRegistrationSucess();
                progressBarScanCompleted.setProgress(100);
                textview_progress_status.setText("100 %");
                progress_bar_scanInProgress.setVisibility(View.INVISIBLE);
                mainActivity.fetchAllFingerPrintData(bleAddressToCommunicate);
                break;
            }
        }
    }

    private void dialgoShow(String topic, String subTopic, byte indexCount) {
        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFFcc0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        builder.setTitle(topic);
        builder.setMessage(subTopic)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivity.startFingerPrintScannig(indexCount, bleAddressToCommunicate);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        WindowManager.LayoutParams wmlp = alert.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
       /* wmlp.x = 50;   //x position
        wmlp.y = 50;   //y position*/
        alert.show();
    }

    private void instructionDialog(String topic, String subTopic) {
        builder.setTitle(topic);
        builder.setMessage(subTopic)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivity.startFingerPrintScannig(FRIST_TIME, bleAddressToCommunicate);
                        scanningRegistrationStart();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askForNextFingerPrintDataSample(byte indexToAsk) {
        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF0C3987, android.graphics.PorterDuff.Mode.MULTIPLY);
        String topic = getString(R.string.NEXT_FINGER_PRINT_TOPIC);
        topic = topic + indexToAsk + "/" + "10)";
        builder.setTitle(topic);
        String message = "\n1)Lift Finger & Touch Sensor again\n2)Press OK after Placing finger";
        builder.setMessage(message)
                //   builder.setMessage(getString(R.string.NEXT_FINGER_PRINT_SUB_TOPIC))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (indexToAsk) {
                            case FRIST_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(SECOND_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case SECOND_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(THIRD_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case THIRD_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(FOURTH_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case FOURTH_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(FIFTH_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case FIFTH_TIME: {
                                // UnComment for the 10 samples.
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(SIXTH_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case SIXTH_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(SEVENTH_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case SEVENTH_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(EIGHT_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case EIGHT_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(NINETH_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case NINETH_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                mainActivity.startFingerPrintScannig(TENTH_TIME, bleAddressToCommunicate);
                                break;
                            }
                            case TENTH_TIME: {
                                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                                // mainActivity.startFingerPrintScannig(FIFTH_TIME);
                                break;
                            }
                        }
                    }
                });
        AlertDialog alert = builder.create();
        WindowManager.LayoutParams wmlp = alert.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        alert.show();
    }

    private void onClickListner() {
        imageView_toolbar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
    }

    private void vibrateOnRegistrationSucess() {
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(900);
    }

    private void bottomNavigationInvisible(boolean invisible_true_false) {
        mainActivity.bottoNavigationVisibility(invisible_true_false);
    }

    private void showDialog_trial_1() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.delete_test_trial_1, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.setView(deleteDialogView);
        /*deleteDialogView.findViewById(R.id.yes).setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                deleteDialog.dismiss();
            }
        });
        deleteDialogView.findViewById(R.id.no).setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });*/

        deleteDialog.show();
    }





    private void sendNextFingerPrintRequest(byte indexPassed){
        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF0C3987, android.graphics.PorterDuff.Mode.MULTIPLY);
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                switch (indexPassed) {
                    case FRIST_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(SECOND_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case SECOND_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(THIRD_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case THIRD_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(FOURTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case FOURTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(FIFTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case FIFTH_TIME: {
                        // UnComment for the 10 samples.
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(SIXTH_TIME, bleAddressToCommunicate);


                        break;
                    }
                    case SIXTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(SEVENTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case SEVENTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(EIGHT_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case EIGHT_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(NINETH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case NINETH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(TENTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case TENTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        break;
                    }
                }
            }

        }.start();

    }

 /*   private void showBottomSheetNextRegistration(byte indexPassed) {
        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF0C3987, android.graphics.PorterDuff.Mode.MULTIPLY);
       *//* bottomSheetNextRegistration.setCancelable(false);
        bottomSheetNextRegistration = new BottomSheetNextRegistration(indexPassed);
        bottomSheetNextRegistration.setCancelable(false);
        bottomSheetNextRegistration.show(getFragmentManager(), "Chect");
        bottomSheetNextRegistration.setDialogListener(new BottomSheetNextRegistration.DialogListener() {
            @Override
            public void dialogClosed(byte indexpassed) {
                bottomSheetNextRegistration.dismiss();
                *//**//**
                 * ask for the Next Data From the Firmware..
                 *//**//*
                switch (indexpassed) {
                    case FRIST_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(SECOND_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case SECOND_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(THIRD_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case THIRD_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(FOURTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case FOURTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(FIFTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case FIFTH_TIME: {
                        // UnComment for the 10 samples.
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(SIXTH_TIME, bleAddressToCommunicate);


                        break;
                    }
                    case SIXTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(SEVENTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case SEVENTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(EIGHT_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case EIGHT_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(NINETH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case NINETH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        mainActivity.startFingerPrintScannig(TENTH_TIME, bleAddressToCommunicate);
                        break;
                    }
                    case TENTH_TIME: {
                        progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                        progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                        break;
                    }
                }
            }
        });*//*


        switch (indexPassed) {
            case FRIST_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(SECOND_TIME, bleAddressToCommunicate);
                break;
            }
            case SECOND_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(THIRD_TIME, bleAddressToCommunicate);
                break;
            }
            case THIRD_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(FOURTH_TIME, bleAddressToCommunicate);
                break;
            }
            case FOURTH_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(FIFTH_TIME, bleAddressToCommunicate);
                break;
            }
            case FIFTH_TIME: {
                // UnComment for the 10 samples.
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(SIXTH_TIME, bleAddressToCommunicate);


                break;
            }
            case SIXTH_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(SEVENTH_TIME, bleAddressToCommunicate);
                break;
            }
            case SEVENTH_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(EIGHT_TIME, bleAddressToCommunicate);
                break;
            }
            case EIGHT_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(NINETH_TIME, bleAddressToCommunicate);
                break;
            }
            case NINETH_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                mainActivity.startFingerPrintScannig(TENTH_TIME, bleAddressToCommunicate);
                break;
            }
            case TENTH_TIME: {
                progress_bar_scanInProgress.setVisibility(View.VISIBLE);
                progress_bar_scanInProgress.getIndeterminateDrawable().setColorFilter(0xFF40FF24, android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            }
        }
    }*/
}
