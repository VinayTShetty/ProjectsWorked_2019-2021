package com.proofofLife.Activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.proofofLife.MainActivity;
import com.proofofLife.R;
public class LoginActivity extends AppCompatActivity {
    TextView textView_id_register_here;
    Button button_id_Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intializeView();
        clickonListner();

    }

    private void clickonListner() {
        textView_id_register_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        button_id_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void intializeView() {
        textView_id_register_here=(TextView)findViewById(R.id.textView_id_register_here);
        button_id_Login=(Button) findViewById(R.id.button_id_Login);
    }

}