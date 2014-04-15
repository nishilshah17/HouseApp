<?php

include 'mysqlconnect.php';
include 'getdata.php';

$eventID = $json->{'eventID'};

mysql_query('DELETE FROM events WHERE eventID = "'.$eventID.'"');

?>