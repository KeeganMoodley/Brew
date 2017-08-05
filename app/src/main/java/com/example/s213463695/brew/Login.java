package com.example.s213463695.brew;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    public String email = "";
    public String password = "";
    public static Login login = null;
    public static ServerThread serverLink = null;
    public boolean callsActivity = false;
    public boolean disconnected = false;
    public boolean paused = false;

    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.inject(this);

        if (login == null)
            login = this;
        if (serverLink == null || !serverLink.isConnected()) {
            serverLink = new ServerThread();
            serverLink.start();
        }
        //serverLink.requestFood();

        loadSavedLoginInfo();
        _loginButton.setEnabled(true);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                callsActivity = true;
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
            return;
        }

        _loginButton.setEnabled(false);

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        //Invokes the login and authentication on the server side
        callsActivity = true;
        serverLink.invokeLogin(email, password);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    private void loadSavedLoginInfo() {
        SharedPreferences preferences = getSharedPreferences("brew_prefs", MODE_MULTI_PROCESS);
        if (!preferences.getString("email", "").equals("")) {
            _emailText.setText(preferences.getString("email", ""));
            String test = preferences.getString("password", "");
            _passwordText.setText(test);
            login();
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
                paused = true;
                serverLink.cancelConnection("login");
                disconnected = true;
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (paused) {
            if (serverLink == null || !serverLink.isConnected()) {
                serverLink = new ServerThread();
                serverLink.start();
                paused = false;
            }
        }
        disconnected = false;
        super.onResume();
    }
}

