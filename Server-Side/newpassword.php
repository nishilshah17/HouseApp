<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>myGov Forgot Password</title>
<link rel="stylesheet" type="text/css" href="css/view.css" media="all">
<script type="text/javascript" src="js/view.js"></script>

</head>
<body id="main_body" >
	
	<img id="top" src="images/top.png" alt="">
	<div id="form_container">
	
		<h1><a>MyGov</a></h1>
		<form id="form_822286" class="appnitro"  method="post" action="">
					<div class="form_description">
			<h2>MyGov</h2>
            <p>Forgot Password?</p>
		</div>						
			<ul>
			
					<li id="li_1" >
		<label class="description" for="element_1">Password:</label>
		<div>
			<input id="element_1" name="element_1" class="element text medium" type="password"/> 
		</div> 
		</li>		<li id="li_2" >
		<label class="description" for="element_2">Confirm Password: </label>
		<div>
			<input id="element_2" name="element_2" class="element text medium" type="password"/> 
		</div> 
		</li>
				<li class="buttons">
			    <input type="hidden" name="form_id" value="822286" />
				<input id="saveForm" class="button_text" type="submit" name="submit" value="Reset" />
		</li>
			</ul>
		</form>	
		<div id="footer">	
		</div>
	</div>
	<img id="bottom" src="images/bottom.png">
	</body>
</html>

<?php
    $userID = $_GET["id"];

    if (isset($_POST['submit'])) 
    { 
        include 'mysqlconnect.php';
        include 'password.php';
        
        $unsecure = substr($userID,3);
        $unsecure = base64_decode($unsecure);
        
        $password = $_POST['element_1'];
        $confirmpassword = $_POST['element_2'];
        
        $hash = password_hash($password, PASSWORD_DEFAULT);
        
        if($password == $confirmpassword) {
            
            if(strlen($password)>=6){
                mysql_query('UPDATE users SET hash = "'.$hash.'" WHERE userID = "'.$unsecure.'"');
        
                echo "Your password has been reset. You can now log in and use MyGov!";
            } else {
                echo "<font color='white'>Your password must be at least 6 characters.</font>";
            }

        } else {
            echo "<font color='white'>Your passwords do not match.</font>";   
        }
    }
?>