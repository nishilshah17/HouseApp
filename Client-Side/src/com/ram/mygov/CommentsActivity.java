package com.ram.mygov;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
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
import java.util.ArrayList;

public class CommentsActivity extends Activity {

    //
    String userID;

    String postText;
    String postID;

    CommentTask commentTask;
    String commentText;
    EditText commentingET;

    RetrieveCommentsTask retrieveCommentsTask;
    ArrayList<Comment> comments;

    MenuItem refreshItem;
    View refreshBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Action Bar Progress Bar
        setContentView(R.layout.refresh_bar);

        refreshBar = findViewById(R.id.refreshBar);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(85, 30);
        refreshBar.setLayoutParams(params);

        // New Layout
        setContentView(R.layout.activity_post);

        Bundle bundle = getIntent().getExtras();

        userID = (String) bundle.get("UserID");
        postID = (String) bundle.get("PostID");

        retrieveCommentsTask = new RetrieveCommentsTask();
        retrieveCommentsTask.execute((Void) null);

        commentingET = (EditText) findViewById(R.id.commentBox);

        ImageButton commentingBtn = (ImageButton) findViewById(R.id.commentBtn);

        commentingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentText = commentingET.getText().toString();

                if (commentText != null && !commentText.equals("")) {
                    commentTask = new CommentTask();
                    commentTask.execute((Void) null);
                }

                commentingET.setText("");

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);

        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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

                    commentText = input.getText().toString();

                    if (commentText != null && !commentText.equals("")) {
                        commentTask = new CommentTask();
                        commentTask.execute((Void) null);
                    }

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

    public class CommentTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params)
        {

            String path = "http://clubbedinapp.com/houseapp/php/newcomment.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), PostsFragment.timeout);
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.commentsString,commentText);
                json.put(PHPScriptVariables.postIDString,postID);
                json.put(PHPScriptVariables.userIDString,userID);
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
            commentTask = null;

            if (success) {

                refresh(0);

            } else {
                // Error Occured
            }

        }

        @Override
        protected void onCancelled() {
            commentTask = null;
        }

    }

    public class RetrieveCommentsTask extends AsyncTask<Void, Void, Boolean> {

        private String[] tmpResponse;

        private boolean refresh = false;

        private MenuItem item;

        private int offset;

        private void setRefresh(boolean b, MenuItem item, int offset) {
            refresh = b;
            this.item = item;
            this.offset = offset;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String path = "http://clubbedinapp.com/houseapp/php/getcomments.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), PostsFragment.timeout);
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
                TextView postView = (TextView) findViewById(R.id.postCommentsTextView);
                postView.setText(postText);

                for (int i = 1; i < tmpResponse.length; i++) {
                    String text = JSONParser.getVariableFromString(tmpResponse[i],PHPScriptVariables.commentsString);
                    String username = JSONParser.getVariableFromString(tmpResponse[i],PHPScriptVariables.postUsernameString);
                    comments.add(new Comment(text,username));
                }

                String[] commentsText;

                if (comments.size() > 0) {
                    commentsText = new String[comments.size()];
                    for (int i = 0; i < comments.size(); i++) {
                        commentsText[i] = comments.get(i).commentText;
                    }
                } else {
                    commentsText = new String[]{"No Comments"};
                }

                ListView commentsListView  = (ListView) findViewById(R.id.commentsListView);
                ArrayAdapter adapter = new ArrayAdapter(CommentsActivity.this,android.R.layout.simple_list_item_1,commentsText);
                commentsListView.setAdapter(adapter);

                if (refresh) {

                    if (item != null)
                        item.setActionView(null);

                    animateCommentsView(offset);

                }

            }

        }

        @Override
        protected void onCancelled() {
            retrieveCommentsTask = null;
        }

    }

    public void refresh(int offset) {

        refreshItem.setActionView(refreshBar);

        retrieveCommentsTask = new RetrieveCommentsTask();
        retrieveCommentsTask.setRefresh(true, refreshItem, offset);
        retrieveCommentsTask.execute((Void) null);

    }

    public void animateCommentsView(int offset) {

        ListView commentsView = (ListView) findViewById(R.id.commentsListView);

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(PostsFragment.STANDARD_FADE_DURATION);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);

        commentsView.setAnimation(animationSet);

    }

}