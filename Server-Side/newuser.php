<?php

include 'mysqlconnect.php';
require 'password.php';
include 'getdata.php';
include 'phpmailer.php';
include 'class.phpmailer.php';
include 'class.pop3.php';
include 'class.smtp.php';

$data = file_get_contents('php://input');
$json = json_decode($data);
$email = $json->{'suemail'};
$password = $json->{'supassword'};
$firstname = $json->{'sufirstname'};
$lastname = $json->{'sulastname'};
$username = $json->{'suusername'};
$type = $json->{'sutype'};
$confirmed = 0;

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
$resultusername = mysql_query('SELECT * FROM users WHERE username = "'.$username.'"');

if(mysql_num_rows($resultemail) > 0){
    $register = "false";
} else if (mysql_num_rows($resultusername) > 0){
    $register = "false2";
} else {
    mysql_query('INSERT INTO users (userID, firstname, lastname, email, hash, username, type, confirmed) VALUES ("'.$userID.'", "'.$firstname.'","'.$lastname.'", "'.$email.'", "'.$password.'", "'.$username.'", "'.$type.'", "'.$confirmed.'")');
    $register = "true";
}
echo json_encode(array('register' => $register));

$secure = rand(100,999).base64_encode($userID);


//SEND CONFIRMATION EMAIL
if($register != "false"){
    
    $mail = new PHPMailer;
     
    $mail->isSMTP();           
    $mail->Host = 'smtp.gmail.com';          
    $mail->SMTPAuth = true;                
    $mail->Username = 'mygovapp@gmail.com';          
    $mail->Password = 'mygov2014';          
    $mail->SMTPSecure = 'tls';                  
    $mail->Port = 587;                              
    $mail->setFrom('mygovapp@gmail.com', 'myGov');  
    $mail->addReplyTo('mygovapp@gmail.com', 'myGov');
    $mail->addAddress($email, $firstname." ".$lastname);
    $mail->WordWrap = 50;                              
    $mail->isHTML(true);                                 
     
    $mail->Subject = $firstname.', Confirm your email address';
    $mail->Body    = 'Dear '.$firstname.' '.$lastname.', <br><br>You recently joined myGov. Click <a href="http://www.clubbedinapp.com/houseapp/php/confirmemail.php?id='.$secure.'">here</a> to confirm your email address and start using the application! <br><br> From, <br> The myGov Team';
     
    if(!$mail->send()) {
       exit;
    }
}



?>