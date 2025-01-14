package com.example.qrattendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AbsentStudents extends AppCompatActivity {

    private DatabaseReference studentsRef, usersRef;
    private TextView textViewResult;
    int j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_students);

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        studentsRef = database.getReference("students");
        usersRef = database.getReference("users");

        textViewResult = findViewById(R.id.textViewResult);

        // Display students when activity is created
        displayStudents();
    }

    // Method to display students
    private void displayStudents() {
        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String rollNo = studentSnapshot.child("rollNo").getValue(String.class);
                    String name = studentSnapshot.child("name").getValue(String.class);
                    String parentMobile = studentSnapshot.child("parentMobile").getValue(String.class);

                    // Check if the data for the current date, roll number, and name already exists in users node
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean dataExists = false;
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String userData = userSnapshot.getValue(String.class);
                                if (userData != null) {
                                    String[] parts = userData.split(" ");
                                    if (parts.length >= 3 && parts[0].equals(rollNo) && parts[1].equals(name) && parts[3].equals(currentDate)) {
                                        // Data for the current roll number, name, and date already exists
                                        dataExists = true;
                                        break;
                                    }
                                }
                            }

                            if (!dataExists) {
                                j++;
                                // Data from students node not found in users node for the current date
                                String result = j+")\n\tRoll No: " + rollNo +
                                        "\n\tName: " + name+"\n\tParent Mobile: "+parentMobile;
                                textViewResult.append(result + "\n\n\n\n");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Failed to check data existence: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load students: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to send SMS when button is clicked
    public void send(View view) {
        // Implement sending SMS here
        // For example:
        sendSMSForAbsentStudents();
        Toast.makeText(this, "SMS sent ", Toast.LENGTH_SHORT).show();
    }

    // Method to send SMS for absent students
    private void sendSMSForAbsentStudents() {
        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String rollNo = studentSnapshot.child("rollNo").getValue(String.class);
                    String name = studentSnapshot.child("name").getValue(String.class);
                    String parentMobile = studentSnapshot.child("parentMobile").getValue(String.class);

                    // Check if the data for the current date, roll number, and name already exists in users node
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean dataExists = false;
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String userData = userSnapshot.getValue(String.class);
                                if (userData != null) {
                                    String[] parts = userData.split(" ");
                                    if (parts.length >= 3 && parts[0].equals(rollNo) && parts[1].equals(name) && parts[3].equals(currentDate)) {
                                        // Data for the current roll number, name, and date already exists
                                        dataExists = true;
                                        break;
                                    }
                                }
                            }

                            if (!dataExists) {
                                // Data from students node not found in users node for the current date
                                // Send SMS to parent mobile number
                                sendSMS(parentMobile, name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Failed to check data existence: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load students: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to send SMS
    private void sendSMS(String phoneNumber, String name) {
        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            // Send SMS
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Dear Parent, \n" + name + " was absent today in lecture";
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, send SMS
            // You can handle sending SMS here if permission is granted
        } else {
            Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
