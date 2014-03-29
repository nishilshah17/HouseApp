package com.ram.mygov;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
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

public class LoginActivity extends Activity {

    // UI Elements
    private TextView invalidView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private Button signInButton;
    private ProgressBar progressBar;
	private Button registerButton;

    // Login Details
    private String mEmail;
    private String mPassword;

    // Account Information
    private String userID;

    // Internet Connection
    private UserLoginTask loginTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initLoginUI();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return false;
	}

	public void initLoginUI() {
		
		setContentView(R.layout.activity_login);

        invalidView = (TextView) findViewById(R.id.invalidView);
        invalidView.setVisibility(View.INVISIBLE);

		mEmailView = (EditText) findViewById(R.id.emailText);
        mEmailView.setHint("Email");
        mEmailView.setHintTextColor(Color.argb(100,190,230,255));

		mPasswordView = (EditText) findViewById(R.id.passwordText);
        mPasswordView.setHint("Password");
        mPasswordView.setHintTextColor(Color.argb(100,190,230,255));
		
		signInButton = (Button) findViewById(R.id.signin);
		
		signInButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
			
		}
		);
		
		registerButton = (Button) findViewById(R.id.register);
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(intent);

			}
		
		}
		);

        progressBar = (ProgressBar) findViewById(R.id.signinBar);
        progressBar.setVisibility(View.INVISIBLE);

	}

	public void attemptLogin() {

		mEmailView.setError(null);
		mPasswordView.setError(null);

		String email = "ramsai.vellanki@gmail.com";//mEmailView.getText().toString();
		String password = "aaaaaa";//mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!email.contains("@") || !email.contains(".")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);
			authenticate(email,password);
		}

	}

	private void showProgress(boolean b) {

        if (b)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.INVISIBLE);

	}

    public void showIncorrectAccountMessage() {

        invalidView.setVisibility(View.VISIBLE);
        invalidView.setText(R.string.IncorrectAccount);

        progressBar.setVisibility(View.INVISIBLE);

        mPasswordView.setText("");
        mPasswordView.requestFocus();

    }

    public void showUnconfirmedAccountMessage() {

        invalidView.setVisibility(View.VISIBLE);
        invalidView.setText("Please confirm your email before you log in");

        progressBar.setVisibility(View.INVISIBLE);

    }
	
	private void authenticate(String email, String password) {
		
        mEmail = email;
        mPassword = password;

        loginTask = new UserLoginTask();
        loginTask.execute((Void) null);

	}

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params)
        {

            String path = "http://clubbedinapp.com/houseapp/php/validatelogin.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            HttpResponse response;
            JSONObject json = new JSONObject();
            try {
                HttpPost post = new HttpPost(path);
                json.put("email", mEmail);
                json.put("password",mPassword);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    userID = JSONParser.convertStreamToString(in,PHPScriptVariables.userIDString);
                    if (!userID.equals(PHPScriptVariables.CONNECTION_FAILED) && !userID.equals(PHPScriptVariables.CONECTION_UNCONFIRMED))
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
            loginTask = null;
            showProgress(false);

            if (success)
            {

                Intent intent = new Intent(LoginActivity.this,MyGovActivity.class);
                intent.putExtra(PHPScriptVariables.userIDString,userID);
                LoginActivity.this.startActivity(intent);

            } else
            {
                if (userID.equals(PHPScriptVariables.CONECTION_UNCONFIRMED))
                    showUnconfirmedAccountMessage();
                else if (userID.equals(PHPScriptVariables.CONNECTION_FAILED))
                    showIncorrectAccountMessage();
            }
        }

        @Override
        protected void onCancelled()
        {
            loginTask = null;
            showProgress(false);
        }

    }

}
