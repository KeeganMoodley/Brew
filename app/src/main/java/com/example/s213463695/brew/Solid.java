package com.example.s213463695.brew;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by s214079694 on 2017/07/01.
 */

public class Solid extends Food implements Serializable{
    double length, width, height;

    public Solid(int id, int type, byte[] image, double price, String title, String nutrition, String dietary, boolean halaal, int quantityAvailable, double length, double width, double height) {
        super(id, type, image, price, title, nutrition, dietary, halaal, quantityAvailable);
        this.length = length;
        this.width = width;
        this.height = height;
    }

    /*public Solid(byte[] image, double price, String title, String nutrition, String dietary, boolean halaal, int quantityAvailable, double length, double width, double height) {
        super(image, price, title, nutrition, dietary, halaal, quantityAvailable);
        this.length = length;
        this.width = width;
        this.height = height;
    }*/

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
