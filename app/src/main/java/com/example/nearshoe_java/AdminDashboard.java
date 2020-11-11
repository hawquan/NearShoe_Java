package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboard extends AppCompatActivity implements View.OnClickListener {

    TextView welcome;
    Intent getDataIntent;
    UserMC userMC;
    FirebaseAuth mAuth;
    CircleImageView profileImage;
    Button btnViewReports, btnCustomerFeedback, btnProfile, btnUpdateServiceStatus, btnStaffManagement, btnProductManagement, btnLogout;

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
            } else {
                Glide.with(AdminDashboard.this).load(R.drawable.ic_camera).into(profileImage);
            }
        }

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
            startActivity(new Intent(AdminDashboard.this, AdminUpdateServiceStatus.class));
        } else if (id == R.id.btnStaffManagement_id) {
            startActivity(new Intent(AdminDashboard.this, AdminStaffManagement.class));
        } else if (id == R.id.btnProductManagement_id) {
            startActivity(new Intent(AdminDashboard.this, AdminProductManagement.class));
        } else if (id == R.id.btnAdminViewReports_id) {
            startActivity(new Intent(AdminDashboard.this, AdminViewReports.class));
        } else if (id == R.id.btnAdminViewCustFeedback_id) {
            startActivity(new Intent(AdminDashboard.this, AdminViewFeedback.class));
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
        Utilities.DB_USERS_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMC = new UserMC();
                userMC.setId(snapshot.child("id").getValue(String.class));
                userMC.setName(snapshot.child("name").getValue(String.class));
                userMC.setEmail(snapshot.child("email").getValue(String.class));
                userMC.setPassword(snapshot.child("password").getValue(String.class));
                userMC.setAddress(snapshot.child("address").getValue(String.class));
                userMC.setPhone(snapshot.child("phone").getValue(String.class));
                userMC.setUserType(snapshot.child("userType").getValue(String.class));
                userMC.setImage(snapshot.child("image").getValue(String.class));
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

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Click on Logout to Logout of Application", Toast.LENGTH_SHORT).show();
    }

    private void initializeComponents() {
        profileImage = findViewById(R.id.profileImage);
        welcome = findViewById(R.id.tvAdminWelcome_id);
        btnLogout = findViewById(R.id.btnAdminLogout_id);
        btnProfile = findViewById(R.id.btnAdminProfile_id);
        btnUpdateServiceStatus = findViewById(R.id.btnAdminServiceStatus_id);
        btnStaffManagement = findViewById(R.id.btnStaffManagement_id);
        btnProductManagement = findViewById(R.id.btnProductManagement_id);
        btnViewReports = findViewById(R.id.btnAdminViewReports_id);
        btnCustomerFeedback = findViewById(R.id.btnAdminViewCustFeedback_id);
        btnProfile.setOnClickListener(this);
        btnProductManagement.setOnClickListener(this);
        btnStaffManagement.setOnClickListener(this);
        btnUpdateServiceStatus.setOnClickListener(this);
        btnViewReports.setOnClickListener(this);
        btnCustomerFeedback.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }


}