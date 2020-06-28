<?php

if (!isset($_POST['raw'])) return;

$raw = $_POST['raw'];

if (strlen($raw) > 0)
	echo hash('sha256', $raw);

?>