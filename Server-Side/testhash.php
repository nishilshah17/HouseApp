<?php

include 'password.php';

$password = "pass";

$password = password_hash($password, PASSWORD_DEFAULT);

echo $password;

?>