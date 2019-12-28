package com.zuccessful.trueharmony.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;

public class RegisterActivity extends AppCompatActivity {

    private Button registerSubmit;
    private SakshamApp app;
    private EditText passText;
    private EditText conpassText;
    private EditText emailText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String TAG = "REGISTERTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        Log.d(TAG, "onCreate: reached activity register");
        mAuth = FirebaseAuth.getInstance();
        emailText = (EditText) findViewById(R.id.editText_name_register);
        passText = (EditText) findViewById(R.id.editText_password_register);
        conpassText = (EditText) findViewById(R.id.editText_confirmPassword_register);
        registerSubmit = (Button) findViewById(R.id.button_register_submit);
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register()
    {

        String email = emailText.getText().toString().trim();
        String pass = passText.getText().toString().trim();
        String confirmPass  = conpassText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass))
        {
            Toast.makeText(RegisterActivity.this,"Field empty",Toast.LENGTH_LONG).show();
        }

        else if (!pass.equals(confirmPass))
        {
            Toast.makeText(RegisterActivity.this,"Passwords don't match",Toast.LENGTH_LONG).show();
        }

        else
        {
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        /*Map<String, Object> docData = new HashMap<>();
                        docData.put("alarmIds", Arrays.asList(0,1));
                        docData.put("id",0);
                        docData.put("name","Wake Up");
                        docData.put("reminders","7:00");
                        db.collection("alarms/").document(app.getAppUser(null).getId()).collection("daily_routine").document("Wake Up")
                                .set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: "+"Document Successful");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onSuccess: "+"Document Unsuccessful");
                            }
                        });*/

                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(i);
                        Toast.makeText(RegisterActivity.this,"Registered",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    }
}

