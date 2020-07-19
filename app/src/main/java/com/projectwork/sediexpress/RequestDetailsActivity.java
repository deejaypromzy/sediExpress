package com.projectwork.sediexpress;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class RequestDetailsActivity extends AppCompatActivity {
    private ArrayList<RequestInfo> ReqModel;
    private ReqAdapter reqAdapter;


    private ProgressBar progressbar;
    private FirebaseAuth auth;
    private FirebaseUser fireuser;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mref;
    private FirebaseAuth mAuth;
    private String itemID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra(RequestModel._ID) != null) {
            itemID = getIntent().getStringExtra(RequestModel._ID);
        }

        auth = FirebaseAuth.getInstance();
        fireuser = auth.getCurrentUser();
        mfirebaseDatabase = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        mref = mfirebaseDatabase.getReference().child("sedi").child("delivery_status").child(itemID);
        mref.keepSynced(true);


        progressbar = findViewById(R.id.progressbar);

        final RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

//Set the Layout Manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//Initialize the adapter and set it ot the RecyclerView

//Initialize the ArrayList that will contain the data
        ReqModel = new ArrayList<>();
        reqAdapter = new ReqAdapter(this, ReqModel);
        mRecyclerView.setAdapter(reqAdapter);

        new CountDownTimer(200, 200) {
            public void onTick(long ms) {
                progressbar.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                progressbar.setVisibility(View.GONE);

                if (mref != null) {
                    mref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            showData(dataSnapshot);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressbar.setVisibility(View.GONE);

                        }
                    });
                }
            }
        }.start();
    }

    private void showData(DataSnapshot dataSnapshot) {
        //Create the ArrayList of Sports objects with the titles, images
        // and information about each sport
        ReqModel.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            RequestInfo userDatabase = new RequestInfo();
            userDatabase.set_id((ds.getValue(RequestInfo.class)).get_id());
            userDatabase.setDate((ds.getValue(RequestInfo.class)).getDate());
            userDatabase.setAgent_id((ds.getValue(RequestInfo.class)).getAgent_id());
            userDatabase.setDescription((ds.getValue(RequestInfo.class)).getDescription());
            userDatabase.setDestination((ds.getValue(RequestInfo.class)).getDestination());
            userDatabase.setStatus((ds.getValue(RequestInfo.class)).getStatus());
            FirebaseUser user = mAuth.getCurrentUser();

            Log.d("TAG", "showData: " + itemID);
            if (Objects.equals(userDatabase.get_id(), itemID))
                ReqModel.add(new RequestInfo(userDatabase.getAgent_id(), userDatabase.getDate(), userDatabase.getDescription(), userDatabase.getDestination(), userDatabase.getStatus(), userDatabase.get_id()));
        }
        //Notify the adapter of the change
        reqAdapter.notifyDataSetChanged();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
