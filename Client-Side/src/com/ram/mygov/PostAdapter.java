package com.ram.mygov;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {

    private PostsFragment postsFragment;
    private ArrayList<Post> posts;

    private ViewHolder holder;

    private static final int MAX_LENGTH = 80;

    public PostAdapter(PostsFragment postsFragment, ArrayList<Post> posts){
        this.postsFragment = postsFragment;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){

            convertView = postsFragment.getActivity().getLayoutInflater().inflate(R.layout.row,viewGroup,false);
            //TODO: get correct row - https://www.youtube.com/watch?v=wDBM6wVEO70 19:00
            holder = new ViewHolder();

            holder.tvPost = (TextView) convertView.findViewById(R.id.lvPostView);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.lvUsername);
            holder.tvUpVotes = (TextView) convertView.findViewById(R.id.upvoteTotal);
            holder.tvDownVotes = (TextView) convertView.findViewById(R.id.downvoteTotal);
            holder.upButton = (ImageButton) convertView.findViewById(R.id.button1);
            holder.downButton = (ImageButton) convertView.findViewById(R.id.button2);
            holder.position = position;

            convertView.setTag(holder);

        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        final String postID = posts.get(position).postID;

        String postText = posts.get(position).postText;
        if (postText.length() > MAX_LENGTH)
            postText = postText.substring(0,MAX_LENGTH)+" ...";
        holder.tvPost.setText(postText);

        holder.tvUsername.setText(posts.get(position).postUsername);
        holder.tvUpVotes.setText(posts.get(position).postUpVotes);
        holder.tvDownVotes.setText(posts.get(position).postDownVotes);

        if (posts.get(position).postUserVote.equals("up")) {
            holder.upButton.setImageResource(R.drawable.upvote_pressed);
            holder.downButton.setImageResource(R.drawable.downvote_normal);
        } else if (posts.get(position).postUserVote.equals("down")) {
            holder.upButton.setImageResource(R.drawable.upvote_normal);
            holder.downButton.setImageResource(R.drawable.downvote_pressed);
        } else if (posts.get(position).postUserVote.equals("none")) {
            holder.upButton.setImageResource(R.drawable.upvote_normal);
            holder.downButton.setImageResource(R.drawable.downvote_normal);
        }

        final int tmpPosition = position;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postsFragment.commentsScreen(posts.get(tmpPosition).postID);

            }
        });

        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postsFragment.vote(1,postID);

            }
        });

        holder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postsFragment.vote(0, postID);

            }
        });

        return convertView;

    }

    private static class ViewHolder{

        public TextView tvPost;
        public TextView tvUsername;
        public TextView tvUpVotes;
        public TextView tvDownVotes;
        public ImageButton upButton;
        public ImageButton downButton;
        public int position;

    }

}