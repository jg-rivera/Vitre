<?php

include "../php/TeacherUtils.php";

if (!isset($_POST['name']) or !isset($_POST['honorific'])) return;

$name = $_POST['name'];
$honorific = $_POST['honorific'];

TeacherUtils::register($name, $honorific);

?>