<?php

include 'mysqlconnect.php';
include 'getdata.php';
include 'prettyprint.php';

$userID = $json->{'userID'};

$polls = array();

$result = mysql_query('SELECT * FROM polls');

while ($row = mysql_fetch_assoc($result)){
    
    $pollID = $row['pollID'];
    $resultUser = mysql_query('SELECT * FROM users WHERE userID = "'.$row['userID'].'"');

    $rowUser = mysql_fetch_assoc($resultUser);
        
    $options = array();
    
    $resultOptions = mysql_query('SELECT * FROM options WHERE pollID = "'.$pollID.'"');
    
    while ($rowOptions = mysql_fetch_assoc($resultOptions)) {
        
        array_push($options, array("option" => $rowOptions['option'], "votes" => $rowOptions['votes']));
        
    }
    
    $resultUserVote = mysql_query('SELECT * FROM userpollvote WHERE pollID = "'.$pollID.'" AND userID = "'.$userID.'"');
    
    if(mysql_num_rows($resultUserVote) > 0){
        $rowUserVote = mysql_fetch_assoc($resultUserVote);
        $uservote = $rowUserVote['optionID'];
    } else {
        $uservote = 0;   
    }
    
        
    array_push($polls, array("pollID" => $pollID, "question" => $row['question'], "stamp" => $row['stamp'], "username" => $rowUser['userName'], "uservote" => $uservote, "polloptions" => $options));
    
}

echo json_encode($polls);
?>