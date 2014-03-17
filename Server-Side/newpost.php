<?php
//mysql login information

include 'mysqlconnect.php';

$dbhost = 'localhost';
$dbuser = 'clubbed_houseapp';
$dbpass = 'houseapp2014';
$db = 'clubbed_houseapp';

$dbserver = mysql_connect($dbhost, $dbuser, $dbpass)
    or die("Unable to connect to MySQL: " . mysql_error());
mysql_select_db($db)
    or die("Unable to select database: " . mysql_error());

?>