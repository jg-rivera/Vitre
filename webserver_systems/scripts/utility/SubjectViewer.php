<?php

include_once 'SqlUtils.php';
include_once 'SubjectManager.php';

if (!isset($_POST['s_id'])) exit;
if(empty($_POST['s_id'])) exit;

$s_id = $_POST['s_id'];

$conn = SqlUtils::connect();

$sub = SubjectManager::processSubject($conn, $s_id);

$conn->close();
?>