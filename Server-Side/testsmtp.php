<?php
include 'phpmailer.php';
include 'class.phpmailer.php';
include 'class.pop3.php';
include 'class.smtp.php';

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
$mail->addAddress('nishilshah17@gmail.com', 'Nishil Shah');
$mail->WordWrap = 50;                              
$mail->isHTML(true);                                 
$secure = fdsa;
$mail->Subject = 'Confirm your email address';
$mail->Body    = 'You recently joined myGov. Click <a href="http://www.clubbedinapp.com/houseapp/php/confirmemail.php?id='.$secure.'">here</a> to confirm your email address and start using the application!';
$mail->AltBody = 'Your recently joined myGov. Click here to confirm your email address and start using the application!';
 
if(!$mail->send()) {
   exit;
}
 

?>