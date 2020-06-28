<?php

include "../php/TeacherUtils.php";

if (!isset($_POST['id'])) return;

$id = $_POST['id'];
TeacherUtils::delete($id);


?>