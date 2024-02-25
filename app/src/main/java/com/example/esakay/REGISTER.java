package com.example.esakay;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class REGISTER extends AppCompatActivity {

    EditText fistName, mobileNumber, password, email,confirmPass;
    Button createAccount;
    TextView login;
    Spinner spinner;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        fistName = (EditText) findViewById(R.id.first_name);
        mobileNumber = (EditText) findViewById(R.id.Mobile_number);
        password = (EditText) findViewById(R.id.password1);
        confirmPass= findViewById(R.id.confirmPass);
        createAccount = (Button) findViewById(R.id.create_btn);
        email = (EditText) findViewById(R.id.Email);
        spinner = (Spinner) findViewById(R.id.spinnerMain);
        login = (TextView) findViewById(R.id.LogIn);
        String mNumber = mobileNumber.getText().toString();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(REGISTER.this, Gmail_Login.class);
                intent.putExtra("selectedValue", spinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        //spinner value choices for the users
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ((TextView) adapterView.getSelectedView()).setError("Please select your role");
                // Request focus on the spinner
                adapterView.requestFocus();
            }
        });
        ArrayList<String> spinnerList = new ArrayList<>();
        spinnerList.add("Passenger");
        spinnerList.add("Driver");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spinnerList);
        spinner.setAdapter(spinnerAdapter);


        //create account codes
        createAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String fName, mNumber, pass, mail, spin,confirm;
                mNumber = mobileNumber.getText().toString();
                fName = fistName.getText().toString();
                pass = password.getText().toString();
                confirm=confirmPass.getText().toString();
                mail = email.getText().toString();
                spin = spinner.getSelectedItem().toString();

                //toast message if the edittext values are empty
                if (TextUtils.isEmpty(fName)) {
                    fistName.setError("Enter Your Full Name");
                    fistName.requestFocus();
                } else if (TextUtils.isEmpty(mNumber)) {
                    mobileNumber.setError("Enter Mobile Number");
                    mobileNumber.requestFocus();

                }else if(!isValidMobile(mNumber)){
                    mobileNumber.setError("Enter a valid mobile number");
                    mobileNumber.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(mail)) {
                    email.setError("Enter Your Email Address");
                    email.requestFocus();
                }else if(!isValidEmail(mail)){
                    email.setError("Enter a valid email address");
                    email.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(pass)) {
                    password.setError("Enter password");
                    password.requestFocus();
                } else if (TextUtils.isEmpty(confirm)) {
                    confirmPass.setError("Enter Your Password");
                    confirmPass.requestFocus();
                }else if (!pass.equals(confirm)) {
                    confirmPass.setError("Passwords do not match");
                    confirmPass.requestFocus();
                    return;
                }else{
                    //for email and password authentication
                    mAuth.createUserWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //getting the user id after successful authentication
                                        userID = mAuth.getCurrentUser().getUid();
                                        // send email verification
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //storing the input data from the data base
                                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("fName", fName);
                                                    user.put("mNum", mNumber);
                                                    user.put("email", mail);
                                                    user.put("pass", pass);
                                                    user.put("role", spin);
                                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "onSuccess: User profile is created for " + userID);
                                                            Toast.makeText(REGISTER.this, "Authentication success. Please verify your account",
                                                                    Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(getApplicationContext(), Gmail_Login.class);
                                                            startActivity(intent);
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(REGISTER.this, "Authentication failed.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                }
            }
            private boolean isValidMobile(String mNumber) {
               String regex ="\\d{11}$";
               return mNumber.matches(regex);
            }
            private boolean isValidEmail(String mail) {
                return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
            }
        });

    }
}
