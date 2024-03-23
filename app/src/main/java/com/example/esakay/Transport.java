package com.example.esakay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Calendar;
import java.util.Locale;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Transport extends AppCompatActivity implements OnMapReadyCallback{

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton;
    private Button timeButton;
    ImageButton back;
    private GoogleMap myMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        back = findViewById(R.id.back);
        timeButton = findViewById(R.id.timePicker);
        dateButton = findViewById(R.id.datePicker);
        initDatePicker(); // Initialize the DatePickerDialog
        initTimePicker(); // Initialize the TimePickerDialog
        dateButton.setText(getTodaysDate());
        timeButton.setText(getCurrentTime());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.transportMap);
        mapFragment.getMapAsync( this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    //displaying the current time
    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return makeTimeString(hour, minute);
    }
    // displaying the current date
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }
    private String getMonthFormat(int month) {
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        } else {
            return "INVALID MONTH";
        }
    }

// for the date picker
private void initDatePicker() {
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        }

    };

    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);

    Calendar minDate = Calendar.getInstance(); // Minimum date is today
    minDate.set(Calendar.HOUR_OF_DAY, 0);
    minDate.set(Calendar.MINUTE, 0);
    minDate.set(Calendar.SECOND, 0);
    minDate.set(Calendar.MILLISECOND, 0);

    int style = AlertDialog.THEME_HOLO_LIGHT;
    datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis()); // Set minimum date to today
    datePickerDialog.getDatePicker().setCalendarViewShown(false); // Hide calendar view
}

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
    // for the time picker code
    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String time = makeTimeString(hourOfDay, minute);
                timeButton.setText(time);
            }
        };

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        timePickerDialog = new TimePickerDialog(this,style, timeSetListener, hour, minute, false); // false for 12-hour format
    }

    public void openTimePicker(View view) {
        timePickerDialog.show();
    }

    private String makeTimeString(int hour, int minute) {
        String am_pm = (hour < 12) ? "AM" : "PM";
        hour = (hour == 0 || hour == 12) ? 12 : hour % 12;
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, am_pm);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap= googleMap;

        LatLng philippines = new LatLng(17.2279,120.5740);
        myMap.addMarker(new MarkerOptions().position(philippines).title("Ilocos Sur"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(philippines));
    }
}