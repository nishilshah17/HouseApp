README HouseApp
===============

**approvepost.php**

INPUT: postID

OUTPUT:

**downvote.php**

INPUT: userID, postID

OUTPUT:

**getcomments.php**

INPUT: postID

OUTPUT: array of (comment, username, stamp)

**getevents.php**

not complete

**getpolls.php**

INPUT: userID

OUTPUT: array of (question, stamp, username, polloptions: (option, votes))

**getposts.php**

INPUT: userID

OUTPUT: post, postID, postType, stamp, up, down, username

**getusertype.php**

INPUT: userID

OUTPUT: type

**increaseoption.php**

INPUT: pollID, optionID, userID

OUTPUT: 

**newcomment.php**

INPUT: comment, postID, userID

OUTPUT:

**newevent.php**

not complete

**newpoll.php**

INPUT: pollquestion, userID

OUTPUT:

**newpost.php**

INPUT: post, userID

OUTPUT:

**newuser.php**

INPUT: suemail, supassword, sufirstname, sulastname, suusername, sutype

OUTPUT: register

**upvote.php**

INPUT: userID, postID

OUTPUT:

**validatelogin.php**

INPUT: email, password

OUTPUT: userID, type