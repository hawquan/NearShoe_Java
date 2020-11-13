package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.OrderItemMC;
import com.example.nearshoe_java.ModelClasses.UserMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminStaffManagement extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    FloatingActionButton mFAB;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<UserMC, AdminStaffManagement.PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_management);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        getData();
    }

    private void initializeComponents() {
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.rv_Admins_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mFAB = findViewById(R.id.fab_addAdmin);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAdmin();
            }
        });
    }

    private void addAdmin() {
        startActivity(new Intent(AdminStaffManagement.this,AddAdmin.class));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getData() {
        Query mPostsRef = DBUtilClass.DB_USERS_REF;
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<UserMC> options = new FirebaseRecyclerOptions.Builder<UserMC>()
                .setQuery(mPostsRef, UserMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<UserMC, AdminStaffManagement.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminStaffManagement.PostsViewHolder holder, int position, @NonNull final UserMC userMC) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.name.setText(userMC.getName());
                holder.email.setText(userMC.getEmail());
                holder.phone.setText(userMC.getPhone());
                holder.address.setText(userMC.getAddress());
                holder.type.setText(userMC.getUserType());
                if (!userMC.getUserType().equals("Customer")) {
                    holder.pass.setText(userMC.getPassword());
                    holder.pass.setVisibility(View.GONE);
                }
                if (userMC.getImage().isEmpty()) {

                } else {
                    Glide.with(AdminStaffManagement.this).load(userMC.getImage()).fitCenter().into(holder.userImage);
                }

            }

            @Override
            public void onDataChanged() {
                Log.i("FirebaseRecyclerAdapter", "onDataChanged");
                super.onDataChanged();
            }


            @Override
            public void onError(DatabaseError e) {
                Log.i("FirebaseRecyclerAdapter", e.getMessage());
                //Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            @NonNull
            @Override
            public AdminStaffManagement.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_user_view_row, viewGroup, false);
                return new AdminStaffManagement.PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, pass, phone, address, type;
        ImageView userImage;


        public PostsViewHolder(@NonNull View v) {
            super(v);
            pass = v.findViewById(R.id.tvViewUserPass_id);
            userImage = v.findViewById(R.id.userImageView);
            name = v.findViewById(R.id.tvViewUserName_id);
            email = v.findViewById(R.id.tvViewUserEmail_id);
            phone = v.findViewById(R.id.tvViewUserPhone_id);
            address = v.findViewById(R.id.tvViewUserAddress_id);
            type = v.findViewById(R.id.tvViewUserType_id);
        }
    }

}