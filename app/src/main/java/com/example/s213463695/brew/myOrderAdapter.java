package com.example.s213463695.brew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import static com.example.s213463695.brew.Home.foodOrder;
import static com.example.s213463695.brew.Home.orders;

/**
 * Created by s214079694 on 2017/07/28.
 */

public class myOrderAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Order> order; // header titles
    // child data in format of header title, child title
    private HashMap<Order, List<Food>> foods;

    public myOrderAdapter(Context context, List<Order> order, HashMap<Order, List<Food>> foods) {
        this.context = context;
        this.order = order;
        this.foods = foods;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return foods.get(orders.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Food childText = (Food) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.food_view, null);
        }

        TextView q = (TextView) convertView.findViewById(R.id.txtQ);
        TextView title = (TextView) convertView.findViewById(R.id.txtTitleNoti);
        TextView price = (TextView) convertView.findViewById(R.id.txtPriceNoti);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imgNoti);

        q.setText(String.valueOf(childText.getQuantity()));
        title.setText(childText.getTitle());
        price.setText("R" + String.format("%.2f", childText.getPrice() * childText.quantity));
        byte[] bytes = childText.getImage();
        Bitmap pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(pic);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return foods.get(orders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return orders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return orders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //String headerTitle = (String) getGroup(groupPosition);
        Order order = (Order) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        TextView time = (TextView) convertView.findViewById(R.id.txtTime);
        TextView total = (TextView) convertView.findViewById(R.id.txtTotalPrice);
        TextView queue = (TextView) convertView.findViewById(R.id.txtQueue);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        if (!order.getStopThread()) {
            if (order.getCurMinute() < 10) {
                if (order.getCurSecond() < 10)
                    time.setText("(0" + String.format("%1.0f", order.getCurMinute()) + ":0" + String.format("%1.0f", order.getCurSecond()) + ")");
                else
                    time.setText("(0" + String.format("%1.0f", order.getCurMinute()) + ":" + String.format("%2.0f", order.getCurSecond()) + ")");
            } else if (order.getCurSecond() < 10)
                time.setText("(" + String.format("%1.0f", order.getCurMinute()) + ":0" + String.format("%1.0f", order.getCurSecond()) + ")");
            else
                time.setText("(" + String.format("%1.0f", order.getCurMinute()) + ":" + String.format("%2.0f", order.getCurSecond()) + ")");
        } else {
            time.setText("");
        }
        if (order.getStatus().equals("queue"))
            queue.setText("Order in queue ");
        else
            queue.setText("Order dispatched");
        lblListHeader.setText("Order " + order.getOrderNum());
        total.setText("R" + String.format("%.2f", order.getTotal()));
        return convertView;
    }

    private class timeThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
