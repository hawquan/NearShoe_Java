package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.UserMC;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    UserMC userMC;
    Button btnGotoRegister, btnLogin;
    EditText etEmail, etPassword;
    TextView forgetPassword;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();

        btnGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    etEmail.setError("Enter Email!");
                    etEmail.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    etEmail.setError("Enter Valid Email!");
                    etEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    etPassword.setError("Enter Password!");
                    etPassword.requestFocus();
                } else {
                    completeLogin(email, pass);
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                finish();
            }
        });

    }

    private void completeLogin(String email, String password) {
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Authenticating user, please wait!");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    redirectToDashboard();
                } else {
                    Toast.makeText(Login.this, "Login failed due to " + task.getException(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void redirectToDashboard() {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Login.this, CustomerDashboard.class));
    }

    private void initializeComponents() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        forgetPassword = findViewById(R.id.forgetPassword_id);
        btnGotoRegister = findViewById(R.id.btn_gotoReg_id);
        btnLogin = findViewById(R.id.btn_Login_id);
        etEmail = findViewById(R.id.editEmail_id);
        etPassword = findViewById(R.id.editPassword_id);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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
                    if (userMC.getUserType().equals("Admin")) {
                        Intent intent = new Intent(Login.this, AdminDashboard.class);
                        intent.putExtra("UserMC", userMC);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Login.this, CustomerDashboard.class);
                        intent.putExtra("UserMC", userMC);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}