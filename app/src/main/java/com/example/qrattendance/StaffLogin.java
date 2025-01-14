package com.example.qrattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaffLogin extends AppCompatActivity {
    ImageButton btnScanQR, btnAllStudents, checkbtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        String username = getIntent().getStringExtra("USERNAME_EXTRA");
        TextView user = findViewById(R.id.hellouser);
        user.setText("Hello " + username);

        btnScanQR = findViewById(R.id.btnScanQR);
        btnAllStudents = findViewById(R.id.allstudents);



        btnScanQR.setOnClickListener(v -> new IntentIntegrator(StaffLogin.this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("Scan a QR code")
                .initiateScan());

        btnAllStudents.setOnClickListener(v -> openAllStudentsActivity());

       checkbtn = findViewById(R.id.checkperf);
        checkbtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(StaffLogin.this, CheckPerformance.class);
            startActivity(intent2);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            // QR code scanned successfully
            String qrData = result.getContents();
            processQRData(qrData);
        } else {
            // QR code scanning canceled
            Toast.makeText(this, "Scanning canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void processQRData(String qrData) {
        // Split the QR data into RollNo, Name, and Subject
        String[] qrParts = qrData.split(" ");
        if (qrParts.length >= 3) {
            String rollNo = qrParts[0].trim();
            String name = qrParts[1].trim();
            String subject = qrParts[2].trim();
            // Get current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());
            String currentTime = timeFormat.format(new Date());

            // Check if the data for the current date, roll number, and subject already exists
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean dataExists = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userData = dataSnapshot.getValue(String.class);
                        if (userData != null) {
                            String[] parts = userData.split(" ");
                            if (parts.length >= 3 && parts[0].equals(rollNo) && parts[2].equals(subject)) {
                                // Data for the current roll number and subject already exists
                                dataExists = true;
                                break;
                            }
                        }
                    }

                    if (!dataExists) {
                        // Construct the data to be stored
                        String data = rollNo + " " + name + " " + subject + " " + currentDate + " " + currentTime;

                        // Write data to Firebase Realtime Database
                        writeToFirebaseDatabase(data);

                        Toast.makeText(StaffLogin.this, "Data added to Firebase", Toast.LENGTH_SHORT).show();

                        // Restart the scanning activity
                        new IntentIntegrator(StaffLogin.this)
                                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                                .setPrompt("Scan a QR code")
                                .initiateScan();
                    } else {
                        Toast.makeText(StaffLogin.this, "Data for the current roll number and subject already exists", Toast.LENGTH_SHORT).show();
                        // Play sound
                        playSound();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(StaffLogin.this, "Failed to check data existence: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
            playSound();
        }
    }

    private void playSound() {
        // Initialize MediaPlayer with the sound file
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.unsuccessful);

        // Start playing the sound
        mediaPlayer.start();

        // Release the MediaPlayer resources after the sound has finished playing
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }


    private void openAllStudentsActivity() {
        // Open the AllStudentsActivity
        Intent intent = new Intent(this, AllStudentsActivity.class);
        startActivity(intent);
    }

    private void writeToFirebaseDatabase(String data) {
        // Get reference to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get reference to the "users" table in the database
        DatabaseReference usersRef = database.getReference("users");

        // Generate a unique key for the new user entry
        String key = usersRef.push().getKey();

        // Set the data under the generated key
        usersRef.child(key).setValue(data);
    }


    public void absent(View view) {
        Intent intent = new Intent(this, AbsentStudents.class);
        startActivity(intent);
    }
}
