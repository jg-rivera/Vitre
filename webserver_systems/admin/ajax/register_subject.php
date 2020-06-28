<?php

include "../php/SubjectUtils.php";

if (!isset($_POST['name']) or !isset($_POST['icon_name']) or !isset($_POST['instructor']) or !isset($_POST['track']) or !isset($_POST['semester'])) return;

$name = $_POST['name'];
$icon_name = $_POST['icon_name'];
$instructor = $_POST['instructor'];
$track = $_POST['track'];
$semester = $_POST['semester'];

$instructor_id = SubjectUtils::getTeacherID($instructor);
$track_id = SubjectUtils::getTrackID($track);

SubjectUtils::register($name, $track_id, $instructor_id, $semester, $icon_name);


?>