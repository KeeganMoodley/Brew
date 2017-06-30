package com.example.s213463695.brew;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by s213463695 on 2016/07/01.
 */
public class Block implements Serializable{

    private String name;
    private ArrayList<Row> rows;

    public Block(String name, ArrayList<Row> rows) {
        this.name = name;
        this.rows = rows;
    }

    public Block(String s) {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public void addRow(Integer i, Integer seatC) {
        Row newR=new Row(i,seatC);
        rows.add(newR);
    }

    public class Row implements Serializable{
        private Integer number;
        private Integer seatCount;

        public Row(Integer number, Integer seatCount) {
            this.number = number;
            this.seatCount = seatCount;
        }

        public Integer getNumber() {
            return number;
        }

        public Integer getSeatCount() {
            return seatCount;
        }
    }
}
