package com.benjaminshamoilia.trackerapp.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentFeedback extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    @BindView(R.id.frg_feedback_et_email)
    AppCompatEditText mEditTextEmail;
    @BindView(R.id.frg_feedback_et_msg)
    AppCompatEditText mEditTextMsg;
    @BindView(R.id.frg_feedback_button_send)
    AppCompatButton mButtonSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_feedback, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.doorbell_title);

        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.showBackButton(false);
        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });

        return mViewRoot;
    }

    @OnClick(R.id.frg_feedback_button_send)
    public void onButtonSaveClick(View mView) {
        if (isAdded()) {
            mActivity.onBackPressed();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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


}
