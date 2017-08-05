package com.example.s213463695.brew;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.s213463695.brew.Login.serverLink;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeContainer.HomeListener,
        PlaceOrder.PlaceOrderListener,
        Notifications.NotificationsListener,
        Profile.ProfileListener,
        LocationMain.LocationListener,
        FoodOrder.FoodOrderListener,
        Payment.PaymentListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    static FragmentManager fragMan;
    static FragmentTransaction fragTran;
    private static String curTitle = "";
    private String quantity = "";
    public static ArrayList<Order> orders;
    public static HashMap<Order, List<Food>> map = new HashMap<>();
    public static ArrayList<Block> blocks;
    public static ImageView profilePictureFrame;
    public static Bitmap profilePicture;
    public static Bitmap profileExpanded;
    public static Home main = null;
    public static String email = "";
    public static String cell = "+27 000 00 00";
    public static String phone = "+27 410 00 00 0";
    public static String password = "";
    public static String username = "";
    public TextView user;
    public TextView e_mail;
    private static Integer Count;
    private static Stack<String> titleStack;
    public static boolean event = false;
    private static boolean mainContaining;
    public boolean callsActivity = false;
    public boolean disconnected = false;
    private boolean locationManual = true;
    private File orderData;
    static ArrayList<Food> finalFoods = new ArrayList<>();

    public static Notifications not = null;
    public static Profile prof = null;
    public static LocationMain loc = null;
    public static FoodOrder foodOrder = null;
    public static Payment payment = null;

    String block, row, seat;
    Double total;

    double region = 1000; //Use 35 for realistic demo, 1000 for testing purposes

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

    private double longitude, latitude;
    private boolean cash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Count = 0;
        main = this;
        orders = new ArrayList<>();
        mainContaining = true;
        titleStack = new Stack();
        blocks = new ArrayList<>();
        fragMan = getSupportFragmentManager();
        //buildGoogleAPIClient();
        HomeContainer homeC = HomeContainer.newInstance(this);
        homeC.setMainListener(this);
        replaceFragment(homeC, "Home", true, "HOME");
        titleStack.push(curTitle);

        Boolean signup = false;
        Intent in = getIntent();
        if (in.getExtras() != null) {
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            username = in.getStringExtra("username");
            event = in.getBooleanExtra("event", false);
            signup = in.getBooleanExtra("signup", false);
            saveLoginInfo();
        }
        if (signup) {
            try {
                SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
                if (!preferences.getString("encodedBitmap", "").equals("")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("encodedBitmap");
                    editor.commit();
                }
            } catch (Exception e) {
            }
        }
        loadProfilePictures();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        profilePictureFrame = (ImageView) header.findViewById(R.id.profile_dp);
        profilePictureFrame.setImageBitmap(profilePicture);
        profilePictureFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerProfile();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        user = (TextView) header.findViewById(R.id.userN);
        e_mail = (TextView) header.findViewById(R.id.e_mail);
        user.setText(username);
        e_mail.setText(email);

        if (signup) {
            storeUpdateDate(true);
        } else {
            serverLink.getUpdate();
        }
        try {
            //readOrderData();
            createFile();
        } catch (Exception e) {
        }
    }

    public void createFile() {
        orderData = new File(getFilesDir().getAbsolutePath());
        try {
            FileWriter fo = new FileWriter(orderData + "/order_data.txt");
            fo.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*for (Order or : orders) {
            writeToFile(or.getTime(), or.getDate(), or.getQuantity(), or.getPrice(), or.getCurMinute(), or.getCurSecond(), or.getOrderNum(), or.getStatus(), or.getAddedAt(), or.getDateIndex(), or.getFoods());
        }*/
        writetoFileFoodOrdersObject(orders);
    }

    private void readOrderData() {
        /*try {
            Scanner scan = new Scanner(new File(getFilesDir().getAbsolutePath() + "/order_data.txt"));
            while (scan.hasNext()) {
                String time = scan.nextLine();
                String date = scan.nextLine();
                String quantity = scan.nextLine();
                String price = scan.nextLine();
                String number = scan.nextLine();
                String status = scan.nextLine();
                String stopTime = scan.nextLine();
                String dateIndex = scan.nextLine();

                Order prevO = null;
                Date curDate = new Date();
                Date prevDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(stopTime);
                if (status.equals("counted")) {
                    prevO = new Order(time, date, quantity, price, 0.0, 0.0, Integer.parseInt(number), status, prevDate, dateIndex);
                    prevO.setStopThread(true);
                } else {
                    Double secondsBetween = (int) (curDate.getTime() - prevDate.getTime()) / 1000.0;
                    Double secondsStop = 180.0;
                    Double secondsLeft = secondsStop - secondsBetween;

                    if (secondsLeft >= 0 && secondsLeft < 60) {
                        prevO = new Order(time, date, quantity, price, 0.0, secondsLeft, Integer.parseInt(number), "queue", prevDate, dateIndex);
                        prevO.startThread();
                    } else {
                        Double minutes = Math.floor(secondsLeft / 60);
                        Double seconds = secondsLeft % 60;
                        prevO = new Order(time, date, quantity, price, minutes, seconds, Integer.parseInt(number), "queue", prevDate, dateIndex);
                        prevO.startThread();
                    }
                }
                if (prevO != null) {
                    orders.add(prevO);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //My code
        try {
            //Scanner scan = new Scanner(new File(getFilesDir().getAbsolutePath() + "/order_data.txt"));
            FileInputStream fileInputStream = new FileInputStream(new File(getFilesDir().getAbsolutePath() + "/order_data.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            orders.clear();
            int num = objectInputStream.readInt();
            for (int i = 0; i < num; i++) {
                Order order = (Order) objectInputStream.readObject();
                orders.add(order);
            }
            for (Order or : orders) {
                String time = or.getTime();
                String date = or.getDate();
                String quantity = or.getQuantity();
                String price = or.getPrice();
                String number = String.valueOf(or.getOrderNum());
                String status = or.getStatus();
                String stopTime = String.valueOf(or.getAddedAt());
                String dateIndex = or.getDateIndex();
                ArrayList<Food> foods = or.getFoods();
                //, , , , or.getCurMinute(), or.getCurSecond(), , , or.getAddedAt(), , or.getFoods()
                Order prevO = null;
                Date curDate = new Date();
                Date prevDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(stopTime);
                if (status.equals("counted")) {
                    prevO = new Order(time, date, quantity, price, 0.0, 0.0, Integer.parseInt(number), status, prevDate, dateIndex, foods);
                    prevO.setStopThread(true);
                } else {
                    Double secondsBetween = (int) (curDate.getTime() - prevDate.getTime()) / 1000.0;
                    Double secondsStop = 180.0;
                    Double secondsLeft = secondsStop - secondsBetween;

                    if (secondsLeft >= 0 && secondsLeft < 60) {
                        prevO = new Order(time, date, quantity, price, 0.0, secondsLeft, Integer.parseInt(number), "queue", prevDate, dateIndex, foods);
                        prevO.startThread();
                    } else {
                        Double minutes = Math.floor(secondsLeft / 60);
                        Double seconds = secondsLeft % 60;
                        prevO = new Order(time, date, quantity, price, minutes, seconds, Integer.parseInt(number), "queue", prevDate, dateIndex, foods);
                        prevO.startThread();
                    }
                }
                if (prevO != null) {
                    orders.add(prevO);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void storeUpdateDate(boolean b) {
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        if (b) {
            progressDialog.setMessage("Creating Account...\n\nInstalling Stadium-Map...");
        } else {
            progressDialog.setMessage("Installing Stadium-Map...");
        }
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 5000);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateString = dateFormat.format(date);

        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("update", dateString);
        editor.apply();

        serverLink.getLocations();
    }

    public void compareUpdates(String update) {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        String lastUpdate = "";
        boolean updateNeeded = false;
        if (!preferences.getString("update", "").equals("")) {
            lastUpdate = preferences.getString("update", "");

            String[] updateArray = update.split("/");
            String[] lastUpdateArray = lastUpdate.split("/");
            if (updateArray[0].equals(lastUpdateArray[0])) {
                if (updateArray[1].equals(lastUpdateArray[1])) {
                    if (Integer.parseInt(updateArray[2]) > Integer.parseInt(lastUpdateArray[2])) {
                        updateNeeded = true;
                    }
                } else if (Integer.parseInt(updateArray[1]) > Integer.parseInt(lastUpdateArray[1])) {
                    updateNeeded = true;
                }
            } else if (Integer.parseInt(updateArray[0]) > Integer.parseInt(lastUpdateArray[0])) {
                updateNeeded = true;
            }
        } else {
            updateNeeded = true;
        }

        Scanner scan = null;
        Integer blockC = 0;
        try {
            scan = new Scanner(new File(getFilesDir().getAbsolutePath() + "/stadium_data.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scan == null)
            updateNeeded = true;

        if (!updateNeeded) {
            String blockCS = scan.nextLine();
            blockC = Integer.parseInt(blockCS);
            for (int i = 0; i < blockC; i++) {
                String name = scan.nextLine();
                Block B = new Block(name, new ArrayList<Block.Row>());
                String indicator = "";
                while (!indicator.equals("next")) {
                    if (indicator.equals(""))
                        B.addRow(Integer.parseInt(scan.nextLine()), Integer.parseInt(scan.nextLine()));
                    else
                        B.addRow(Integer.parseInt(indicator), Integer.parseInt(scan.nextLine()));
                    indicator = scan.nextLine();
                }
                blocks.add(B);
            }
        } else {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    storeUpdateDate(false);
                }
            });
        }
    }

    private void loadProfilePictures() {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        if (!preferences.getString("encodedBitmap", "").equals("")) {
            String encoded = preferences.getString("encodedBitmap", "");
            try {
                byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                profilePicture = decodedByte;
                profileExpanded = decodedByte;
            } catch (Exception e) {
                Log.d("error", e.toString());
            }
        } else {
            profilePicture = BitmapFactory.decodeResource(getResources(), R.drawable.profile_default_image);
            profileExpanded = BitmapFactory.decodeResource(getResources(), R.drawable.profile_default_image);
        }
    }

    private void triggerProfile() {
        prof = Profile.newInstance();
        prof.setMainListener(this);
        replaceFragment(prof, "Profile", false, "PROFILE");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (mainContaining) {
            moveTaskToBack(true);
            this.onPause();
        } else if (Count == 0) {
            moveTaskToBack(true);
            this.onPause();
        } else {
            if (titleStack.size() != 0) {
                curTitle = titleStack.pop();
                setTitle(curTitle);
            }
            Count--;
            try {
                super.onBackPressed();
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_account) {
            AlertDialog.Builder adb = new AlertDialog.Builder(main);
            adb.setTitle("Delete Account");
            adb.setMessage("Are you sure you want to permanently delete your account?");
            adb.setPositiveButton("Delete", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();

                    serverLink.cancelConnection(email);
                    disconnected = true;

                    Intent logout = new Intent(Home.this, Login.class);
                    startActivity(logout);
                    dialog.cancel();
                }
            });
            adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.home: {
                titleStack.push(curTitle);
                Count = 0;
                //HomeContainer home = new HomeContainer();
                HomeContainer home = HomeContainer.newInstance(this);
                home.setMainListener(this);
                replaceFragment(home, "Home", true, "HOME");
                break;
            }
            case R.id.place: {
                PlaceOrder placeO = PlaceOrder.newInstance();
                placeO.setMainListener(this);
                replaceFragment(placeO, "Place Order", false, "OTHER");

                /*foodOrder = FoodOrder.newInstance();
                foodOrder.setMainListener(this);
                replaceFragment(foodOrder, "Food Order", false, "OTHER");*/
                break;
            }
            case R.id.view: {
                not = Notifications.newInstance();
                not.setMainListener(this);
                replaceFragment(not, "Notification", false, "OTHER");
                break;
            }
            case R.id.pay: {
                //not = Notifications.newInstance();
                //not.setMainListener(this);
                //foodOrder = FoodOrder.newInstance();
                //foodOrder.setMainListener(this);
                total = foodOrder.getOrderTotal();
                payment = Payment.newInstance(total);
                payment.setPaymentListener(this);
                /*Bundle bundle = new Bundle();
                bundle.putDouble("total", total);
                payment.setArguments(bundle);*/
                replaceFragment(payment, "Payment", false, "OTHER");
                break;
            }
            case R.id.location: {
                loc = LocationMain.newInstance();
                loc.setMainListener(this);
                replaceFragment(loc, "Location Setup", false, "OTHER");
                loc.settingsIndication(true);
                break;
            }
            case R.id.password: {
                passwordSetup();
                break;
            }
            case R.id.logout: {
                SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("password");
                //editor.commit();
                editor.apply();

                callsActivity = true;
                serverLink.cancelConnection("homeOut");
                disconnected = true;

                Intent logout = new Intent(Home.this, Login.class);
                startActivity(logout);
                break;
            }
            default:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void passwordSetup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Change Password");
        final EditText oldPass = new EditText(Home.this);
        final EditText newPass = new EditText(Home.this);
        final EditText confirmPass = new EditText(Home.this);

        oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        oldPass.setHint("Old Password");
        newPass.setHint("New Password");
        confirmPass.setHint("Confirm Password");
        LinearLayout ll = new LinearLayout(Home.this);
        ll.setPadding(24, 24, 24, 24);
        ll.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        ll.setOrientation(LinearLayout.VERTICAL);

        ll.addView(oldPass);

        ll.addView(newPass);
        ll.addView(confirmPass);
        alertDialog.setView(ll);
        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!oldPass.getText().toString().equals(password))
                            Toast.makeText(Home.this, "Your old password is not entered correct, please try again", Toast.LENGTH_SHORT).show();
                        else if (!newPass.getText().toString().equals(confirmPass.getText().toString()))
                            Toast.makeText(Home.this, "The confirmed and new passwords do not match!", Toast.LENGTH_SHORT).show();
                        else if (newPass.getText().toString().isEmpty() || newPass.getText().toString().length() < 4 || newPass.getText().toString().length() > 10)
                            Toast.makeText(Home.this, "Between 4 and 10 alphanumeric characters!", Toast.LENGTH_SHORT).show();
                        else
                            serverLink.changePassword(newPass.getText().toString(), email, username);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertDialog.create();
        alert11.show();
    }

    public void replaceFragment(Fragment fragment, String title, Boolean mainContain, String name) {
        mainContaining = mainContain;
        if (!title.equals(""))
            setTitle(title);
        if (!curTitle.equals("Home") || !curTitle.equals(""))
            titleStack.push(curTitle);
        curTitle = title;
        if (!mainContaining)
            Count++;
        fragTran = fragMan.beginTransaction();
        fragTran.replace(R.id.container, fragment, name);
        fragTran.addToBackStack("Stack");
        fragTran.commit();
    }

    private void setTitle(String title) {
        android.support.v7.app.ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setTitle(title);
        }
    }

    private void saveLoginInfo() {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        if (!preferences.getString("password", "").equals(password)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }
    }

    @Override
    public void triggerPlaceOrderFragment() {
        if (blocks.size() == 0) {
            Toast.makeText(this, "Please wait a moment. Connecting to server...", Toast.LENGTH_SHORT).show();
        } else {
            /*PlaceOrder placeO = PlaceOrder.newInstance();
            placeO.setMainListener(this);
            replaceFragment(placeO, "Place Order", false, "ORDER");*/
            FoodOrder foodOrder = FoodOrder.newInstance();
            foodOrder.setMainListener(this);
            //setTitle("Food Order");
            replaceFragment(foodOrder, "Food Order", false, "ORDER");
        }
    }

    @Override
    public void triggerPaymentFrag(ArrayList<Food> foods, String currentBlock, String currentRow, String currentSeat) {
        //Double total = foodOrder.getOrderTotal();
        /*for (Food f : foods) {
            if (f.quantity > 0) {
                finalFoods.add(f);
            }
        }*/
        total = FoodOrder.total;
        payment = Payment.newInstance(total);
        payment.setPaymentListener(this);
        block = currentBlock;
        row = currentRow;
        seat = currentSeat;
        Bundle bundle = new Bundle();
        bundle.putString("Block", currentBlock);
        bundle.putString("Row", currentRow);
        bundle.putString("Seat", currentSeat);
        bundle.putDouble("total", total);
        //setTitle("Payment");
        replaceFragment(payment, "Payment selection", false, "Payment");
    }

    @Override
    public void triggerCancel() {
        if (titleStack.size() != 0) {
            curTitle = titleStack.pop();
            setTitle(curTitle);
        }
        Count--;
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public Boolean getWindowBool() {
        SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        Boolean window = preferences.getBoolean("locationWindow", true);
        return window;
    }

    @Override
    public void setWindowBool(boolean b) {
        SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("locationWindow", b);
        try {
            editor.apply();
        } catch (Exception e) {
        }
    }

    @Override
    public void previousLocation(LocationMain locationMain) {
        SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        if (!preferences.getString("currentBlock", "").equals("")) {
            String curB = preferences.getString("currentBlock", "");
            String curR = preferences.getString("currentRow", "");
            String curS = preferences.getString("currentSeat", "");

            locationMain.setLocation(curB, curR, curS);
        }
    }

    @Override
    public void postLocation(String currentBlock, String currentRow, String currentSeat) {
        SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentBlock", currentBlock);
        editor.putString("currentRow", currentRow);
        editor.putString("currentSeat", currentSeat);
        try {
            editor.apply();
        } catch (Exception e) {
        }
        Log.i(TAG, "postLocation: Another one");
        Iterator iterator = map.entrySet().iterator();
        int q = 0;
        while (iterator.hasNext()) {
            Map.Entry<Order, List<Food>> pair = (Map.Entry<Order, List<Food>>) iterator.next();
            q += pair.getValue().size();
        }
        quantity = String.valueOf(q);
        q = 0;
        for (Food f : finalFoods) {
            q += f.getQuantity();
        }
        quantity = String.valueOf(q);
        serverLink.postLocation(quantity, currentBlock, currentRow, currentSeat, username, finalFoods, total, cash);
    }

    @Override
    public void triggerPay(String currentBlock, String currentRow, String currentSeat) {

    }

    @Override
    public void changeLocation(String currentBlock, String currentRow, String currentSeat) {
        SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentBlock", currentBlock);
        editor.putString("currentRow", currentRow);
        editor.putString("currentSeat", currentSeat);
        try {
            editor.apply();
        } catch (Exception e) {
        }
        serverLink.changeLocation(currentBlock, currentRow, currentSeat, username);
    }

    @Override
    public Double getPrice() {
        return serverLink.getBeerPrice();
    }

    public void triggerNotifications(String t, String d, String q, String p, String androidIndex, ArrayList<Food> foods) {
        Order newO = null;
        Date curDate = new Date();
        if (orders.size() != 0) {
            newO = new Order(t, d, q, p, 2.0, 59.0, orders.get(orders.size() - 1).getOrderNum() + 1, "queue", curDate, androidIndex, foods);
            orders.add(newO);
            writeToFile(t, d, q, p, 2.0, 59.0, orders.get(orders.size() - 1).getOrderNum() + 1, "queue", curDate, androidIndex, foods);
        } else {
            newO = new Order(t, d, q, p, 2.0, 59.0, orders.size() + 1, "queue", curDate, androidIndex, foods);
            orders.add(newO);
            writeToFile(t, d, q, p, 2.0, 59.0, orders.size() + 1, "queue", curDate, androidIndex, foods);
        }
        for (Order o : orders) {
            map.put(o, o.getFoods());
        }
        not = Notifications.newInstance();
        not.setMainListener(this);
        replaceFragment(not, "", false, "NOTIFICATION");
        orders.get(orders.size() - 1).startThread();
    }

    public void notifyDispatchDown() {
        Toast.makeText(main, "Sorry, the responsible dispatch-point is inactive at the moment...", Toast.LENGTH_LONG).show();
    }

    private void writetoFileFoodOrdersObject(ArrayList<Order> orders) {
        try {
            FileOutputStream fos = new FileOutputStream(orderData);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeInt(orders.size());
            for (Order o : orders) {
                os.writeObject(o);
            }
            os.close();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "writetoFileFoodOrdersObject: " + e.getMessage());
        }
    }

    private void writeToFile(String t, String d, String q, String p, double v, double v1, int i, String queue, Date curDate, String dateIndex, ArrayList<Food> foods) {
        try {
            BufferedWriter fo = new BufferedWriter(new FileWriter(orderData + "/order_data.txt", true));
            fo.write(t);
            fo.write(System.getProperty("line.separator"));
            fo.write(d);
            fo.write(System.getProperty("line.separator"));
            fo.write(q);
            fo.write(System.getProperty("line.separator"));
            fo.write(p);
            fo.write(System.getProperty("line.separator"));
            fo.write(String.valueOf(v));
            fo.write(System.getProperty("line.separator"));
            fo.write(String.valueOf(v1));
            fo.write(System.getProperty("line.separator"));
            fo.write(String.valueOf(i));
            fo.write(System.getProperty("line.separator"));
            fo.write(queue);
            fo.write(System.getProperty("line.separator"));
            fo.write(String.valueOf(curDate));
            fo.write(System.getProperty("line.separator"));
            fo.write(dateIndex);
            fo.write(System.getProperty("line.separator"));
            for (Food f : foods) {
                fo.write(String.valueOf(f.image));
                fo.write(System.getProperty("line.separator"));
                //double price, String title, String nutrition, String dietary, boolean halaal, int quantityAvailable
                fo.write(f.title);
                fo.write(System.getProperty("line.separator"));
                fo.write(f.nutrition);
                fo.write(System.getProperty("line.separator"));
                fo.write(f.dietary);
                fo.write(System.getProperty("line.separator"));
                fo.write(String.valueOf(f.halaal));
                fo.write(System.getProperty("line.separator"));
                fo.write(String.valueOf(f.quantityAvailable));
                fo.write(System.getProperty("line.separator"));
            }
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeOrder(Order order, String status) {
        order.setStatus(status);
        order.setCurMinute(0.0);
        order.setCurSecond(0.0);
        if (not != null)
            not.notifyAdapt();
    }

    @Override
    public void setNotificationTitle(String title) {
        setTitle(title);
    }

    /*@Override
    public void cancelOrder(Order order) {
        serverLink.removeOrder(order);
    }*/

    public void orderRemoved(String time) {

        for (Order o : orders) {
            if (o.getTime().equals(time)) {
                orders.remove(o);
                o.stopThread();
                not.notifyAdapt();
                break;
            }
        }
        createFile();
        Toast.makeText(this, "Your order has been deleted!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void triggerChanges(String name, String email, String cell, String phone) {
        String tempUser = this.username;
        String tempEmail = this.email;
        saveProfileInfo(email, cell, phone);
        safeLocalInformation(name, email, cell, phone);
        serverLink.storeProfile(tempUser, tempEmail, name, email, cell, phone);
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
        overwriteTextFile();
    }

    private void overwriteTextFile() {
        try {
            File file = new File(getFilesDir().getAbsolutePath());
            FileWriter fo = new FileWriter(file + "/stadium_data.txt");

            String sizeS = "" + blocks.size();
            fo.write(sizeS);
            fo.write(System.getProperty("line.separator"));
            for (int i = 0; i < blocks.size(); i++) {

                fo.write(blocks.get(i).getName());
                fo.write(System.getProperty("line.separator"));
                Integer size = blocks.get(i).getRows().size();
                for (int j = 0; j < size; j++) {

                    fo.write(blocks.get(i).getRows().get(j).getNumber().toString());
                    fo.write(System.getProperty("line.separator"));
                    fo.write(blocks.get(i).getRows().get(j).getSeatCount().toString());
                    fo.write(System.getProperty("line.separator"));
                }
                fo.write("next");
                fo.write(System.getProperty("line.separator"));

            }
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void triggerLocationMain(String quantity) {
        this.quantity = quantity;

        if (event) {
            loc = LocationMain.newInstance();
            loc.setMainListener(this);
            replaceFragment(loc, "Location Setup", false, "LOCATION");
        } else {
            Toast.makeText(this, "Please wait with your order until the event!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void triggerLocation(ArrayList<Food> foods) {
        //this.foods = foods;
        //this.cash = cash;
        loc = LocationMain.newInstance();
        loc.setMainListener(this);
        for (Food value : foods) {
            if (value.quantity > 0) {
                finalFoods.add(value);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", foods);
        loc.setArguments(bundle);
        replaceFragment(loc, "Location Setup", false, "OTHER");
        //loc.settingsIndication(true);
    }

    @Override
    public Solid getSolid() {
        return serverLink.getSolid();
    }

    @Override
    public ArrayList<Food> getFoods() {
        //return serverLink.getFoods();
        return null;
    }

    @Override
    public void triggerFoodList() {
        /*if (FoodOrder.foods.size() == 0) {
            FoodOrder.populateList(serverLink.getFoods());
            if (serverLink.getFoods().size() == 0)
                Log.e(TAG, "triggerFoodList: Foods list is empty from DB");
            else
                Log.e(TAG, "triggerFoodList: Foods list has food from DB");
            //FoodOrder.foodAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void saveProfilePicture(String encoded) {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.getString("encodedBitmap", "").equals(encoded))
            editor.putString("encodedBitmap", encoded);
        try {
            editor.apply();
        } catch (Exception e) {
        }
    }

    private void safeLocalInformation(String name, String email, String cell, String phone) {
        this.email = email;
        this.username = name;
        this.cell = cell;
        this.phone = phone;
    }

    private void saveProfileInfo(String email, String cell, String phone) {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.getString("email", "").equals(email))
            editor.putString("email", email);
        if (!preferences.getString("cell", "").equals(cell))
            editor.putString("cell", cell);
        if (!preferences.getString("phone", "").equals(phone))
            editor.putString("phone", phone);
        try {
            editor.apply();
        } catch (Exception e) {
        }
    }

    @Override
    public void LoadAdditionalInfo() {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        if (!preferences.getString("cell", "").equals("")) {
            cell = preferences.getString("cell", "");
        }
        if (!preferences.getString("phone", "").equals("")) {
            phone = preferences.getString("phone", "");
        }
    }

    public void notifyDuplicate(String indicator, String tempUser, String tempEmail) {
        if (indicator.equals("email")) {
            this.email = tempEmail;
            prof.setUser_email(tempEmail);
            user.setText(prof.getUser_name());
        } else if (indicator.equals("user")) {
            this.username = tempUser;
            prof.setUser_name(tempUser);
            e_mail.setText(prof.getUser_email());
        } else if (indicator.equals("both")) {
            this.email = tempEmail;
            this.username = tempUser;
            prof.setUser_email(tempEmail);
            prof.setUser_name(tempUser);
        } else {
            e_mail.setText(prof.getUser_email());
            user.setText(prof.getUser_name());

            SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();
            if (!preferences.getString("email", "").equals(prof.getUser_email()))
                editor.putString("email", email);
            if (!preferences.getString("email", "").equals(prof.getUser_email()))
                editor.putString("email", email);
            if (!preferences.getString("email", "").equals(prof.getUser_email()))
                editor.putString("email", email);
            try {
                editor.apply();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!callsActivity) {
            if (!disconnected) {
                serverLink.cancelConnection("login");
                disconnected = true;
            }
        }
        if (!checkPlayServices()) {
            //latLng.setText("Please install Google Play services.");
            Toast.makeText(this, "Please install Google Play services.", Toast.LENGTH_SHORT).show();
        }
        stopLocationUpdates();
    }

    @Override
    protected void onPause() {
        if (!callsActivity) {
            if (!disconnected) {
                serverLink.cancelConnection("login");
                disconnected = true;
            }
        }
        try {
            SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currentBlock", block);
            editor.putString("currentRow", row);
            editor.putString("currentSeat", seat);
            editor.putString("total", String.valueOf(total));
            //editor.putString("finalFood", finalFoods);
            editor.apply();
        } catch (Exception e) {

        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (serverLink == null || !serverLink.isConnected()) {
            serverLink = new ServerThread();
            serverLink.start();
        }
        disconnected = false;
        callsActivity = false;

        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                String s = uri.getHost();
                if (s.contains("success")) {
                    //go to location
                    //triggerLocation(finalFoods);
                    try {
                        new sendOrder().execute();
                        /*SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
                        block = preferences.getString("currentBlock", "");
                        row = preferences.getString("currentRow", "");
                        seat = preferences.getString("currentSeat", "");
                        total = Double.valueOf(preferences.getString("total","0"));
                        Log.i(TAG, "onResume: working");
                        postLocation(block, row, seat);*/
                    } catch (Exception e) {

                    }
                } else {
                    //go to payment, display toast
                    triggerPaymentFrag(finalFoods, block, row, seat);
                    Toast.makeText(main, "Zapper payment was unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onResume();
    }

    class sendOrder extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(main);
            dialog.setMessage("Sending order to merchant");
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
            block = preferences.getString("currentBlock", "");
            row = preferences.getString("currentRow", "");
            seat = preferences.getString("currentSeat", "");
            total = Double.valueOf(preferences.getString("total", "0"));
            Log.i(TAG, "onResume: working");
            postLocation(block, row, seat);
            while (!serverLink.isDataIsSent()) {
                Log.i(TAG, "doInBackground: Waiting to send data");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }

    public Boolean getManualBool() {
        return this.locationManual;
    }

    public void switchManualBool() {
        this.locationManual = !this.locationManual;
    }

    public void updatePasswordSuccess(String newPassword) {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", newPassword);
        editor.apply();
    }

    public void notifyDispatch() {
        for (Order o : orders) {
            if (!o.getStatus().equals("counted")) {
                o.setCurMinute(0.0);
                o.setCurSecond(0.0);
                o.setStatus("counted");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanningResult = null;
        try {
            scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
        }
        if (data != null) {
            if (scanningResult != null) {
                if (scanningResult.getContents() != null) {
                    try {
                        String scanContent = scanningResult.getContents();
                        String[] location = scanContent.split(",");
                        String block = location[0];
                        String row = location[1];
                        String seat = location[2];

                        Boolean settings = loc.getSettings();

                        /*if (!settings) {
                            postLocation(block, row, seat, "no_setting");
                        } else {
                            postLocation(block, row, seat, "setting");
                            loc.setLocation(block, row, seat);
                        }*/
                    } catch (Exception e) {
                    }
                }
            } else if (data.getAction() == null && resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap uploadImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    if (uploadImage != null) {
                        prof.uploadImage = uploadImage;
                        profilePicture = uploadImage;
                        profileExpanded = uploadImage;
                        profilePictureFrame.setImageBitmap(profilePicture);
                        prof.profile_picture.setImageBitmap(uploadImage);

                        Toast.makeText(main, "Your profile picture has been updated", Toast.LENGTH_SHORT).show();

                        try {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            profilePicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            saveProfilePicture(encoded);
                        } catch (Exception e) {
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (data.getAction().equals("inline-data") && resultCode == Activity.RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profilePicture = imageBitmap;
                    profileExpanded = imageBitmap;
                    profilePictureFrame.setImageBitmap(profilePicture);
                    prof.profile_picture.setImageBitmap(imageBitmap);

                    Toast.makeText(main, "Your profile picture has been updated", Toast.LENGTH_SHORT).show();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    profilePicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    saveProfilePicture(encoded);
                } catch (Exception e) {
                }
            }
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLocation != null) {
            //latLng.setText("Latitude : "+mLocation.getLatitude()+" , Longitude : "+mLocation.getLongitude());
            //txtMyLat.setText("Latitude : " + mLocation.getLatitude());
            //txtMyLong.setText("Longitude : " + mLocation.getLongitude());
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            Log.e(TAG, "onLocationChanged Lat:\t" + latitude);
            Log.e(TAG, "onLocationChanged Long:\t" + longitude);
        }

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            //latLng.setText("Latitude : "+mLocation.getLatitude()+" , Longitude : "+mLocation.getLongitude());
            //txtMyLat.setText("Latitude : " + mLocation.getLatitude());
            //txtMyLong.setText("Longitude : " + mLocation.getLongitude());
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            Log.e(TAG, "onLocationChanged Lat:\t" + latitude);
            Log.e(TAG, "onLocationChanged Long:\t" + longitude);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Home.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void triggerNotifi() {
        cash = true;
        postLocation(block, row, seat);
    }
}
