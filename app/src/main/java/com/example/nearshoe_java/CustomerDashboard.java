package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.UserMC;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CustomerDashboard extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnLogout, btnProfile;
    TextView tvWelcome;
    UserMC userMC;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
    }

    private void initializeComponents() {
        userImage = findViewById(R.id.userImage_id);
        btnProfile = findViewById(R.id.btnProfile_id);
        tvWelcome = findViewById(R.id.tvWelcome_id);
        btnLogout = findViewById(R.id.btnLogout_id);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboard.this, Profile.class);
                intent.putExtra("UserMC", userMC);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(CustomerDashboard.this, Login.class));
        Toast.makeText(this, "User Logged Out Successfully!!!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        } else {
            getUserDetails(currentUser);
        }
    }

    private void getUserDetails(FirebaseUser currentUser) {
        Utilities.DB_USERS_REF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMC = new UserMC();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userMC = dataSnapshot.getValue(UserMC.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void redirectToLogin() {
        startActivity(new Intent(CustomerDashboard.this, Login.class));
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}