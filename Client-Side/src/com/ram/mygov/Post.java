package com.ram.mygov;

public class Post {

    public String postText;
    public String postID;
    public String postType;
    public String postStamp;
    public String postUpVotes;
    public String postDownVotes;
    public String postUsername;
    public String postUserVote;

   public Post(String text, String id, String type, String stamp, String up, String down, String username, String userVote) {

       postText = text;
       postID = id;
       postType = type;
       postStamp = stamp;
       postUpVotes = up;
       postDownVotes = down;
       postUsername = username;
       postUserVote = userVote;

   }

}
