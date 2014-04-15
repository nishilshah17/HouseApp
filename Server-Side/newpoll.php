<?php

include 'mysqlconnect.php';
include 'getdata.php';

$question = $json->{'pollquestion'};
$userID = $json->{'userID'};
$option1 = $json->{'option1'};
$option2 = $json->{'option2'};
$option3 = $json->{'option3'};
$option4 = $json->{'option4'};

$rand = FALSE;

while (!$rand)
{
	$pollID = rand(10000,99999);
    
	$result = mysql_query('SELECT * FROM polls WHERE pollID = "'.$pollID.'"');
	if(mysql_num_rows($result) == 0) {
        echo "true";
		 $rand = TRUE;
	} else {
        echo "false";
	}

}

mysql_query('INSERT INTO polls (question, userID, pollID) VALUES ("'.$question.'", "'.$userID.'", "'.$pollID.'")');

if(empty($option1)){
    mysql_query('INSERT INTO options (option, pollID) VALUES ("'.$option1.'", "'.$pollID.'")');
}

if(empty($option2)){
    mysql_query('INSERT INTO options (option, pollID) VALUES ("'.$option2.'", "'.$pollID.'")');
}

if(empty($option3)){
    mysql_query('INSERT INTO options (option, pollID) VALUES ("'.$option3.'", "'.$pollID.'")');
}

if(empty($option4)){
    mysql_query('INSERT INTO options (option, pollID) VALUES ("'.$option4.'", "'.$pollID.'")');
}

?>