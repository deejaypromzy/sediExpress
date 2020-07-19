package com.projectwork.sediexpress;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FAQFragment extends Fragment {
    private ProgressBar progressbar;
    private ArrayList<Model> ModelFaq;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private FirebaseUser fireuser;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mref;
    private FaqAdapter faqAdapter;

    public static FAQFragment newInstance() {
        return new FAQFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Frequently Asked Questions");

        return inflater.inflate(R.layout.fragment_faq, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        // bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
        auth = FirebaseAuth.getInstance();
        fireuser = auth.getCurrentUser();
        mfirebaseDatabase = FirebaseDatabase.getInstance();


        mref = mfirebaseDatabase.getReference().child("sedi").child("faq");
        mref.keepSynced(true);

        progressbar = view.findViewById(R.id.progressbar);

        final RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);

//Set the Layout Manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//Initialize the adapter and set it ot the RecyclerView

//Initialize the ArrayList that will contain the data
        ModelFaq = new ArrayList<>();
        faqAdapter = new FaqAdapter(getActivity(), ModelFaq);
        mRecyclerView.setAdapter(faqAdapter);
        new CountDownTimer(200, 200) {
            public void onTick(long ms) {
                progressbar.setVisibility(View.VISIBLE);
            }

            public void onFinish() {

                if (mref != null) {
                    mref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            showData(dataSnapshot);
                            progressbar.setVisibility(View.GONE);

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
            Model userDatabase = new Model();
            userDatabase.setName((ds.getValue(Model.class)).getName());
            userDatabase.setTitle((ds.getValue(Model.class)).getTitle());


            ModelFaq.add(new Model(userDatabase.getName(), userDatabase.getTitle()));

        }
        //Notify the adapter of the change
        faqAdapter.notifyDataSetChanged();
    }

}
