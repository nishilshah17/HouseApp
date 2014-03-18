<?php

include 'mysqlconnect.php';

$rand = FALSE;
$firstname = $_POST['sufirstname'];
$email = $_POST['sulastname'];
$password = $_POST['supassword'];

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
    mysql_query('INSERT INTO user (userID, firstname, lastname, email, hash) VALUES ("'.$userID.'", "'.$firstname.'","'.$lastname.'", "'.$email.'", "'.$password.'")');
}
echo json_encode(array('userID' => $userID));

?>