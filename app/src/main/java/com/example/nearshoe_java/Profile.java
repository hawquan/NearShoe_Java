package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.ProductMC;
import com.example.nearshoe_java.ModelClasses.UserMC;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.nearshoe_java.DBUtilClass.DB_USERS_REF;

public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnGoBack, btnSave;
    LinearLayout nameBox;
    EditText etName, etPhone, etEmail, etAddress;
    UserMC userMC;
    CircleImageView profileImage;
    Intent getDataIntent;
    ProgressDialog progressDialog;

    Uri postPhotoUri = null;
    String photoURL;
    ProgressDialog mProgressDialog;
    DatabaseReference mPostRef;
    private static final int REQUEST_PERMISSION = 1111;
    private static final int IMAGE_PICK = 2222, IMAGE_PICK2 = 3333;
    String[] storagePermission;

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
            } else {
                Glide.with(Profile.this).load(R.drawable.ic_camera).into(profileImage);
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
        mProgressDialog = new ProgressDialog(this);
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        btnGoBack = findViewById(R.id.back_id);
        nameBox = findViewById(R.id.nameBox);
        btnSave = findViewById(R.id.save);
        etName = findViewById(R.id.profileName);
        etPhone = findViewById(R.id.profilePhone);
        etEmail = findViewById(R.id.profileEmail);
        etAddress = findViewById(R.id.profileAddress);
        profileImage = findViewById(R.id.profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    pickFromGallery(IMAGE_PICK);
                }
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, storagePermission, REQUEST_PERMISSION);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void pickFromGallery(int code) {
        if (code == IMAGE_PICK) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, code);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK) {
                postPhotoUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), postPhotoUri);
                    profileImage.setImageBitmap(bitmap);
                    uploadToFirebase(postPhotoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadToFirebase(final Uri photoUri) {
        mProgressDialog.setMessage("Profile Image");
        mProgressDialog.setMessage("Please wait will profile image is being updated!!!");
        mProgressDialog.show();
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathName = "PostsImages/" + "post_" + timeStamp;
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathName);
        final UploadTask uploadTask = ref.putFile(photoUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            //Toast.makeText(mContext, "Error 1: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return ref.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            photoURL = task.getResult().toString();
                            Log.i("URL", photoURL);
                            DB_USERS_REF.child(mAuth.getCurrentUser().getUid()).child("image").setValue(photoURL);
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}