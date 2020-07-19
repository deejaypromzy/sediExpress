package com.projectwork.sediexpress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    // UI references.
    private EditText mEmailView, mPasswordView;
    private Button signinBtn;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        prefManager = new PrefManager(this);

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        signinBtn = findViewById(R.id.signinBtn);

        setupFirebaseAuth();
        hideSoftKeyboard();

    }


    private void signIn(final String email, String password) {


        if (!validateForm()) {
            return;
        }
        //check if the fields are filled out
        if (isEmpty(email)
                || isEmpty(password)) {
            Log.d(TAG, "onClick: attempting to authenticate.");
            progressDialog = ProgressDialog.show(Login.this, "Login", "Please Wait", false, false);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,
                    password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(Login.this, "Successful", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login.this, "Verify your email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Login.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmpty(String string) {
        return !string.equals("");
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean validateForm() {
        String email = mEmailView.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailView.getText().toString().trim()).matches()) {
            mEmailView.setError("Enter correct email.");
            return false;
        } else {
            mEmailView.setError(null);
        }


        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password can't be empty.");
            return false;
        } else {
            mPasswordView.setError(null);
        }
        return true;
    }

    public void GoToSignup(View view) {
        startActivity(new Intent(Login.this, Signup.class));
    }


    public void Signinmeton(View view) {
        view.requestFocusFromTouch();
        if (Utils.haveNetworkConnection(this))
            signIn(mEmailView.getText().toString().trim(), mPasswordView.getText().toString().trim());
        else
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //check if email is verified
                    // if(user.isEmailVerified()){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // Toast.makeText(Login.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    if (user.isEmailVerified()) {

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    //}
//                    else{
//                        Toast.makeText(Login.this, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
//                        FirebaseAuth.getInstance().signOut();
//                    }

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void resetPass(View view) {
        view.requestFocusFromTouch();
        if (Utils.haveNetworkConnection(this)) {
            if (emailChk()) {
                sendPasswordResetEmail(mEmailView.getText().toString().trim());
            }
        } else {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }


    }


    private boolean emailChk() {
        String email = mEmailView.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailView.getText().toString().trim()).matches()) {
            mEmailView.setError("Enter correct email.");
            return false;
        } else {
            mEmailView.setError(null);
        }
        return true;
    }


    public void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Password Reset Email sent.");
//                            Toast.makeText(mContext, "Password Reset Link Sent to Email",
//                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(Login.this, "Password Reset Link Sent to Email", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "onComplete: No user associated with that email.");
                            Toast.makeText(Login.this, "No User is Associated with that Email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Error Occurred Try Again!", Toast.LENGTH_SHORT).show();
            }
        });


    }

}

