package com.projectwork.sediexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class AgentFragment extends Fragment {

    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Agent Portal");
        return inflater.inflate(R.layout.fragment_agent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFragment(new AgentPendingFragment());


        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        // bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);


        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        setFragment(new AgentPendingFragment());
                        break;
                    case 1:
                        setFragment(new AgentOngoingFragment());
                        break;
                    case 2:
                        setFragment(new AgentCompleteFragment());
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.home_container, fragment);
        t.commit();
    }
}
