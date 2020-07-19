package com.projectwork.sediexpress;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class RequestActivity extends AppCompatActivity implements View.OnClickListener {
    public TextView pk, dr, appdistance, est_amt;
    private PrefManager prefManager;
    private Button request;
    private String amt;
    private EditText body, rname, rnumber, secrete, desc;
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    public static float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return ((float) ((int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp))) / pow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        prefManager = new PrefManager(this);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);


        body = findViewById(R.id.body);
        rnumber = findViewById(R.id.rnumber);
        rname = findViewById(R.id.rname);
        secrete = findViewById(R.id.secrete);
        desc = findViewById(R.id.desc);


        pk = findViewById(R.id.pk);
        dr = findViewById(R.id.dr);
        appdistance = findViewById(R.id.appdistance);
        est_amt = findViewById(R.id.est_amt);
        request = findViewById(R.id.confirm_button);
        request.setOnClickListener(this);

        pk.setText(prefManager.getPickLocation());
        dr.setText(prefManager.getDropLocation());
        appdistance.setText(prefManager.getDistance() + " Km");

        if ((prefManager.getDistance()) > 0) {
            amt = String.valueOf(round((float) (prefManager.getDistance() * prefManager.getDistance()), 2));
        } else {
            amt = "25";

        }

        est_amt.setText(" GHC" + amt);

    }

    private boolean Validation() {
        String email = rname.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            rname.setError("Recipient's name can't be empty.");
            rname.requestFocus();
            return false;
        } else {
            rname.setError(null);
        }
        email = rnumber.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            rnumber.requestFocus();
            rnumber.setError("Recipient's number can't be empty.");
            return false;
        } else {
            rnumber.setError(null);
        }

        email = secrete.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            secrete.setError("Code can't be empty.");
            secrete.requestFocus();
            return false;
        } else {
            secrete.setError(null);
        }

        email = desc.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            desc.setError("Location Description can't be empty.");
            desc.requestFocus();
            return false;
        } else {
            desc.setError(null);
        }
        email = body.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            body.setError("Item Description can't be empty.");
            body.requestFocus();
            return false;
        } else {
            body.setError(null);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (Validation()) {
            String key = mref.push().getKey();
            FirebaseUser user = mAuth.getCurrentUser();
            RequestModel db = new RequestModel();
            db.set_id(key);
            db.setId(user.getUid());
            db.setPickup(prefManager.getPickLocation());
            db.setDrop(prefManager.getDropLocation());
            db.setDistance(String.valueOf(prefManager.getDistance()));
            db.setAmount(String.valueOf(amt));
            db.setRecipient(rname.getText().toString().trim());
            db.setRecipient_phone(rnumber.getText().toString().trim());
            db.setSender_phone(prefManager.getUserPhone());
            db.setDesc(body.getText().toString().trim());
            db.setDate(String.valueOf(new Date()));
            db.setStatus(Utils.PENDING);
            db.setSecreteCode(secrete.getText().toString());
            db.setLocationDesc(desc.getText().toString().trim());
            progressDialog = ProgressDialog.show(this, "Submitting Request", "Please Wait", false, false);

            Utils.SMS_SEND sms_send = new Utils.SMS_SEND();
            String msg = "Delivery request ID: " + key + "Code: " + secrete.getText().toString() + " Desc: " + desc.getText().toString().trim();
            String phone = rnumber.getText().toString().trim();
            sms_send.execute(phone, msg);

            mref.child("sedi").child("requests").child(key).setValue(db).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Request Submitted", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });
        }
    }
}
