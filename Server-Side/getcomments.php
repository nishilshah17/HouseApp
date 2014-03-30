<?php

include 'mysqlconnect.php';
include 'getdata.php';

$postID = $json->{'postID'};

$comments = array();

$resultPost = mysql_query('SELECT * FROM posts WHERE postID = "'.$postID.'"');
$rowPost = mysql_fetch_assoc($resultPost);

$userID = $rowPost['userID'];
$resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
$rowUser = mysql_fetch_assoc($resultUser);
$username = $rowUser['userName'];

array_push($comments, array("comment" => $rowPost['post'], "username" => $username));

$resultComments = mysql_query('SELECT * FROM comments WHERE postID = "'.$postID.'"');

while ($rowComments = mysql_fetch_assoc($resultComments)) {
    
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$rowComments['userID'].'"');
    $rowUser= mysql_fetch_assoc($resultUser);
    
	array_push($comments, array("comment" => $rowComments['comment'], "username" => $rowUser['userName']));

}

echo json_encode($comments);

?>