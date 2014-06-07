package com.ram.mygov;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PollAdapter extends BaseAdapter {

    private PollsFragment pollsFragment;
    private ArrayList<Poll> polls;

    private int userType;

    private ViewHolder holder;

    private static final int MAX_LENGTH = 80;

    public PollAdapter(PollsFragment pollsFragment, ArrayList<Poll> polls){
        this.pollsFragment = pollsFragment;
        this.userType = pollsFragment.getUserType();
        this.polls = polls;
    }

    @Override
    public int getCount() {
        return polls.size();
    }

    @Override
    public Object getItem(int i) {
        return polls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){

            convertView = pollsFragment.getActivity().getLayoutInflater().inflate(R.layout.poll_row,viewGroup,false);
            //TODO: get correct row - https://www.youtube.com/watch?v=wDBM6wVEO70 19:00
            holder = new ViewHolder();

            holder.tvPoll = (TextView) convertView.findViewById(R.id.lvPollView);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.lvUsername);
            holder.tvVotes = (TextView) convertView.findViewById(R.id.textView);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.dltBtn);
            holder.selectButton = (ImageView) convertView.findViewById(R.id.imageView2);
            holder.position = position;

            convertView.setTag(holder);

        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Poll poll = polls.get(position);

        holder.tvPoll.setText(polls.get(position).pollText);
        holder.tvUsername.setText(polls.get(position).pollUsername);
        holder.tvVotes.setText(""+polls.get(position).pollVotes);

        if (userType == 1) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollsFragment.delete(poll.pollID);
            }
        });

        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollsFragment.pollsScreen(poll);
            }
        });

        return convertView;

    }

    private static class ViewHolder{

        public TextView tvPoll;
        public TextView tvUsername;
        public TextView tvVotes;
        public ImageButton deleteButton;
        public ImageView selectButton;
        public int position;

    }

}