<?php

include 'mysqlconnect.php';
include 'getdata.php';

$post = $json->{'post'};
$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
$type = $row['type'];

if($type==0){
    $approved = 0;
} else {
    $approved = 2;
}

mysql_query('INSERT INTO posts (post, userID, approved) VALUES ("'.$post.'", "'.$userID.'","'.$approved.'")');
?>