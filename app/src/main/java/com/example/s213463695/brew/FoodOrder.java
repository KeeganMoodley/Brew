package com.example.s213463695.brew;

import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by s214079694 on 2017/06/29.
 */

public class FoodOrder extends Fragment implements TotalListener {
    static ArrayList<Food> foods = new ArrayList<>();
    private FoodOrderListener mListener;

    public void setPaymentListener(Payment.PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    private Payment.PaymentListener paymentListener;
    View view;
    double total;
    TextView txtTotal;
    static FoodAdapter foodAdapter = new FoodAdapter(foods);

    public static FoodAdapter getFoodAdapter() {
        return foodAdapter;
    }
    //private static ServerThread server = null;

    public FoodOrder() {
    }

    public static FoodOrder newInstance() {
        FoodOrder fragment = new FoodOrder();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Login.serverLink.requestFood();
        //populateList(food);
        return fragment;
    }

    protected static void populateList(ArrayList<Food> food) {
        /*foods.add(new Food(R.drawable.brew_logo, 30, "Burger", true));
        foods.add(new Food(R.drawable.brew_logo, 40, "Hot dog", true));
        foods.add(new Food(R.drawable.brew_logo, 20, "Russian Roll", false));
        foods.add(new Food(R.drawable.brew_logo, 10, "Hot Chips", true));
        foods.add(new Food(R.drawable.brew_logo, 100, "Wors", true));
        foods.add(new Food(R.drawable.brew_logo, 250, "Salad", true));*/
        //Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.brew_logo);
        //foods.add(new Liquid(icon, 15, "Hotdog", "nutrituion", "dietary", false, 100, 500));
        if (food.size() == 0)
            Log.e(TAG, "populateList: List is empty from db");
        for (Food f : food)
            foods.add(f);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //populateList();
        view = inflater.inflate(R.layout.fragment_order_food, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        Button btnContinue = (Button) view.findViewById(R.id.btnContinue);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.triggerPaymentFrag();
                /*FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, Payment.newInstance(), "Payment");
                fragmentTransaction.addToBackStack("PAYMENT");
                fragmentTransaction.commit();*/
            }
        });

        //Solid solid = mListener.getSolid();
        //populateList(solid);
        //foods = mListener.getFoods();
        //populateList(mListener.getFoods());
        if (foods.size() == 0)
            Log.e(TAG, "onCreateView: The food array is empty");
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
        txtTotal.setText("Total: R" + String.format("%.2f", total));
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
        void triggerPaymentFrag();

        Solid getSolid();

        ArrayList<Food> getFoods();

        void triggerFoodList();
    }
}
