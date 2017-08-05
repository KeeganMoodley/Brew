package com.example.s213463695.brew;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by s214079694 on 2017/06/29.
 */

public abstract class Food implements Serializable, Cloneable {
        int quantity, quantityAvailable, id, type;
        double price;
        String title, nutrition, dietary;
        boolean halaal;
        byte[] image;

        public Food(int id, int type, byte[] image, double price, String title, String nutrition, String dietary, boolean halaal, int quantityAvailable) {
            this.image = image;
            this.price = price;
            this.title = title;
            this.halaal = halaal;
            quantity = 0;
            this.quantityAvailable = quantityAvailable;
            this.nutrition = nutrition;
            this.dietary = dietary;
            this.id = id;
            this.type = type;
        }

        public void incQuantity() {
            quantity++;
        }

        public void decQuantity() {
            if (quantity > 0)
                quantity--;
        }

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
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

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Food food = null;
            try {
                food = (Food) super.clone();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return food;
        }
}
