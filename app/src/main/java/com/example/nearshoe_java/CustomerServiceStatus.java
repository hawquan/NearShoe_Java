package com.example.nearshoe_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerServiceStatus extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_status);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}