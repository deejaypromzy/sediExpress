package com.projectwork.sediexpress;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddStatus extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private EditText destination, desc;
    private Button request;
    private ProgressDialog progressDialog;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Dispatch Status");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);


        desc = findViewById(R.id.desc);
        destination = findViewById(R.id.destination);


        request = findViewById(R.id.confirm_button);
        request.setOnClickListener(this);


        if (getIntent().getStringExtra(RequestModel.ID) != null) {
            uid = getIntent().getStringExtra(RequestModel._ID);
        }

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
        String key = String.valueOf(mref.push().getKey());
        Log.i("TAG", "onClick: " + key);
        progressDialog = ProgressDialog.show(this, "Submitting", "Please Wait", false, false);
        FirebaseUser user = mAuth.getCurrentUser();
        Map<String, Object> messageUpload = new HashMap<>();
        messageUpload.put("/" + "agent_id", user.getUid());
        messageUpload.put("/" + "_id", uid);
        messageUpload.put("/" + "destination", destination.getText().toString().trim());
        messageUpload.put("/" + "description", desc.getText().toString().trim());
        messageUpload.put("/" + "date", String.valueOf(new Date()));
        messageUpload.put("/" + "status", "ongoing");
        mref.child("sedi").child("delivery_status").child(uid).child(key).updateChildren(messageUpload).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                onBackPressed();
                Toast.makeText(AddStatus.this, "Status Updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStatus.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
