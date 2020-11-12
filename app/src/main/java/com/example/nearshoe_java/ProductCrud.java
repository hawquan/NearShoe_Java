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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.nearshoe_java.ModelClasses.ProductMC;
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

public class ProductCrud extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    Uri postPhotoUri = null;
    String photoURL;
    ProgressDialog mProgressDialog;
    ImageView productImage;
    DatabaseReference mPostRef;
    private static final int REQUEST_PERMISSION = 1111;
    private static final int IMAGE_PICK = 2222, IMAGE_PICK2 = 3333;
    String[] storagePermission;
    EditText etProd_name, etProd_description, etProd_amount;
    RadioGroup availabilityStatusRG;
    RadioButton availableRB, unAvailableRB;
    Button btnSave;
    boolean isAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_crud);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
    }

    private void initializeComponents() {
        mPostRef = DBUtilClass.DB_PRODUCTS_REF;
        mProgressDialog = new ProgressDialog(this);
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        btnSave = findViewById(R.id.btnProductSave_id);
        availabilityStatusRG = findViewById(R.id.availableStatusRG_id);
        availableRB = findViewById(R.id.availableRB_id);
        unAvailableRB = findViewById(R.id.un_availableRB_id);
        etProd_name = findViewById(R.id.et_productName_id);
        etProd_description = findViewById(R.id.et_productDesc_id);
        etProd_amount = findViewById(R.id.et_productPrice_id);
        productImage = findViewById(R.id.imageView_product_id);
        productImage.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        availabilityStatusRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.availableRB_id) {
                    isAvailable = true;
                } else {
                    isAvailable = false;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageView_product_id) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                pickFromGallery(IMAGE_PICK);
            }
        } else if (id == R.id.btnProductSave_id) {
            finalizePost();
        }
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
                    productImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void finalizePost() {
        String prodName = etProd_name.getText().toString();
        String prodDesc = etProd_description.getText().toString();
        String prodAmount = etProd_amount.getText().toString();
        if (prodName.isEmpty()) {
            etProd_name.setError("Enter Product Name!!!");
            etProd_name.requestFocus();
        } else if (prodDesc.isEmpty()) {
            etProd_description.setError("Enter Product Description!!!");
            etProd_description.requestFocus();
        } else if (prodAmount.isEmpty()) {
            etProd_amount.setError("Enter Product Amount!!!");
            etProd_amount.requestFocus();
        } else {
            mProgressDialog.setTitle("Product");
            mProgressDialog.setMessage("Product details are being saved, please wait!!!");
            mProgressDialog.show();
            uploadToFirebase(postPhotoUri, prodName, prodDesc, prodAmount, isAvailable);
        }
    }

    private void uploadToFirebase(final Uri photoUri, String prodName, String prodDesc, String prodAmount, boolean isAvailable) {
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
                            final String currentUser = mAuth.getCurrentUser().getUid();
                            String uniqueId = mPostRef.child(currentUser).push().getKey();
                            final ProductMC productMC = new ProductMC();
                            productMC.setId(uniqueId);
                            productMC.setName(prodName);
                            productMC.setImageUrl(photoURL);
                            productMC.setDescription(prodDesc);
                            productMC.setPrice(prodAmount);
                            productMC.setTime(timeStamp);
                            if (isAvailable) {
                                productMC.setIsAvailable("Available");
                            } else {
                                productMC.setIsAvailable("Out of stock");
                            }

                            mPostRef.child(uniqueId).setValue(productMC).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProductCrud.this, "Product Information Saved Successfully!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductCrud.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}