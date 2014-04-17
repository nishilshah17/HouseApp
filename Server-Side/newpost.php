<?php

include 'mysqlconnect.php';
include 'getdata.php';

$post = $json->{'post'};
$userID = $json->{'userID'};

mysql_query('INSERT INTO posts (post, userID) VALUES ("'.$post.'", "'.$userID.'")');
?>