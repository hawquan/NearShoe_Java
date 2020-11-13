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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class AdminViewFeedback extends AppCompatActivity {
    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<OrderItemMC, AdminViewFeedback.PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        getPosts();
    }

    private void initializeComponents() {
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.recyclerViewCustomerFeedBack_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void getPosts() {
        Query mPostsRef = DBUtilClass.DB_ORDER_REF.orderByChild("feedback").equalTo("");
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<OrderItemMC> options = new FirebaseRecyclerOptions.Builder<OrderItemMC>()
                .setQuery(mPostsRef, OrderItemMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<OrderItemMC, AdminViewFeedback.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminViewFeedback.PostsViewHolder holder, int position, @NonNull final OrderItemMC orderItemMC) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                if (orderItemMC.getFeedback().isEmpty()) {
                    holder.quantity_NameTV.setText(orderItemMC.getItems());
                    holder.feedBackTV.setText("Feedback: " + orderItemMC.getFeedback());
                    holder.ratingTV.setText("Ratings: " + orderItemMC.getRating() + "/5.0");
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
            public AdminViewFeedback.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_feedback_row, viewGroup, false);
                return new AdminViewFeedback.PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView quantity_NameTV, feedBackTV, ratingTV;


        public PostsViewHolder(@NonNull View v) {
            super(v);
            quantity_NameTV = v.findViewById(R.id.tv_Items_id);
            feedBackTV = v.findViewById(R.id.feedBackRemarks_id);
            ratingTV = v.findViewById(R.id.feedBackRatings_id);

        }
    }

}