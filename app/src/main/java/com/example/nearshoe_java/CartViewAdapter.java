package com.example.nearshoe_java;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.CartItemMC;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.MyHolder> {
    Context context;
    List<CartItemMC> cartItemMCList;
    FirebaseAuth mAuth;
    private CartViewAdapter.CallBack callBack;

    public CartViewAdapter(Context context, List<CartItemMC> usersList, FirebaseAuth mAuth,CartViewAdapter.CallBack callBack) {
        this.context = context;
        this.cartItemMCList = usersList;
        this.mAuth = mAuth;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_cart_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        CartItemMC cartItemMC = cartItemMCList.get(position);
        Log.i("CustomerViewCart", "onBindViewHolder");
        holder.name.setText(cartItemMC.getItemName());
        holder.price.setText(cartItemMC.getItemPrice());
        Glide.with(context).load(cartItemMC.getImageUrl()).into(holder.postImage);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                int itemIntValue = Integer.parseInt(item);
                String price = String.valueOf(Integer.parseInt(cartItemMC.getItemPrice()) * itemIntValue);
                holder.price.setText(price);
                DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).child(cartItemMC.getId()).child("tempPrice").setValue(price);
                DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).child(cartItemMC.getId()).child("quantity").setValue(item);
                callBack.onItemClicked();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CustomerViewCart", "Deleting:" + cartItemMC.getId());
                DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).child(cartItemMC.getId()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemMCList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView postImage, deleteBtn;
        TextView name, quantity, price;
        Spinner spinner;

        public MyHolder(@NonNull View v) {
            super(v);
            spinner = v.findViewById(R.id.quantitySpinner);
            name = v.findViewById(R.id.productNameTextView);
            quantity = v.findViewById(R.id.textViewQTY);
            price = v.findViewById(R.id.productTotalPriceTextView);
            deleteBtn = v.findViewById(R.id.deleteProductButton);
            postImage = v.findViewById(R.id.productImageView);
        }
    }

    public interface CallBack {
        void onItemClicked();

        void onItemClickedWithPosition(String id, String action, int position);

    }

}