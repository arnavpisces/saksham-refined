package com.zuccessful.trueharmony.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.services.AccelerometerSensorService;
import com.zuccessful.trueharmony.services.CallService;
import com.zuccessful.trueharmony.services.GyroscopeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9220;
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String PREF_PID = "patient_id";
    private String patientId;
    private SakshamApp app;
    private FirebaseAuth mAuth;
    private EditText patientIdEt, passworEt;
    private ProgressDialog progressDialog;
    private AnimationDrawable animationDrawable;
    private ConstraintLayout constraintLayout;
    private SharedPreferences preferences;
    Button signInButton;
//    private Button registerButton;
    private TextView registerButton;
    public static final int RequestPermissionCode = 7;



    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = this.getSharedPreferences(PREF_PID, MODE_PRIVATE);
        patientId = preferences.getString(PREF_PID, null);



        app = SakshamApp.getInstance();

        app.setPatientID(patientId);

        constraintLayout = findViewById(R.id.linear_layout);
//        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
//        animationDrawable.setEnterFadeDuration(5000);
//        animationDrawable.setExitFadeDuration(2000);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");



        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        registerButton = (TextView)findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        patientIdEt = findViewById(R.id.patient_id_et);

        passworEt = findViewById(R.id.password_et);

        if (patientId != null) {
            signIn();
            return;
        }
    }

    private void signIn() {
//        Log.d(TAG, "Sign In Called");
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//
//        startActivityForResult(signInIntent, RC_SIGN_IN);

        updateUI(null);
    }

//    private void signOut() {
//        Log.d(TAG, "Sign Out Called");
//        mAuth.signOut();
//
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                progressDialog.dismiss();
//                updateUI(null);
//            }
//        });
//    }

//    private void revokeAccess() {
//        Log.d(TAG, "Revoke Access Called");
//        mAuth.signOut();
//
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                updateUI(null);
//            }
//        });
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            progressDialog.show();
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//                updateUI(null);
//            }
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        animationDrawable.start();
    }

    private void updateUI(FirebaseUser currentUser) {
        progressDialog.dismiss();
        Intent accintent = new Intent(this, AccelerometerSensorService.class);
        Log.e("AccelerometerService", "Acc Service Started ");
        startService(accintent);

        Intent gyrointent = new Intent(this, GyroscopeService.class);
        Log.e("GyroService", "Gyro Service Started ");
        startService(gyrointent);
        Log.d(TAG, patientId);




//        Intent callintent = new Intent(this, CallService.class);
//        Log.e("CallService", "Call Service Started ");
//        startService(callintent);
//        Log.d(TAG, patientId);



        if (
//                currentUser != null &&
                patientId != null) {
            Patient p = new Patient(patientId, patientId, patientId, "Caregiver 1");
            app.getAppUser(p);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            final FirebaseUser user = mAuth.getCurrentUser();
//                            if (user != null && patientId != null && user.getEmail() != null) {
//                                app.getFirebaseDatabaseInstance().collection("authorization")
//                                        .document(patientId)
//                                        .get()
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                if (documentSnapshot.exists()) {
//                                                    String patientMail = (String) documentSnapshot.get("patient");
//                                                    Log.d(TAG, documentSnapshot.getData().toString() + "\n\n" + patientMail);
//                                                    if (patientMail.equalsIgnoreCase(
//                                                            user.getEmail())) {
//                                                        updateUI(user);
//                                                    } else {
//                                                        Toast.makeText(LoginActivity.this, "Not authorized to access this information!", Toast.LENGTH_SHORT).show();
//                                                        signOut();
//                                                        updateUI(null);
//                                                    }
//                                                } else {
//                                                    Toast.makeText(LoginActivity.this, "Patient ID not found!!!", Toast.LENGTH_SHORT).show();
//                                                    signOut();
//                                                    updateUI(null);
//                                                }
//                                            }
//                                        });
////                                updateUI(user);
//                            }
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
//                            signOut();
//                        }
//                    }
//                });
//    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.sign_in_button) {

            final String password = passworEt.getText().toString();
            patientId = patientIdEt.getText().toString().toUpperCase();
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(patientId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        if(CheckingPermissionIsEnabledOrNot())
                        {
                            Toast.makeText(getApplicationContext(), "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
                            preferences.edit().putString(PREF_PID, patientId).apply();
                            app.setPatientID(patientId);
                            Toast.makeText(LoginActivity.this,"Signed In",Toast.LENGTH_LONG).show();
                            signIn();
                        }

                        // If, If permission is not enabled then else condition will execute.
                        else {

                            //Calling method to enable permission.
                            RequestMultiplePermission();

                        }

                    }
                }
            });

        }
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        READ_CALL_LOG,
                        READ_SMS,
                        RECEIVE_SMS
                }, RequestPermissionCode);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean CallLogPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadSMSPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean ReceiveSMSPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && CallLogPermission && ReadSMSPermission && ReceiveSMSPermission) {

//                        Toast.makeText(this, "All Permission Granted", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
                        preferences.edit().putString(PREF_PID, patientId).apply();
                        app.setPatientID(patientId);
                        Toast.makeText(LoginActivity.this,"Signed In",Toast.LENGTH_LONG).show();
                        signIn();
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    // Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED;

    }


}