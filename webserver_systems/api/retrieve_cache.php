<?php

if (!isset($_POST['fn'])) return;

$hash = $_POST['fn'];

$fileRoot = $_SERVER['DOCUMENT_ROOT'] . '/files/grade11/';
$filePath = $fileRoot . $hash;

if (!file_exists($filePath)) {
    return;
}

$size = filesize($filePath);
header("Content-length: $size");
header("Content-Disposition: attachment; filename=$hash");
header('Content-Type: text/plain');
readfile($filePath);

?>