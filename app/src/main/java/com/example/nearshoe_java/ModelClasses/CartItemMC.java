package com.example.nearshoe_java.ModelClasses;

public class CartItemMC {
    public CartItemMC() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public CartItemMC(String id, String itemPrice, String itemName, String requesterId, String quantity, String imageUrl) {
        this.id = id;
        this.itemPrice = itemPrice;
        this.itemName = itemName;
        this.requesterId = requesterId;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    String id;
    String itemPrice;
    String itemName;
    String requesterId;
    String quantity;
    String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
