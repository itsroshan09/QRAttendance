package com.example.qrattendance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupStudent extends AppCompatActivity {

    private EditText editTextName, editTextRollNo, editTextParentMobile;
    private Button buttonSubmit;
    private DatabaseReference studentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_student);

        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        studentsRef = database.getReference("students");

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextRollNo = findViewById(R.id.editTextRollNo);
        editTextParentMobile = findViewById(R.id.editTextParentMobile);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        // Get the input values from EditText fields
        String name = editTextName.getText().toString().trim();
        String rollNo = editTextRollNo.getText().toString().trim();
        String parentMobile = editTextParentMobile.getText().toString().trim();

        // Check if any field is empty
        if (name.isEmpty() || rollNo.isEmpty() || parentMobile.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Student object with the input data
        Student student = new Student(name, rollNo, parentMobile);

        // Push the student data to the Firebase database under "students" node
        studentsRef.push().setValue(student);

        // Show success message
        Toast.makeText(this, "Form submitted successfully", Toast.LENGTH_SHORT).show();

        // Clear the EditText fields
        editTextName.setText("");
        editTextRollNo.setText("");
        editTextParentMobile.setText("");
    }
}
