package com.example.qrattendance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllStudentsData extends AppCompatActivity {

    private DatabaseReference studentsRef;
    private TableLayout dataTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students_data);

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        studentsRef = database.getReference("students");

        dataTable = findViewById(R.id.data_table);

        displayStudents();
    }

    private void displayStudents() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing table rows
                dataTable.removeAllViews();

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    final String studentId = studentSnapshot.getKey();
                    final Student student = studentSnapshot.getValue(Student.class);

                    // Create a new row for each student
                    TableRow row = new TableRow(getApplicationContext());

                    TextView nameTextView = createTextView(student.getName());
                    row.addView(nameTextView);

                    TextView parentMobileTextView = createTextView(student.getParentMobile());
                    row.addView(parentMobileTextView);

                    TextView rollNoTextView = createTextView(student.getRollNo());
                    row.addView(rollNoTextView);

                    Button deleteButton = createButton("Delete");
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Delete button action
                            studentsRef.child(studentId).removeValue();
                            Toast.makeText(getApplicationContext(), "Student data deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    row.addView(deleteButton);

                    dataTable.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load students: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private Button createButton(String text) {
        Button button = new Button(getApplicationContext());
        button.setText(text);
        return button;
    }
}
