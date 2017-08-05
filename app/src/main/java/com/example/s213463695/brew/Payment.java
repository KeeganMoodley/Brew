package com.example.s213463695.brew;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.zapper.qrcode.PaymentField;
import com.zapper.qrcode.ZapperCode;
import com.zapper.qrcode.enums.Currency;
import com.zapper.qrcode.enums.PaymentFieldType;

import org.json.JSONObject;
import org.tukaani.xz.check.SHA256;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.security.auth.callback.Callback;

import cz.msebera.android.httpclient.Header;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by s214079694 on 2017/07/02.
 */

public class Payment extends Fragment {
    private TotalL totalL;
    private PaymentListener paymentListener;
    private ImageView imgQRCode;
    private String qrString;
    static double total = 12.34;
    private JSONObject jsonObject;
    final static int MerchantId = 817, SiteId = 463; //Central Perk details (works)
    final static String merchantName = "Nelson Mandela University";
    private String block, row, seat;
    private ArrayList<Food> foods = new ArrayList<>();

    //QR code structure
    //http://2.zap.pe?t=6&i={MerchantId}:{SiteId}:7[34|{Amount}|{AmountType},{Tip},66|{MerchantReference}|10,60|1:10[38|{ShortMerchantName},39|{CurrencyIsoCode}
    //example
    //http://2.zap.pe?t=6&i=817:463:7[34|99.99|11,66|ReferenceId|10,60|1:10[38|Central Perk,39|ZAR
    String qrCode = "http://2.zap.pe?t=6&i=" + MerchantId + ":" + SiteId + ":7[34|" + total + "|11,66|ReferenceId|10,60|1:10[38|" + merchantName + ",39|ZAR";

    /**
     * To invoke the most basic QR Code (Amount and a Reference):
     */
    public void zapper(double amount) {
        ZapperCode zapperCode = new ZapperCode();
        int n = (int) amount;
        if (n < 100)
            n = n * 100;
        else
            n = n * 1000;
        zapperCode.setBillAmount(n);
        PaymentField paymentField = new PaymentField(PaymentFieldType.Reference, "REF-001");
        zapperCode.paymentFields.add(paymentField);

        //R code structure

        //qrString = zapperCode.toString();
        qrString = qrCode;
        //qrString = "http://2.zap.pe?t=6&i=817:463:7[34|99.99|11,66|123|10,60|1:10[38|Central Perk,39|ZAR";
        //qrString = "zapper://payment?qr=http://2.zap.pe?t=6&i=817:463:7[34|80|3,66|4630000000000001|10,60|1:10[39n|ZAR,38|Central Perk&appName=Central Perk&successCallbackURL=centralperk://success?posid=4630000000000001&failureCallbackURL=centralperk://failure?posid=4630000000000001";
        request();
    }

    /**
     * HTTPS request Zapper API
     */
    public void request() {
        /*try {
            URL url = new URL("https://zapapi.zapzap.mobi/zapperpointofsale/api/v2");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, new SecureRandom());
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }*/

        //new ZapperAPI().execute("https://zapapi.zapzap.mobi/zapperpointofsale/api/payments/CreateCustomerAndInitiatePayment");
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

    public static Payment newInstance(double t) {
        Payment fragment = new Payment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        initialiseZapper();
        total = t;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        block = bundle.getString("block");
        row = bundle.getString("row");
        seat = bundle.getString("seat");
        foods = (ArrayList<Food>) bundle.getSerializable("food");
    }

    /**
     * This method takes in the MerchantId, SiteId, Currency and Merchant Name. After calling this once, all future Zapper QR Codes will use this configuration
     */
    private static void initialiseZapper() {
        ZapperCode.initialize(MerchantId, SiteId, Currency.ZAR, merchantName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ImageView imgZapper = (ImageView) view.findViewById(R.id.imgZapper);
        ImageView imgCash = (ImageView) view.findViewById(R.id.imgCash);
        final RadioButton rbZapper = (RadioButton) view.findViewById(R.id.rbZapper);
        final RadioButton rbCash = (RadioButton) view.findViewById(R.id.rbCash);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rgb);
        //imgQRCode = (ImageView) view.findViewById(R.id.imgQRCode);

        boolean cash = false;
        final boolean[] finalCash = {cash};
        imgZapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbZapper.setChecked(true);
                rbCash.setChecked(false);
                finalCash[0] = false;
            }
        });
        imgCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbZapper.setChecked(false);
                rbCash.setChecked(true);
                finalCash[0] = true;
            }
        });

        finalCash[0] = rbCash.isChecked();

        TextView txtTotal = (TextView) view.findViewById(R.id.txtPaymentTotal);
        txtTotal.setText("Total: R" + String.format("%.2f", total));

        Button btnContinue = (Button) view.findViewById(R.id.btnContinuePayment);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbZapper.isChecked()) {
                    zapper(total);
                    String encoder = "";
                    String qrString = "http://2.zap.pe?t=6&i=817:463:7[34|" + total + "|3,66|4630000000000001|10,60|1:10[39n|ZAR,38|Brew";
                    String appString = "Brew";
                    String successString = "brew://success?posid=4630000000000001";
                    String failureString = "brew://failure?posid=4630000000000001";
                    String uriString = "";
                    try {
                        //encoder = URLEncoder.encode(qrString, "utf-8");
                        uriString = "zapper://payment?qr=" + URLEncoder.encode(qrString, "utf-8") + "&appName=" + URLEncoder.encode(appString, "utf-8") + "&successCallbackURL=" + URLEncoder.encode(successString, "utf-8") + "&failureCallbackURL=" + URLEncoder.encode(failureString, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Uri uri = Uri.parse(uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    paymentListener.triggerNotifi(); //true
                }
            }
        });

        final PackageManager packageManager = this.getContext().getPackageManager();


        Button btnCancel = (Button) view.findViewById(R.id.btnCancelPayment);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                startActivity(sendIntent);*/
                if (rbZapper.isChecked()) {
                    try {
                        Intent intent = packageManager.getLaunchIntentForPackage("com.zapper.android");
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(view.getContext(), "Zapper is currently not installed", Toast.LENGTH_SHORT).show();
                    }
                }

                //Intent intent = new Intent(brew.SCAN);
            }
        });

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
        void triggerNotifi();
    }

    public interface TotalL {
        double getOrderTotal();
    }

    public void setPaymentListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }
}
