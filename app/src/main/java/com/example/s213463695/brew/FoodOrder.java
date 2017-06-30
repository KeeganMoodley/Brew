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
    ArrayList<Food> foods;
    private FoodOrderListener mListener;
    View view;

    public FoodOrder() {

    }

    public static FoodOrder newInstance() {
        FoodOrder fragment = new FoodOrder();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void populateList() {
        foods.add(new Food(R.drawable.brew_logo, 30, "Burger", true));
        foods.add(new Food(R.drawable.brew_logo, 40, "Hot dog", true));
        foods.add(new Food(R.drawable.brew_logo, 20, "Russian Roll", false));
        foods.add(new Food(R.drawable.brew_logo, 10, "Hot Chips", true));
        foods.add(new Food(R.drawable.brew_logo, 100, "Wors", true));
        foods.add(new Food(R.drawable.brew_logo, 250, "Salad", true));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        foods = new ArrayList<>();
        populateList();
        view = inflater.inflate(R.layout.fragment_order_food, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        FoodAdapter foodAdapter = new FoodAdapter(foods);
        recyclerView.setAdapter(foodAdapter);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcTotal();
            }
        });

        return view;
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
        TextView txtTotal = (TextView) view.findViewById(R.id.total);
        double tot = 0;
        for (Food food : foods)
            tot += food.getTotal();
        txtTotal.setText("R" + tot);
    }

    public interface FoodOrderListener {
        void triggerCancel();

        void triggerLocationMain(String quantity);
    }
}