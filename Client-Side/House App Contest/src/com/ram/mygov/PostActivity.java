package com.ram.mygov;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PostActivity extends Activity {

    Post post;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);

        Bundle bundle = getIntent().getExtras();

        String[] postValues = bundle.getStringArray("Post");
        post = new Post(postValues[0],postValues[1],postValues[2],postValues[3]);

        TextView postView = (TextView) findViewById(R.id.posttextview);
        postView.setText(post.postText);

    }

}