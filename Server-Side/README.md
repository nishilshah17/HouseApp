README HouseApp
===============

**downvote.php**

INPUT: userID, postID

OUTPUT:

**deletepost.php**

INPUT: postID

OUTPUT:

**deletepoll.phpp**

INPUT: pollID

OUTPUT:

**deleteevent.php**

INPUT: eventID

OUTPUT:

**forgotpassword.php**

INPUT: forgotemail

OUTPUT: 

**getcomments.php**

INPUT: postID

OUTPUT: array of (comment, username, stamp)

**getevents.php**

INPUT:

OUTPUT: array of (eventName, info, location, date, time)

**getpolls.php**

INPUT: userID

OUTPUT: array of (question, stamp, username, userVote, polloptions: (option, optionID, votes))

    if(userVote == 0){user did not vote for this poll};
    else if(userVote <> 0){user voted for this poll option};

**getposts.php**

INPUT: userID

OUTPUT: array of (post, postID, postType, stamp, up, down, username, userVote)

    if(userVote == "up"){it means that the user has voted up for that post};
    if(userVote == "down"){it means that the user has voted down for that post};
    if(userVote == "none"){it means that the user has not voted for that post};

**getusertype.php**

INPUT: userID

OUTPUT: type

**newcomment.php**

INPUT: comment, postID, userID

OUTPUT:

**newpoll.php**

INPUT: pollquestion, userID, option1, option2, option3, option4

OUTPUT:

**newpost.php**

INPUT: post, userID

OUTPUT:

**newuser.php**

INPUT: suemail, supassword, sufirstname, sulastname, suusername, sutype

OUTPUT: register

**newevent.php**

INPUT: eventName, location, time, date, info

OUTPUT: 

**pollvote.php**

INPUT: optionID, userID

OUTPUT: 

**upvote.php**

INPUT: userID, postID

OUTPUT:

**validatelogin.php**

INPUT: email, password

OUTPUT: userID, type