package com.example.qrattendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class LeaveApplication extends AppCompatActivity {

    private EditText editTextRollNo, editTextName, editTextSubject, editTextReason;
    private TextView editTextDate;
    private Button buttonSubmit;
    private DatabaseReference applicationsRef;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);

        editTextRollNo = findViewById(R.id.editTextRollNo);
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextReason = findViewById(R.id.editTextReason);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        applicationsRef = database.getReference("applications");

        // Initialize calendar
        calendar = Calendar.getInstance();

        // Set onClickListener for editTextDate to show DatePickerDialog
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update editTextDate with the selected date
                        editTextDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    private void submitForm() {
        String rollNo = editTextRollNo.getText().toString();
        String name = editTextName.getText().toString();
        String date = editTextDate.getText().toString();
        String subject = editTextSubject.getText().toString();
        String reason = editTextReason.getText().toString();

        // Validating fields
        if (rollNo.isEmpty() || name.isEmpty() || date.isEmpty() || subject.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the date to "yyyy-MM-dd"
        String[] dateParts = date.split("/");
        String year = dateParts[2];
        String month = String.format("%02d", Integer.parseInt(dateParts[1])); // Ensure leading zero for month
        String day = String.format("%02d", Integer.parseInt(dateParts[0]));   // Ensure leading zero for day
        date = year + "-" + month + "-" + day;

        // Creating application object
        Application application = new Application(rollNo, name, date, subject, reason);

        // Saving data to Firebase
        applicationsRef.push().setValue(application)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LeaveApplication.this, "Application submitted successfully", Toast.LENGTH_SHORT).show();
                        // Clearing form fields after successful submission
                        editTextRollNo.setText("");
                        editTextName.setText("");
                        editTextDate.setText("");
                        editTextSubject.setText("");
                        editTextReason.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LeaveApplication.this, "Failed to submit application: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
