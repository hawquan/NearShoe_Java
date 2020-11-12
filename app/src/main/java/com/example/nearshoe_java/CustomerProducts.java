package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.CartItemMC;
import com.example.nearshoe_java.ModelClasses.ProductMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CustomerProducts extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    FloatingActionButton mFAB;
    TextView textCartItemCount;
    int mCartItemCount = 0;
    boolean mProcessCart;
    DatabaseReference mCartRef;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<ProductMC, CustomerProducts.PostsViewHolder> firebasePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_products);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        initializeComponents();
        getPosts();
    }

    @Override
    public void onBackPressed() {
        int count = Integer.parseInt(textCartItemCount.getText().toString());
        if (count == 0) {
            finish();
        } else {
            pressingBackButton();
        }

    }

    private void initializeComponents() {
        mProgressDialog = new ProgressDialog(this);
        mCartRef = DBUtilClass.DB_PRODUCTS_REF;
        mRecyclerView = findViewById(R.id.recyclerView_customer_products_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void getPosts() {
        Query mPostsRef = DBUtilClass.DB_PRODUCTS_REF;
        Log.i("FirebaseRecyclerAdapter", "gettingPosts");
        FirebaseRecyclerOptions<ProductMC> options = new FirebaseRecyclerOptions.Builder<ProductMC>()
                .setQuery(mPostsRef, ProductMC.class).build();
        firebasePostAdapter = new FirebaseRecyclerAdapter<ProductMC, CustomerProducts.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CustomerProducts.PostsViewHolder holder, int position, @NonNull final ProductMC myPost) {
                Log.i("FirebaseRecyclerAdapter", "onBindViewHolder");
                holder.name.setText(myPost.getName());
                holder.desc.setText(myPost.getDescription());
                holder.amount.setText("Price:" + myPost.getPrice());
                Log.i("FirebaseRecyclerAdapter", "Available:" + myPost.getIsAvailable());
                if (myPost.getIsAvailable().equals("Available")) {
                    holder.available.setText("Available");
                } else {
                    holder.available.setText("Out of stock");
                }
                Glide.with(CustomerProducts.this).load(myPost.getImageUrl()).fitCenter().into(holder.postImage);

                // Set Title for Add / Remove to Cart,
                DBUtilClass.DB_CART_REF.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(myPost.getId())) {
                            holder.addToCart.setText("RemoveFromCart");
                        } else {
                            holder.addToCart.setText("AddtoCart");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // If like button is clicked
                holder.addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("CustomerProducts", "AddCartClicked");
                        mProcessCart = true;
                        DBUtilClass.DB_CART_REF.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (mProcessCart) {
                                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(myPost.getId())) {
                                        DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).child(myPost.getId()).removeValue();
                                        int count = Integer.parseInt(textCartItemCount.getText().toString());
                                        int newValue = count-1;
                                        setupBadge(newValue);
                                        Toast.makeText(CustomerProducts.this, "Item removed from Cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mProgressDialog.setTitle("Cart");
                                        mProgressDialog.setMessage("Adding Item to Cart");
                                        mProgressDialog.show();
                                        final String currentUser = mAuth.getCurrentUser().getUid();
                                        CartItemMC cartItemMC = new CartItemMC();
                                        cartItemMC.setId(myPost.getId());
                                        cartItemMC.setRequesterId(currentUser);
                                        cartItemMC.setQuantity("1");
                                        cartItemMC.setItemName(myPost.getName());
                                        cartItemMC.setItemPrice(myPost.getPrice());
                                        DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).child(myPost.getId()).setValue(cartItemMC).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    int count = Integer.parseInt(textCartItemCount.getText().toString());
                                                    int newValue = count+1;
                                                    setupBadge(newValue);
                                                    Toast.makeText(CustomerProducts.this, "Item Added In Cart", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(CustomerProducts.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    mProcessCart = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
            public CustomerProducts.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                Log.i("FirebaseRecyclerAdapter", "onCreateViewHolder");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_single_post, viewGroup, false);
                return new CustomerProducts.PostsViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebasePostAdapter);
        firebasePostAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, amount, available, addToCart;
        ImageView postImage;
        CardView cardView;

        public PostsViewHolder(@NonNull View v) {
            super(v);
            addToCart = v.findViewById(R.id.postAddToCart_id);
            cardView = v.findViewById(R.id.cardView_id);
            name = v.findViewById(R.id.postName_id);
            desc = v.findViewById(R.id.postDesc_id);
            amount = v.findViewById(R.id.postAmount_id);
            available = v.findViewById(R.id.postAvailable);
            postImage = v.findViewById(R.id.postImage_id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);
        final MenuItem menuItem = menu.findItem(R.id.cartFragment);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge_text_view);
        textCartItemCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCart();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setupBadge(int mCartItemCount) {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void showCart() {
        viewCart();
    }

    private void pressingBackButton() {
        int count = Integer.parseInt(textCartItemCount.getText().toString());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Cart");
        alertDialogBuilder.setMessage("What do you want to do with " + count + " Items in cart?");
        alertDialogBuilder.setPositiveButton("Discard them", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).removeValue();
                Toast.makeText(CustomerProducts.this, "Cart Items Discarded", Toast.LENGTH_SHORT).show();
                setupBadge(0);
            }
        });
        alertDialogBuilder.setNegativeButton("View Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewCart();
            }
        });
        alertDialogBuilder.setNeutralButton("Nothing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void viewCart() {
        startActivity(new Intent(CustomerProducts.this, CustomerViewCart.class));
    }
}

