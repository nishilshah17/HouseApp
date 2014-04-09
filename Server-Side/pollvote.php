<?php

include 'mysqlconnect.php';
include 'getdata.php';

$optionID = $json->{'optionID'};
$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM options WHERE optionID = "'.$optionID.'"');
$row = mysql_fetch_assoc($result);

$pollID = $row['pollID'];

mysql_query('INSERT INTO userpollvote (userID, optionID, pollID) VALUES ("'.$userID.'", "'.$optionID.'", "'.$pollID.'")'); 

?>