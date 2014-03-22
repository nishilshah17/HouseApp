<?php

include 'mysqlconnect.php';
include 'getdata.php';

$userID = $json->{'userID'};

$result = mysql_query('SELECT * FROM posts');

$posts = array();

while ($row = mysql_fetch_assoc($result)) {

	$result2 = mysql_query('SELECT * FROM user WHERE userID = "'.$row['userID'].'"');
	$row2 = mysql_fetch_assoc($result2);

	array_push($members, array("name" => $row2['name'], "id" => $row['userID']));

}

$tmp = Array();
foreach($members as &$ma)
    $tmp[] = &$ma["name"];
$tmp = array_map('strtolower', $tmp);
array_multisort($tmp, $members);

	echo json_encode($members);
?>