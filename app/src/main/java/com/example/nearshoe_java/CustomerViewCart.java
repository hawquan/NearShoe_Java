package com.example.nearshoe_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nearshoe_java.ModelClasses.CartItemMC;
import com.example.nearshoe_java.ModelClasses.OrderItemMC;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class CustomerViewCart extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnPlaceOrder;
    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    FirebaseUser currentUser;
    ImageButton pickLocation;
    EditText currentAddress;
    String getAddress;
    double totalPrice;
    TextView totalAmountTV;
    CartViewAdapter adapter;
    List<CartItemMC> cartItemMCList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_view_cart);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseApp.initializeApp(this);
        adapter = new CartViewAdapter(this, cartItemMCList, mAuth, callback);

        mProgressDialog = new ProgressDialog(CustomerViewCart.this);
        mProgressDialog.setTitle("Order");
        mProgressDialog.setMessage("Placing order, please wait!!!");

        currentAddress = findViewById(R.id.deliveryLocation);
        btnPlaceOrder = findViewById(R.id.placeOrderBtn_id);
        totalAmountTV = findViewById(R.id.totalAmountTV_id);
        pickLocation = findViewById(R.id.btPickLocation);
        mRecyclerView = findViewById(R.id.view_cart_recycler_view_id);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        getCartItemsFromRealTimeDatabase();

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    placeOrder();
                }

            }
        });


        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlacePicker();
            }
        });

        //getCartItems();
    }

    private void getCartItemsFromRealTimeDatabase() {
        DBUtilClass.DB_CART_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        CartItemMC itemMC = ds.getValue(CartItemMC.class);
                        cartItemMCList.add(itemMC);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validate() {
        if (currentAddress.getText().toString().isEmpty()) {
            currentAddress.setError("Address is required.");
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CustomerViewCart.this, CustomerDashboard.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void placeOrder() {
        mProgressDialog.show();
        DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String items = "";
                OrderItemMC orderItemMC = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        items += "(" + ds.child("quantity").getValue(String.class) + ")" + ds.child("itemName").getValue(String.class) + " ";
                        String currentUserId = mAuth.getCurrentUser().getUid();
                        String uniqueId = DBUtilClass.DB_ORDER_REF.child(currentUserId).push().getKey();
                        orderItemMC = new OrderItemMC();
                        orderItemMC.setOrderId(uniqueId);
                        orderItemMC.setAmount(totalAmountTV.getText().toString());
                        orderItemMC.setCustomerId(currentUserId);
                        orderItemMC.setStatus("Pending");
                        orderItemMC.setItems(items);
                        orderItemMC.setAddress(getAddress);
                        orderItemMC.setFeedback("");
                        orderItemMC.setCustomerName(mAuth.getCurrentUser().getEmail());
                        orderItemMC.setRating("");
                    }
                }
                DBUtilClass.DB_ORDER_REF.child(orderItemMC.getOrderId()).setValue(orderItemMC).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(CustomerViewCart.this, "Order placed successfully!!!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(CustomerViewCart.this, CustomerDashboard.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            Toast.makeText(CustomerViewCart.this, "Unable to place order due to " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private final static int PLACE_PICKER_REQUEST = 999;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;

                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        currentAddress.setText(address);
                        getAddress = address.trim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    private CartViewAdapter.CallBack callback = new CartViewAdapter.CallBack() {
        @Override
        public void onItemClicked() {
            Log.i("", "onItemClicked Called");
            calculateTotalPrice();
        }

        @Override
        public void onItemClickedWithPosition(String id, String action, int position) {

        }
    };

    private void calculateTotalPrice() {
        Log.i("CustomerViewCart", "calculateTotalPrice called");
        DBUtilClass.DB_CART_REF.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int ttl = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        ttl = ttl + Integer.parseInt(ds.child("tempPrice").getValue(String.class));
                        Log.i("CustomerViewCart", ds.child("tempPrice").getValue(String.class) + "|" + ttl);
                    }
                }
                totalAmountTV.setText("Total Bill is " + String.valueOf(ttl));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}