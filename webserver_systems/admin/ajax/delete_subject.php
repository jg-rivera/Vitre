<?php

include "../php/SubjectUtils.php";

if (!isset($_POST['id'])) return;

$id = $_POST['id'];
SubjectUtils::delete($id);


?>