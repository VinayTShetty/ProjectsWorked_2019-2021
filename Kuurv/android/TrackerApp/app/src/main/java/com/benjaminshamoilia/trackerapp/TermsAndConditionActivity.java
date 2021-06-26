package com.benjaminshamoilia.trackerapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;




public class TermsAndConditionActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, SoundPool.OnLoadCompleteListener, OnPageChangeListener, OnLoadCompleteListener {
    private static final String SAMPLE_FILE ="TERMSandCond.pdf" ;
    TermsAndConditionActivity TermsAndConditionActivity;
    MainActivity mActivity;
    PDFView pdfView;
    int pageNumber=0;
    // public String mStringFilePath="TERMSandCond.pdf";
    String mStringtitle="";
    boolean allowLandscapeMode=false;
    String pdfFileName;
    TextView mTextview;

    ImageView mImageViewBack;
    //private FitPolicy FitPolicy;
    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;

    private PreferenceHelper mPreferenceHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_n_condition);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        pdfView=findViewById(R.id.pdfView);
        mTextview=findViewById(R.id.activity_textview);
        mImageViewBack=(ImageView)findViewById(R.id.activity_image_back);

        mPreferenceHelper = new PreferenceHelper(TermsAndConditionActivity.this);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(v.getContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        displayFromAsset(SAMPLE_FILE);
        mTextview.setText("Kuurv");
        mTextview.setTextSize(40);
        mTextview.setTextColor(Color.WHITE);

        mPreferenceHelper.setfromTemrs(true);

    }

    /*Display Image From Asset */
    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset("TERMSandCond.pdf")
                .pages(0,1,2,3,4,5,6,7,8) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(pageNumber)
                .password(null)
                .spacing(0)
                .load();
    }
    void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            //  Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber=page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Terms Activity Destroyed..");

    }
}


