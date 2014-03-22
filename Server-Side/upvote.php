<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};
$postID = $json->{'postID'};
$vote = 1;

$result = mysql_query('SELECT * FROM uservote WHERE userID = "'.$userID.'" AND postID = "'.$postID.'"');
if(mysql_num_rows($result) == 0)
{
    mysql_query('INSERT INTO uservote (userID, postID, vote) VALUES ("'.$userID.'", "'.$postID.'","'.$vote.'")');
} else {
    $currentvote = $row['vote'];
    if($currentvote == 0) {   
        mysql_query('UPDATE uservote SET vote = "'.$vote.'" WHERE userID = "'.$userID.'" AND postID = "'.$postID.'"');
        mysql_query('UPDATE posts SET upvotes = upvotes + 1 WHERE postID = "'.$postID.'"');
    } else {
    }
}

?>