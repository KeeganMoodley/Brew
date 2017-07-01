package com.example.s213463695.brew;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by s214079694 on 2017/06/29.
 */

public class FoodOrder extends Fragment implements TotalListener {
    static ArrayList<Food> foods;
    private FoodOrderListener mListener;
    View view;
    double total;
    TextView txtTotal;
    //private static ServerThread server = null;

    public FoodOrder() {

    }

    public static FoodOrder newInstance() {
        FoodOrder fragment = new FoodOrder();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Login.serverLink.requestFood();
        return fragment;
    }

    protected static void populateList(Food food) {
        /*foods.add(new Food(R.drawable.brew_logo, 30, "Burger", true));
        foods.add(new Food(R.drawable.brew_logo, 40, "Hot dog", true));
        foods.add(new Food(R.drawable.brew_logo, 20, "Russian Roll", false));
        foods.add(new Food(R.drawable.brew_logo, 10, "Hot Chips", true));
        foods.add(new Food(R.drawable.brew_logo, 100, "Wors", true));
        foods.add(new Food(R.drawable.brew_logo, 250, "Salad", true));*/

        foods.add(new Liquid(R.drawable.brew_logo, 30, "Coke", "nutrituion", "dietary", false, 100, 500));
        foods.add(food);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        foods = new ArrayList<>();
        //populateList();
        view = inflater.inflate(R.layout.fragment_order_food, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        FoodAdapter foodAdapter = new FoodAdapter(foods);
        foodAdapter.setTotalListener(FoodOrder.this);
        recyclerView.setAdapter(foodAdapter);

        getTotal();

        /*recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcTotal();
            }
        });*/

        return view;
    }

    private void getTotal() {
        txtTotal = (TextView) view.findViewById(R.id.total);
        total = 0;
        for (Food food : foods)
            total += food.getTotal();
        txtTotal.setText("R" + String.format("%.2f", total));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainListener(FoodOrderListener order) {
        this.mListener = order;
    }

    @Override
    public void calcTotal() {
        getTotal();
    }

    public interface FoodOrderListener {
        void triggerCancel();

        void triggerLocationMain(String quantity);
    }
}
