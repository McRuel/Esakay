package com.example.esakay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Activity extends AppCompatActivity {

    ImageButton home,message,account,activity,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        back= findViewById(R.id.back);
        home = findViewById(R.id.home);
        message =  findViewById(R.id.message);
        account = findViewById(R.id. account);
        activity = findViewById(R.id.activity);

        back.setOnClickListener(this::onClick);
        home.setOnClickListener(this::onClick);
        message.setOnClickListener(this::onClick);
        account.setOnClickListener(this::onClick);
        activity.setOnClickListener(this::onClick);
    }
    public void onClick(View v){
        if(v.getId()== R.id.home){
            startActivity(new Intent(Activity.this, PassengerScreen.class));
        }  else if (v.getId()== R.id.message) {
            startActivity(new Intent(Activity.this,Message.class));
        } else if (v.getId()== R.id.activity) {
            startActivity(new Intent(Activity.this,Activity.class));
        }else if (v.getId()== R.id.account) {
            startActivity(new Intent(Activity.this,passengerAccount.class));
        }else if (v.getId()== R.id.back) {
            finish();
        }
    }
}