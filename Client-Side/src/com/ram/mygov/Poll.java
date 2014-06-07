package com.ram.mygov;

import android.util.Log;
import org.json.JSONException;

public class Poll {

    public String pollText;
    public String pollID;
    public String pollStamp;
    public String pollUsername;
    public String pollUserVote;
    public int pollVotes;
    public String[] pollOptions;

    public Poll(String text, String id, String stamp, String username, String userVote, String[] polloptions) {

        pollText = text;
        pollID = id;
        pollStamp = stamp;
        pollUsername = username;
        pollUserVote = userVote;
        this.pollOptions = polloptions;

        pollVotes = 0;

        for (int i = 0; i < pollOptions.length; i++)
            pollVotes+=Integer.parseInt(JSONParser.getVariableFromString(pollOptions[i],"votes"));

    }

}
