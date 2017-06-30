package com.example.s213463695.brew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class HomeContainer extends Fragment {

    private HomeListener mListener;
    private static AppCompatButton fab;

    //My variables
    GPSValidation gpsValidation;
    double longitude, latitude, region = 35;

    public HomeContainer() {
    }

    public static HomeContainer newInstance() {
        HomeContainer fragment = new HomeContainer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home_container, container, false);
        final View myV = v;

        Button btn = (Button) v.findViewById(R.id.gpsButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(myV.getContext(), GPSValidation.class));
            }
        });
        btn.setVisibility(View.GONE);

        fab = (AppCompatButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.triggerPlaceOrderFragment();
                /*FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FoodOrder foodOrder = new FoodOrder();
                Fragment fragment = new FoodOrder();
                fragmentTransaction.replace(R.layout.fragment_home_container, foodOrder);
                fragmentTransaction.commit();*/
            }
        });

        Boolean validLocation = checkLocation();
        if (validLocation) {
            Toast.makeText(v.getContext(), "Location is valid", Toast.LENGTH_LONG).show();
            //fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
        } else {
            //fab.setEnabled(false);
            fab.setVisibility(View.GONE);
            Toast.makeText(v.getContext(), "You are not at the stadium", Toast.LENGTH_LONG).show();
        }

        return v;
    }

    private Boolean checkLocation() {
        gpsValidation = new GPSValidation();
        longitude = gpsValidation.getLongitude();
        latitude = gpsValidation.getLatitude();

        Boolean result = true;
        //distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2)
        double distance = gpsValidation.distance(-34.0081827, -34.0081803, 25.6696243, 25.6694945, 0, 0);
        Log.e(TAG, "distance: " + distance);

        distance = gpsValidation.distance(-34.0081827, -34.0077136, 25.6696243, 25.6697538, 0, 0);
        Log.e(TAG, "distance: " + distance);

        distance = gpsValidation.distance(-34.0076772, -34.0077136, 25.6695404, 25.6697538, 0, 0);
        Log.e(TAG, "distance: " + distance);

        distance = gpsValidation.distance(-34.0076772, -34.0081803, 25.6695404, 25.6694945, 0, 0);
        Log.e(TAG, "distance: " + distance);

        //Calculate distance from centre
        distance = gpsValidation.distance(latitude, -34.0078965, longitude, 25.6696302, 0, 0);
        if (distance > region)
            result = false;

        //return result;
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainListener(HomeListener home) {
        this.mListener = home;
    }

    public interface HomeListener {
        void triggerPlaceOrderFragment();
    }

    public AppCompatButton getFab() {
        return fab;
    }
}
