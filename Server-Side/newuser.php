<?php

include 'mysqlconnect.php';
require 'password.php';
include 'getdata.php';

$data = file_get_contents('php://input');
$json = json_decode($data);
$email = $json->{'suemail'};
$password = $json->{'supassword'};
$firstname = $json->{'sufirstname'};
$lastname = $json->{'sufirstname'};
$username = $json->{'suusername'};
$type = $json->{'sutype'};

$rand = FALSE;

$password = password_hash($password, PASSWORD_DEFAULT); //encrypt password

while (!$rand)
{
	$userID = rand(100000000, 999999999);
	$result = mysql_query('SELECT * FROM users WHERE userID = "'.$userID.'"');
	if(mysql_num_rows($result) == 0) {
		$rand = TRUE;
	} else {
	}
}

$resultemail = mysql_query('SELECT * FROM users WHERE email = "'.$email.'"');
if(mysql_num_rows($resultemail) > 0){
    $userID = "Email Already In Use";
} else {
    mysql_query('INSERT INTO users (userID, firstname, lastname, email, hash, username, type) VALUES ("'.$userID.'", "'.$firstname.'","'.$lastname.'", "'.$email.'", "'.$password.'", "'.$username.'", "'.$type.'")');
}
echo json_encode(array('userID' => $userID));

?>