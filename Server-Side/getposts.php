<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM posts');

$posts = array();

while ($row = mysql_fetch_assoc($result)) {
    
    $userID = $row['userID'];
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
    $rowUser = mysql_fetch_assoc($resultUser);
    $username = $rowUser['userName'];
    
	array_push($posts, array("post" => $row['post'], "postID" => $row['postID'], "postType" => $row['approved'], "stamp" => $row['stamp'], "up" => $row['upvotes'], "down" => $row['downvotes'], "username" => $username));

}

usort($posts, function($item1, $item2) {
    $ts1 = strtotime($item1['stamp']);
    $ts2 = strtotime($item2['stamp']);
    return $ts2 - $ts1;
});

echo json_encode($posts);

?>