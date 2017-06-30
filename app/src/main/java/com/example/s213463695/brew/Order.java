package com.example.s213463695.brew;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.Date;

import static com.example.s213463695.brew.Home.main;
import static com.example.s213463695.brew.Home.not;
import static com.example.s213463695.brew.Home.orders;

/**
 * Created by s213463695 on 2016/06/24.
 */
public class Order {

    private String time;
    private String date;
    private String quantity;
    private String price;
    private Double curMinute;
    private Double curSecond;
    private Integer orderNum;
    private String status;
    private OrderThread orderT;
    private Boolean stopThread =false;
    private Date addedAt;
    private String dateIndex;

    public void setStopThread(Boolean stopThread) {
        this.stopThread = stopThread;
    }

    public Boolean getStopThread() {
        return stopThread;
    }

    public Order(String time, String date, String quantity, String price, Double curMinute, Double curSecond, Integer orderNum, String status,Date added, String dateIndex) {
        this.time = time;
        this.date = date;
        this.quantity = quantity;
        this.price = price;
        this.curMinute = curMinute;
        this.curSecond = curSecond;
        this.orderNum = orderNum;
        this.status = status;
        this.addedAt=added;
        this.dateIndex=dateIndex;
    }

    public String getDateIndex() {
        return dateIndex;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void startThread(){
        this.orderT=new OrderThread(this);
        orderT.start();
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void stopThread(){
        stopThread=true;
    }

    public void setCurMinute(Double curMinute) {
        this.curMinute = curMinute;
    }

    public void setCurSecond(Double curSecond) {
        this.curSecond = curSecond;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public void decrease(){
        if(curSecond<=0&&curMinute>0){
            curMinute--;
            curSecond=59.0;
        }else if(curSecond>0) {
            curSecond--;
        }
    }

    public Double getCurSecond() {
        return curSecond;
    }

    public Double getCurMinute() {
        return curMinute;
    }

    public class OrderThread extends Thread{

        private Order parent;
        public OrderThread(Order p){
            super();
            this.parent=p;
        }

        @Override
        public void run() {
            while (!getStopThread()) {
                decrease();
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(not!=null)
                            not.notifyAdapt();
                    }
                });
                if (getCurMinute() <= 0 && getCurSecond() <= 0) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.changeOrder(parent,"counted");
                                if(not!=null)
                                    not.notifyAdapt();
                            }
                        });
                        parent.setStopThread(true);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
