package com.ram.mygov;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

public class PollsActivity extends Activity {

    // UI Elements
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;

    // PHP Values
    private String userID;
    private String optionID;
    private Poll poll;
    private String[] optionIDs;

    // Internet Connection
    public static final int timeout = 10000;
    private PollVoteTask pollVoteTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_polls);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString(PHPScriptVariables.userIDString);

        poll = new Poll(bundle.getString("pollText"), bundle.getString("pollID"), bundle.getString("pollStamp"), bundle.getString("pollUsername"),bundle.getString("pollUservote"),bundle.getStringArray("pollOptions"));
        poll.pollVotes = bundle.getInt("pollVotes");

        ((TextView) findViewById(R.id.textView)).setText(poll.pollText);
        ((TextView) findViewById(R.id.textView3)).setText(""+poll.pollVotes);

        String[] options = new String[4];
        optionIDs = new String[4];

        for (int i = 0; i < poll.pollOptions.length; i++) {
            options[i] = JSONParser.getVariableFromString(poll.pollOptions[i],"option")+"("+JSONParser.getVariableFromString(poll.pollOptions[i],"votes")+")";
            optionIDs[i] = JSONParser.getVariableFromString(poll.pollOptions[i],"optionID");
        }

        option1 = (RadioButton) findViewById(R.id.opt1);
        option1.setText(options[0]);
        option2 = (RadioButton) findViewById(R.id.opt2);
        option2.setText(options[1]);
        option3 = (RadioButton) findViewById(R.id.opt3);
        option3.setText(options[2]);
        option4 = (RadioButton) findViewById(R.id.opt4);
        option4.setText(options[3]);

        option1.setChecked(true);
        optionID = optionIDs[0];

        option1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    optionID = optionIDs[0];
                    option2.setChecked(false);
                    option3.setChecked(false);
                    option4.setChecked(false);
                }
            }
        });


        option2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    optionID = optionIDs[1];
                    option1.setChecked(false);
                    option3.setChecked(false);
                    option4.setChecked(false);
                }
            }
        });

        option3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    optionID = optionIDs[2];
                    option1.setChecked(false);
                    option2.setChecked(false);
                    option4.setChecked(false);
                }
            }
        });

        option4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    optionID = optionIDs[3];
                    option1.setChecked(false);
                    option2.setChecked(false);
                    option3.setChecked(false);
                }
            }
        });

        Button submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poll.pollUserVote.equals("0")) {
                    pollVoteTask = new PollVoteTask();
                    pollVoteTask.execute((Void) null);
                } else {
                    Toast.makeText(PollsActivity.this,"Already Voted",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (options[2] == null || options[2].equals("")) {
            option3.setVisibility(View.INVISIBLE);
        }
        if (options[3] == null || options[3].equals("")) {
            option4.setVisibility(View.INVISIBLE);
        }

    }

    public class PollVoteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            String path = "http://clubbedinapp.com/houseapp/php/pollvote.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            try {

                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.pollOptionString,optionID);
                Log.i("optionID",optionID);
                json.put(PHPScriptVariables.pollIDString,poll.pollID);
                Log.i("pollID",poll.pollID);
                json.put(PHPScriptVariables.userIDString,userID);
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
            pollVoteTask = null;

            if (success) {

                finish();

            } else {
                // Error Occured
            }

        }

        @Override
        protected void onCancelled() {
            pollVoteTask = null;
        }

    }

}