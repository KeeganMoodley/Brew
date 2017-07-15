package com.example.s213463695.brew;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zapper.qrcode.PaymentField;
import com.zapper.qrcode.ZapperCode;
import com.zapper.qrcode.enums.Currency;
import com.zapper.qrcode.enums.PaymentFieldType;

import java.io.IOException;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by s214079694 on 2017/07/02.
 */

public class Payment extends Fragment {
    private PaymentListener paymentListener;
    private ImageView imgQRCode;
    private String qrString;

    /**
     * To invoke the most basic QR Code (Amount and a Reference):
     */
    public void zapper(double amount) {
        ZapperCode zapperCode = new ZapperCode();
        zapperCode.setBillAmount((int) Math.round(amount));
        PaymentField paymentField = new PaymentField(PaymentFieldType.Reference, "REF-001");
        zapperCode.paymentFields.add(paymentField);
        qrString = zapperCode.toString();
    }

    /**
     * Creates a QR Code used to open Zapper and process a payment order
     *
     * @param qrCodeData
     * @param qrCodewidth
     * @param qrCodeheight
     */
    public static Bitmap createQRCode(String qrCodeData, int qrCodewidth, int qrCodeheight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight);

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            int[] pixels = new int[width * height];

            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "createQRCode: Error");
            return null;
        }
    }

    public Payment() {
    }

    public static Payment newInstance() {
        Payment fragment = new Payment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        initialiseZapper();
        return fragment;
    }

    /**
     * This method takes in the MerchantId, SiteId, Currency and Merchant Name. After calling this once, all future Zapper QR Codes will use this configuration
     */
    private static void initialiseZapper() {
        ZapperCode.initialize(10, 10, Currency.ZAR, "Port Elizabeth");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ImageView imgZapper = (ImageView) view.findViewById(R.id.imgZapper);
        ImageView imgCash = (ImageView) view.findViewById(R.id.imgCash);
        final RadioButton rbZapper = (RadioButton) view.findViewById(R.id.rbZapper);
        final RadioButton rbCash = (RadioButton) view.findViewById(R.id.rbCash);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rgb);
        imgQRCode = (ImageView) view.findViewById(R.id.imgQRCode);
        //radioGroup.addView(rbZapper);
        //radioGroup.addView(rbCash);
        imgZapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbZapper.setChecked(true);
                rbCash.setChecked(false);
            }
        });
        imgCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbZapper.setChecked(false);
                rbCash.setChecked(true);

            }
        });
        TextView txtTotal = (TextView) view.findViewById(R.id.txtPaymentTotal);
        Button btnContinue = (Button) view.findViewById(R.id.btnContinuePayment);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapper(123);
                setQRCode(qrString, imgQRCode.getWidth(), imgQRCode.getHeight());
            }
        });
        Button btnCancel = (Button) view.findViewById(R.id.btnCancelPayment);

        //return super.onCreateView(inflater, container, savedInstanceState);
        Log.e(TAG, "onCreateView: Payment frag view is created");
        return view;
    }

    private void setQRCode(String code, int width, int height) {
        imgQRCode.setImageBitmap(createQRCode(code, width, height));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        paymentListener = null;
    }


    public interface PaymentListener {

    }

    public void setPaymentListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }
}
