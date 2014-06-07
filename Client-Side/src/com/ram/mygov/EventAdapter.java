package com.ram.mygov;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {

    private EventsFragment eventsFragment;
    private ArrayList<Event> events;

    private int userType;

    private ViewHolder holder;

    private static final int MAX_LENGTH = 80;

    public EventAdapter(EventsFragment eventsFragment, ArrayList<Event> events){
        this.eventsFragment = eventsFragment;
        this.userType = eventsFragment.getUserType();
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){

            convertView = eventsFragment.getActivity().getLayoutInflater().inflate(R.layout.event_row,viewGroup,false);
            //get correct row - https://www.youtube.com/watch?v=wDBM6wVEO70 19:00
            holder = new ViewHolder();

            holder.tvEvent = (TextView) convertView.findViewById(R.id.lvPostView);
            holder.tvDate = (TextView) convertView.findViewById(R.id.lvUsername);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.dltBtn);
            holder.selectButton = (ImageView) convertView.findViewById(R.id.imageView2);
            holder.position = position;

            convertView.setTag(holder);

        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Event event = events.get(position);

        holder.tvEvent.setText(event.eventName);
        holder.tvDate.setText(event.eventDate);

        if (userType == 1) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setClickable(true);
        } else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsFragment.delete(event.eventID);
            }
        });

        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsFragment.eventsScreen(event);
            }
        });


        return convertView;

    }

    private static class ViewHolder{

        public TextView tvEvent;
        public TextView tvDate;
        public ImageButton deleteButton;
        public ImageView selectButton;
        public int position;

    }

}