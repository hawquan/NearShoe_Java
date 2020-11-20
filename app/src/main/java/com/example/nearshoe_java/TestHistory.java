package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.nearshoe_java.ModelClasses.OrderItemMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class TestHistory extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<OrderItemMC, TestHistory.PostsViewHolder> firebasePostAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_history);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        recyclerView = findViewById(R.id.testRV_id);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);

        getPosts();
    }

    private void getPosts() {
        Log.i("FirebaseRecyclerAdapter", "Getting Posts");
        Query mPostsRef = DBUtilClass.DB_ORDER_REF.orderByChild("customerId").equalTo(mAuth.getCurrentUser().getUid());
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<OrderItemMC> options = new FirebaseRecyclerOptions.Builder<OrderItemMC>()
                .setQuery(mPostsRef, OrderItemMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<OrderItemMC, TestHistory.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TestHistory.PostsViewHolder holder, int position, @NonNull final OrderItemMC orderItemMC) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.quantity_NameTV.setText(orderItemMC.getItems());
                holder.statusTV.setText(orderItemMC.getStatus());
                holder.userEmail.setText(orderItemMC.getCustomerName());
                if (orderItemMC.getStatus().equals("Pending")) {
                    holder.statusTV.setTextColor(Color.BLUE);
                } else if (orderItemMC.getStatus().equals("Completed")) {
                    holder.statusTV.setTextColor(Color.GREEN);
                } else if (orderItemMC.getStatus().equals("Cancelled")) {
                    holder.statusTV.setTextColor(Color.RED);
                }

                holder.amountTV.setText(orderItemMC.getAmount());
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
            public TestHistory.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_order_view_row, viewGroup, false);
                return new TestHistory.PostsViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        Button feedbackBtn, pickingUpBtn, deliveringBtn, cancelledBtn, completedBtn;
        TextView quantity_NameTV, statusTV, amountTV, userEmail;
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
            userEmail = v.findViewById(R.id.tv_userEmail);
            editText.setVisibility(View.GONE);
            feedbackBtn.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            feedbackBtn.setVisibility(View.GONE);
            pickingUpBtn.setVisibility(View.GONE);
            deliveringBtn.setVisibility(View.GONE);
            cancelledBtn.setVisibility(View.GONE);
            completedBtn.setVisibility(View.GONE);
        }
    }
}