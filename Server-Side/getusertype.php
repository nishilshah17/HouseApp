<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');

$type = $result['type'];

echo json_encode(array('type' => $type));

?>