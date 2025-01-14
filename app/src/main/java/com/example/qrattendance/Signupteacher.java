package com.example.qrattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Signupteacher extends AppCompatActivity {

    Button signupBtn;
    EditText username, password, securityKey, captchaEdit;
    TextView captchaText;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupteacher);

        signupBtn = findViewById(R.id.signupBtn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        securityKey = findViewById(R.id.securityKey);
        captchaEdit = findViewById(R.id.captchaEdit);
        captchaText = findViewById(R.id.captchaText);

        int randomNumber = generateRandomNumber();

        // Display the random number in the TextView
        captchaText.setText(String.valueOf(randomNumber));

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void signup(View v) {
        if (username.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                securityKey.getText().toString().isEmpty() ||
                captchaEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
        }
        else if(!securityKey.getText().toString().equals("admin")){
            Toast.makeText(this, "Invalid Security Key", Toast.LENGTH_SHORT).show();
        }
        else if(!captchaText.getText().toString().equals(captchaEdit.getText().toString())){
            Toast.makeText(this, "Invalid Captcha", Toast.LENGTH_SHORT).show();
        }
        else {
            // Save credentials to Firebase
            saveCredentials(username.getText().toString(), password.getText().toString());
            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this, Staffverify.class);
            startActivity(i);
        }
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    private void saveCredentials(String username, String password) {
        // Create a unique key for the user
        String userId = mDatabase.push().getKey();

        // Create a User object
        User user = new User(username, password);

        // Add the user to the database under the unique key
        mDatabase.child("teachers").child(userId).setValue(user);
    }
}
