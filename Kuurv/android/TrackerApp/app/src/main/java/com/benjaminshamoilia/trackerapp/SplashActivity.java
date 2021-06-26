package com.benjaminshamoilia.trackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.benjaminshamoilia.trackerapp.db.DBHelper;
import com.benjaminshamoilia.trackerapp.helper.PreferenceHelper;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    Timer timer;
    private PreferenceHelper mPreferenceHelper;
    DBHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mPreferenceHelper = new PreferenceHelper(SplashActivity.this);
        mDbHelper = DBHelper.getDBHelperInstance(SplashActivity.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void start() {
        handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (timer != null) {
                    timer.cancel();
                }
              /*  Intent mIntent = new Intent(SplashActivity.this, AppIntroActivity.class);
                mIntent.putExtra("isFirstTime", true);
                startActivity(mIntent);
                finishAffinity();*/


                if (mPreferenceHelper.getUserId().equalsIgnoreCase("")) {
                    if (!mPreferenceHelper.getIsShowIntro())
                    {
                        Intent mIntent_a = new Intent(SplashActivity.this, AppIntroActivity.class);
                        mIntent_a.putExtra("isFirstTime", true);
                        startActivity(mIntent_a);
                        finishAffinity();
                    }else
                    {
                        Intent mIntent_b = new Intent(SplashActivity.this, LoginActivity.class);
                        mIntent_b.putExtra("is_from_add_account",false);
                        startActivity(mIntent_b);
                        finishAffinity();
                    }
                } else
                {
                    Intent mIntent_c = new Intent(SplashActivity.this, MainActivity.class);
                    mIntent_c.putExtra("isFromNotification", false);
                    mIntent_c.putExtra("isFromLogin", false);
                    startActivity(mIntent_c);
                    finishAffinity();
                }


            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
        finish();
    }
}

