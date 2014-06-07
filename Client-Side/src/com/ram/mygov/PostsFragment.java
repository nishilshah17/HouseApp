package com.ram.mygov;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
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

public class PostsFragment extends Fragment {

    private SwipeActivity mActivity;
    private boolean started = false;

    // UI Elements
    public static final int STANDARD_FADE_DURATION = 1500;
    private static final int fadeInTime = STANDARD_FADE_DURATION;
    private static final int fadeOutTime = STANDARD_FADE_DURATION;
    private static final int postsFadeTime = STANDARD_FADE_DURATION;
    private View refreshBar;
    private MenuItem refreshItem;
    private ListView postList;
    private ImageView welcomeView;

    // PHP Values
    private String userID;
    private int userType;
    private String postString;
    private ArrayList<Post> posts;
    private String[] postIDs;
    private String currentPostID;

    // Internet Connection
    public static final int timeout = 10000;
    private PostTask postTask;
    private RetrievePostTask retrievePostsTask;
    private VoteTask voteTask;
    private int voteType;

    public PostsFragment() {
        super();
    }

    public PostsFragment(String userID, int userType) {
        super();
        this.userID = userID;
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (SwipeActivity) getActivity();
        userID = mActivity.userID;
        userType = mActivity.userType;

        View view = inflater.inflate(R.layout.activity_main, container, false);

        welcomeView = (ImageView) view.findViewById(R.id.welcome);

        postList = (ListView) view.findViewById(R.id.postListView);

        if (mActivity.getMenu() != null) {
            MenuItem item = mActivity.getMenu().findItem(R.id.action_new);
            if (item != null)
                item.setVisible(true);
        }

        refreshBar = view.findViewById(R.id.refreshBar);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(85, 30); // ASUS Nexus 7 Specific Dimensions
        refreshBar.setLayoutParams(params);

        setHasOptionsMenu(true);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Menu sMenu = mActivity.getMenu();

        if (sMenu != null) {

            MenuItem item = sMenu.findItem(R.id.action_new);
            if (item != null) {
                item.setVisible(true);
            }

        }

        setRefreshItem(menu.findItem(R.id.action_refresh));

        if (!started) {
            started = true;
            initialize();
        }

        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActivity.getPosition() == 1) {

            if (item.getItemId() == R.id.action_new) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("New Post");

                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        postString = input.getText().toString();

                        if (postString != null && !postString.equals("") && userID != null && !userID.equals("")) {
                            postTask = new PostTask();
                            postTask.execute((Void) null);
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

                return true;

            }

        }

        return super.onOptionsItemSelected(item);

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
                json.put(PHPScriptVariables.userIDString,userID);
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
                    String postUpVotes = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.upVote);
                    String postDownVotes = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.downVote);
                    String postUsername = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.postUsernameString);
                    String postUserVote = JSONParser.getVariableFromString(tmpPosts[i], PHPScriptVariables.userVote);

                    posts.add(new Post(postText, postID, postType, postStamp, postUpVotes, postDownVotes, postUsername,postUserVote));

                }

                postIDs = new String[posts.size()];
                for (int i = 0; i < posts.size(); i++) {
                    postIDs[i] = posts.get(i).postID;
                }


                PostAdapter adapter = new PostAdapter(PostsFragment.this,posts);
                postList.setAdapter(adapter); //TODO UI NOT UPDATING

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

    public class VoteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params)
        {

            String path = "";

            if(voteType == 1){
                path = "http://clubbedinapp.com/houseapp/php/upvote.php";
            } else if (voteType == 0) {
                path = "http://clubbedinapp.com/houseapp/php/downvote.php";
            }

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.userIDString,userID);
                json.put(PHPScriptVariables.postIDString,currentPostID);
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
            voteTask = null;

            if (success) {

                refresh(0);

            } else {
                // Error Occured
            }

        }

        @Override
        protected void onCancelled() {
            voteTask = null;
        }

    }

    public void setRefreshItem(MenuItem item) {
        refreshItem = item;
    }

    public void refresh(int offset) {

        if (refreshItem != null)
            refreshItem.setActionView(refreshBar);

        retrievePostsTask = new RetrievePostTask();
        retrievePostsTask.setRefresh(true, refreshItem, offset);
        retrievePostsTask.execute((Void) null);

    }

    public void vote(int voteType, String postID) {

        this.voteType = voteType;
        this.currentPostID = postID;

        voteTask = new VoteTask();
        voteTask.execute((Void) null);

    }

    public void commentsScreen(String postID) {

        Intent intent = new Intent(getActivity(),CommentsActivity.class);
        intent.putExtra("UserID",userID);
        intent.putExtra("PostID",postID);
        startActivity(intent);

    }

    public void initialize() {

        animateWelcomeView();

        refresh(fadeInTime + fadeOutTime);

    }

    public void animateWelcomeView() {

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(fadeInTime);

        Animation fadeOut = new AlphaAnimation(1, 0);
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

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(postsFadeTime);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);

        postList.setAnimation(animationSet);

    }

}