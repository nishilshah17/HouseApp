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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends Activity {

    private TextView invalidView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private Button signInButton;
    private ProgressBar progressBar;
	private Button registerButton;

    private String mEmail;
    private String mPassword;

    private String userID;

    private UserLoginTask loginTask;

    private String[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initLoginUI();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void initLoginUI() {
		
		setContentView(R.layout.activity_login);

        invalidView = (TextView) findViewById(R.id.invalidView);
        invalidView.setVisibility(View.INVISIBLE);

		mEmailView = (EditText) findViewById(R.id.emailText);
		mEmailView.setTextColor(Color.BLACK);

		mPasswordView = (EditText) findViewById(R.id.passwordText);
		mPasswordView.setTextColor(Color.BLACK);
		
		signInButton = (Button) findViewById(R.id.signin);
		signInButton.setBackgroundColor(Color.rgb(0, 255, 210));
		
		signInButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
			
		}
		);
		
		registerButton = (Button) findViewById(R.id.register);
		registerButton.setBackgroundColor(Color.rgb(0, 255, 210));
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.activity_register);
			}
		
		}
		);

        progressBar = (ProgressBar) findViewById(R.id.spinner);
        progressBar.setVisibility(View.INVISIBLE);
		
	}

    public void resetLoginUI() {

        invalidView.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.INVISIBLE);

        mPasswordView.setText("");
        mPasswordView.requestFocus();

    }

	public void attemptLogin() {

		mEmailView.setError(null);
		mPasswordView.setError(null);

		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

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
                    data = convertStreamToString(in);
                    Log.i("Read",userID);
                    if (!userID.equals("false"))
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

                setContentView(R.layout.activity_welcome);

                //Intent myIntent = new Intent(LoginActivity.this,CalculatorActivity.class);
                //LoginActivity.this.startActivity(myIntent);
            } else
            {
                resetLoginUI();
            }
        }

        @Override
        protected void onCancelled()
        {
            loginTask = null;
            showProgress(false);
        }

    }

    private static String[] convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String s = sb.toString();

        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ',')
                count++;
        }

        String[] data = new String[count];



        return data;

    }

}
