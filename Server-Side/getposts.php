<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM posts');

$posts = array();

while ($row = mysql_fetch_assoc($result)) {

	array_push($posts, array("post" => $row['post'], "postID" => $row['postID'], "postType" => $row['approved'], "stamp" => $row['stamp'], "up" => $row['upvotes'], "down" => $row['downvotes']));

}

/*$tmp = Array();
foreach($members as &$ma)
    $tmp[] = &$ma["name"];
$tmp = array_map('strtolower', $tmp);
array_multisort($tmp, $members);*/

echo json_encode($posts);

?>