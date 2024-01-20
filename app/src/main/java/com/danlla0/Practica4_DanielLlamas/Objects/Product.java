package com.danlla0.Practica4_DanielLlamas.Objects;

import android.graphics.Bitmap;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int times_in_lists;
    private String amount;

    private String imgName;
    private Bitmap imgProduct;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public Bitmap getImgProduct() {
        return imgProduct;
    }

    public void setImgProduct(Bitmap imgProduct) {
        this.imgProduct = imgProduct;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimes_in_lists() {
        return times_in_lists;
    }

    public void setTimes_in_lists(int times_in_lists) {
        this.times_in_lists = times_in_lists;
    }

    public Product(int id, String name, String description, double price, String imgName, Bitmap imgProduct) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgName = imgName;
        this.imgProduct = imgProduct;
    }
    public Product(int id, String name, String description, double price, int times_in_lists, String imgName, Bitmap imgProduct) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgName = imgName;
        this.imgProduct = imgProduct;
    }

    public Product(int id, String name, String description, double price, Bitmap imgProduct) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgName = imgName;
        this.imgProduct = imgProduct;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", times_in_lists=" + times_in_lists +
                ", amount='" + amount + '\'' +
                ", imgName='" + imgName + '\'' +
                ", imgProduct=" + imgProduct +
                '}';
    }

    public Product() {

    }
}
