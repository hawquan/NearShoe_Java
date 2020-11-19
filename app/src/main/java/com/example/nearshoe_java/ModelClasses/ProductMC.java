package com.example.nearshoe_java.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductMC implements Parcelable {
    protected ProductMC(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        isAvailable = in.readString();
        imageUrl = in.readString();
        time = in.readString();
    }

    public static final Creator<ProductMC> CREATOR = new Creator<ProductMC>() {
        @Override
        public ProductMC createFromParcel(Parcel in) {
            return new ProductMC(in);
        }

        @Override
        public ProductMC[] newArray(int size) {
            return new ProductMC[size];
        }
    };

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public ProductMC(String id, String name, String description, String price, String isAvailable, String imageUrl, String time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
        this.time = time;
    }

    private String id;
    private String name;
    private String description;
    private String price;
    private String isAvailable;
    private String imageUrl;
    private String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public ProductMC() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(isAvailable);
        dest.writeString(imageUrl);
        dest.writeString(time);
    }
}
