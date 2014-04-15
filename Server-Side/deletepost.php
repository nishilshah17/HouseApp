<?php

include 'getdata.php';
include 'mysqlconnect.php';

$postID = $json->{'postID'};

mysql_query('DELETE FROM posts WHERE postID = "'.$postID.'"');
mysql_query('DELETE FROM comments WHERE postID = "'.$postID.'"');

?>