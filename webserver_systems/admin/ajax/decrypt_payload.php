<?php

if (!isset($_POST['raw'])) return;

include '../php/Cypher.php';
error_reporting (E_ALL ^ E_WARNING);

$raw = $_POST['raw'];

if (strlen($raw) > 0) {
	$dec = Cypher::payload($raw);
	$json = json_decode ($dec, true);
	echo json_encode($json, JSON_PRETTY_PRINT);
}
?>