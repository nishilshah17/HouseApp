package com.ram.mygov;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.*;
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

public class MyGovActivity extends Activity {

    // UI Elements
    public static final int STANDARD_FADE_DURATION = 1500;
    private static final int fadeInTime = STANDARD_FADE_DURATION;
    private static final int fadeOutTime = STANDARD_FADE_DURATION;
    private static final int postsFadeTime = STANDARD_FADE_DURATION;
    private View refreshBar;
    private MenuItem refreshItem;

    // PHP Values
    private String userID;
    private String postString;

    // Internet Connection
    public static final int timeout = 10000;
    private PostTask postTask;
    private RetrievePostTask retrievePostsTask;

    private ArrayList<Post> posts;
    private String[] postIDs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Action Bar Progress Bar
        setContentView(R.layout.refresh_bar);

        refreshBar = findViewById(R.id.refreshBar);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(85, 30);
        refreshBar.setLayoutParams(params);

        // New Layout
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");

    }

    public void initialize() {

        // Logo Animation
        animateWelcomeView();

        // Retrieve + Animate Posts
        refresh(fadeInTime+fadeOutTime);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);

        refreshItem = menu.findItem(R.id.action_refresh);

        initialize();

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_new) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("New Post");

            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    postString = input.getText().toString();

                    postTask = new PostTask();
                    postTask.execute((Void) null);

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

    public void logout() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Logout");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                MyGovActivity.this.logout();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();

    }

    public class PostTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params)
        {

            String path = "http://clubbedinapp.com/houseapp/php/newpost.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.userIDString,userID);
                json.put(PHPScriptVariables.postString,postString);
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
            postTask = null;

            if (success) {

                refresh(0);

            } else {
                // Error Occured
            }

        }

        @Override
        protected void onCancelled() {
            postTask = null;
        }

    }

    public class RetrievePostTask extends  AsyncTask<Void, Void, Boolean> {

        private String[] tmpPosts;

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

            String path = "http://clubbedinapp.com/houseapp/php/getposts.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            HttpResponse response;

            try {

                HttpPost post = new HttpPost(path);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    tmpPosts = JSONParser.convertStreamToArray(in);
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            retrievePostsTask = null;

            if (success) {

                posts = new ArrayList<Post>();

                for (int i = 0; i < tmpPosts.length; i++) {

                    String postText = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.postString);
                    String postID = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.postIDString);
                    String postType = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.postTypeString);
                    String postStamp = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.postStampString);
                    String postUsername = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.postUsernameString);

                    posts.add(new Post(postText, postID, postType, postStamp, postUsername));

                }

                String[] postTexts = new String[posts.size()];
                for (int i = 0; i < posts.size(); i++) {
                    postTexts[i] = posts.get(i).postText;
                }

                postIDs = new String[posts.size()];
                for (int i = 0; i < posts.size(); i++) {
                    postIDs[i] = posts.get(i).postID;
                }

                ArrayAdapter adapter = new ArrayAdapter(MyGovActivity.this, android.R.layout.simple_list_item_1, postTexts);
                ListView postList = (ListView) findViewById(R.id.postListView);
                postList.setAdapter(adapter);
                postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        refresh(0);
                        commentsScreen(postIDs[position]);
                    }
                });

                if (refresh) {

                    if (item != null)
                        item.setActionView(null);

                    animatePostsView(offset);
                }

            }

        }

        @Override
        protected void onCancelled() {
            retrievePostsTask = null;
        }

    }

    public void refresh(int offset) {

        refreshItem.setActionView(refreshBar);

        retrievePostsTask = new RetrievePostTask();
        retrievePostsTask.setRefresh(true, refreshItem, offset);
        retrievePostsTask.execute((Void) null);

    }

    public void commentsScreen(String postID) {

        Intent intent = new Intent(MyGovActivity.this,CommentsActivity.class);
        intent.putExtra("PostID",postID);
        startActivity(intent);

    }

    public void animateWelcomeView() {

        ImageView welcomeView = (ImageView) findViewById(R.id.welcome);

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(fadeInTime);

        Animation fadeOut = new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(fadeInTime);
        fadeOut.setDuration(fadeOutTime);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(fadeOut);

        welcomeView.setAnimation(animationSet);

        welcomeView.setVisibility(View.INVISIBLE);

    }

    public void animatePostsView(int offset) {

        ListView postsView = (ListView) findViewById(R.id.postListView);

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(postsFadeTime);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);

        postsView.setAnimation(animationSet);

    }

}