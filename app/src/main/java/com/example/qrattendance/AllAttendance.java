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

public class AllAttendance extends AppCompatActivity {

    private TableLayout tableLayout;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);

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
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Attach a listener to read the data from Firebase
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the data from each snapshot
                    String userData = snapshot.getValue(String.class);
                    String userId = snapshot.getKey(); // Get user ID
                    // Add the data to the table
                    addRowToTable(userData, userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("FirebaseDataError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void addRowToTable(String data, String userId) {
        // Split the data into RollNo, Name, Subject, Date, and Time
        String[] rowData = data.split(" ");

        // Create a new TableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        // Create TextViews for each column
        for (String columnData : rowData) {
            TextView textView = new TextView(this);
            textView.setText(columnData);
            textView.setPadding(16, 8, 16, 8);
            textView.setTextSize(16); // Set your desired size
            tableRow.addView(textView);
        }

        // Create delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the user data from Firebase
                deleteUser(userId);
                // Remove the row from the table
                tableLayout.removeView(tableRow);
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
        query = query.toLowerCase(); //  lowercase for case-insensitive search
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                boolean matchFound = false;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View childView = row.getChildAt(j);
                    if (childView instanceof TextView) {
                        TextView textView = (TextView) childView;
                        String cellText = textView.getText().toString().toLowerCase();
                        if (cellText.contains(query)) {
                            // Found a match in this row, make the row visible
                            matchFound = true;
                            break;
                        }
                    }
                }
                // Show or hide the row based on matchFound
                row.setVisibility(matchFound ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void deleteUser(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
