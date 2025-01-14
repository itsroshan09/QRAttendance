package com.example.qrattendance;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Your package and imports here

public class CheckPerformance extends AppCompatActivity {

    EditText editTextLectures, editTextSubject, editTextRollNo;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_performance);

        editTextLectures = findViewById(R.id.editTextLectures);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextRollNo = findViewById(R.id.editTextRollNo);
        result = findViewById(R.id.result);

        ImageButton checkBtn = findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(view -> checkAttendance());
    }

    private void checkAttendance() {
        String lecturesText = editTextLectures.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String rollNoText = editTextRollNo.getText().toString().trim();

        if (lecturesText.isEmpty() || subject.isEmpty() || rollNoText.isEmpty()) {
            Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show();
            return;
        }

        // Additional check for valid numeric values
        if (!lecturesText.matches("\\d+") || !rollNoText.matches("\\d+")) {
            Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalLectures = Integer.parseInt(lecturesText);
        int studentRollNo = Integer.parseInt(rollNoText);

        // Get reference to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get reference to the users node
        DatabaseReference usersRef = database.getReference("users");

        // Retrieve user data
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Process user data
                    calculateAttendance(snapshot, studentRollNo, totalLectures, subject);
                } else {
                    // Handle scenario where no user data is available
                    Toast.makeText(CheckPerformance.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(CheckPerformance.this, "Failed to read user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAttendance(DataSnapshot snapshot, int studentRollNo, int totalLectures, String subject) {
        int occurrenceCount = 0;
        boolean studentFound = false;
        for (DataSnapshot userData : snapshot.getChildren()) {
            String data = userData.getValue(String.class);
            String[] parts = data.split(" ");
            if (parts.length >= 3) { // Ensure there are at least three parts
                int rollNo = Integer.parseInt(parts[0]);
                String userSubject = parts[2];
                if (rollNo == studentRollNo && subject.equals(userSubject)) {
                    occurrenceCount++;
                    studentFound = true;
                }
            }
        }

        // Check if the student was found in the database
        if (studentFound) {
            // Calculate attendance percentage
            double attendancePercentage = (occurrenceCount * 100.0) / totalLectures;

            // Display the attendance percentage
            displayResult(attendancePercentage, studentRollNo);
        } else {
            // Handle scenario where the student roll number does not exist for the given subject
            Toast.makeText(CheckPerformance.this, "Student with roll number " + studentRollNo + " not found for subject " + subject, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResult(double attendancePercentage, int studentRollNo) {
        if (attendancePercentage > 100 || attendancePercentage < 0) {
            result.setText("Invalid");
        } else {
            String message = (attendancePercentage >= 85) ? "\n\t\tNice Keep It Up...!" : "Not So Good";
            result.setText(String.format("Attendance Percentage for roll number %d: %.2f%%\n%s", studentRollNo, attendancePercentage, message));
        }
    }
}