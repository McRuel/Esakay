package com.example.esakay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PassengerScreen extends AppCompatActivity {

    ImageButton home,message,account,activity,transport;
    TextView user;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_screen);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        user= findViewById(R.id.userName);
        home = findViewById(R.id.home);
        message =  findViewById(R.id.message);
        account = findViewById(R.id. account);
        activity = findViewById(R.id.activity);
        transport = findViewById(R.id.imageTransport);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Retrieve user's data from Firestore
            fStore.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get user's first name
                                String fName = document.getString("fName");
                                // Display user's first name
                                user.setText("Hello " + fName +"!!");
                            }
                        }
                    });
        }

        home.setOnClickListener(this::onClick);
        message.setOnClickListener(this::onClick);
        account.setOnClickListener(this::onClick);
        activity.setOnClickListener(this::onClick);
        transport.setOnClickListener(this::onClick);
    }
    public void onClick(View v){
        if(v.getId()== R.id.home){
            startActivity(new Intent(PassengerScreen.this, PassengerScreen.class));
        }  else if (v.getId()== R.id.message) {
            startActivity(new Intent(PassengerScreen.this,Message.class));
        } else if (v.getId()== R.id.activity) {
            startActivity(new Intent(PassengerScreen.this,Activity.class));
        }else if (v.getId()== R.id.account) {
            startActivity(new Intent(PassengerScreen.this,passengerAccount.class));
        }else if (v.getId()== R.id.imageTransport) {
            startActivity(new Intent(PassengerScreen.this,Transport.class));
        }
    }

}