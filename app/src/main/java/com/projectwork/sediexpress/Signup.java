package com.projectwork.sediexpress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    final int PERMS_REQUEST_CODE = 4444;
    int flag = 0;
    private EditText name, etEmail, etphone;
    private EditText cpassword, password;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private String UserType;
    private ScrollView user;
    private LinearLayout linear;
    private ProgressDialog progressDialog;
    private PrefManager prefManager;

    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        prefManager = new PrefManager(this);


        // Views
        name = findViewById(R.id.lastname);
        etEmail = findViewById(R.id.email);
        etphone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);
        // [END initialize_auth]*/


//        new CountDownTimer(1500, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//
//                if(noPermissions())
//                    requestPermission();
//
//            }
//
//        }.start();


    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        boolean allowed = true;
//        switch (requestCode) {
//            case PERMS_REQUEST_CODE:
//                for (int res : grantResults)
//                    allowed = allowed && res == PackageManager.PERMISSION_GRANTED;
//                break;
//            default:
//                allowed = false;
//                break;
//
//
//        }
//        if (allowed)
//            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
//        else {
//            Toast.makeText(getApplicationContext(), " Permission Denied!!!", Toast.LENGTH_SHORT).show();
//
//        }
//            /*if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//                Toast.makeText(getApplicationContext(), " Storage Warning not permitted...", Toast.LENGTH_SHORT).show();
//            else if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS))
//                Toast.makeText(getApplicationContext(), "SMS Warning not permitted...", Toast.LENGTH_SHORT).show(); */
//    }
//


//    // [START on_start_check_user]
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        progressDialog = ProgressDialog.show(this, "Creating Account", "Please Wait", false, false);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Sign in is successful
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendEmailVerification();
                                                Log.d("success", "User profile updated.");
                                            }
                                        }
                                    });

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Signup.this, " Failed to Sign Up.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        progressDialog.dismiss();
                    }
                });
        // [END create_user_with_email]
    }

    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]

                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Signup.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
    }

    private boolean validateForm() {
        String mylname = name.getText().toString();
        if (TextUtils.isEmpty(mylname)) {
            name.setError("Name can't be empty.");
            name.requestFocus();
            return false;
        } else {
            name.setError(null);
        }
        String myemail = etEmail.getText().toString();
        if (TextUtils.isEmpty(myemail)) {
            etEmail.setError("Email can't be empty..");
            etEmail.requestFocus();
            return false;
        } else {
            etEmail.setError(null);
        }
        String mphone = etphone.getText().toString();
        if (TextUtils.isEmpty(mphone)) {
            etphone.setError("Phone # can't be empty..");
            etphone.requestFocus();
            return false;
        } else if (mphone.length() < 10) {
            etphone.setError("Enter correct phone #.");
            etphone.requestFocus();
            return false;
        } else {
            etphone.setError(null);
        }
        String mypassword = password.getText().toString();
        if (TextUtils.isEmpty(mypassword)) {
            password.setError("Password can't be empty..");
            password.requestFocus();
            return false;
        } else if (mypassword.length() < 6) {
            password.setError("Password must be more than 4 characters");
            password.requestFocus();
            return false;
        } else {
            password.setError(null);
        }
        String mycpassword = cpassword.getText().toString();
        if (TextUtils.isEmpty(mycpassword)) {
            cpassword.setError("Cornfirm password");
            name.requestFocus();
            return false;
        } else if (!mypassword.equals(mycpassword)) {
            cpassword.setError("Password do not match");
            cpassword.requestFocus();
            return false;
        } else {
            cpassword.setError(null);
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
        progressDialog.dismiss();
        if (user != null) {
            onBackPressed();
            Database dbUser = new Database(
                    name.getText().toString(),
                    etphone.getText().toString(),
                    user.getUid(),
                    "user"
            );

            prefManager.setUserPhone(etphone.getText().toString());
            prefManager.setUserName(name.getText().toString());
            prefManager.setUserEmail(etEmail.getText().toString());


            mref.child("sedi").child("users").child(user.getUid()).setValue(dbUser);
            startActivity(new Intent(Signup.this, Login.class));
        }
    }

    public void CreateAccount(View view) {
        if (Utils.haveNetworkConnection(this))
            createAccount(etEmail.getText().toString(), password.getText().toString());
        else
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();

        // String sepnames[] = nam1.split(":");
        //Toast.makeText(getApplicationContext(),sepnames[0]+" and "+sepnames[1],Toast.LENGTH_LONG).show();
    }

    public void GoToLogin(View view) {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

