package com.example.nearshoe_java;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBUtilClass {
    public static final DatabaseReference DB_USERS_REF = FirebaseDatabase.getInstance().getReference().child("USERS");
    public static final DatabaseReference DB_PRODUCTS_REF = FirebaseDatabase.getInstance().getReference().child("PRODUCTS");
    public static final DatabaseReference DB_CART_REF = FirebaseDatabase.getInstance().getReference().child("CART");
    public static final DatabaseReference DB_PURCHASE_REF = FirebaseDatabase.getInstance().getReference().child("PURCHASE_HISTORY");
    public static final DatabaseReference DB_ORDER_REF = FirebaseDatabase.getInstance().getReference().child("ORDERS");

}
