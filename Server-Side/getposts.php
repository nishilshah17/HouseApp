<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM posts');

$posts = array();

while ($row = mysql_fetch_assoc($result)) {
    
    $posterID = $row['userID'];
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$posterID.'"');
    $rowUser = mysql_fetch_assoc($resultUser);
    $username = $rowUser['userName'];
    
    $resultVote = mysql_query('SELECT * FROM uservote WHERE userID = "'.$userID.'" AND postID = "'.$row['postID'].'"');

    if(mysql_num_rows($resultVote) > 0)
    {
        $rowVote = mysql_fetch_assoc($resultVote);
        if($rowVote['vote'] == 1) {
            $userVote = "up";
        } else if ($rowVote['vote'] == 0) {
            $userVote = "down";
        } 
    } else {
        $userVote = "none";
    }
    
	array_push($posts, array("post" => $row['post'], "postID" => $row['postID'], "postType" => $row['approved'], "stamp" => $row['stamp'], "up" => $row['up'], "down" => $row['down'], "username" => $username, "userVote" => $userVote));

}

usort($posts, function($item1, $item2) {
    $ts1 = strtotime($item1['stamp']);
    $ts2 = strtotime($item2['stamp']);
    return $ts2 - $ts1;
});

echo json_encode($posts);

?>