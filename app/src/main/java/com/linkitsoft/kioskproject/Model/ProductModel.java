package com.linkitsoft.kioskproject.Model;

public class ProductModel {

    int Id;
    int kioskId;
    int productId;
    int stock;
    int quantity;
    double price;
    String pname;

    public ProductModel() {
    }

    public ProductModel(int id, int kioskId, int productId, int stock, int quantity, double price, String pname) {
        Id = id;
        this.kioskId = kioskId;
        this.productId = productId;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.pname = pname;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getKioskId() {
        return kioskId;
    }

    public void setKioskId(int kioskId) {
        this.kioskId = kioskId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
