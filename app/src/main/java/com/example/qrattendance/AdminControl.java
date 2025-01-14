package com.example.qrattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminControl extends AppCompatActivity {

    private TableLayout tableLayout;
    private EditText searchEditText;
    ImageButton checkbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control);


        checkbtn = findViewById(R.id.checkperf2);
        checkbtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(this, CheckPerformance.class);
            startActivity(intent2);
        });

    }

    public void attendancesheet(View v){
            Intent i=new Intent(this,AllAttendance.class);
            startActivity(i);
    }


    public void allteachers(View v){
        Intent i=new Intent(this, AllTeachersActivity.class);
        startActivity(i);
    }
    public void open(View v){
        Intent i=new Intent(getApplicationContext(),AllApplications.class);
        startActivity(i);
    }

    public void deleteDataContents(View v) {
        // Get reference to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get reference to the "users" node
        DatabaseReference usersRef = database.getReference("users");

        // Remove all children under the "users" node
        usersRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "All users deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void allstudents(View v){
        Intent i=new Intent(getApplicationContext(),AllStudentsData.class);
        startActivity(i);
    }
}
