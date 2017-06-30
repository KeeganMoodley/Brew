package com.example.s213463695.brew;

/**
 * Created by s214079694 on 2017/06/29.
 */

public class Food {
    int image, quantity;
    double price;
    String title;
    boolean halaal;

    public Food(int image, double price, String title, boolean halaal) {
        this.image = image;
        this.price = price;
        this.title = title;
        this.halaal = halaal;
        quantity = 0;
    }

    public void incQuantity() {
        quantity++;
    }

    public void decQuantity() {
        quantity--;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHalaal() {
        return halaal;
    }

    public void setHalaal(boolean halaal) {
        this.halaal = halaal;
    }

    public double getTotal() {
        return quantity * price;
    }
}
