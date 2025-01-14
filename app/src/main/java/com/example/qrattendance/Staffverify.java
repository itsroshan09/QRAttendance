package com.example.qrattendance;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Staffverify extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffverify);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();//not in current use

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ImageButton loginButton = findViewById(R.id.loginButton);

        // Set click listener for the login button
        loginButton.setOnClickListener(view -> {
            // Get entered username and password
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if(username.equals("admin") && password.equals("admin")){
                Intent i = new Intent(Staffverify.this, AdminControl.class);
                startActivity(i);
            }else{
            // Perform login authentication using Firebase Auth
            signInWithEmailPassword(username, password);
        }});

        // Set click listener for the signup text
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(v -> {
            Intent i = new Intent(Staffverify.this, Signupteacher.class);
            startActivity(i);
        });
    }

    private void signInWithEmailPassword(String email, String password) {
        // Check the username and password in the Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("teachers");
        usersRef.orderByChild("username").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isValidUser = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(password)) {
                        isValidUser = true;
                        break;
                    }
                }
                if (isValidUser) {
                    // Valid username and password
                    showToast("Login Successful");
                    Intent i = new Intent(Staffverify.this, StaffLogin.class);
                    i.putExtra("USERNAME_EXTRA", email);
                    startActivity(i);
                } else {
                    // Invalid username or password
                    showToast("Authentication failed. Invalid email or password.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("Error: " + databaseError.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
