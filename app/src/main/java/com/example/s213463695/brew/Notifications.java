package com.example.s213463695.brew;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.s213463695.brew.Home.main;
import static com.example.s213463695.brew.Home.map;
import static com.example.s213463695.brew.Home.orders;

public class Notifications extends Fragment {

    private NotificationsListener mListener;
    private ListView listview = null;
    private OrderAdapter adapter = null;
    //private ExpandableListAdapter expandableListAdapter = null;

    public Notifications() {
    }

    public static Notifications newInstance() {
        Notifications fragment = new Notifications();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        View v = inflater.inflate(R.layout.frag_noti, container, false);
        mListener.setNotificationTitle("Notifications");
        listview = (ListView) v.findViewById(R.id.notifications);
        //adapter = new OrderAdapter(getActivity(), orders);
        //listview.setAdapter(adapter);
        /*for (Order o : orders) {
            map.put(o, o.getFoods());
        }*/
        ExpandableListView expandableListView = (ExpandableListView) v.findViewById(R.id.expList);

        myOrderAdapter myOrderAdapter = new myOrderAdapter(getActivity(), orders, map);

        expandableListView.setAdapter(myOrderAdapter);

        if (orders.size() == 0) {
            Toast.makeText(main, "No orders currently placed!", Toast.LENGTH_LONG).show();
        }

        /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (orders.get(position).getStatus().equals("dispatched")) {
                    notifyDispatch();
                } else if (orders.get(position).getStatus().equals("counted")) {
                    Snackbar.make(getView(), "Remove by clicking on the right -->", Snackbar.LENGTH_LONG)
                            .setAction("Remove", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int x = position; x <= orders.size() - 1; x++) {
                                        Order curO = orders.get(x);
                                        curO.setOrderNum(curO.getOrderNum() - 1);
                                    }
                                    orders.remove(position);
                                    notifyAdapt();
                                    main.createFile();
                                }
                            }).show();
                } else {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Order " + orders.get(position).getOrderNum());
                    adb.setMessage("Do You want to withdraw your order?");
                    adb.setPositiveButton("Withdraw", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (orders.get(position).getStatus().equals("dispatched")) {
                                Toast.makeText(main, "Sorry the order has been dispatched already!", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            } else {
                                mListener.cancelOrder(orders.get(position));
                            }
                        }
                    });
                    adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    adb.show();
                }
            }
        });*/
        return v;
    }

    private void notifyDispatch() {
        Toast.makeText(main, "Your order has been dispatched and will arrive soon.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainListener(NotificationsListener home) {
        this.mListener = home;
    }

    public void notifyAdapt() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public interface NotificationsListener {
        void setNotificationTitle(String title);

        //void cancelOrder(Order order);
    }
}
