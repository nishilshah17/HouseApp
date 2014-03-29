<?php

include 'mysqlconnect.php';
include 'getdata.php';

$postID = $json->{'postID'};

$comments = array();


$resultComments = mysql_query('SELECT * FROM comments WHERE postID = "'.$postID.'"');

while ($rowComments = mysql_fetch_assoc($result)) {
    
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
    $rowUser= mysql_fetch_assoc($resultUser);
	array_push($comments, array("comment" => $rowComments['comment'], "username" => $rowUser['userName']);

}

echo json_encode($comments);

?>