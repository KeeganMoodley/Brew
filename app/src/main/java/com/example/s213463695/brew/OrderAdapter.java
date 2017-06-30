package com.example.s213463695.brew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by s213463695 on 2016/06/24.
 */

public class OrderAdapter extends ArrayAdapter<Order> {

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, R.layout.fragment_notifications, orders);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View orderView = view;
        if (orderView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            orderView = inflater.inflate(R.layout.order_view, parent, false);
        }
        orderView.setTag(getItem(position));

        Order or = getItem(position);
        TextView title = (TextView) orderView.findViewById(R.id.dateTime);
        TextView cost = (TextView) orderView.findViewById(R.id.cost);
        TextView quant = (TextView) orderView.findViewById(R.id.quantB);
        TextView countdown = (TextView) orderView.findViewById(R.id.countdown);
        TextView status = (TextView) orderView.findViewById(R.id.status);

        title.setText("Order " + or.getOrderNum() + " (" + or.getDate() + " - " + or.getTime() + ")");
        Double costD = Double.parseDouble(or.getPrice());
        cost.setText("Cost: R" + String.format("%.2f", costD));
        quant.setText(or.getQuantity() + " x ");
        if (!or.getStopThread()) {
            if (or.getCurSecond() < 10)
                countdown.setText("(0" + String.format("%1.0f", or.getCurMinute()) + ":0" + String.format("%1.0f", or.getCurSecond()) + ")");
            else
                countdown.setText("(0" + String.format("%1.0f", or.getCurMinute()) + ":" + String.format("%2.0f", or.getCurSecond()) + ")");
        } else {
            countdown.setText("");
        }
        if (or.getStatus().equals("queue"))
            status.setText("Order in queue ");
        else
            status.setText("Order dispatched");

        return orderView;
    }
}
