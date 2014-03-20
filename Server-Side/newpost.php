<?php

include 'mysqlconnect.php';
include 'getdata.php';

$post = $json->{'post'};
$userID = $json->{'userID'};
$approved = 0;

mysql_query('INSERT INTO posts (post, userID, approved) VALUES ("'.$post.'", "'.$userID.'","'.$approved.'")');
?>