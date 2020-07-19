package com.projectwork.sediexpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    public BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private NavController navController;
    private boolean exit = false;

    private FirebaseAuth auth;
    private FirebaseUser fireuser;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mref;
    private String role = "";
    private NavGraph graph;
    private NavHostFragment navHostFragment;
    private DrawerLayout drawer;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefManager = new PrefManager(this);

        auth = FirebaseAuth.getInstance();
        fireuser = auth.getCurrentUser();
        mfirebaseDatabase = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        mref = mfirebaseDatabase.getReference().child("sedi").child("users");
        mref.keepSynced(true);


        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        mAuth = FirebaseAuth.getInstance();


        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);


        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);  // Hostfragment
        NavInflater inflater = navHostFragment.getNavController().getNavInflater();
        graph = inflater.inflate(R.navigation.nav_graph);


        // NavigationView navigationView = findViewById(R.id.navigationView);
        NavigationUI.setupWithNavController(navigationView, navHostFragment.getNavController());


        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Database userDatabase = new Database();
                    userDatabase.set_id((ds.getValue(Database.class)).get_id());
                    userDatabase.setRole((ds.getValue(Database.class)).getRole());

                    if (fireuser.getUid().equals(userDatabase.get_id()))
                        role = userDatabase.getRole();
                    prefManager.setRole(role);

                    switch (role) {
                        case "agent":
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.activity_agent_drawer);
                            graph.setStartDestination(R.id.AgentFragment);
                            navHostFragment.getNavController().setGraph(graph);
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


                            break;
                        case "user":
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.activity_main_drawer);
                            graph.setStartDestination(R.id.FirstFragment);
                            navHostFragment.getNavController().setGraph(graph);
                            break;

                        case "admin":
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.activity_admin_drawer);
                            graph.setStartDestination(R.id.FirstFragment);
                            navHostFragment.getNavController().setGraph(graph);
                            break;
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        switch (role) {
//            case "agent":
//                navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//                navController.navigate(R.id.AgentFragment);
//                break;
//            case "user":
//            case "admin":
//
//                navController.navigate(R.id.FirstFragment);
//                break;
//        }
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logout) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Sedi Express Mobile");
            alertDialog.setIcon(R.mipmap.ic_launcher_round);
            // Setting Dialog Message
            alertDialog.setMessage("Are you sure you want to log out?");

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // Write your code here to invoke YES event
                    signOut();
                }
            });


            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //  menuItem.setChecked(true);
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_home:
                Toast.makeText(MainActivity.this, role, Toast.LENGTH_SHORT).show();

                switch (role) {
                    case "agent":
                        navController.navigate(R.id.AgentFragment);
                        break;
                    case "user":
                    case "admin":
                        navController.navigate(R.id.FirstFragment);
                        break;
                }
                break;

            case R.id.nav_profile:
                navController.navigate(R.id.startFragment);
                break;

            case R.id.drawer_menu:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_menu:

                navController.navigate(R.id.profileFragment);

                break;

            case R.id.nav_history:
                navController.navigate(R.id.historyFragment);
                break;

            case R.id.nav_info:
                navController.navigate(R.id.aboutFragment);
                break;
            case R.id.admin:
                navController.navigate(R.id.adminFragment);
                break;
            case R.id.agent:
                navController.navigate(R.id.agentFragment);
                break;
            case R.id.complaint:
                navController.navigate(R.id.complainFragment);
                break;

        }
        return true;

    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3000);

            }
        } else
            super.onBackPressed();
    }
}
