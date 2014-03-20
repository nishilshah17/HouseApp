<?php

include 'mysqlconnect.php';
include 'getdata.php';

$question = $json->{'pollquestion'};
$userID = $json->{'userID'};

mysql_query('INSERT INTO polls (question, userID) VALUES ("'.$question.'", "'.$userID.'")');
?>