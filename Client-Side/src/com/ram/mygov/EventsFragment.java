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

public class EventsFragment extends Fragment {

    private SwipeActivity mActivity;
    public boolean started = false;

    // UI Elements
    private MenuItem refreshItem;
    private ListView eventList;

    public static final int STANDARD_FADE_DURATION = 1500;
    private static final int eventsFadeTime = STANDARD_FADE_DURATION;

    private View refreshBar;

    // PHP Values
    private String userID;
    private int userType;
    private ArrayList<Event> events;
    private String eventID;

    // Internet Connection
    public static final int timeout = 10000;
    private GetEventsTask getEventsTask;
    private DeleteEventTask deleteEventTask;

    public EventsFragment() {
        super();
    }

    public EventsFragment(String userID, int userType) {
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

        if (mActivity.getPosition() == 2) {

            if (item.getItemId() == R.id.action_refresh) {
                refresh(0);
            }

            if (item.getItemId() == R.id.action_new) {

                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                intent.putExtra(PHPScriptVariables.userIDString,userID);
                startActivity(intent);

                return true;

            }

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (SwipeActivity) getActivity();
        if (mActivity.pollsFragment != null)
            mActivity.pollsFragment.started = false;
        userID = mActivity.userID;
        userType = mActivity.userType;

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        refreshBar = view.findViewById(R.id.refreshBar);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(85, 30); // ASUS Nexus 7 Specific Dimensions
        refreshBar.setLayoutParams(params);

        eventList = (ListView) view.findViewById(R.id.eventListView);

        setHasOptionsMenu(true);

        return view;

    }

    public class GetEventsTask extends AsyncTask<Void, Void, Boolean> {


        private String[] tmpEvents;
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

            String path = "http://clubbedinapp.com/houseapp/php/getevents.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            HttpResponse response;

            try {

                HttpPost post = new HttpPost(path);
                post.setHeader("json", json.toString());
                json.put(PHPScriptVariables.userIDString,userID);
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    tmpEvents = JSONParser.convertStreamToArray(in);
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            getEventsTask = null;

            if (success && tmpEvents != null) {

                events = new ArrayList<Event>();

                for (int i = 0; i < tmpEvents.length; i++) {

                    String eventName = JSONParser.getVariableFromString(tmpEvents[i], PHPScriptVariables.eventNameString);
                    String eventInfo = JSONParser.getVariableFromString(tmpEvents[i], PHPScriptVariables.eventInfoString);
                    String eventLocation = JSONParser.getVariableFromString(tmpEvents[i], PHPScriptVariables.eventLocationString);
                    String eventDate = JSONParser.getVariableFromString(tmpEvents[i], PHPScriptVariables.eventDateString);
                    String eventTime = JSONParser.getVariableFromString(tmpEvents[i], PHPScriptVariables.eventTimeString);
                    String eventID = JSONParser.getVariableFromString(tmpEvents[i], PHPScriptVariables.eventIDString);

                    events.add(new Event(eventName, eventInfo, eventLocation, eventDate, eventTime, eventID));

                }

                EventAdapter adapter = new EventAdapter(EventsFragment.this, events);
                eventList.setAdapter(adapter);

                eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        refresh(0);
                        eventsScreen((Event) parent.getAdapter().getItem(position));
                    }
                });

                if (refresh) {

                    if (item != null)
                        item.setActionView(null);

                    animateEventsView(offset);

                }

            }

        }

        @Override
        protected void onCancelled() {
            getEventsTask = null;
        }

    }

    public class DeleteEventTask extends AsyncTask<Void, Void, Boolean> {


        private String[] tmpEvents;
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

            String path = "http://clubbedinapp.com/houseapp/php/deleteevent.php";

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            JSONObject json = new JSONObject();

            HttpResponse response;

            try {

                HttpPost post = new HttpPost(path);
                post.setHeader("json", json.toString());
                json.put(PHPScriptVariables.eventIDString, eventID);
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
            deleteEventTask = null;

            refresh(0);

        }

        @Override
        protected void onCancelled() {
            deleteEventTask = null;
        }

    }

    public void setRefreshItem(MenuItem item) {
        refreshItem = item;
    }

    public void refresh(int offset) {

        if (refreshItem != null)
            refreshItem.setActionView(refreshBar);

        getEventsTask = new GetEventsTask();
        getEventsTask.setRefresh(true, refreshItem, offset);
        getEventsTask.execute((Void) null);

    }

    public void delete(String eventID) {

        this.eventID = eventID;

        deleteEventTask = new DeleteEventTask();
        deleteEventTask.execute((Void) null);

    }

    public void eventsScreen(Event event) {

        Intent intent = new Intent(getActivity(),EventsActivity.class);
        intent.putExtra("name",event.eventName);
        intent.putExtra("info",event.eventInfo);
        intent.putExtra("loc",event.eventLoc);
        intent.putExtra("date",event.eventDate);
        intent.putExtra("time",event.eventTime);
        intent.putExtra("id",event.eventID);
        startActivity(intent);

    }

    public void animateEventsView(int offset) {

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(eventsFadeTime);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);

        eventList.setAnimation(animationSet);

    }

}