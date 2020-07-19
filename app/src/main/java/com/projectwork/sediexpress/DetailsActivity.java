package com.projectwork.sediexpress;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Request Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView id = findViewById(R.id.id);
        TextView desc = findViewById(R.id.desc);
        TextView pkuplocation = findViewById(R.id.pkuplocation);
        TextView droplocation = findViewById(R.id.droplocation);
        TextView distance = findViewById(R.id.distance);
        TextView recipient = findViewById(R.id.recipient);
        TextView reciepientnumber = findViewById(R.id.reciepientnumber);
        TextView amount = findViewById(R.id.amount);
        TextView date = findViewById(R.id.date);
        TextView status = findViewById(R.id.status);
        TextView agent = findViewById(R.id.agent);
        if (getIntent().getStringExtra(RequestModel.ID) != null) {


            id.setText(getIntent().getStringExtra(RequestModel._ID));
            desc.setText(getIntent().getStringExtra(RequestModel.DESC));
            pkuplocation.setText(getIntent().getStringExtra(RequestModel.PICKUP));
            droplocation.setText(getIntent().getStringExtra(RequestModel.DROP));
            distance.setText(getIntent().getStringExtra(RequestModel.DISTANCE));
            recipient.setText(getIntent().getStringExtra(RequestModel.RECIPIENT));
            reciepientnumber.setText(getIntent().getStringExtra(RequestModel.RECIPIENT_PHONE));
            amount.setText(getIntent().getStringExtra(RequestModel.AMOUNT));
            date.setText(getIntent().getStringExtra(RequestModel.DATE));
            status.setText(getIntent().getStringExtra(RequestModel.STATUS).toUpperCase());
            agent.setText(getIntent().getStringExtra(RequestModel.AGENT));
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
