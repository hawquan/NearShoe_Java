package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.UserMC;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.nearshoe_java.Utilities.DB_USERS_REF;

public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnGoBack, btnSave;
    LinearLayout nameBox;
    EditText etName, etPhone, etEmail, etAddress;
    UserMC userMC;
    CircleImageView profileImage;
    Intent getDataIntent;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Saving data, please wait!");
        getDataIntent = getIntent();
        userMC = getDataIntent.getParcelableExtra("UserMC");
        if (userMC != null) {
            etName.setText(userMC.getName());
            etPhone.setText(userMC.getPhone());
            etEmail.setText(userMC.getEmail());
            etAddress.setText(userMC.getAddress());
            if (!userMC.getImage().equals("")) {
                Glide.with(Profile.this).load(userMC.getImage()).into(profileImage);
            }
        }
        //getCurrentUserInformation();
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });


    }

    private void getCurrentUserInformation() {
        DB_USERS_REF.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userMC = new UserMC();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds != null) {
                        etName.setText(ds.child("name").getValue(String.class));
                        etPhone.setText(ds.child("phone").getValue(String.class));
                        etEmail.setText(ds.child("email").getValue(String.class));
                        etAddress.setText(ds.child("address").getValue(String.class));
                        if (!userMC.getImage().equals("")) {
                            Picasso.get().load(userMC.getImage()).into(profileImage);
                        }
                        Log.i("Profile", ds.child("name").getValue(String.class));
                    } else {
                        Log.i("Profile", "Null");

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveProfile() {
        if (etName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Name!!!", Toast.LENGTH_SHORT).show();
        } else {
            userMC.setName(etName.getText().toString().trim());
            userMC.setPhone(etPhone.getText().toString().trim());
            userMC.setAddress(etAddress.getText().toString());
            progressDialog.show();
            DB_USERS_REF.child(mAuth.getCurrentUser().getUid()).setValue(userMC).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(Profile.this, "Profile Information Saved!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile.this, "Unable to upload data due to " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void initializeComponents() {
        btnGoBack = findViewById(R.id.back_id);
        nameBox = findViewById(R.id.nameBox);
        btnSave = findViewById(R.id.save);
        etName = findViewById(R.id.profileName);
        etPhone = findViewById(R.id.profilePhone);
        etEmail = findViewById(R.id.profileEmail);
        etAddress = findViewById(R.id.profileAddress);
        profileImage = findViewById(R.id.profileImage);
    }


}