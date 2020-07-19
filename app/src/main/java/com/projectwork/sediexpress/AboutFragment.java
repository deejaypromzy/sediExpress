package com.projectwork.sediexpress;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AboutFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private EditText subject, email, username, body;
    private PrefManager prefManager;
    private Button btnsend;
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private NavController navController;


    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Contact Us");

        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        // bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.nav_info).setChecked(true);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(getActivity());
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        btnsend = view.findViewById(R.id.btnsend);
        subject = view.findViewById(R.id.subject);
        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        body = view.findViewById(R.id.body);

        email.setText(prefManager.getUserEmail());
        username.setText(prefManager.getUserName());

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText().toString())) {
                    subject.setError("Subject cant be empty");
                    subject.requestFocus();
                    Toast.makeText(getActivity(), "Subject cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    subject.setError(null);
                }
                if (TextUtils.isEmpty(body.getText().toString())) {
                    body.setError("Message cant be empty");
                    body.requestFocus();
                    Toast.makeText(getActivity(), "Message cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    body.setError(null);
                }


                sendInfo();

            }
        });

    }

    private void sendInfo() {
        Database dbUser = new Database(
                prefManager.getUserName(),
                prefManager.getUserPhone(),
                prefManager.getUserEmail(),
                body.getText().toString(),
                subject.getText().toString()
        );
        FirebaseUser user = mAuth.getCurrentUser();
        mref.child("sedi").child("comments").child(user.getUid()).setValue(dbUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Comments Sent Successfully!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.FirstFragment);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Comments Sending Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
