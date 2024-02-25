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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Gmail_Login extends AppCompatActivity {

    TextView forgotPass, Reg;
    EditText email, password;
    Button phone, Login;
    FirebaseAuth mAuth;
    //get the value of selected item of the spinner form the register.java class
    String selectedValue, userId;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_login);
        selectedValue = getIntent().getStringExtra("selectedValue");

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //fire store data base
        userId = String.valueOf(mAuth.getCurrentUser());


        phone = (Button) findViewById(R.id.phone);
        forgotPass = (TextView) findViewById(R.id.forgot);
        Reg = (TextView) findViewById(R.id.register);
        Login = findViewById(R.id.signup);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LOGIN.class);
                startActivity(intent);
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FORGOTPASSWORD.class);
                startActivity(intent);
            }
        });
        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), REGISTER.class);
                startActivity(intent);
            }
        });

// if the button is clicked
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass, mail;

                pass = password.getText().toString();
                mail = email.getText().toString();

                if (TextUtils.isEmpty(mail)) {
                    email.setError("Enter email");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(pass)) {
                    password.setError("Enter Password");
                    password.requestFocus();
                } else {
//Email password authentication from firebase
                    mAuth.signInWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (mAuth.getCurrentUser().isEmailVerified()) {

                                            // getting the user role in the fire store

                                            DocumentReference docref = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
                                            docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String userRole = documentSnapshot.getString("role");
                                                        if (userRole != null) {
                                                            if (userRole.equals("Passenger")) {
                                                                Intent intent = new Intent(Gmail_Login.this, PassengerScreen.class);
                                                                startActivity(intent);
                                                            } else if (userRole.equals("Driver")) {
                                                                Intent intent = new Intent(Gmail_Login.this, DriverScreen.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(Gmail_Login.this, "Please verify your account first", Toast.LENGTH_LONG).show();
                                        }

                                    } else {

                                        Toast.makeText(Gmail_Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}