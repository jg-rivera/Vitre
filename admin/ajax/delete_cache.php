<?php

include "../php/CacheUtils.php";

if (!isset($_POST['hash'])) return;

$hash = $_POST['hash'];
CacheUtils::deleteCache($hash);


?>