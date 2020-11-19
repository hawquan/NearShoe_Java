package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.ProductMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class AdminProductManagement extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    RecyclerView mRecyclerView;
    FloatingActionButton mFAB;
    public static String type = "ADMIN";
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<ProductMC, PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        getPosts();

    }

    private void getPosts() {
        Query mPostsRef = DBUtilClass.DB_PRODUCTS_REF;
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<ProductMC> options = new FirebaseRecyclerOptions.Builder<ProductMC>()
                .setQuery(mPostsRef, ProductMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<ProductMC, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, int position, @NonNull final ProductMC myPost) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.name.setText(myPost.getName());
                holder.desc.setText(myPost.getDescription());
                holder.amount.setText("Price:" + myPost.getPrice());
                if (myPost.getIsAvailable().equals("Available")) {
                    holder.available.setText("Available");
                } else {
                    holder.available.setText("Out of stock");
                }
                Glide.with(AdminProductManagement.this).load(myPost.getImageUrl()).fitCenter().into(holder.postImage);

                holder.editPostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminProductManagement.this, ProductCrud.class);
                        intent.putExtra("Source", "AdminProductManagement");
                        intent.putExtra("Purpose", "EditProduct");
                        intent.putExtra("Product",myPost);
                        startActivity(intent);
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
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_single_post, viewGroup, false);
                return new PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, amount, available, addToCart;
        ImageView postImage, editPostImage;
        CardView cardView;

        public PostsViewHolder(@NonNull View v) {
            super(v);
            editPostImage = v.findViewById(R.id.editPost_id);
            addToCart = v.findViewById(R.id.postAddToCart_id);
            addToCart.setVisibility(View.GONE);
            cardView = v.findViewById(R.id.cardView_id);
            name = v.findViewById(R.id.postName_id);
            desc = v.findViewById(R.id.postDesc_id);
            amount = v.findViewById(R.id.postAmount_id);
            available = v.findViewById(R.id.postAvailable);
            postImage = v.findViewById(R.id.postImage_id);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab_addProduct) {
            addProduct();
        }
    }

    private void addProduct() {
        Intent intent = new Intent(AdminProductManagement.this, ProductCrud.class);
        intent.putExtra("Source", "AdminProductManagement");
        intent.putExtra("Purpose", "AddProduct");
        startActivity(intent);
    }

    private void initializeComponents() {
        mRecyclerView = findViewById(R.id.rv_products_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mFAB = findViewById(R.id.fab_addProduct);
        mFAB.setOnClickListener(this);
    }


}