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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearshoe_java.ModelClasses.OrderItemMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class CustomerServiceStatus extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    float rating = 0;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<OrderItemMC, CustomerServiceStatus.PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_status);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        getPosts();

    }

    private void initializeComponents() {
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.customer_service_status_rv_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void getPosts() {
        Query mPostsRef = DBUtilClass.DB_ORDER_REF.orderByChild("customerId").equalTo(mAuth.getCurrentUser().getUid());
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<OrderItemMC> options = new FirebaseRecyclerOptions.Builder<OrderItemMC>()
                .setQuery(mPostsRef, OrderItemMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<OrderItemMC, CustomerServiceStatus.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CustomerServiceStatus.PostsViewHolder holder, int position, @NonNull final OrderItemMC orderItemMC) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.quantity_NameTV.setText(orderItemMC.getItems());
                holder.statusTV.setText(orderItemMC.getStatus());
                if (orderItemMC.getStatus().equals("Pending")) {
                    holder.statusTV.setTextColor(Color.BLUE);
                } else if (orderItemMC.getStatus().equals("Completed")) {
                    holder.statusTV.setTextColor(Color.GREEN);
                } else if (orderItemMC.getStatus().equals("Cancelled")) {
                    holder.statusTV.setTextColor(Color.RED);
                }
                holder.amountTV.setText(orderItemMC.getAmount());
                if (orderItemMC.getStatus().equals("Completed")) {
                    if (orderItemMC.getFeedback().equals("")) {
                        holder.feedbackBtn.setVisibility(View.VISIBLE);
                        holder.editText.setVisibility(View.VISIBLE);
                        holder.ratingBar.setFocusable(true);
                    } else {
                        holder.ratingBar.setRating(Float.parseFloat(orderItemMC.getRating()));
                        holder.ratingBar.setIsIndicator(true);
                        holder.editText.setText(orderItemMC.getFeedback());
                        holder.editText.setVisibility(View.VISIBLE);
                        holder.editText.setFocusable(false);
                        holder.feedbackBtn.setVisibility(View.GONE);
                    }
                } else {
                    holder.feedbackBtn.setVisibility(View.GONE);
                }
                holder.feedbackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.editText.getText().toString().isEmpty()) {
                            holder.editText.setError("Enter Valid Feedback!!!");
                            holder.editText.requestFocus();
                        } else {
                            DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).child("rating").setValue(String.valueOf(Float.parseFloat(String.valueOf(holder.ratingBar.getRating()))));
                            DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).child("feedback").setValue(holder.editText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CustomerServiceStatus.this, "Feedback Saved, thanks for your value able feedback.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(CustomerServiceStatus.this, "Feedback Not Saved due to " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
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
            public CustomerServiceStatus.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_order_view_row, viewGroup, false);
                return new CustomerServiceStatus.PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        Button feedbackBtn, cancelledBtn, completedBtn, pickingUpBtn, deliveringBtn;
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
            cancelledBtn.setVisibility(View.GONE);
            completedBtn.setVisibility(View.GONE);
            pickingUpBtn.setVisibility(View.GONE);
            deliveringBtn.setVisibility(View.GONE);
        }
    }

}