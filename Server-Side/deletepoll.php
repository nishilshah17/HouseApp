<?php

include 'mysqlconnect.php';
include 'getdata.php';

$pollID = $json->{'pollID'};

mysql_query('DELETE FROM polls WHERE pollID = "'.$pollID.'"');
mysql_query('DELETE FROM options WHERE pollID = "'.$pollID.'"');
mysql_query('DELETE FROM userpollvote WHERE pollID = "'.$pollID.'"');

?>