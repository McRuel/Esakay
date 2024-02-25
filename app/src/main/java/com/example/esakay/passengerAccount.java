package com.example.esakay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class passengerAccount extends AppCompatActivity {

    ImageButton home,message,account,activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_account2);


        home = findViewById(R.id.home4);
        message =  findViewById(R.id.message4);
        account = findViewById(R.id. account4);
        activity = findViewById(R.id.activity4);

        home.setOnClickListener(this::onClick);
        message.setOnClickListener(this::onClick);
        account.setOnClickListener(this::onClick);
        activity.setOnClickListener(this::onClick);
    }

    public void onClick(View v){
        if(v.getId()== R.id.home4){
            startActivity(new Intent(passengerAccount.this, PassengerScreen.class));
        }  else if (v.getId()== R.id.message4) {
            startActivity(new Intent(passengerAccount.this,Message.class));
        } else if (v.getId()== R.id.activity4) {
            startActivity(new Intent(passengerAccount.this,Activity.class));
        }else if (v.getId()== R.id.account4) {
            startActivity(new Intent(passengerAccount.this,passengerAccount.class));
        }
    }
}