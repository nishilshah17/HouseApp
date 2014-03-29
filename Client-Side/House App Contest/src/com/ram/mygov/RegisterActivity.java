package com.ram.mygov;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.InputStream;

public class RegisterActivity extends Activity {

    // UI Elements
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mFNameView;
    private EditText mLNameView;
    private EditText mPasswordView;
    private ProgressBar registerBar;
    private TextView invalidView;

    // Registration Details
    String mUsername;
    String mEmail;
    private String mFName;
    private String mLName;
    private String mPassword;
    private int mType;

    // Registration Requirements
    private static final int MIN_PASSWORD_LENGTH = 6;

    // Account Information
    private String userID;

    // Internet Connection
    private UserRegisterTask registerTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register);

        mUsernameView = ((EditText) findViewById(R.id.suUsername));
        mEmailView = ((EditText) findViewById(R.id.suEmail));
        mFNameView = ((EditText) findViewById(R.id.suFName));
        mLNameView = ((EditText) findViewById(R.id.suLName));
        mPasswordView = ((EditText) findViewById(R.id.suPassword));

        Button constRegisterButton = (Button) findViewById(R.id.suConstRegister);
        constRegisterButton.setBackgroundColor(Color.rgb(0, 255, 210));
        constRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(0);
            }
        }
        );

        Button govRegisterButton = (Button) findViewById(R.id.suGovRegister);
        govRegisterButton.setBackgroundColor(Color.rgb(0, 255, 210));
        govRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(1);
            }
        }
        );

        registerBar = (ProgressBar) findViewById(R.id.registerBar);
        registerBar.setVisibility(View.INVISIBLE);

        invalidView = (TextView) findViewById(R.id.invalidAccountView);
        invalidView.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return false;
    }

    public void displayInvalidMessage(String error) {

        if (error.equals(PHPScriptVariables.CONNECTION_FAILED))
            invalidView.setText("Email Already In Use");
        else if (error.equals(PHPScriptVariables.CONNECTION_FAILED2))
            invalidView.setText("Username Already In Use");

        invalidView.setVisibility(View.VISIBLE);

    }

    public void register(int type) {

        mType = type;

        mUsernameView.setError(null);
        mEmailView.setError(null);
        mFNameView.setError(null);
        mLNameView.setError(null);
        mPasswordView.setError(null);

        mUsername = mUsernameView.getText().toString();
        mEmail = mEmailView.getText().toString();
        mFName = mFNameView.getText().toString();
        mLName = mLNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@") || !mEmail.contains(".")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (mType == 1 && !mEmail.contains(".gov")) {
            mEmailView.setError("Use your official government email");
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mFName)) {
            mFNameView.setError(getString(R.string.error_field_required));
            focusView = mFNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mLName)) {
            mLNameView.setError(getString(R.string.error_field_required));
            focusView = mLNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < MIN_PASSWORD_LENGTH) {
            mPasswordView.setError("Minimum password length of 6 characters required");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            showProgress(true);

            registerTask = new UserRegisterTask();
            registerTask.execute((Void) null);

        }

    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params)
        {

            String path = "http://clubbedinapp.com/houseapp/php/newuser.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            HttpResponse response;
            JSONObject json = new JSONObject();
            try {
                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.suusernameString,mUsername);
                json.put(PHPScriptVariables.suemailString, mEmail);
                json.put(PHPScriptVariables.sufirstnameString,mFName);
                json.put(PHPScriptVariables.sulastnameString,mLName);
                json.put(PHPScriptVariables.supasswordString,mPassword);
                json.put(PHPScriptVariables.sutypeString, mType);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    userID = JSONParser.convertStreamToString(in,PHPScriptVariables.registerString);
                    if (!userID.equals(PHPScriptVariables.CONNECTION_FAILED) && !userID.equals(PHPScriptVariables.CONNECTION_FAILED2))
                        return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            registerTask = null;
            showProgress(false);

            if (success)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                alertDialogBuilder.setTitle("Confirmation");

                alertDialogBuilder.setMessage("Confirmation email has been sent");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegisterActivity.this.finish();
                    }
                }
                );

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

            } else
            {
                if (userID.equals(PHPScriptVariables.CONNECTION_FAILED) || userID.equals(PHPScriptVariables.CONNECTION_FAILED2))
                    displayInvalidMessage(userID);

            }
        }

        @Override
        protected void onCancelled()
        {
            registerTask = null;
            showProgress(false);
        }

    }

    private void showProgress(boolean b) {

        if (b)
            registerBar.setVisibility(View.VISIBLE);
        else
            registerBar.setVisibility(View.INVISIBLE);

    }

}