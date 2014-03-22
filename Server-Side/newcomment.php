<?php

include 'mysqlconnect.php';
include 'getdata.php';

$comment = $json->{'comment'};
$postID = $json->{'postID'};

mysql_query('INSERT INTO comments (comment, postID) VALUES ("'.$comment.'", "'.$postID.'")');

?>