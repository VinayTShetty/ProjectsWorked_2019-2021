package com.benjaminshamoilia.trackerapp.fragment;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.db.DataHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FragmentSharedInfo extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    @BindView(R.id.frg_shared_info_et_name) AppCompatEditText meditText_name;
    @BindView(R.id.frg_shared_info_et_email) AppCompatEditText meditText_email;
    @BindView(R.id.frg_shared_info_et_mobile_no) AppCompatEditText meditText_number;

@BindView(R.id.frg_shared_info_til_name) TextInputLayout mTextInputlayout_name;
@BindView(R.id.frg_shared_info_til_email) TextInputLayout mTextInputlayout_email;
@BindView(R.id.frg_shared_info_til_mobile_no) TextInputLayout mTextInputlayout_number;

    String    use_stored_name;
    String    use_stored_email;
    String    use_stored_number;
    String    user_id;

    @BindView(R.id.frg_shared_info_btn_save) AppCompatButton mButtonFinish;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_shared_info, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_shared_info);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER );
        mActivity.mTextViewTitle.setTextSize(20);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);
        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });

        get_UserInfo();
        meditText_name.setText(use_stored_name);
        meditText_email.setText(use_stored_email);
       /* System.out.println("use_stored_number outside = "+use_stored_number);

        if(use_stored_number != null && !use_stored_number.isEmpty()&& !use_stored_name.equalsIgnoreCase("null")&& !use_stored_name.equalsIgnoreCase("NA"))
        {
            meditText_number.setText(use_stored_number);
            System.out.println("use_stored_number inside if = "+use_stored_number);
        }*/

        if (use_stored_number == null || use_stored_number.isEmpty() || use_stored_number.equalsIgnoreCase("NA")) {
            meditText_number.setText("");
        }


        else
            {
                meditText_number.setText(use_stored_number);
                System.out.println("use_stored_number inside else = "+use_stored_number);
            }


        mTextInputlayout_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayout_name.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayout_name.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayout_name.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTextInputlayout_email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayout_email.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayout_email.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayout_email.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTextInputlayout_number.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputlayout_number.setErrorEnabled(false);
                //  textInputEmail.setError("");
                mTextInputlayout_number.setHintEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTextInputlayout_number.getEditText().setBackgroundTintList(getResources().getColorStateList(R.color.colorAppTheme));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return mViewRoot;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewBack.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void get_UserInfo()
    {
        DataHolder mDataHolderLight;
        String querey="select * from User_Set_Info";
        mDataHolderLight=mActivity.mDbHelper.readData(querey);
        for (int i = 0; i < mDataHolderLight.get_Listholder().size(); i++)
        {
            use_stored_name=  mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.name);
            use_stored_email= mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.email);
            use_stored_number= mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.mobile);
            user_id= mDataHolderLight.get_Listholder().get(i).get(mActivity.mDbHelper.User_Set_Info_user_id);

        }

    }



    private void Update_userSetInfo(String name, String Email, String Number, String userid) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(mActivity.mDbHelper.name, name);
        mContentValues.put(mActivity.mDbHelper.email, Email);
        mContentValues.put(mActivity.mDbHelper.mobile, Number);
        mActivity.mDbHelper.updateRecord(mActivity.mDbHelper.User_Set_Info, mContentValues, mActivity.mDbHelper.user_id + "=?", new String[]{userid});
    }

    @OnClick(R.id.frg_shared_info_btn_save)
    public void onButtonFinishClick(View mView) {
        if (isAdded())
        {
            String name=meditText_name.getText().toString().trim();
            String email=meditText_email.getText().toString().trim();
            String number=meditText_number.getText().toString().trim();

            if (name.equalsIgnoreCase(""))
            {
                mTextInputlayout_name.setError("Please enter your name"); // This is the helper text i.e Red color
                mTextInputlayout_name.setHintEnabled(false);
                return;
            }

            if(email.equalsIgnoreCase(""))
            {
                mTextInputlayout_email.setError("Please enter your email"); // This is the helper text i.e Red color
                mTextInputlayout_email.setHintEnabled(false);
            }


            if (!isValidEmail(email)) {
                mTextInputlayout_email.setError("Please enter valid email"); // This is the helper text i.e Red color
                mTextInputlayout_email.setHintEnabled(false);
                return;
            }

            Update_userSetInfo(meditText_name.getText().toString().trim()
                    ,meditText_email.getText().toString().trim(),
                    meditText_number.getText().toString().trim(),
                    user_id);
            mActivity.onBackPressed();
            //System.out.println("Device DataTable Printed withImage "+mActivity.mDbHelper.getTableAsString(mActivity.mDbHelper.getReadableDatabase(), mActivity.mDbHelper.User_Set_Info));
        }




    }


    public final boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}


