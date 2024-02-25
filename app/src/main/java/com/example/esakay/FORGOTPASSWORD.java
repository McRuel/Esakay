package com.example.esakay;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FORGOTPASSWORD extends AppCompatActivity {

    EditText Email;
    Button sendLink;
    TextView login;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Email= findViewById(R.id.editTextEmail);
        sendLink= findViewById(R.id.sendVerificationCode);
        login=findViewById(R.id.logIn);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Gmail_Login.class);
                startActivity(intent);
            }
        });

        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString().trim();

                // Check if the email is registered in Firestore
                fStore.collection("users").whereEqualTo("email", email).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        // Email not registered
                                        Email.setError("Email not registered");
                                        Email.requestFocus();
                                    } else {
                                        // Email registered, send verification code
                                        mAuth.sendPasswordResetEmail(email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Verification code sent successfully
                                                            Toast.makeText(FORGOTPASSWORD.this, "Verification code sent to your email", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(getApplicationContext(),Gmail_Login.class);
                                                            startActivity(intent);
                                                        } else {
                                                            // Error sending verification code
                                                            Toast.makeText(FORGOTPASSWORD.this, "Failed to send verification code", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // Error querying Firestore
                                    Toast.makeText(FORGOTPASSWORD.this, "Error checking email", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }
}