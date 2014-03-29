<?php

include 'mysqlconnect.php';
include 'getdata.php';

$postID = $json->{'postID'};

$comments = array();

$resultPost = mysql_query('SELECT * FROM posts WHERE postID = "'.$postID.'"');
$rowPost = mysql_fetch_assoc($result);
$post = $rowPost['post'];
$userID = $rowPost['userID'];

$resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
$rowUser = mysql_fetch_assoc($resultUser);
$username = $rowUser['userName'];

array_push($comments, array("comment" => $post, "username" => $username, "userID" => $userID));

$resultComments = mysql_query('SELECT * FROM comments WHERE postID = "'.$postID.'"');

while ($rowComments = mysql_fetch_assoc($result)) {
    
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
    $rowUser= mysql_fetch_assoc($resultUser);
	array_push($comments, array("comment" => $rowComments['comment'], "username" => $rowUser['userName'], "userID" => $rowComments['userID']));

}

echo json_encode($comments);

?>