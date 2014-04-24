<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};

$resultState = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
$rowState = mysql_fetch_assoc($resultState);
$userState = $rowState['state'];

$result = mysql_query('SELECT * FROM events');

$events = array();

while($row = mysql_fetch_assoc($result)){
    
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$row['userID'].'"');
    $rowUser = mysql_fetch_assoc($eventUser);
    
    $eventState = $rowUser['state'];
    
    if($eventState == $userState)
        array_push($events, array('eventName' => $row['event'], 'info' => $row['info'], 'location'=> $row['location'], 'date'=> $row['date'], 'time'=>$row['time'], 'eventID' => $row['eventID']));
    
}

echo json_encode($events);


?>