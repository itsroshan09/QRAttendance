package com.example.qrattendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class StudentLogin extends AppCompatActivity {
    String rollValue, nameValue, subjectValue;
    ImageButton btn;

    TextView application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        EditText roll = findViewById(R.id.studentRollNo);
        EditText name = findViewById(R.id.studentName);
        EditText subject = findViewById(R.id.subject);
        ImageView imageView = findViewById(R.id.imageViewQRCode);
        application = findViewById(R.id.application);//unused

        btn = findViewById(R.id.btn);

        btn.setOnClickListener(v -> {
            // Fetch data from EditText fields when the button is clicked
            rollValue = roll.getText().toString();
            nameValue = name.getText().toString();
            subjectValue = subject.getText().toString();

            if (!rollValue.isEmpty() && !nameValue.isEmpty() && !subjectValue.isEmpty()) {
                try {
                    // Concatenate roll number, name, and subject to form the QR code content
                    String qrContent = rollValue + " " + nameValue + " " + subjectValue;

                    // Generate the QR code bitmap
                    QRCodeWriter writer = new QRCodeWriter();
                    BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);
                    Bitmap qrBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

                    // Set pixels based on BitMatrix values to create black and white QR code
                    for (int x = 0; x < 300; x++) {
                        for (int y = 0; y < 300; y++) {
                            qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    // Display the generated QR code bitmap in an ImageView
                    imageView.setImageBitmap(qrBitmap);

                    // Provide user feedback (success message, etc.)
                    Toast.makeText(StudentLogin.this, "QR Code generated successfully", Toast.LENGTH_SHORT).show();

                } catch (WriterException e) {
                    // Handle QR code generation error
                    e.printStackTrace();
                    Toast.makeText(StudentLogin.this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Provide user feedback for missing input
                Toast.makeText(StudentLogin.this, "Please enter Roll No, Name, and Subject", Toast.LENGTH_SHORT).show();
            }
        });

        application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentLogin.this, LeaveApplication.class);
                startActivity(intent);
            }
        });

    }
    public void register(View v) {
        Intent intent = new Intent(StudentLogin.this, SignupStudent.class);
        startActivity(intent);
    }
}
