<?php

include 'mysqlconnect.php';
include 'getdata.php';

$comment = $json->{'comment'};
$postID = $json->{'postID'};
$userID = $json->{'userID'};

mysql_query('INSERT INTO comments (comment, postID, userID) VALUES ("'.$comment.'", "'.$postID.'", "'.$userID.'")');

?>