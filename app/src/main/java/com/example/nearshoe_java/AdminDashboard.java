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

public class AdminDashboard extends AppCompatActivity implements View.OnClickListener {

    TextView welcome;
    Intent getDataIntent;
    UserMC userMC;
    FirebaseAuth mAuth;
    ImageView profileImage;
    Button btnProfile, btnUpdateServiceStatus, btnStaffManagement, btnProductManagement, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        getDataIntent = getIntent();
        userMC = getDataIntent.getParcelableExtra("UserMC");
        initializeComponents();
        if (userMC != null) {
            welcome.setText(userMC.getName());
            if (!userMC.getImage().equals("")) {
                Glide.with(AdminDashboard.this).load(userMC.getImage()).into(profileImage);
            }
        }

    }

    private void initializeComponents() {
        profileImage = findViewById(R.id.adminImage_id);
        welcome = findViewById(R.id.tvAdminWelcome_id);
        btnLogout = findViewById(R.id.btnAdminLogout_id);
        btnProfile = findViewById(R.id.btnAdminProfile_id);
        btnUpdateServiceStatus = findViewById(R.id.btnAdminServiceStatus_id);
        btnStaffManagement = findViewById(R.id.btnStaffManagement_id);
        btnProductManagement = findViewById(R.id.btnProductManagement_id);

        btnProfile.setOnClickListener(this);
        btnProductManagement.setOnClickListener(this);
        btnStaffManagement.setOnClickListener(this);
        btnUpdateServiceStatus.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnAdminProfile_id) {
            Intent intent = new Intent(AdminDashboard.this, Profile.class);
            intent.putExtra("UserMC", userMC);
            startActivity(intent);
        } else if (id == R.id.btnAdminLogout_id) {
            signOutUser();
        } else if (id == R.id.btnAdminServiceStatus_id) {

        } else if (id == R.id.btnStaffManagement_id) {

        } else if (id == R.id.btnProductManagement_id) {

        } else if (id == R.id.btnAdminViewReports_id) {

        } else if (id == R.id.btnAdminViewCustFeedback_id) {

        }
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

    private void redirectToLogin() {
        startActivity(new Intent(AdminDashboard.this, Login.class));
        finish();
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

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AdminDashboard.this, Login.class));
        Toast.makeText(this, "User Logged Out Successfully!!!", Toast.LENGTH_SHORT).show();
        finish();
    }
}