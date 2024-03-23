package com.example.esakay;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SmsVerification extends AppCompatActivity {
    EditText otpInput;
    Button nextBtn;
    FirebaseAuth mAuth;
    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    TextView resendOtpTextView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        phoneNumber = getIntent().getStringExtra("mNum");
        otpInput = findViewById(R.id.code);
        nextBtn = findViewById(R.id.btnContinue);
        resendOtpTextView = findViewById(R.id.resendOTP);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        sendOtp(phoneNumber, false);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredOtp = otpInput.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
                setInProgress(true);
            }
        });

        resendOtpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp(phoneNumber, true);
            }
        });
    }

    // for sending otp to the phone number
    void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SmsVerification.this, "OTP verification Failed", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(SmsVerification.this, "OTP sent Successfully", Toast.LENGTH_SHORT).show();
                                setInProgress(false);

                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            // Sign-in successful, proceed to fetch user role
                            fetchUserRole(phoneNumber);
                        } else {
                            // Sign-in failed, display an error message
                            Toast.makeText(SmsVerification.this, "OTP verification  failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    void fetchUserRole(String phoneNumber) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        // Query Firestore for the user role associated with the phone number
        Query query = usersRef.whereEqualTo("mNum", phoneNumber);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userRole = document.getString("role");
                        // Start the appropriate activity based on the user role
                        if ("Passenger".equals(userRole)) {
                            startActivity(new Intent(SmsVerification.this, PassengerScreen.class));
                        }
                        // Finish the current activity
                        finish();
                        return; // Exit the loop after finding the user role
                    }
                } else {
                    Log.e(TAG, "Error fetching user role: ", task.getException());
                    Toast.makeText(SmsVerification.this, "Error fetching user role", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // for the resend timer clock
    void startResendTimer(){
    resendOtpTextView. setEnabled(false);
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeoutSeconds--; // Decrement remaining time
                    if (timeoutSeconds > 0) {
                        resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    } else {
                        timer.cancel(); // Stop the timer
                        resendOtpTextView.setEnabled(true); // Enable resend button
                        resendOtpTextView.setText("Resend OTP"); // Reset button text
                    }
                }
            });
        }
    },0,1000);
    }

}



