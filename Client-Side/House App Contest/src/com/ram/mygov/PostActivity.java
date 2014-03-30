package com.ram.mygov;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;

public class PostActivity extends Activity {

    String postText;
    String postID;

    RetrieveCommentsTask retrieveCommentsTask;

    ArrayList<Comment> comments;

    MenuItem refreshItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);

        Bundle bundle = getIntent().getExtras();

        postID = (String) bundle.get("PostID");

        retrieveCommentsTask = new RetrieveCommentsTask();
        retrieveCommentsTask.execute((Void) null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);

        refreshItem = menu.findItem(R.id.action_refresh);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_new) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("New Comment");

            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Comment", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {



                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled
                }
            });

            alert.show();

            return true;

        } else if (item.getItemId() == R.id.action_refresh) {

            refresh(0);

        }

        return super.onOptionsItemSelected(item);
    }

    public class RetrieveCommentsTask extends AsyncTask<Void, Void, Boolean> {

        private String[] tmpResponse;

        @Override
        protected Boolean doInBackground(Void... params) {

            String path = "http://clubbedinapp.com/houseapp/php/getcomments.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), MyGovActivity.timeout);
            JSONObject json = new JSONObject();

            HttpResponse response;

            try {

                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.postIDString,postID);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    tmpResponse = JSONParser.convertStreamToArray(in);
                    return  true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success)
        {

            retrieveCommentsTask = null;

            comments = new ArrayList<Comment>();

            if (success) {

                postText = JSONParser.getVariableFromString(tmpResponse[0],PHPScriptVariables.commentsString);
                Log.i("postText",postText);

                for (int i = 1; i < tmpResponse.length; i++) {
                    String text = JSONParser.getVariableFromString(tmpResponse[i],PHPScriptVariables.commentsString);
                    String username = JSONParser.getVariableFromString(tmpResponse[i],PHPScriptVariables.postUsernameString);
                    comments.add(new Comment(text,username));
                }

                String[] commentsText = new String[comments.size()];
                for (int i = 0; i < comments.size(); i++) {
                    commentsText[i] = comments.get(i).commentText;
                }

                ListView commentsListView  = (ListView) findViewById(R.id.commentsListView);
                ArrayAdapter adapter = new ArrayAdapter(PostActivity.this,android.R.layout.simple_list_item_1,commentsText);
                commentsListView.setAdapter(adapter);

            }

        }

        @Override
        protected void onCancelled() {
            retrieveCommentsTask = null;
        }

    }

    private void refresh(int offset) {
        // TODO
    }

}