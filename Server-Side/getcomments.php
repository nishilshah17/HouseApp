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

array_push($comments, array("comment" => $rowPost['post'], "username" => $username, "stamp" => $rowPost['stamp']));

$resultComments = mysql_query('SELECT * FROM comments WHERE postID = "'.$postID.'"');

while ($rowComments = mysql_fetch_assoc($resultComments)) {
    
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$rowComments['userID'].'"');
    $rowUser= mysql_fetch_assoc($resultUser);
    
	array_push($comments, array("comment" => $rowComments['comment'], "username" => $rowUser['userName'], "stamp" => $rowComments['stamp']));

}

usort($comments, function($item1, $item2) {
    $ts1 = strtotime($item1['stamp']);
    $ts2 = strtotime($item2['stamp']);
    return $ts1 - $ts2;
});

echo json_encode($comments);

?>