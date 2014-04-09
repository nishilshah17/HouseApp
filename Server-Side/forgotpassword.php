<?php

include 'mysqlconnect.php';
include 'getdata.php';
include 'password.php';
include 'phpmailer.php';
include 'class.phpmailer.php';
include 'class.pop3.php';
include 'class.smtp.php';

$email = $json->{'forgotemail'};

$result = mysql_query('SELECT * FROM users WHERE email = "'.$email.'"');
$row = mysql_fetch_assoc($result);

if(mysql_num_rows($result) > 0) {
$secure = rand(100,999).base64_encode($row['userID']);

//SEND FORGOT PASSWORD EMAIL
    
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
    $mail->addAddress($email, $row['firstName']." ".$row['lastName']);
    $mail->WordWrap = 50;                              
    $mail->isHTML(true);                                 
     
    $mail->Subject = $firstname.'Forgot your password?';
    $mail->Body    = 'Dear '.$row['firstName'].' '.$row['lastName'].', <br><br>Click <a href="http://www.clubbedinapp.com/houseapp/php/newpassword.php?id='.$secure.'">here</a> to reset your password! <br><br> From, <br> The myGov Team';
     
    if(!$mail->send()) {
       exit;
    }

}

?>