<?php
include 'mysqlconnect.php';
require 'password.php';

$password = "testpassword";
$email = "test@gmail.com";

$result = mysql_query('SELECT * FROM users WHERE email = "'.$email.'"');
$row = mysql_fetch_assoc($result);

$matched = password_verify($password, $row['hash']);


echo $password;
echo $matched;

?>