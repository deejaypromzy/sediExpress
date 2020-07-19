package com.projectwork.sediexpress;

import android.app.ProgressDialog;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EndTransaction extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private EditText destination, desc;
    private Button request;
    private ProgressDialog progressDialog;
    private String uid, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recipient Delivery");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
            gestureView.setDrawingCacheEnabled(true);
            Bitmap bm = Bitmap.createBitmap(gestureView.getDrawingCache());
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "signature.png");
            f.createNewFile();
            FileOutputStream os = new FileOutputStream(f);
            os = new FileOutputStream(f);
            //compress to specified format (PNG), quality - which is ignored for PNG, and out stream
            bm.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();

        } catch (Exception e) {

            Log.v("Gestures", e.getMessage());
            e.printStackTrace();
        }


        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);


        desc = findViewById(R.id.desc);
        destination = findViewById(R.id.destination);


        request = findViewById(R.id.confirm_button);
        request.setOnClickListener(this);


        if (getIntent().getStringExtra(RequestModel._ID) != null) {
            uid = getIntent().getStringExtra(RequestModel._ID);
            code = getIntent().getStringExtra(RequestModel.CODE);
        }
        Log.i("TAG", "onClick: " + code + "----" + uid);

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
        if (destination.getText().toString().equals(code)) {


            final String key = String.valueOf(mref.push().getKey());
            progressDialog = ProgressDialog.show(this, "Submitting", "Please Wait", false, false);
            FirebaseUser user = mAuth.getCurrentUser();
            final Map<String, Object> messageUpload = new HashMap<>();
            messageUpload.put("/" + "agent_id", user.getUid());
            messageUpload.put("/" + "_id", uid);
            messageUpload.put("/" + "destination", destination.getText().toString().trim());
            messageUpload.put("/" + "description", desc.getText().toString().trim());
            messageUpload.put("/" + "comments", desc.getText().toString().trim());
            messageUpload.put("/" + "date", String.valueOf(new Date()));
            messageUpload.put("/" + "end_date", String.valueOf(new Date()));
            messageUpload.put("/" + "status", "complete");
            mref.child("sedi").child("delivery_status").child(uid).child(key).updateChildren(messageUpload).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                    Toast.makeText(EndTransaction.this, "Transaction Complete", Toast.LENGTH_SHORT).show();

                    Map<String, Object> messageUpload2 = new HashMap<>();
                    messageUpload2.put("/" + "status", "complete");
                    mref.child("sedi").child("requests").child(uid).updateChildren(messageUpload2);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EndTransaction.this, "Failed", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Toast.makeText(EndTransaction.this, "Invalid Code", Toast.LENGTH_SHORT).show();

        }

    }
}
