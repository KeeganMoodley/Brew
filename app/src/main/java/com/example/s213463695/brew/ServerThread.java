package com.example.s213463695.brew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by s213463695 on 2016/06/20.
 */
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.s213463695.brew.Home.foodOrder;
import static com.example.s213463695.brew.Home.username;
import static com.example.s213463695.brew.Login.login;
import static com.example.s213463695.brew.Signup.signup;
import static com.example.s213463695.brew.Home.main;

public class ServerThread extends Thread {

    //private static final String ip = "csdev.nmmu.ac.za"; //Server hosted on csdev
    private static final String ip = "10.112.49.25"; //Local to check if working (Labs PC IP address)
    //private static final String ip = "192.168.172.2";

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private boolean connected;
    private boolean priceSet;
    private String command;
    private Double price;
    private boolean dataIsSent = false;

    /*public ArrayList<Food> getFoods() {
        return foods;
    }

    public void setFoods(ArrayList<Food> foods) {
        this.foods = foods;
    }

    private ArrayList<Food> foods = new ArrayList<>();*/

    public Solid getSolid() {
        return solid;
    }

    public void setSolid(Solid solid) {
        this.solid = solid;
    }

    private Solid solid;

    public ServerThread() {
        super();
        this.command = "";
    }

    @Override
    public void run() {
        if (connect()) {
            try {
                while (isConnected()) {
                    //Read in the current command or wait for next command
                    command = in.readUTF();

                    if (command.equals("#SUCCESSFUL_VERIFICATION")) {
                        String username = in.readUTF();
                        String eventS = in.readUTF();
                        Boolean event = false;
                        if (eventS.equals("true"))
                            event = true;
                        Intent in = new Intent(login, Home.class);
                        in.putExtra("username", username);
                        in.putExtra("email", login.email);
                        in.putExtra("password", login.password);
                        in.putExtra("event", event);
                        login.startActivity(in);
                        login.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                login._loginButton.setEnabled(true);
                            }
                        });

                    } else if (command.equals("#UNSUCCESSFUL_VERIFICATION")) {
                        final String indicator = in.readUTF();
                        login.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (indicator.equals("verification"))
                                    Toast.makeText(login, "Login failed!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(login, "Server connection failed!", Toast.LENGTH_SHORT).show();
                                login._loginButton.setEnabled(true);
                            }
                        });

                    } else if (command.equals("#SUCCESSFUL_SAVE")) {
                        signup.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                signup._signupButton.setEnabled(true);
                            }
                        });
                        String eventS = in.readUTF();
                        Boolean event = false;
                        if (eventS.equals("true"))
                            event = true;
                        Intent in = new Intent(login, Home.class);
                        in.putExtra("username", signup.name);
                        in.putExtra("email", signup.email);
                        in.putExtra("password", signup.password);
                        in.putExtra("signup", true);
                        in.putExtra("event", event);
                        signup.startActivity(in);

                    } else if (command.equals("#UNSUCCESSFUL_SAVE")) {
                        final String indicator = in.readUTF();
                        signup.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (indicator.equals("duplicateE"))
                                    Toast.makeText(signup, "Email-Address already exists!", Toast.LENGTH_SHORT).show();
                                else if (indicator.equals("duplicateU"))
                                    Toast.makeText(signup, "Username already exists!", Toast.LENGTH_SHORT).show();
                                else if (indicator.equals("duplicateEU"))
                                    Toast.makeText(signup, "Username and Email-Address already exists!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(signup, "Server connection failed!", Toast.LENGTH_SHORT).show();
                                signup._signupButton.setEnabled(true);
                            }
                        });
                    } else if (command.equals("#PRICE_RETURNED")) {
                        String priceString = in.readUTF();
                        price = Double.parseDouble(priceString);
                        priceSet = true;
                    } else if (command.equals("#ORDER_INSERTED")) {
                        String time = in.readUTF();
                        String date = in.readUTF();
                        String quantity = in.readUTF();
                        String price = in.readUTF();
                        String androidIndex = in.readUTF();

                        main.triggerNotifications(time, date, quantity, price, androidIndex, Home.finalFoods);
                        dataIsSent = true;
                    }
                    //Get food from sever
                    else if (command.equals("#GET_STOCK")) {
                        FoodOrder.obtainFoodInfo obtainFoodInfo = new FoodOrder.obtainFoodInfo(socket);
                        obtainFoodInfo.execute();

                    } else if (command.equals("#DISPATCH_DOWN")) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.notifyDispatchDown();
                            }
                        });
                    } else if (command.equals("#ORDER_REMOVED")) {
                        final String time = in.readUTF();
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.orderRemoved(time);
                            }
                        });
                    } else if (command.equals("#ORDER_REMOVAL_FAILURE")) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(main, "The removal of the order failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (command.equals("#SUCCESSFUL_UPDATE")) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.notifyDuplicate("neither", "", "");
                                Toast.makeText(main, "Your information has been successfully saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (command.equals("#UNSUCCESSFUL_UPDATE")) {
                        final String indicator = in.readUTF();
                        final String tempUser = in.readUTF();
                        final String tempEmail = in.readUTF();
                        if (indicator.equals("duplicateE")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main.notifyDuplicate("email", "", tempEmail);
                                    Toast.makeText(main, "Email-Address already exists!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (indicator.equals("duplicateU")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main.notifyDuplicate("user", tempUser, "");
                                    Toast.makeText(main, "Username already exists!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (indicator.equals("duplicateEU")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main.notifyDuplicate("both", tempUser, tempEmail);
                                    Toast.makeText(main, "Username and Email-Address already exists!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(main, "Server Connection Error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else if (command.equals("#SUCCESSFUL_LOCATION_RETRIEVAL")) {
                        String countS = in.readUTF();
                        Integer count = Integer.parseInt(countS);
                        final ArrayList<Block> blocks = new ArrayList<>();
                        for (int x = 0; x <= count - 1; x++) {
                            String curN = in.readUTF();
                            String rowCS = in.readUTF();
                            Integer rowC = Integer.parseInt(rowCS);
                            Block curB = new Block(curN, new ArrayList<Block.Row>());
                            for (int y = 0; y <= rowC - 1; y++) {
                                String rowNumS = in.readUTF();
                                String seatCS = in.readUTF();

                                curB.addRow(Integer.parseInt(rowNumS), Integer.parseInt(seatCS));
                            }
                            blocks.add(curB);
                        }
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.setBlocks(blocks);
                            }
                        });
                    } else if (command.equals("#UNSUCCESSFUL_LOCATION_RETRIEVAL")) {

                    } else if (command.equals("#UPDATE")) {
                        String update = in.readUTF();
                        main.compareUpdates(update);
                    } else if (command.equals("#SUCCESSFUL_LOGOUT")) {
                        String context = "";
                        try {
                            context = in.readUTF();
                        } catch (Exception e) {
                        }
                        if (context.equals("homeOut")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(main, "Successfully signed out!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (context.contains("@")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(main, "Account was successfully deleted!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        connected = false;
                        interrupt();
                    } else if (command.equals("#UNSUCCESSFUL_LOGOUT")) {
                        String context = "";
                        try {
                            context = in.readUTF();
                        } catch (Exception e) {
                        }
                        if (context.equals("homeOut")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(main, "Could not sign out!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (context.contains("@")) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(main, "Could not delete account!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        connected = false;
                        interrupt();
                    } else if (command.equals("#SUCCESSFUL_LOCATION_UPDATE")) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(main, "Location has been successfully updated!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (command.equals("#SUCCESSFUL_PASSWORD_UPDATE")) {
                        final String newPassword = in.readUTF();
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.updatePasswordSuccess(newPassword);
                                Toast.makeText(main, "Password has been successfully updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (command.equals("#UNSUCCESSFUL_PASSWORD_UPDATE")) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(main, "Password update failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (command.equals("#ORDER_DISPATCHED")) {
                        String indexString = in.readUTF();
                        final String index = indexString;
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.notifyDispatch();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                // input stream no longer working, e.g. connection lost
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public boolean isDataIsSent() {
        return dataIsSent;
    }

    //Request to server to get food
    public void requestFood() {
        if (isConnected()) {
            try {
                out.writeUTF("#GET_STOCK");
                out.flush();
                Log.e(TAG, "requestFood: Food has been requested");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void invokeLogin(String email, String password) {
        if (isConnected()) {
            try {
                out.writeUTF("#VERIFY_LOGIN_DATA");
                out.writeUTF(email);
                out.writeUTF(password);
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void invokeSignUp(String email, String password, String name) {
        if (isConnected()) {
            try {
                out.writeUTF("#SIGN_UP");
                out.writeUTF(email);
                out.writeUTF(password);
                out.writeUTF(name);
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public Double getBeerPrice() {
        priceSet = false;
        if (isConnected()) {
            try {
                out.writeUTF("#GET_PRICE");
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
        while (!priceSet) {
        }
        return price;
    }

    //Used to send location and food to server, after location has been identified.
    public void postLocation(String quantity, String block, String row, String seat, String user, ArrayList<Food> finalFoods, Double total, boolean cash) {
        if (isConnected()) {
            try {
                Log.i(TAG, "postLocation: Order is being sent to the server");
                out.writeUTF("#INSERT_ORDER");
                out.writeUTF(quantity);//quantity is "0"
                out.writeUTF(block);
                out.writeUTF(row);
                out.writeUTF(seat);
                //out.writeUTF(setting);
                out.writeUTF(user);
                out.writeDouble(total); //total is null
                out.writeInt(finalFoods.size());
                for (Food f : finalFoods) {
                    out.writeInt(f.id);
                    out.writeInt(f.getQuantity());
                }
                out.writeBoolean(cash);
                out.flush();
                //dataIsSent = true;
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void changeLocation(String block, String row, String seat, String user) {
        if (isConnected()) {
            try {
                out.writeUTF("#CHANGE_LOCATION");
                out.writeUTF(block);
                out.writeUTF(row);
                out.writeUTF(seat);
                out.writeUTF(user);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean connect() {
        connected = false;
        try {
            // connect to server (if can)
            socket = new Socket(ip, 8050);

            // obtain streams
            out = new DataOutputStream(socket.getOutputStream());
            out.flush();
            in = new DataInputStream(socket.getInputStream());

            connected = true;

        } catch (Exception e) {
            // something went wrong with the connect
            Log.e("THREAD", "ERROR: " + e.getMessage());
            Log.e("Server", "Not connecting");
        }
        return connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public void removeOrder(Order order) {
        if (isConnected()) {
            try {
                String time = order.getTime();
                out.writeUTF("#REMOVE_ORDER");
                out.writeUTF(time);
                out.writeUTF(username);
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void storeProfile(String tempUser, String tempEmail, String name, String email, String cell, String phone) {
        if (isConnected()) {
            try {
                out.writeUTF("#STORE_PROFILE");
                out.writeUTF(tempUser);
                out.writeUTF(tempEmail);
                out.writeUTF(name);
                out.writeUTF(email);
                out.writeUTF(cell);
                out.writeUTF(phone);
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void cancelConnection(String context) {
        if (isConnected()) {
            try {
                out.writeUTF("#QUIT");
                out.writeUTF(context);
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void getLocations() {
        if (isConnected()) {
            try {
                out.writeUTF("#RETRIEVE_LOCATIONS");
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void getUpdate() {
        if (isConnected()) {
            try {
                out.writeUTF("#RETRIEVE_UPDATE");
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

    public void changePassword(String password, String email, String username) {
        if (isConnected()) {
            try {
                out.writeUTF("#CHANGE_PASSWORD");
                out.writeUTF(password);
                out.writeUTF(email);
                out.writeUTF(username);
                out.flush();
            } catch (IOException e) {
                Log.e("THREAD", "ERROR: " + e.getMessage());
            }
        }
    }

//    public void triggerTestRun() {
//        if(isConnected()){
//            try{
//                out.writeUTF("#TEST_RUN");
//                out.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
