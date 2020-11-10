package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    Button btnSubmit;
    EditText etEmail;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passwod);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Password Reset");
                String email = etEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    etEmail.setError("Enter Email!");
                    etEmail.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    etEmail.setError("Enter Valid Email!");
                    etEmail.requestFocus();
                } else {
                    completeResetActivity(email);
                }
            }
        });
    }

    private void completeResetActivity(String email) {
        progressDialog.setMessage("Sending password reset link on "+email+" please wait!");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(etEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Dear User check your email for password reset link!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                } else {
                    Toast.makeText(ForgotPassword.this, "Password reset failed due to " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initializeComponents() {
        btnSubmit = findViewById(R.id.btn_Submit_id);
        etEmail = findViewById(R.id.editEmailForgotPass_id);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }
}