package com.example.nearshoe_java.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMC implements Parcelable {

    String id;
    String name;
    String email;
    String password;
    String address;
    String phone;
    String userType;
    String image;

    public UserMC(String id, String name, String email, String password, String address, String phone, String userType, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.userType = userType;
        this.image = image;
    }

    public UserMC(String name, String email,String phone, String userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }

    public UserMC() {

    }

    protected UserMC(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        address = in.readString();
        phone = in.readString();
        userType = in.readString();
        image = in.readString();
    }

    public static final Creator<UserMC> CREATOR = new Creator<UserMC>() {
        @Override
        public UserMC createFromParcel(Parcel in) {
            return new UserMC(in);
        }

        @Override
        public UserMC[] newArray(int size) {
            return new UserMC[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(userType);
        dest.writeString(image);
    }
}
