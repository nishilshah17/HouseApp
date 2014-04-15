<?php

include 'mysqlconnect.php';
include 'getdata.php';

$result = mysql_query('SELECT * FROM events');

$events = array();

while($row = mysql_fetch_assoc($result)){
    
    array_push($events, array('eventName' => $row['event'], 'info' => $row['info'], 'location'=> $row['location'], 'date'=> $row['date'], 'time'=>$row['time'], 'eventID' => $row['eventID']));
    
}

echo json_encode($events);


?>