package com.proofofLife.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.proofofLife.MainActivity;
import com.proofofLife.R;

public class SpalashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash);
        showAnimationNavigate();
    }

    private void showAnimationNavigate() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SpalashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}