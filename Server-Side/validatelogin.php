<?php

include 'mysqlconnect.php';
require 'password.php';
include 'getdata.php';

$email = $json->{'email'};
$password = $json->{'password'};

$result = mysql_query('SELECT * FROM users WHERE email = "'.$email.'"');
$row = mysql_fetch_assoc($result);
$type = "2";

if(mysql_num_rows($result)==0)
{
    $message = "false";
} else {
    
    $matched = password_verify($password, $row['hash']);
    
    if($matched == true){
        if($row['confirmed'] == 0){
            $message = "unconfirmed";
        } else {
            $message = strval($row['userID']);
            $type = strval($row['type']);
        }
    } else {
        $message = "false";
    }
}

echo json_encode(array('userID' => $message, 'type' => $type));

?>