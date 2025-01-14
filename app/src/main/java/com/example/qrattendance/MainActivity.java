package com.example.qrattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageButton stud,staff;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stud=findViewById(R.id.stud_login);
        staff=findViewById(R.id.staff_login);

        stud.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,StudentLogin.class);
            startActivity(i);
        });
        staff.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,Staffverify.class);
            startActivity(i);
        });

    }
}