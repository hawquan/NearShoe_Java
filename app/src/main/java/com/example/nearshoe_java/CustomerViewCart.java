package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.CartItemMC;
import com.example.nearshoe_java.ModelClasses.ProductMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class CustomerViewCart extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    FirebaseUser currentUser;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<ProductMC, CustomerViewCart.PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_view_cart);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseApp.initializeApp(this);

        mRecyclerView = findViewById(R.id.view_cart_recycler_view_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getPosts() {/*
        Query mCartRef = DBUtilClass.DB_CART_REF;
        Log.i("FirebaseRecyclerAdapter", "gettingCartItems");
        FirebaseRecyclerOptions<CartItemMC> options = new FirebaseRecyclerOptions.Builder<CartItemMC>()
                .setQuery(mCartRef, CartItemMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<CartItemMC, CustomerViewCart.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CustomerViewCart.PostsViewHolder holder, int position, @NonNull final CartItemMC cartItemMC) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.name.setText(cartItemMC.getItemName());
                Glide.with(CustomerViewCart.this).load(cartItemMC.getImageUrl()).fitCenter().into(holder.postImage);
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
            public CustomerViewCart.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_cart_row, viewGroup, false);
                return new CustomerViewCart.PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();*/
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage,deleteBtn;
        TextView name, quantity, amount, available, addToCart;
        CardView cardView;
        Spinner spinner;

        public PostsViewHolder(@NonNull View v) {
            super(v);
//            addToCart = v.findViewById(R.id.postAddToCart_id);
//            addToCart.setVisibility(View.GONE);
//            cardView = v.findViewById(R.id.cardView_id);
//            name = v.findViewById(R.id.postName_id);
//            desc = v.findViewById(R.id.postDesc_id);
//            amount = v.findViewById(R.id.postAmount_id);
//            available = v.findViewById(R.id.postAvailable);
//            postImage = v.findViewById(R.id.postImage_id);
        }
    }

}