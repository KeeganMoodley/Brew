package com.example.s213463695.brew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by s214079694 on 2017/07/02.
 */

public class Payment extends Fragment {

    public Payment() {
    }

    public static Payment newInstance() {
        Payment fragment = new Payment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ImageView imgZapper = (ImageView) view.findViewById(R.id.imgZapper);
        ImageView imgCash = (ImageView) view.findViewById(R.id.imgCash);
        final RadioButton rbZapper = (RadioButton) view.findViewById(R.id.rbZapper);
        final RadioButton rbCash = (RadioButton) view.findViewById(R.id.rbCash);
        RadioGroup radioGroup = new RadioGroup(view.getContext());
        radioGroup.addView(rbZapper);
        radioGroup.addView(rbCash);
        imgZapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbZapper.setEnabled(true);
            }
        });
        imgCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbCash.setEnabled(true);
            }
        });

        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }
}
