<?php

include 'mysqlconnect.php';
require 'password.php';
include 'getdata.php';

//$email = $json->{'email'};
//$password = $json->{'password'};

$email = "ram@.";
$password = "qr";

$result = mysql_query('SELECT * FROM users WHERE email = "'.$email.'"');
$row = mysql_fetch_assoc($result);

if(mysql_num_rows($result)==0)
{
    $message = "false";
} else {
    
    $matched = password_verify($password, $row['hash']);
    
    if($matched == true){
        $message = $row['userID'];
    } else {
        $message = "false";
    }
}

$fullname = $row['firstName']." ".$row['lastName'];

//echo json_encode(array('userID' => $email, 'fullname' => $fullname));

echo $message;
?>