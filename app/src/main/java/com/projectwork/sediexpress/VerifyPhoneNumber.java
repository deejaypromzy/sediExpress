package com.projectwork.sediexpress;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;


public class VerifyPhoneNumber extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText etCode;
    private Button signinBtn;
    private TextView resend;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        etCode = findViewById(R.id.verification);
        signinBtn = findViewById(R.id.signinBtn);
        resend = findViewById(R.id.resend);
        signinBtn.setOnClickListener(this);
        resend.setOnClickListener(this);

        prefManager = new PrefManager(this);
        if (!prefManager.isVerified()) {
            launchMainActivity();
            finish();
        }
    }

    private void launchMainActivity() {
        prefManager.setVerified(false);
        startActivity(new Intent(VerifyPhoneNumber.this, MainActivity.class));
        finish();
    }


    public void Resend() {
        String phone = prefManager.getUserPhone();
        // new SendVerificationAsync().execute("resendVerification", phone);
        resend.setText("Resend Again in 5mins");
        resend.setTextColor(getResources().getColor(R.color.bg_screen3));
        resend.setEnabled(false);

    }

    public void VerifyMyEmail() {
        if (!TextUtils.isEmpty(etCode.getText().toString())) {
            //  new VerifyAsync().execute("verify", etCode.getText().toString().trim());

        } else {
            etCode.requestFocus();
            etCode.setError("Field is required");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinBtn:
                v.requestFocusFromTouch();
                VerifyMyEmail();
                break;
            case R.id.resend:
                Resend();
                break;
            case R.id.signout:
                prefManager.setRegistered(true);
                startActivity(new Intent(VerifyPhoneNumber.this, MainActivity.class));
                finish();
                break;
        }
    }

}
