package com.ram.mygov;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
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
    private TextView forgotView;

    // Login Details
    private String mEmail;
    private String mPassword;

    // Account Information
    private String userID;
    private int userType;

    // Internet Connection
    private UserLoginTask loginTask;
    private ForgotPasswordTask forgotPasswordTask;

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

        forgotView = (TextView) findViewById(R.id.frgtPswd);
        forgotView.setPaintFlags(forgotView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

                alert.setTitle("Forgot Password");

                final EditText input = new EditText(LoginActivity.this);
                input.setHint("Email");
                alert.setView(input);

                alert.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        mEmail = input.getText().toString();

                        forgotPasswordTask = new ForgotPasswordTask();
                        forgotPasswordTask.execute((Void) null);

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled
                    }
                });

                alert.show();

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.signinBar);
        progressBar.setVisibility(View.INVISIBLE);

	}

	public void attemptLogin() {

		mEmailView.setError(null);
		mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

		email = "ramsai.vellanki@gmail.com";
		password = "aaaaaa";

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

        Log.i("email: ",email);
        Log.i("password: ",password);

        loginTask = new UserLoginTask();
        loginTask.execute((Void) null);

	}

    public class ForgotPasswordTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params)
        {

            String path = "http://clubbedinapp.com/houseapp/php/forgotpassword.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.forgotemail, mEmail);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                post.setEntity(se);
                client.execute(post);

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success)
        {

            forgotPasswordTask = null;

            Toast emailsentToast = Toast.makeText(LoginActivity.this,"Recovery Email Sent",1000);
            emailsentToast.setGravity(Gravity.BOTTOM,0,10);
            emailsentToast.show();

        }

        @Override
        protected void onCancelled()
        {
            forgotPasswordTask = null;
        }

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
                json.put(PHPScriptVariables.email, mEmail);
                json.put(PHPScriptVariables.password,mPassword);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    String tmp = JSONParser.convertStreamToString(in);
                    userID = JSONParser.getVariableFromString(tmp,PHPScriptVariables.userIDString);
                    userType = Integer.parseInt(JSONParser.getVariableFromString(tmp,"type"));
                    userType = 1; //TODO Remove
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

                Intent intent = new Intent(LoginActivity.this,SwipeActivity.class);
                intent.putExtra(PHPScriptVariables.userIDString,userID);
                intent.putExtra(PHPScriptVariables.userTypeString,userType);
                LoginActivity.this.startActivity(intent);

            } else
            {

                if (userID == null)
                    Toast.makeText(LoginActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                else if (userID.equals(PHPScriptVariables.CONECTION_UNCONFIRMED))
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
