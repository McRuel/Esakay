package com.example.esakay;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    EditText mobile, password;
    TextView forgot, register;

    Button signUp;
    FirebaseAuth mAuth;
    String verificationId;
    String selectedValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobile = (EditText) findViewById(R.id.mobile);
        password = (EditText) findViewById(R.id.password);
        forgot = (TextView) findViewById(R.id.forgot);
        register = (TextView) findViewById(R.id.register);
        signUp = (Button) findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();
        selectedValue = getIntent().getStringExtra("selectedValue");


        //forgot Onclick listener for the forgot activity
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FORGOTPASSWORD.class);
                startActivity(intent);
            }
        });
        //register Onclick listener for the register activity
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), REGISTER.class);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass, phone;

                pass = password.getText().toString();
                phone = mobile.getText().toString();

                if (TextUtils.isEmpty(phone)) {
                    mobile.setError("Enter Mobile Number");
                    mobile.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(pass)) {
                    password.setError("Enter Mobile Number");
                    password.requestFocus();
                    return;
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference usersRef = db.collection("users");

                    Query query = usersRef.whereEqualTo("mNum", phone).whereEqualTo("pass", pass);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Get user role
                                    String userRole = document.getString("role");
                                    // Navigate to appropriate activity based on user role
                                    if ("Passenger".equals(userRole)) {
                                        // Start PassengerActivity
                                        Intent intent = new Intent(LOGIN.this, PassengerScreen.class);
                                        startActivity(intent);
                                        finish(); // Finish current activity to prevent user from going back to login screen
                                    } else if ("Driver".equals(userRole)) {
                                        // Start DriverActivity
                                        Intent intent = new Intent(LOGIN.this, DriverScreen.class);
                                        startActivity(intent);
                                        finish(); // Finish current activity to prevent user from going back to login screen
                                    } else {
                                        // Handle other roles if needed
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


                }
            }
        });

    }
}