package com.example.s213463695.brew;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class HomeContainer extends Fragment {

    private HomeListener mListener;
    private static AppCompatButton fab;

    //My variables
    GPSValidation gpsValidation;
    private static final String TAG = "";
    Location mLocation;
    TextView txtMyLong, txtMyLat;
    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private double longitude, latitude, region = 500, distance;

    private static Home curHome = null;
    private boolean inStadium = false;

    Button searchBtn = null;
    Intent locatorService = null;
    AlertDialog alertDialog = null;
    View curV = null;

    public HomeContainer() {
    }

    public static HomeContainer newInstance(Home home) {
        HomeContainer fragment = new HomeContainer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        curHome = home;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home_container, container, false);
        final View myV = v;
        curV = v;

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
        fab.setVisibility(View.GONE);
        //curHome.onConnected(null);

        //Boolean validLocation = checkLocation();

        if (!startService()) {
            //CreateAlert("Error!", "Service Cannot be started");
            Log.e(TAG, "onCreateView: Error!");
        } else {
            //Toast.makeText(FastMainActivity.this, "Service Started", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreateView: Service Started");
        }
        return v;
    }

    private Boolean checkLocation() {
        //gpsValidation.onConnected();
        //longitude = curHome.getLongitude();
        //latitude = curHome.getLatitude();
        GPSValidation gpsValidation = new GPSValidation();
        longitude = gpsValidation.getLongitude();
        latitude = gpsValidation.getLatitude();

        Boolean result = true;
        //distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2)

        /*double distance = gpsValidation.distance(-34.0081827, -34.0081803, 25.6696243, 25.6694945, 0, 0);
        Log.e(TAG, "distance: " + distance);

        distance = gpsValidation.distance(-34.0081827, -34.0077136, 25.6696243, 25.6697538, 0, 0);
        Log.e(TAG, "distance: " + distance);

        distance = gpsValidation.distance(-34.0076772, -34.0077136, 25.6695404, 25.6697538, 0, 0);
        Log.e(TAG, "distance: " + distance);

        distance = gpsValidation.distance(-34.0076772, -34.0081803, 25.6695404, 25.6694945, 0, 0);
        Log.e(TAG, "distance: " + distance);*/

        //Calculate distance from centre
        double distance = gpsValidation.distance(latitude, -34.0078965, longitude, 25.6696302, 0, 0);
        //double distance = curHome.distance(latitude, -34.008722, longitude, 25.6695236, 0, 0);
        Log.e(TAG, "checkLocation:\t" + distance);
        if (distance > region)
            result = false;

        return result;
        //return true;
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

    public boolean stopService() {
        if (this.locatorService != null) {
            this.locatorService = null;
        }
        return true;
    }

    public boolean startService() {
        try {
            // this.locatorService= new
            // Intent(FastMainActivity.this,LocatorService.class);
            // startService(this.locatorService);

            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }

    }

    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(curHome).create();

        alert.setTitle(title);

        alert.setMessage(message);

        return alert;

        //return null;
    }

    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        //ProgressDialog progDialog = null;

        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public VeggsterLocationListener mVeggsterLocationListener;

        /**
         * Creating google api client object
         */
        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(curHome)
                    .addConnectionCallbacks(curHome)
                    .addOnConnectionFailedListener(curHome)
                    .addApi(LocationServices.API).build();
        }

        /**
         * Method to verify google play services on the device
         */
        private boolean checkPlayServices() {
            int resultCode = GooglePlayServicesUtil
                    .isGooglePlayServicesAvailable(curHome);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, curHome,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    /*Toast.makeText(getApplicationContext(),
                            "This device is not supported.", Toast.LENGTH_LONG)
                            .show();
                    finish();*/
                    Log.e(TAG, "checkPlayServices: Error");
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            // First we need to check availability of play services
            if (checkPlayServices()) {

                // Building the GoogleApi client
                buildGoogleApiClient();
            }

            mVeggsterLocationListener = new VeggsterLocationListener();
            mLocationManager = (LocationManager) curHome.getSystemService(Context.LOCATION_SERVICE);
            while (mLocation == null) {
                /*try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                if (ActivityCompat.checkSelfPermission(curHome, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(curHome, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 10, mVeggsterLocationListener);
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (mLocation != null) {
                    lati = mLocation.getLatitude();
                    longi = mLocation.getLongitude();
                }
            }
            double labLat = -34.008722, labLong = 25.6695236; //Coordinates of emulator and phone taken from my pc at the honours lab
            //double resLat = -34.0034649, resLong = 25.667304;//Coordinates from my room at Protea res
            distance = distance(labLat, lati, labLong, longi, 0, 0);
            //distance = distance(resLat, lati, resLong, longi, 0, 0);

            /*progDialog = new ProgressDialog(curHome);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FetchCordinates.this.cancel(true);
                }
            });
            progDialog.setMessage("Obtaining GPS coordinates");
            progDialog.setIndeterminate(true);
            progDialog.setCancelable(true);
            progDialog.show();*/

        }

        @Override
        protected void onCancelled() {
            System.out.println("Cancelled by user!");
            //progDialog.dismiss();
            mLocationManager.removeUpdates(mVeggsterLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            //progDialog.dismiss();
            inStadium = distance <= region;
            //Toast.makeText(curHome, "LATITUDE :" + lati + " LONGITUDE :" + longi, Toast.LENGTH_LONG).show();
            Log.i(TAG, "onPostExecute: LATITUDE :" + lati + " LONGITUDE :" + longi);
            if (inStadium) {
                //Toast.makeText(v.getContext(), "Location is valid", Toast.LENGTH_LONG).show();
                Toast.makeText(curV.getContext(), "Location is valid\ndistance:\t" + distance, Toast.LENGTH_LONG).show();
                //fab.setEnabled(true);
                fab.setVisibility(View.VISIBLE);
            } else {
                //fab.setEnabled(false);
                fab.setVisibility(View.GONE);
                Toast.makeText(curV.getContext(), "You are not at the stadium\ndistance:\t" + distance, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            /*while (mLocation == null) {
                *//*try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*//*
                if (ActivityCompat.checkSelfPermission(curHome, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(curHome, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return "";
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 10, mVeggsterLocationListener);
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (mLocation != null) {
                    lati = mLocation.getLatitude();
                    longi = mLocation.getLongitude();
                }
            }
            double labLat = -34.008722, labLong = 25.6695236; //Coordinates of emulator and phone taken from my pc at the honours lab
            double resLat = -34.0034649, resLong = 25.667304;//Coordinates from my room at Protea res
            distance = distance(labLat, lati, labLong, longi, 0, 0);
            //distance = distance(resLat, lati, resLong, longi, 0, 0);*/
            return null;
        }

        /**
         * Calculate distance between two points in latitude and longitude taking
         * into account height difference. If you are not interested in height
         * difference pass 0.0. Uses Haversine method as its base.
         * <p>
         * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
         * el2 End altitude in meters
         *
         * @returns Distance in Meters
         */
        public double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
            final int R = 6371; // Radius of the earth

            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters

            double height = el1 - el2;

            distance = Math.pow(distance, 2) + Math.pow(height, 2);

            return Math.sqrt(distance);
        }

        public class VeggsterLocationListener implements LocationListener {
            @Override
            public void onLocationChanged(Location location) {

                int lat = (int) location.getLatitude(); // * 1E6);
                int log = (int) location.getLongitude(); // * 1E6);
                int acc = (int) (location.getAccuracy());

                String info = location.getProvider();
                try {

                    // LocatorService.myLatitude=location.getLatitude();

                    // LocatorService.myLongitude=location.getLongitude();

                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }

    }

}
