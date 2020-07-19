package com.projectwork.sediexpress;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class StartService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FAQFragment.newInstance())
                    .commitNow();
        }
    }
}
