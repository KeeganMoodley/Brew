package com.example.s213463695.brew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by s213463695 on 2016/06/20.
 */
import static com.example.s213463695.brew.Login.serverLink;

public class Signup extends AppCompatActivity {

    private static final String TAG = "Signup";
    public static Signup signup;
    public String name;
    public String email;
    public String password;
    public boolean callsActivity = false;
    public boolean disconnected = false;

    @InjectView(R.id.input_name)
    EditText _nameText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.input_confirm)
    EditText _confirmText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        ButterKnife.inject(this);

        signup = this;

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callsActivity = true;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            _signupButton.setEnabled(true);
            return;
        }
        _signupButton.setEnabled(false);

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        //Invokes the sign up on the server side
        callsActivity = true;

        serverLink.invokeSignUp(email, password, name);
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirm = _confirmText.getText().toString();

        if (password.equals(confirm)) {
            _confirmText.setError(null);
        } else {
            _confirmText.setError("passwords do not match");
            valid = false;
        }

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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
        super.onResume();
    }
}
