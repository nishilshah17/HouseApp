<?php

include 'mysqlconnect.php';
include 'getdata.php';

$postID = $json->{'postID'};

$comments = array();

$resultPost = mysql_query('SELECT * FROM posts WHERE postID = "'.$postID.'"');
$rowPost = mysql_fetch_assoc($result);
$post = $rowPost['post'];
array_push($comments, array("comment" => $post));

$resultComments = mysql_query('SELECT * FROM comments WHERE postID = "'.$postID.'"');

while ($rowComments = mysql_fetch_assoc($result)) {

	array_push($comments, array("comment" => $rowComments['comment']));

}

echo json_encode($comments);

?>