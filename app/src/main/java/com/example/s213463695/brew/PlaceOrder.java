package com.example.s213463695.brew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceOrder extends Fragment {

    private PlaceOrderListener mListener;
    private ImageView plus;
    private ImageView minus;
    private TextView quantity;
    private TextView price;
    private Double curPrice;
    private AppCompatButton cancel;
    private AppCompatButton order;

    public PlaceOrder() {
    }

    public static PlaceOrder newInstance() {
        PlaceOrder fragment = new PlaceOrder();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        curPrice = mListener.getPrice();
        minus = (ImageView) v.findViewById(R.id.minus);
        plus = (ImageView) v.findViewById(R.id.plus);
        cancel = (AppCompatButton) v.findViewById(R.id.cancel_order);
        order = (AppCompatButton) v.findViewById(R.id.place_order);
        quantity = (TextView) v.findViewById(R.id.quant);
        price = (TextView) v.findViewById(R.id.price);

        quantity.setText("1");
        price.setText(String.format("%.2f", curPrice));

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quantity.getText().toString().equals("1")) {
                    Integer curQ = Integer.parseInt(quantity.getText().toString());
                    curQ--;
                    Double newPrice = curQ * curPrice;
                    quantity.setText(curQ.toString());
                    price.setText(String.format("%.2f", newPrice));
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quantity.getText().toString().equals("50")) {
                    Integer curQ = Integer.parseInt(quantity.getText().toString());
                    curQ++;
                    Double newPrice = curQ * curPrice;
                    quantity.setText(curQ.toString());
                    price.setText(String.format("%.2f", newPrice));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.triggerCancel();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.triggerLocationMain(quantity.getText().toString());
            }
        });
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainListener(PlaceOrderListener order) {
        this.mListener = order;
    }

    public interface PlaceOrderListener {
        void triggerCancel();

        Double getPrice();

        void triggerLocationMain(String quantity);
    }

}
