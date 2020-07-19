package com.projectwork.sediexpress;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class PendingFragment extends Fragment {
    private ProgressBar progressbar;

    private FirebaseAuth auth;
    private FirebaseUser fireuser;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mref;
    private ArrayList<RequestModel> ModelFaq;
    private RequestAdapter requestAdapter;
    private FirebaseAuth mAuth;
    private PrefManager prefManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(getActivity());

        auth = FirebaseAuth.getInstance();
        fireuser = auth.getCurrentUser();
        mfirebaseDatabase = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        mref = mfirebaseDatabase.getReference().child("sedi").child("requests");
        mref.keepSynced(true);


        progressbar = view.findViewById(R.id.progressbar);
        final RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);

//Set the Layout Manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//Initialize the adapter and set it ot the RecyclerView

//Initialize the ArrayList that will contain the data
        ModelFaq = new ArrayList<>();
        requestAdapter = new RequestAdapter(getActivity(), ModelFaq);
        mRecyclerView.setAdapter(requestAdapter);
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
        ModelFaq.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            RequestModel userDatabase = new RequestModel();
            userDatabase.set_id((ds.getValue(RequestModel.class)).get_id());
            userDatabase.setId((ds.getValue(RequestModel.class)).getId());
            userDatabase.setPickup((ds.getValue(RequestModel.class)).getPickup());
            userDatabase.setDrop((ds.getValue(RequestModel.class)).getDrop());
            userDatabase.setDistance((ds.getValue(RequestModel.class)).getDistance());
            userDatabase.setAmount((ds.getValue(RequestModel.class)).getAmount());
            userDatabase.setRecipient((ds.getValue(RequestModel.class)).getRecipient());
            userDatabase.setDate((ds.getValue(RequestModel.class)).getDate());
            userDatabase.setStatus((ds.getValue(RequestModel.class)).getStatus());
            userDatabase.setDesc((ds.getValue(RequestModel.class)).getDesc());
            userDatabase.setRecipient_phone((ds.getValue(RequestModel.class)).getRecipient_phone());
            userDatabase.setSender_phone((ds.getValue(RequestModel.class)).getSender_phone());
            userDatabase.setSecreteCode((ds.getValue(RequestModel.class)).getSecreteCode());
            FirebaseUser user = mAuth.getCurrentUser();
            if (prefManager.getRole().equals(Utils.ADMIN)) {
                ModelFaq.add(new RequestModel(userDatabase.get_id(), userDatabase.getId(), userDatabase.getPickup(), userDatabase.getDrop(), userDatabase.getDistance(),
                        userDatabase.getAmount(), userDatabase.getRecipient(), userDatabase.getRecipient_phone(), userDatabase.getDesc(), userDatabase.getDate(), userDatabase.getStatus(), userDatabase.getSender_phone(), userDatabase.getSecreteCode()));
            } else if (Objects.equals(userDatabase.getId(), user.getUid()) && Objects.equals(userDatabase.getStatus(), Utils.PENDING)
                    || Objects.equals(userDatabase.getId(), user.getUid()) && Objects.equals(userDatabase.getStatus(), Utils.PAID)
                    || Objects.equals(userDatabase.getId(), user.getUid()) && Objects.equals(userDatabase.getStatus(), Utils.APPROVED))
                ModelFaq.add(new RequestModel(userDatabase.get_id(), userDatabase.getId(), userDatabase.getPickup(), userDatabase.getDrop(), userDatabase.getDistance(),
                        userDatabase.getAmount(), userDatabase.getRecipient(), userDatabase.getRecipient_phone(), userDatabase.getDesc(), userDatabase.getDate(), userDatabase.getStatus(), userDatabase.getSender_phone(), userDatabase.getSecreteCode()));

        }
        //Notify the adapter of the change
        requestAdapter.notifyDataSetChanged();
    }

}