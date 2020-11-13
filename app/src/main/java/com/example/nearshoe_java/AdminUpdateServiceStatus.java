package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearshoe_java.ModelClasses.OrderItemMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class AdminUpdateServiceStatus extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<OrderItemMC, AdminUpdateServiceStatus.PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_status);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        getPosts();

    }

    private void initializeComponents() {
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.adminUpdateServiceStatus_rv_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void getPosts() {
        Query mPostsRef = DBUtilClass.DB_ORDER_REF.orderByChild("status");
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<OrderItemMC> options = new FirebaseRecyclerOptions.Builder<OrderItemMC>()
                .setQuery(mPostsRef, OrderItemMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<OrderItemMC, AdminUpdateServiceStatus.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminUpdateServiceStatus.PostsViewHolder holder, int position, @NonNull final OrderItemMC orderItemMC) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.quantity_NameTV.setText(orderItemMC.getItems());
                holder.statusTV.setText(orderItemMC.getStatus());
                if (orderItemMC.getStatus().equals("Pending")) {
                    holder.statusTV.setTextColor(Color.BLUE);
                    holder.completedBtn.setVisibility(View.VISIBLE);
                    holder.cancelledBtn.setVisibility(View.VISIBLE);
                    holder.deliveringBtn.setVisibility(View.VISIBLE);
                    holder.pickingUpBtn.setVisibility(View.VISIBLE);
                } else if (orderItemMC.getStatus().equals("Completed")) {
                    holder.statusTV.setTextColor(Color.GREEN);
                    holder.completedBtn.setVisibility(View.GONE);
                    holder.cancelledBtn.setVisibility(View.GONE);
                    holder.deliveringBtn.setVisibility(View.GONE);
                    holder.pickingUpBtn.setVisibility(View.GONE);
                } else if (orderItemMC.getStatus().equals("Cancelled")) {
                    holder.statusTV.setTextColor(Color.RED);
                    holder.completedBtn.setVisibility(View.GONE);
                    holder.cancelledBtn.setVisibility(View.GONE);
                    holder.deliveringBtn.setVisibility(View.GONE);
                    holder.pickingUpBtn.setVisibility(View.GONE);
                }


                holder.amountTV.setText(orderItemMC.getAmount());

                holder.completedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).child("status").setValue("Completed");
                        Toast.makeText(AdminUpdateServiceStatus.this, "Status updated to Completed", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.cancelledBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).child("status").setValue("Cancelled");
                        Toast.makeText(AdminUpdateServiceStatus.this, "Status updated to Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.pickingUpBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).child("status").setValue("PickingUp");
                        Toast.makeText(AdminUpdateServiceStatus.this, "Status updated to PickingUp", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.deliveringBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).child("status").setValue("Delivering");
                        Toast.makeText(AdminUpdateServiceStatus.this, "Status updated to Delivering", Toast.LENGTH_SHORT).show();
                    }
                });
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
            public AdminUpdateServiceStatus.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_order_view_row, viewGroup, false);
                return new AdminUpdateServiceStatus.PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        Button feedbackBtn, pickingUpBtn, deliveringBtn, cancelledBtn, completedBtn;
        TextView quantity_NameTV, statusTV, amountTV;
        EditText editText;
        RatingBar ratingBar;

        public PostsViewHolder(@NonNull View v) {
            super(v);
            pickingUpBtn = v.findViewById(R.id.btnPickUp_id);
            deliveringBtn = v.findViewById(R.id.btnDelivering_id);
            ratingBar = v.findViewById(R.id.ratingBar_id);
            cancelledBtn = v.findViewById(R.id.btnCancelled_id);
            completedBtn = v.findViewById(R.id.btnCompleted_id);
            amountTV = v.findViewById(R.id.tv_serviceBill_id);
            feedbackBtn = v.findViewById(R.id.btn_feedBack_id);
            quantity_NameTV = v.findViewById(R.id.tv_quantityName_id);
            statusTV = v.findViewById(R.id.tv_serviceStatus_id);
            editText = v.findViewById(R.id.editTextFeedBack_id);
            editText.setVisibility(View.GONE);
            feedbackBtn.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }
    }

}