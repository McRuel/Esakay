package com.example.esakay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Message extends AppCompatActivity {
    ImageButton home,message,account,activity,back,search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home3);
        message =  findViewById(R.id.message3);
        account = findViewById(R.id. account3);
        activity = findViewById(R.id.activity3);
        search = findViewById(R.id.message_search_btn);

        home.setOnClickListener(this::onClick);
        activity.setOnClickListener(this::onClick);
        message.setOnClickListener(this::onClick);
        account.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);
        search.setOnClickListener(this::onClick);

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
        } else if (v.getId()==R.id.back) {
            finish();
        }
    }
}