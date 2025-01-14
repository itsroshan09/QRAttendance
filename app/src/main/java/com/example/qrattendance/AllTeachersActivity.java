package com.example.qrattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllTeachersActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_teachers);

        // Initialize views
        tableLayout = findViewById(R.id.tableLayout);
        searchEditText = findViewById(R.id.searchEditText);

        // Fetch and display data from Firebase
        fetchAndDisplayData();

        // Setup search functionality
        setupSearch();
    }

    private void fetchAndDisplayData() {
        // Get reference to the Firebase database
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference("teachers");

        // Attach a listener to read the data from Firebase
        teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the username from the snapshot
                    String username = snapshot.child("username").getValue(String.class);
                    // Add the data to the table
                    addRowToTable(snapshot.getKey(), username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("FirebaseDataError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }


    private void addRowToTable(String teacherId, String username) {
        // Create a new TableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        // Create TextView for username
        TextView usernameTextView = new TextView(this);
        usernameTextView.setText(username);
        usernameTextView.setPadding(16, 8, 16, 8);
        usernameTextView.setTextSize(16); // Set your desired size
        usernameTextView.setTag(teacherId); // Set the tag to the teacher's ID
        tableRow.addView(usernameTextView);

        // Create delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click
                deleteUser(teacherId);
            }
        });
        tableRow.addView(deleteButton);

        // Add the TableRow to the TableLayout
        tableLayout.addView(tableRow);
    }

    private void setupSearch() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        query = query.toLowerCase(); // Convert query to lowercase for case-insensitive search
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                TextView usernameTextView = (TextView) row.getChildAt(0);
                String username = usernameTextView.getText().toString().toLowerCase();
                if (username.contains(query)) {
                    row.setVisibility(View.VISIBLE);
                } else {
                    row.setVisibility(View.GONE);
                }
            }
        }
    }

    private void deleteUser(String teacherId) {
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference("teachers");
        teachersRef.child(teacherId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AllTeachersActivity.this, "Teacher deleted successfully", Toast.LENGTH_SHORT).show();
                    // Remove the corresponding row from the table
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        View view = tableLayout.getChildAt(i);
                        if (view instanceof TableRow) {
                            TableRow row = (TableRow) view;
                            TextView usernameTextView = (TextView) row.getChildAt(0);
                            if (usernameTextView.getTag() != null && usernameTextView.getTag().toString().equals(teacherId)) {
                                tableLayout.removeView(row);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AllTeachersActivity.this, "Failed to delete teacher: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
