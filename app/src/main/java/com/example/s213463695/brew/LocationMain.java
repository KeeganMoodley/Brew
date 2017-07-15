package com.example.s213463695.brew;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import static com.example.s213463695.brew.Home.main;
import static com.example.s213463695.brew.Home.blocks;
import static com.example.s213463695.brew.Home.event;
import static com.example.s213463695.brew.Home.orders;

public class LocationMain extends Fragment {

    final List<String> blockNames = new ArrayList<String>();
    final List<String> rowNames = new ArrayList<String>();
    final List<String> seatNames = new ArrayList<String>();
    private LocationListener mListener;
    private AppCompatButton confirm;
    private AppCompatButton cancel;
    private Spinner block = null;
    private Spinner row = null;
    private Spinner seat = null;
    private String initialBlock = "";
    private String initialRow = "";
    private String initialSeat = "";
    private String currentBlock = "Select Block";
    private Block tempBlock = null;
    private String currentRow = "Select Row";
    private String currentSeat = "Select Seat";
    private Boolean prefSetup = false;
    private Boolean settings = false;

    public LocationMain() {
    }

    public static LocationMain newInstance() {
        LocationMain fragment = new LocationMain();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_main, container, false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.qr_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event) {
                    main.callsActivity = true;
                    IntentIntegrator integrator = new IntentIntegrator(main);
                    integrator.initiateScan();
                } else {
                    Toast.makeText(main, "Please wait for an ongoing event!", Toast.LENGTH_LONG);
                }
            }
        });

        block = (Spinner) v.findViewById(R.id.blockSpin);
        row = (Spinner) v.findViewById(R.id.rowSpin);
        seat = (Spinner) v.findViewById(R.id.seatSpin);

        if (blockNames.size() == 0) {
            rowNames.add("Select Row");
            seatNames.add("Select Seat");
            blockNames.add("Select Block");
            for (Block b : blocks)
                blockNames.add(b.getName());
        }

        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, blockNames);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        block.setAdapter(spinnerArrayAdapter1);

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, rowNames);
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        row.setAdapter(spinnerArrayAdapter2);

        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, seatNames);
        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seat.setAdapter(spinnerArrayAdapter3);

        block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!prefSetup) {
                    if (position != 0) {
                        currentBlock = blockNames.get(position);
                        seatNames.clear();
                        seatNames.add("Select Seat");
                        rowNames.clear();
                        rowNames.add("Select Row");
                        row.setSelection(0);
                        seat.setSelection(0);
                        tempBlock = blocks.get(position - 1);
                        for (int x = 0; x <= tempBlock.getRows().size() - 1; x++) {
                            rowNames.add(String.valueOf(tempBlock.getRows().get(x).getNumber()));
                        }
                        row.setEnabled(true);
                    } else {
                        seatNames.clear();
                        seatNames.add("Select Seat");
                        rowNames.clear();
                        rowNames.add("Select Row");
                        row.setSelection(0);
                        seat.setSelection(0);
                        row.setEnabled(false);
                        seat.setEnabled(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        row.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!prefSetup) {
                    if (position != 0) {
                        currentRow = rowNames.get(position);
                        seatNames.clear();
                        seatNames.add("Select Seat");
                        seat.setSelection(0);
                        for (int x = 0; x <= tempBlock.getRows().get(position - 1).getSeatCount() - 1; x++) {
                            seatNames.add(String.valueOf((x + 1)));
                        }
                        seat.setEnabled(true);
                    } else {
                        seat.setEnabled(false);
                        seatNames.clear();
                        seatNames.add("Select Seat");
                        seat.setSelection(0);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        seat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!prefSetup) {
                    if (position != 0) {
                        currentSeat = seatNames.get(position);
                    }
                } else {
                    prefSetup = !prefSetup;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cancel = (AppCompatButton) v.findViewById(R.id.cancel_location_main);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.triggerCancel();
            }
        });

        confirm = (AppCompatButton) v.findViewById(R.id.confirm_location_main);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (block.getSelectedItem().toString().equals("Select Block"))
                    Toast.makeText(main, "Please select a block first.", Toast.LENGTH_SHORT).show();
                else if (row.getSelectedItem().toString().equals("Select Row"))
                    Toast.makeText(main, "Please select a row first.", Toast.LENGTH_SHORT).show();
                else if (seat.getSelectedItem().toString().equals("Select Seat"))
                    Toast.makeText(main, "Please select a seat first.", Toast.LENGTH_SHORT).show();
                else {
                    if (!settings && (checkOrderStatus() || changeListener()))
                        mListener.postLocation(currentBlock, currentRow, currentSeat, "no_setting");
                    else if (checkOrderStatus() || changeListener())
                        mListener.postLocation(currentBlock, currentRow, currentSeat, "setting");
                    else
                        Toast.makeText(main, "Please wait for your dispatched orders to arrive before You change Your location", Toast.LENGTH_LONG).show();
//                    if(!settings)
//                        mListener.postLocation(currentBlock,currentRow,currentSeat,"no_setting");
//                    else
//                        mListener.postLocation(currentBlock,currentRow,currentSeat,"setting");
                }
            }
        });
        if (main.getManualBool())
            triggerLocationMessage();
        else
            main.switchManualBool();
        mListener.previousLocation(this);
        return v;
    }

    public Boolean checkOrderStatus() {
        Boolean cleared = true;
        for (Order or : orders) {
            if (!or.getStatus().equals("counted"))
                cleared = false;
        }
        return cleared;
    }

    public Boolean changeListener() {
        return (initialSeat.equals(currentSeat) &&
                initialRow.equals(currentRow) &&
                initialBlock.equals(currentBlock));
    }

    private void triggerLocationMessage() {
        Boolean window = mListener.getWindowBool();
        if (window) {
            AlertDialog.Builder adb = new AlertDialog.Builder(main);
            adb.setTitle("Location Setup");
            adb.setMessage("To confirm your order you have to specify your location first.To do so scan in the QR code in front of you by selecting the QR button on this page or manually set up your location.");
            adb.setPositiveButton("Don't show again", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mListener.setWindowBool(false);
                    if (currentBlock.equals("Select Block")) {
                        row.setEnabled(false);
                        seat.setEnabled(false);
                    }
                    dialog.cancel();
                }
            });
            adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (currentBlock.equals("Select Block")) {
                        row.setEnabled(false);
                        seat.setEnabled(false);
                    }
                    dialog.cancel();
                }
            }).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainListener(LocationListener order) {
        this.mListener = order;
    }

    public void setLocation(String curB, String curR, String curS) {
        initialBlock = curB;
        initialRow = curR;
        initialSeat = curS;
        Integer bPosition = 0;
        for (int i = 0; i < blockNames.size(); i++) {
            if (blockNames.get(i).equals(curB))
                bPosition = i;
        }
        block.setSelection(bPosition, true);
        prefSetup = true;

        try {
            tempBlock = blocks.get(bPosition - 1);
            rowNames.clear();
            rowNames.add("Select Row");
            for (int x = 0; x <= tempBlock.getRows().size() - 1; x++) {
                rowNames.add(String.valueOf(tempBlock.getRows().get(x).getNumber()));
            }
            row.setEnabled(true);

            Integer rPosition = 0;
            for (int i = 0; i < rowNames.size(); i++) {
                if (rowNames.get(i).equals(curR))
                    rPosition = i;
            }
            row.setSelection(rPosition, true);

            seatNames.clear();
            seatNames.add("Select Seat");
            for (int x = 0; x <= tempBlock.getRows().get(rPosition - 1).getSeatCount() - 1; x++) {
                seatNames.add(String.valueOf((x + 1)));
            }
        } catch (Exception e) {
        }
        seat.setEnabled(true);

        Integer sPosition = 0;
        for (int i = 0; i < seatNames.size(); i++) {
            if (seatNames.get(i).equals(curS))
                sPosition = i;
        }
        seat.setSelection(sPosition, true);

        this.currentRow = curR;
        this.currentSeat = curS;
        this.currentBlock = curB;
    }

    public void settingsIndication(boolean b) {
        this.settings = b;
    }

    public Boolean getSettings() {
        return settings;
    }

    public interface LocationListener {
        void triggerCancel();

        Boolean getWindowBool();

        void setWindowBool(boolean b);

        void previousLocation(LocationMain locationMain);

        void postLocation(String currentBlock, String currentRow, String currentSeat, String setting);
    }
}
