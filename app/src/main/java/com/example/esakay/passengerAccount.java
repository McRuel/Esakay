package com.example.esakay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

public class passengerAccount extends AppCompatActivity {

    ImageButton home,message,account,activity,back;
    ImageView image;
    FloatingActionButton floatingActionButton;

    TextView userFullName, userEmail,name,contact,email,userRole;

    // for the retrieval of the name and email account of the user
    private TextView fullNameTextView;
    private TextView emailTextView;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    private static final String PROFILE_IMAGE_KEY = "profileImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_account2);

        image = findViewById(R.id.imageInsert);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        // for the getting the user name in the firestore;
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userFullName = findViewById(R.id.passengerFullName);
        userEmail = findViewById(R.id.passengerEmailAccount);
        name = findViewById(R.id.fullName);
        contact= findViewById(R.id.mobile_Number);
        email = findViewById(R.id.gmailAccount);
        userRole = findViewById(R.id.role);
        back = findViewById(R.id.back);
        home = findViewById(R.id.home4);
        message =  findViewById(R.id.message4);
        account = findViewById(R.id. account4);
        activity = findViewById(R.id.activity4);

        home.setOnClickListener(this::onClick);
        activity.setOnClickListener(this::onClick);
        message.setOnClickListener(this::onClick);
        account.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccessPhotosDialog();
            }
        });

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
                                String mail = document.getString("email");
                                String num = document.getString("mNum");
                                String role = document.getString("role");
                                // Display user's first name and email
                                userFullName.setText(fName);
                                userEmail.setText(mail);
                                name.setText(fName);
                                contact.setText(num);
                                email.setText(mail);
                                userRole.setText(role);

                        // Check if profile image URI is available
                                if (document.contains(PROFILE_IMAGE_KEY)) {
                                    String profileImageUri = document.getString(PROFILE_IMAGE_KEY);
                                    // Load profile image using Glide
                                    Glide.with(this)
                                            .load(Uri.parse(profileImageUri))
                                            .circleCrop()
                                            .into(image);
                                }
                            }
                        }
                    });
        }

    }

    public void onClick(View v){
        if(v.getId()== R.id.home4){
            startActivity(new Intent(passengerAccount.this, PassengerScreen.class));
        } else if (v.getId()== R.id.message4) {
            startActivity(new Intent(passengerAccount.this,Message.class));
        }  else if (v.getId()== R.id.activity4) {
            startActivity(new Intent(passengerAccount.this,Activity.class));
        }else if (v.getId()== R.id.account4) {
            startActivity(new Intent(passengerAccount.this,passengerAccount.class));
        } else if (v.getId()==R.id.back) {
            finish();
        }
    }
    private void showAccessPhotosDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Allow the app to access your photos?")
                .setPositiveButton("Always Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Always Allow button, start image picker
                        startImagePicker();
                    }
                })
                .setNeutralButton("Allow While Using the App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Allow While Using the App button, start image picker
                        startImagePicker();
                    }
                })
                .setNegativeButton("Do Not Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Do Not Allow button, do nothing
                        dialog.dismiss();
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startImagePicker() {
        ImagePicker.with(passengerAccount.this)
                .cropSquare()
                .compress(1024)          // Final image size will be less than 1 MB (Optional)
                .maxResultSize(200, 200) // Final image resolution will be less than 1080 x 1080 (Optional)
                .start();
    }


    //extension of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK&& data !=null){
            Uri uri = data.getData();
            // Save the URI to Firestore
            Log.d("URI", "Selected image URI: " + uri.toString());
            saveProfileImageUri(uri);
            // Load image into ImageView with circular cropping using Glide
            Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(image);

        }

    }
    private void saveProfileImageUri(Uri imageUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Save the image URI to Firestore
            fStore.collection("users").document(currentUser.getUid())
                    .update(PROFILE_IMAGE_KEY, imageUri.toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(passengerAccount.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(passengerAccount.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Failed to update profile image", e);
                        }
                    });
        }
    }

}

