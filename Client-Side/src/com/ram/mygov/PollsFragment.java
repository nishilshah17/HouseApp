package com.ram.mygov;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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


public class PollsFragment extends Fragment {

    private SwipeActivity mActivity;
    public boolean started = false;

    // UI Elements
    private MenuItem refreshItem;
    private ListView pollList;

    public static final int STANDARD_FADE_DURATION = 1500;
    private static final int pollsFadeTime = STANDARD_FADE_DURATION;

    private View refreshBar;

    // PHP Values
    private String userID;
    private int userType;
    private ArrayList<Poll> polls;
    private String[] pollIDs;
    private String deletePollID;

    // Internet Connection
    public static final int timeout = 10000;
    private RetrievePollsTask retrievePollsTask;
    private DeletePollsTask deletePollsTask;

    public PollsFragment() {
        super();
    }

    public PollsFragment(String userID, int userType) {
        super();
        this.userID = userID;
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Menu sMenu = mActivity.getMenu();

        if (sMenu != null) {

            MenuItem item = sMenu.findItem(R.id.action_new);
            if (item != null && userType == 0) {
                item.setVisible(false);
            } else if (item != null) {
                item.setVisible(true);
            }

            item = sMenu.findItem(R.id.action_refresh);
            if (item != null) {
                item.setActionView(null);
            }

        }

        setRefreshItem(menu.findItem(R.id.action_refresh));

        if (!started) {
            started = true;
            refresh(0);
        }

        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActivity.getPosition() == 0) {

            if (item.getItemId() == R.id.action_refresh) {
                refresh(0);
            }

            if (item.getItemId() == R.id.action_new) {

                Intent intent = new Intent(getActivity(),NewPollActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);

                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (SwipeActivity) getActivity();
        if (mActivity.eventsFragment != null)
            mActivity.eventsFragment.started = false;
        userID = mActivity.userID;
        userType = mActivity.userType;

        View view = inflater.inflate(R.layout.fragment_poll, container, false);

        pollList = (ListView) view.findViewById(R.id.pollListView);

        refreshBar = view.findViewById(R.id.refreshBar);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(85, 30); // ASUS Nexus 7 Specific Dimensions
        refreshBar.setLayoutParams(params);

        setHasOptionsMenu(true);

        return view;

    }

    public class RetrievePollsTask extends AsyncTask<Void, Void, Boolean> {

        private String[] tmpPolls;

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

            String path = "http://clubbedinapp.com/houseapp/php/getpolls.php";

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
                    tmpPolls = JSONParser.convertStreamToArray(in);
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            retrievePollsTask = null;

            if (success) {

                polls = new ArrayList<Poll>();

                for (int i = 0; i < tmpPolls.length; i++) {

                    String pollText = JSONParser.getVariableFromString(tmpPolls[i], "question");
                    String pollID = JSONParser.getVariableFromString(tmpPolls[i], "pollID");
                    String pollStamp = JSONParser.getVariableFromString(tmpPolls[i], "stamp");
                    String pollUsername = JSONParser.getVariableFromString(tmpPolls[i], "username");
                    String pollUserVote = JSONParser.getVariableFromString(tmpPolls[i], "uservote");
                    String[] pollOptions = JSONParser.convertStringToArray(JSONParser.getVariableFromString(tmpPolls[i], "polloptions"));

                    polls.add(new Poll(pollText,pollID,pollStamp,pollUsername,pollUserVote,pollOptions));

                }

                pollIDs = new String[polls.size()];
                for (int i = 0; i < polls.size(); i++) {
                    pollIDs[i] = polls.get(i).pollID;
                }

                PollAdapter adapter = new PollAdapter(PollsFragment.this,polls);
                pollList.setAdapter(adapter);

                if (refresh) {

                    if (item != null)
                        item.setActionView(null);

                    animatePollsView(offset);

                }

            }

        }

        @Override
        protected void onCancelled() {
            retrievePollsTask = null;
        }

    }

    public class DeletePollsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            String path = "http://clubbedinapp.com/houseapp/php/deletepoll.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            try {

                HttpPost post = new HttpPost(path);
                json.put(PHPScriptVariables.pollIDString,deletePollID);
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
            deletePollsTask = null;

            refresh(0);

        }

        @Override
        protected void onCancelled() {
            deletePollsTask = null;
        }

    }

    public void setRefreshItem(MenuItem item) {
        refreshItem = item;
    }

    public void refresh(int offset) {

        if (refreshItem != null)
            refreshItem.setActionView(refreshBar);

        retrievePollsTask = new RetrievePollsTask();
        retrievePollsTask.setRefresh(true,refreshItem,offset);
        retrievePollsTask.execute((Void) null);

    }

    public void delete(String pollID) {

        deletePollID = pollID;

        deletePollsTask = new DeletePollsTask();
        deletePollsTask.execute((Void) null);

    }

    public void pollsScreen(Poll poll) {

        Intent intent = new Intent(getActivity(),PollsActivity.class);
        intent.putExtra(PHPScriptVariables.userIDString,userID);
        intent.putExtra("pollText",poll.pollText);
        intent.putExtra("pollID",poll.pollID);
        intent.putExtra("pollStamp",poll.pollStamp);
        intent.putExtra("pollUsername",poll.pollUsername);
        intent.putExtra("pollUservote",poll.pollUserVote);
        intent.putExtra("pollOptions",poll.pollOptions);
        intent.putExtra("pollVotes",poll.pollVotes);
        startActivity(intent);

    }

    public void animatePollsView(int offset) {

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(pollsFadeTime);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);

        pollList.setAnimation(animationSet);

    }

}