<?php

include 'mysqlconnect.php';
include 'getdata.php';

$postID = $json->{'postID'};

mysql_query('UPDATE posts SET approved = 1 WHERE postID = "'.$postID.'"');

?>