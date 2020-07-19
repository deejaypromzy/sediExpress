package com.projectwork.sediexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private EditText tvPhone, tvEmail, tvUser, tvLocation;
    private PrefManager prefManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("User Profile");
        prefManager = new PrefManager(getActivity());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        // bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.nav_menu).setChecked(true);

        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUser = view.findViewById(R.id.tvUser);
        tvLocation = view.findViewById(R.id.tvLocation);

        tvPhone.setText(prefManager.getUserPhone());
        tvEmail.setText(prefManager.getUserEmail());
        tvUser.setText(prefManager.getUserName());
        tvLocation.setText(prefManager.getUserLocation());
    }
}
