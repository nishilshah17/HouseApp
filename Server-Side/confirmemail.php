<?php

include 'mysqlconnect.php';

$userID = $_GET["id"];

$unsecure = substr($userID,3);
$unsecure = base64_decode($unsecure);

mysql_query('UPDATE users SET confirmed = 1 WHERE userID = "'.$unsecure.'"');

echo "Thank you for confirming your myGov account. You can now login and start using myGov!";
?>