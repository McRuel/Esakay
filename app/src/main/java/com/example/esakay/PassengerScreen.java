package com.example.esakay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class PassengerScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    // for the navigation side bar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    ImageButton home, message, account, activity, transport, menu;
    TextView user;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 2;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_screen);

        //

        // for the getting the user name in the firestore;
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        menu = findViewById(R.id.drawerMenu);
        drawerLayout = findViewById(R.id.drawer_Layout);
        navigationView = findViewById(R.id.nav_view);


        user = findViewById(R.id.userName);
        home = findViewById(R.id.home);
        message = findViewById(R.id.message);
        account = findViewById(R.id.account);
        activity = findViewById(R.id.activity);
        transport = findViewById(R.id.imageTransport);

        // getting the current location of the user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
//extension of the navigation side bar
        navigationView.bringToFront();
        navigationView.setItemBackground(ContextCompat.getDrawable(this, R.drawable.nav_item_bg_selector));
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


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
                                // Display user's first name
                                user.setText("Hello " + fName + "!!");

                                // Display user's first name and email
                                TextView nameTextView = findViewById(R.id.name);
                                TextView emailTextView = findViewById(R.id.gmail);
                                nameTextView.setText(fName);
                                emailTextView.setText(mail);
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

    // for the get current location
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.passengerMap);
                    mapFragment.getMapAsync(PassengerScreen.this);
                }
            }
        });

    }
// for the navigation view
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.myCurrentLocation:
                Toast.makeText( this,"Current location Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rideHistory:
                Toast.makeText( this,"History Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rateRide:
                Toast.makeText( this,"Rate Ride Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.savePlaces:
                Toast.makeText( this,"Saved Places Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.myMap:
                startActivity(new Intent(PassengerScreen.this, MyMap.class));
                break;
            case R.id.settings:
                Toast.makeText( this,"Settings Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText( this,"Share with friends Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rateUs:
                Toast.makeText( this,"Rate Us Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logoutButton:
                showLogoutDialog();
                break;
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        goToGmailSignUpActivity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button
                        dialog.dismiss(); // Dismiss the dialog and stay on the same activity
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void goToGmailSignUpActivity() {
        Intent intent = new Intent(PassengerScreen.this, Gmail_Login.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it when pressing back button
    }
    // for the back button
    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }

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

// maps implementation and setting the latitude and longitude of the country philippines
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap= googleMap;


        LatLng philippines = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude() );
        myMap.addMarker(new MarkerOptions().position(philippines).title("My Location"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(philippines));

        // Reverse geocoding to get address details
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder addressDetails = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressDetails.append(address.getAddressLine(i)).append("\n");
                }
                // Set the address details in the TextView
                TextView locationDetailsTextView = findViewById(R.id.locationDetails);
                locationDetailsTextView.setText(addressDetails.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Location permission is denied, please allow the permission ",Toast.LENGTH_LONG).show();
            }
        }
    }
}