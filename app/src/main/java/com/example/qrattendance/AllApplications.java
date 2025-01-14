package com.example.qrattendance;

import android.os.Bundle;
import android.view.Gravity;
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

public class AllApplications extends AppCompatActivity {

    private DatabaseReference applicationsRef;
    private TableLayout dataTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_applications);

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        applicationsRef = database.getReference("applications");

        dataTable = findViewById(R.id.data_table);

        loadApplications();
    }

    private void loadApplications() {
        applicationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing table rows
                dataTable.removeAllViews();

                for (DataSnapshot applicationSnapshot : dataSnapshot.getChildren()) {
                    final Application application = applicationSnapshot.getValue(Application.class);

                    // Create a new row for each application
                    TableRow row = new TableRow(getApplicationContext());

                    TextView rollNoTextView = createTextView(application.getRollNo(), 1);
                    row.addView(rollNoTextView);

                    TextView nameTextView = createTextView(application.getName(), 1);
                    row.addView(nameTextView);

                    TextView subjectTextView = createTextView(application.getSubject(), 1);
                    row.addView(subjectTextView);

                    TextView dateTextView = createTextView(application.getDate(), 1);
                    row.addView(dateTextView);

                    TextView reasonTextView = createTextView(application.getReason(), 1);
                    row.addView(reasonTextView);

                    Button acceptButton = createButton("✅");
                    TableRow.LayoutParams params = new TableRow.LayoutParams(20, TableRow.LayoutParams.WRAP_CONTENT);
                    acceptButton.setLayoutParams(params);
                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Accept button action
                            // Example: Add data to "users" node with rollNo, name, subject, date, and time as 00:00:00
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                            String rollNo = application.getRollNo();
                            String name = application.getName();
                            String subject = application.getSubject();
                            String date = application.getDate();
                            String[] nameparts=name.split(" ");
                            name="";
                            for(int i=0;i<nameparts.length;i++){
                                name+=nameparts[i];
                            }
                            // Setting time as 00:00:00
                            String time = "00:00:00";

                            // Concatenating date and time
                            String dateTime = date + " " + time;

                            // Adding data to "users" node
                            String userData = rollNo + " " + name + " " + subject + " " + dateTime;
                            usersRef.push().setValue(userData);

                            Toast.makeText(getApplicationContext(), "Application accepted", Toast.LENGTH_SHORT).show();
                            applicationSnapshot.getRef().removeValue();
                        }
                    });
                    row.addView(acceptButton);

                    Button rejectButton = createButton("❌");
                    rejectButton.setLayoutParams(params);
                    rejectButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Reject button action
                            // Example: Delete application from database
                            applicationSnapshot.getRef().removeValue();
                            Toast.makeText(getApplicationContext(), "Application rejected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    row.addView(rejectButton);

                    dataTable.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load applications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextView createTextView(String text, int weight) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        return textView;
    }

    private Button createButton(String text) {
        Button button = new Button(getApplicationContext());
        button.setText(text);
        return button;
    }
}