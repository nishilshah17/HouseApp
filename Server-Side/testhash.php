<?php

/*include 'password.php';

$password = "pass";

$password = password_hash($password, PASSWORD_DEFAULT);

echo $password;*/
$userID = 222222222;
$secure = rand(100,999).base64_encode($userID);

echo $secure;
?>