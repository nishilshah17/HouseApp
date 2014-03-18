<?php

include 'mysqlconnect.php';

$email = $_POST['email'];
$password = $_POST['password'];

$result = mysql_query('SELECT * FROM users WHERE email = "'.$email.'"');

if(mysql_num_rows($result)==0)
{
    $message = "Invalid Email";
} else {
    $row = mysql_fetch_assoc($result);
    $userpassword = $row['hash'];
    
    $matched = password_verify($userpassword, $hash);
    
    if($matched == true){
        $message = "true";
    } else {
        $message = "Invalid Password";
    }
}

echo json_encode(array("result" => $message));
?>