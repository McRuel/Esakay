package com.example.esakay;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class LOGIN extends AppCompatActivity {

    EditText phoneInput;
    String defaultCountryCode = "+63";

    Button sendOtpBtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

      phoneInput = (EditText) findViewById(R.id.mobile);
        sendOtpBtn = (Button) findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();


        progressBar.setVisibility(View.GONE);
        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone;

                phone = phoneInput.getText().toString();

              if (TextUtils.isEmpty(phone)) {
                    phoneInput.setError("Enter Mobile Number");
                     phoneInput.requestFocus();
                    return;
                } else if (phone.startsWith("0")) {
                  phoneInput.setError("Phone number should start with +63");
                  phoneInput.requestFocus();
                  return;
              } else {
                  Intent intent = new Intent(getApplicationContext(),SmsVerification.class);
                  intent.putExtra("mNum", phone);
                  startActivity(intent);
              }
            }
        });



    }
}