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
import android.widget.Toast;

import com.example.nearshoe_java.ModelClasses.UserMC;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.nearshoe_java.DBUtilClass.DB_USERS_REF;

public class AddAdmin extends AppCompatActivity {

    ProgressDialog progressDialog;
    Button btnGotoLogin, btnRegister;
    EditText etName, etEmail, etPassword;
    /*RadioGroup userTypeRadioGroup;
    RadioButton customerRadioButton, adminRadioButton;*/
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    String userType = "Staff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                if (name.isEmpty()) {
                    etName.setError("Enter Name!");
                    etName.requestFocus();
                } else if (email.isEmpty()) {
                    etEmail.setError("Enter Email!");
                    etEmail.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    etEmail.setError("Enter Valid Email!");
                    etEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    etPassword.setError("Enter Password!");
                    etPassword.requestFocus();
                } else {
                    progressDialog.setTitle("Register");
                    progressDialog.setMessage("Data is being saved, please wait!");
                    progressDialog.show();
                    completeRegistration(name, email, pass, userType);
                }
            }
        });
    }

    private void completeRegistration(String name, String email, String pass, String userType) {
        Log.i("RegisterActivity", name + "/" + email + "/" + pass);
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //store additional fields in firebase database
                    String authId = mAuth.getCurrentUser().getUid();
                    UserMC userMC = new UserMC();
                    userMC.setId(authId);
                    userMC.setName(name);
                    userMC.setEmail(email);
                    userMC.setPassword(pass);
                    userMC.setAddress("");
                    userMC.setImage("");
                    userMC.setPhone("");
                    userMC.setUserType(userType);
                    DB_USERS_REF.child(authId).setValue(userMC).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddAdmin.this, "New staff created and saved Successfully!!!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                                Log.i("RegisterActivity", "User created and saved Successfully!!!");
//                                redirectToDashboard(userMC);
                            } else {
                                Log.i("RegisterActivity", task.getException().getMessage());
                                Toast.makeText(AddAdmin.this, "User created but not saved due to " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.i("RegisterActivity", "Unable to createUserWithEmailAndPassword due to " + task.getException().getMessage());
                }
            }
        });
    }


    private void initializeComponents() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        btnRegister = findViewById(R.id.btn_Register_id);
        etName = findViewById(R.id.etName_id);
        etEmail = findViewById(R.id.etEmail_id);
        etPassword = findViewById(R.id.etPassword_id);
    }
}