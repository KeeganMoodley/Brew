package com.example.s213463695.brew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;

import static com.example.s213463695.brew.Login.serverLink;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeContainer.HomeListener,
        PlaceOrder.PlaceOrderListener,
        Notifications.NotificationsListener,
        Profile.ProfileListener,
        LocationMain.LocationListener {

    private FragmentManager fragMan;
    private FragmentTransaction fragTran;
    private String curTitle = "";
    private String quantity = "";
    public static ArrayList<Order> orders;
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
    private Integer Count;
    private Stack<String> titleStack;
    public static boolean event = false;
    private boolean mainContaining;
    public boolean callsActivity = false;
    public boolean disconnected = false;
    private boolean locationManual = true;
    private File orderData;

    public static Notifications not = null;
    public static Profile prof = null;
    public static LocationMain loc = null;

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

        HomeContainer homeC = HomeContainer.newInstance();
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
            readOrderData();
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
        for (Order or : orders) {
            writeToFile(or.getTime(), or.getDate(), or.getQuantity(), or.getPrice(), or.getCurMinute(), or.getCurSecond(), or.getOrderNum(), or.getStatus(), or.getAddedAt(), or.getDateIndex());
        }
    }

    private void readOrderData() {
        try {
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

        if (id == R.id.home) {
            titleStack.push(curTitle);
            Count = 0;
            HomeContainer home = new HomeContainer();
            home.setMainListener(this);
            replaceFragment(home, "Home", true, "HOME");
        } else if (id == R.id.place) {
            /*PlaceOrder placeO = PlaceOrder.newInstance();
            placeO.setMainListener(this);
            replaceFragment(placeO, "Place Order", false, "OTHER");*/
            FoodOrder foodOrder = FoodOrder.newInstance();
            //foodOrder.setMainListener((FoodOrder.FoodOrderListener) this);
            replaceFragment(foodOrder, "Food Order", false, "OTHER");
        } else if (id == R.id.view) {
            not = Notifications.newInstance();
            not.setMainListener(this);
            replaceFragment(not, "Notification", false, "OTHER");
        } else if (id == R.id.location) {
            loc = LocationMain.newInstance();
            loc.setMainListener(this);
            replaceFragment(loc, "Location Setup", false, "OTHER");
            loc.settingsIndication(true);
//        } else if (id == R.id.tester) {
//            serverLink.triggerTestRun();
        } else if (id == R.id.password) {
            passwordSetup();
        } else if (id == R.id.logout) {
            SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("password");
            editor.commit();

            callsActivity = true;
            serverLink.cancelConnection("homeOut");
            disconnected = true;

            Intent logout = new Intent(Home.this, Login.class);
            startActivity(logout);
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

    private void replaceFragment(Fragment fragment, String title, Boolean mainContain, String name) {
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
            PlaceOrder placeO = PlaceOrder.newInstance();
            placeO.setMainListener(this);
            replaceFragment(placeO, "Place Order", false, "ORDER");
        }
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
    public void postLocation(String currentBlock, String currentRow, String currentSeat, String setting) {
        SharedPreferences preferences = main.getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentBlock", currentBlock);
        editor.putString("currentRow", currentRow);
        editor.putString("currentSeat", currentSeat);
        try {
            editor.apply();
        } catch (Exception e) {
        }
        serverLink.postLocation(quantity, currentBlock, currentRow, currentSeat, setting, username);
    }

    @Override
    public Double getPrice() {
        return serverLink.getBeerPrice();
    }

    public void triggerNotifications(String t, String d, String q, String p, String androidIndex) {
        Order newO = null;
        Date curDate = new Date();
        if (orders.size() != 0) {
            newO = new Order(t, d, q, p, 2.0, 59.0, orders.get(orders.size() - 1).getOrderNum() + 1, "queue", curDate, androidIndex);
            orders.add(newO);
            writeToFile(t, d, q, p, 2.0, 59.0, orders.get(orders.size() - 1).getOrderNum() + 1, "queue", curDate, androidIndex);
        } else {
            newO = new Order(t, d, q, p, 2.0, 59.0, orders.size() + 1, "queue", curDate, androidIndex);
            orders.add(newO);
            writeToFile(t, d, q, p, 2.0, 59.0, orders.size() + 1, "queue", curDate, androidIndex);
        }
        not = Notifications.newInstance();
        not.setMainListener(this);
        replaceFragment(not, "", false, "NOTIFICATION");
        orders.get(orders.size() - 1).startThread();
    }

    public void notifyDispatchDown() {
        Toast.makeText(main, "Sorry, the responsible dispatch-point is inactive at the moment...", Toast.LENGTH_LONG).show();
    }

    private void writeToFile(String t, String d, String q, String p, double v, double v1, int i, String queue, Date curDate, String dateIndex) {
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

    @Override
    public void cancelOrder(Order order) {
        serverLink.removeOrder(order);
    }

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
        if (!callsActivity) {
            if (!disconnected) {
                serverLink.cancelConnection("login");
                disconnected = true;
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (!callsActivity) {
            if (!disconnected) {
                serverLink.cancelConnection("login");
                disconnected = true;
            }
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
        /*Boolean validLocation = checkLocation();
        if (validLocation) {
            Toast.makeText(this, "Location is valid", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You are not at the stadium", Toast.LENGTH_LONG).show();
        }*/
        super.onResume();
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

                        if (!settings) {
                            postLocation(block, row, seat, "no_setting");
                        } else {
                            postLocation(block, row, seat, "setting");
                            loc.setLocation(block, row, seat);
                        }
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
}
