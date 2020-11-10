package com.example.nearshoe_java;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Utilities {
    public static final DatabaseReference DB_USERS_REF = FirebaseDatabase.getInstance().getReference().child("USERS");
}
