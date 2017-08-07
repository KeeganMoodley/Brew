package com.example.s213463695.brew;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.s213463695.brew.Home.main;

/**
 * Created by s214079694 on 2017/06/29.
 */

public class FoodOrder extends Fragment implements TotalListener, Payment.TotalL {
    static ArrayList<Food> foods = new ArrayList<>(), curFoods = new ArrayList<>();
    private FoodOrderListener mListener;
    static boolean addFood = true;

    public void setPaymentListener(Payment.PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    private Payment.PaymentListener paymentListener;
    View view;
    static double total;
    TextView txtTotal;
    static FoodAdapter foodAdapter = new FoodAdapter(foods);
    RecyclerView recyclerView;

    public static FoodAdapter getFoodAdapter() {
        return foodAdapter;
    }
    //private static ServerThread server = null;

    public FoodOrder() {
    }

    public static FoodOrder newInstance() {
        FoodOrder fragment = new FoodOrder();
        //total = 0;
        //foods.clear();
        Bundle args = new Bundle();
        args.putDouble("total", total);
        fragment.setArguments(args);
        Login.serverLink.requestFood();
        //main.triggerFoodList();
        //populateList(food);
        return fragment;
    }

    protected static void populateList(ArrayList<Food> food) {
        if (food.size() == 0)
            Log.e(TAG, "populateList: List is empty from db");
        foods.clear();
        //foods = (ArrayList<Food>) food.clone();
        for (Food f : food) {
            try {
                foods.add((Food) f.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //populateList();
        view = inflater.inflate(R.layout.fragment_order_food, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        Button btnContinue = (Button) view.findViewById(R.id.btnContinue);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.triggerLocation(foods);
            }
        });

        //Solid solid = mListener.getSolid();
        //populateList(solid);
        //foods = mListener.getFoods();
        //populateList(mListener.getFoods());
        if (foods.size() == 0) {
            Log.e(TAG, "onCreateView: The food array is empty");
            main.triggerFoodList();
        }
        foodAdapter.setTotalListener(FoodOrder.this);
        recyclerView.setAdapter(foodAdapter);

        getTotal();

        return view;
    }

    private void getTotal() {
        txtTotal = (TextView) view.findViewById(R.id.total);
        total = 0;
        for (Food food : foods)
            total += food.getTotal();
        txtTotal.setText("Total: R" + String.format("%.2f", total));
    }

    //Handles data input from database (Food Data)
    public static class obtainFoodInfo extends AsyncTask<Void, Void, Void> {
        Socket socket = null;

        public obtainFoodInfo(Socket socket) {
            this.socket = socket;
            try {
                backgroundMethod();
                //int n = this.in.readInt();
                //Log.e(TAG, "obtainFoodInfo: " + n);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void backgroundMethod() {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                int numOfItems = in.readInt();
                Log.e(TAG, "run: Number of food items from DB in list = " + numOfItems);
                for (int i = 0; i < numOfItems; i++) {
                    int id = in.readInt();
                    int type = in.readInt(); //1 is solid, 2 is liquid, 3 is packaged
                    int picLength = in.readInt();
                    byte[] curPic = new byte[picLength];
                    if (picLength > 0) {
                        in.readFully(curPic);
                        Log.e(TAG, "run: Pic has been received");
                    }
                    //Bitmap pic = BitmapFactory.decodeByteArray(curPic, 0, picLength);

                    double price = in.readDouble();
                    String title = in.readUTF();
                    String nutrition = in.readUTF();
                    String dietary = in.readUTF();
                    boolean halaal = in.readBoolean();
                    int quantityAvailable = in.readInt();
                    System.out.println(id);
                    double length = in.readDouble();
                    double width = in.readDouble();
                    double height = in.readDouble();
                    double volume = in.readDouble();
                    if (addFood) {
                        switch (type) {
                            case 1:
                                curFoods.add(new Solid(id, type, curPic, price, title, nutrition, dietary, halaal, quantityAvailable, length, width, height));
                                break;
                            case 2:
                                curFoods.add(new Liquid(id, type, curPic, price, title, nutrition, dietary, halaal, quantityAvailable, volume));
                                break;
                            case 3:
                                curFoods.add(new Packaged(id, type, curPic, price, title, nutrition, dietary, halaal, quantityAvailable, length, width, height));
                                break;
                        }
                    }
                }
                addFood = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            //backgroundMethod();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (foods.size() == 0) {
                populateList(curFoods);
            }
            foodAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (total > 0) {
            total = 0;
            //txtTotal.setText("Total: R" + String.format("%.2f", total));
        }
        //foodAdapter = new FoodAdapter(foods);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (total > 0) {
            total = 0;
            txtTotal.setText("Total: R" + String.format("%.2f", total));
            addFood = false;
            populateList(curFoods);
            foodAdapter = new FoodAdapter(foods);
            foodAdapter.setTotalListener(FoodOrder.this);
            recyclerView.setAdapter(foodAdapter);
        }
    }

    public void setMainListener(FoodOrderListener order) {
        this.mListener = order;
    }


    @Override
    public void calcTotal() {
        getTotal();
    }

    @Override
    public double getOrderTotal() {
        return total;
    }


    public interface FoodOrderListener {
        void triggerLocation(ArrayList<Food> foods);

        Solid getSolid();

        ArrayList<Food> getFoods();

        void triggerFoodList();
    }
}
