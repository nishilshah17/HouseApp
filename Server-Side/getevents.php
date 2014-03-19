<?php

include 'mysqlconnect.php';

$data = file_get_contents('php://input');
$json = json_decode($data);
?>