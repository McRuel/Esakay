package com.example.esakay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Message extends AppCompatActivity {
    ImageButton home,message,account,activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        home = findViewById(R.id.home3);
        message =  findViewById(R.id.message3);
        account = findViewById(R.id. account3);
        activity = findViewById(R.id.activity3);

        home.setOnClickListener(this::onClick);
        activity.setOnClickListener(this::onClick);
        message.setOnClickListener(this::onClick);
        account.setOnClickListener(this::onClick);

    }
    public void onClick(View v){
        if(v.getId()== R.id.home3){
            startActivity(new Intent(Message.this, PassengerScreen.class));
        } else if (v.getId()== R.id.message3) {
            startActivity(new Intent(Message.this,Message.class));
        }  else if (v.getId()== R.id.activity3) {
            startActivity(new Intent(Message.this,Activity.class));
        }else if (v.getId()== R.id.account3) {
            startActivity(new Intent(Message.this,passengerAccount.class));
        }
    }
}