<?php

include 'mysqlconnect.php';
include 'getdata.php';

$eventName = $json->{'eventName'};
$location = $json->{'location'};
$time = $json->{'time'};
$date = $json->{'date'};
$info = $json->{'info'};
$userID = $json->{'userID'};

$rand = FALSE;

while (!$rand)
{
	$eventID = rand(10000,99999);
    
	$result = mysql_query('SELECT * FROM events WHERE eventID = "'.$eventID.'"');
	if(mysql_num_rows($result) == 0) {
		 $rand = TRUE;
	} else {
	}

}

mysql_query('INSERT INTO events (event, location, time, date, info, eventID, userID) VALUES ("'.$eventName.'", "'.$location.'", "'.$time.'", "'.$date.'","'.$info.'", "'.$eventID.'", "'.$userID.'")');

?>