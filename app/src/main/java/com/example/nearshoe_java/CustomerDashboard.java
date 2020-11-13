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

public class CustomerDashboard extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    Button btnLogout, btnProfile, btnAvailServices, btnServiceStatus, btnProducts, btnAboutAs;
    TextView tvWelcome;
    UserMC userMC;
    CircleImageView profileImage;
    Intent getDataIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        getDataIntent = getIntent();
        userMC = getDataIntent.getParcelableExtra("UserMC");
        initializeComponents();
        if (userMC != null) {
            tvWelcome.setText(userMC.getName());
            if (!userMC.getImage().equals("")) {
                Glide.with(CustomerDashboard.this).load(userMC.getImage()).into(profileImage);
            } else {
                Glide.with(CustomerDashboard.this).load(R.drawable.ic_camera).into(profileImage);
            }
        }
    }

    private void initializeComponents() {
        profileImage = findViewById(R.id.userImage_id);
        tvWelcome = findViewById(R.id.tvWelcome_id);
        btnProfile = findViewById(R.id.btnProfile_id);
        btnServiceStatus = findViewById(R.id.btnServiceStatus_id);
        btnProducts = findViewById(R.id.btnProducts_id);
        btnAvailServices = findViewById(R.id.btnNeedService_id);
        btnLogout = findViewById(R.id.btnLogout_id);
        btnAboutAs = findViewById(R.id.btnAboutAs);

        btnLogout.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnAvailServices.setOnClickListener(this);
        btnServiceStatus.setOnClickListener(this);
        btnProducts.setOnClickListener(this);
        btnAboutAs.setOnClickListener(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        } else {
            getUserDetails(currentUser);
        }
    }

    private void getUserDetails(FirebaseUser currentUser) {
        DBUtilClass.DB_USERS_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                if (userMC != null) {
                    tvWelcome.setText(userMC.getName());
                    if (!userMC.getImage().equals("")) {
                        Glide.with(CustomerDashboard.this).load(userMC.getImage()).into(profileImage);
                    } else {
                        Glide.with(CustomerDashboard.this).load(R.drawable.ic_camera).into(profileImage);
                    }
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
        Toast.makeText(this, "Click on Logout to Logout of Application", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnProfile_id) {
            Intent intent = new Intent(CustomerDashboard.this, Profile.class);
            intent.putExtra("UserMC", userMC);
            startActivity(intent);
        } else if (id == R.id.btnServiceStatus_id) {
            startActivity(new Intent(CustomerDashboard.this, CustomerServiceStatus.class));
        } else if (id == R.id.btnProducts_id) {
            startActivity(new Intent(CustomerDashboard.this, CustomerProducts.class));
        } else if (id == R.id.btnNeedService_id) {
            startActivity(new Intent(CustomerDashboard.this, pickup2.class));
        } else if (id == R.id.btnLogout_id) {
            signOutUser();
        }else if(id == R.id.btnAboutAs){
            startActivity(new Intent(CustomerDashboard.this,AboutUs.class));
        }
    }
}