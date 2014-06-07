package com.ram.mygov;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;


public class NewPollActivity extends Activity {

    // UI Elements
    EditText question;
    EditText optionA;
    EditText optionB;
    EditText optionC;
    EditText optionD;

    // PHP Values
    private String userID;
    private String pollQuestion;
    private String option1;
    private String option2;
    private String option3;
    private String option4;

    // Internet Connection
    public static final int timeout = 10000;
    private NewPollTask newPollTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");

        setContentView(R.layout.activity_new_poll);

        question = (EditText) findViewById(R.id.eventField);
        optionA = (EditText) findViewById(R.id.radioButton);
        optionB = (EditText) findViewById(R.id.radioButton2);
        optionC = (EditText) findViewById(R.id.radioButton3);
        optionD = (EditText) findViewById(R.id.radioButton4);

        Button submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollQuestion = question.getText().toString();
                option1 = optionA.getText().toString();
                option2 = optionB.getText().toString();
                option3 = optionC.getText().toString();
                option4 = optionD.getText().toString();
                newPollTask = new NewPollTask();
                newPollTask.execute((Void) null);
            }
        });

    }

    public class NewPollTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            String path = "http://clubbedinapp.com/houseapp/php/newpoll.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();


            try {

                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.pollQuestionString,pollQuestion);
                json.put(PHPScriptVariables.userIDString,userID);
                json.put(PHPScriptVariables.pollOption1,option1);
                json.put(PHPScriptVariables.pollOption2,option2);
                json.put(PHPScriptVariables.pollOption3,option3);
                json.put(PHPScriptVariables.pollOption4,option4);

                post.setHeader("json", json.toString());
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
            newPollTask = null;

            if (success) {

                finish();

            } else {
                // Error Occured
            }

        }

        @Override
        protected void onCancelled() {
            newPollTask = null;
        }

    }

}