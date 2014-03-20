<?php

include 'mysqlconnect.php';
include 'getdata.php';

$pollID = $json->{'pollID'};
$optionID = $json->{'optionID'};
$userID = $json->{'userID'};

mysql_query('UPDATE options SET votes = votes + 1 WHERE optionID = "'.$optionID.'"');

mysql_query('INSERT INTO completedpolls (pollID, userID) VALUES ("'.$pollID.'", "'.$userID.'")');

?>