package com.benjaminshamoilia.trackerapp.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentViewPDF extends Fragment implements OnPageChangeListener, OnLoadCompleteListener {
    View mViewRoot;
    MainActivity mActivity;
    private Unbinder unbinder;
    PDFView pdfView;
    int pageNumber = 0;
    public String mStringFilePath = "";
    String mStringTitle = "";
    boolean allowLandscapeMode = false;

    // https://stackoverflow.com/questions/2784847/how-do-i-determine-if-android-can-handle-pdf
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            mStringFilePath = getArguments().getString("intent_file_url");
            mStringTitle = getArguments().getString("intent_title");
            allowLandscapeMode = getArguments().getBoolean("intent_is_landscape");
        }
        if (allowLandscapeMode) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_install_guide, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(mStringTitle);
     //   mActivity.mImageViewBack.setVisibility(View.VISIBLE);
       // mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.INVISIBLE);
      //  mActivity.mImageViewAdd.setVisibility(View.GONE);
     //   mActivity.mRelativeLayoutBottomMenu.setVisibility(View.GONE);


      //  mActivity.mTextViewTitle.setText(R.string.str_title_phone_alert);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.showBackButton(true);


        pdfView = (PDFView) mViewRoot.findViewById(R.id.pdfView);
        displayFromAsset();
        return mViewRoot;
    }

    /*Display pdf from asset*/
    private void displayFromAsset() {
        pdfView.fromAsset(mStringFilePath)
                .defaultPage(pageNumber)
                .enableSwipe(false)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onPageChange(this)
                .enableAnnotationRendering(false)
                .password(null)
                .onLoad(this)
                .scrollHandle(null)
                .load();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }

}
