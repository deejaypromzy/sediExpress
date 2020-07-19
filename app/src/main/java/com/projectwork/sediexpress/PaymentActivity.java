package com.projectwork.sediexpress;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;
    String[] spinnerTitles;
    int[] spinnerImages;
    Spinner mSpinner;
    private TextView amt;
    private EditText acc;
    private boolean isUserInteracting;
    private String mode;
    private DatabaseReference mref;
    private String uid;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);
        acc = findViewById(R.id.acc);
        amt = findViewById(R.id.amt);

        prefManager = new PrefManager(this);

        if (getIntent().getStringExtra("amount") != null) {
            uid = getIntent().getStringExtra("uid");
            amt.setText(getIntent().getStringExtra("amount"));
        }

        mSpinner = (Spinner) findViewById(R.id.spinner);
        spinnerTitles = new String[]{"MTN Mobile Money", "Vodafone Cash", "Airtel Tigo Money", "Monegram", "GCB Mobile Money"};
        spinnerImages = new int[]{R.drawable.momo
                , R.drawable.voda
                , R.drawable.airtel
                , R.drawable.moneyg
                , R.drawable.gn
        };

        CustomAdapter mCustomAdapter = new CustomAdapter(PaymentActivity.this, spinnerTitles, spinnerImages);
        mSpinner.setAdapter(mCustomAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isUserInteracting) {
                    Toast.makeText(PaymentActivity.this, spinnerTitles[i], Toast.LENGTH_SHORT).show();
                    mode = spinnerTitles[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void cancel(View view) {
        onBackPressed();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(("Check your phone to confirm payment..."));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void confirm(View view) {
        String validNumber = "^[+]?[0-9]{8,15}$";
        if (!acc.getText().toString().matches(validNumber)) {
            acc.setError("Invalid Account");
            acc.requestFocus();
            return;

        } else {
            acc.setError(null);
        }
        showProgressDialog();
        mref.child("sedi").child("requests").child(uid).child("status").setValue(Utils.PAID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgressDialog();
                onBackPressed();

                Utils.SMS_SEND sms_send = new Utils.SMS_SEND();
                String msg = "Payment of GHC " + amt + " made successfully, Thank you.";
                String phone = prefManager.getUserPhone();
                sms_send.execute(phone, msg);

                Toast.makeText(PaymentActivity.this, "Payment Made Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
