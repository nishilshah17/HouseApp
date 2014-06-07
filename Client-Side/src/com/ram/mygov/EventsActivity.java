package com.ram.mygov;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class EventsActivity extends Activity {

    private Event event;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);

        Bundle bundle = getIntent().getExtras();

        event = new Event(bundle.getString("name"),bundle.getString("info"),bundle.getString("loc"),bundle.getString("date"),bundle.getString("time"),bundle.getString("id"));

        TextView eventName = (TextView) findViewById(R.id.eventField);
        eventName.setText(event.eventName);
        TextView eventInfo = (TextView) findViewById(R.id.radioButton);
        eventInfo.setText(event.eventInfo);
        TextView eventLocation = (TextView) findViewById(R.id.radioButton2);
        eventLocation.setText(event.eventLoc);
        TextView eventDate = (TextView) findViewById(R.id.radioButton3);
        eventDate.setText(event.eventDate);
        TextView eventTime = (TextView) findViewById(R.id.radioButton4);
        eventTime.setText(event.eventTime);

    }

}