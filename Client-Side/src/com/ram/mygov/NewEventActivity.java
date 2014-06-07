package com.ram.mygov;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

public class NewEventActivity extends Activity {

    // UI Elements
    EditText name;
    EditText loc;
    EditText time;
    EditText date;
    EditText info;

    // PHP Values
    private String userID;
    private String eventName;
    private String eventLocation;
    private String eventTime;
    private String eventDate;
    private String eventInfo;

    // Internet Connection
    public static final int timeout = 10000;
    private NewEventTask newEventTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString(PHPScriptVariables.userIDString);

        setContentView(R.layout.activity_new_event);

        name = (EditText) findViewById(R.id.eventField);
        loc = (EditText) findViewById(R.id.radioButton2);
        time = (EditText) findViewById(R.id.radioButton4);
        date = (EditText) findViewById(R.id.radioButton3);
        info = (EditText) findViewById(R.id.radioButton);

        Button submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName = name.getText().toString();
                eventLocation = loc.getText().toString();
                eventTime = time.getText().toString();
                eventDate = date.getText().toString();
                eventInfo = info.getText().toString();
                newEventTask = new NewEventTask();
                newEventTask.execute((Void) null);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return false;
    }

    public class NewEventTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            String path = "http://clubbedinapp.com/houseapp/php/newevent.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            try {

                HttpPost post = new HttpPost(path);
                post.setHeader("json", json.toString());
                json.put(PHPScriptVariables.userIDString,userID);
                json.put(PHPScriptVariables.eventNameString, eventName);
                json.put(PHPScriptVariables.eventLocationString, eventLocation);
                json.put(PHPScriptVariables.eventTimeString, eventTime);
                json.put(PHPScriptVariables.eventDateString, eventDate);
                json.put(PHPScriptVariables.eventInfoString, eventInfo);
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                client.execute(post);

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }


        @Override
        protected void onPostExecute(final Boolean success) {
            newEventTask = null;

            if (success) {

                finish();

            } else {
                // Error Occured
            }

        }

        @Override
        protected void onCancelled() {
            newEventTask = null;
        }

    }

}