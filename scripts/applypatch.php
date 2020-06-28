<?php
include 'utility/Cypher.php';
include 'utility/Patcher.php';
include 'utility/CacheUtils.php';
include 'utility/Registrar.php';

error_reporting (E_ALL ^ E_WARNING);

if (!isset($_POST['type']) or !isset($_POST['pload'])) return;

// Constant types
$type_content = 'CONTENT';
$type_class = 'CLASS';

$signature = $_POST['sig'];
$patchType = $_POST['type'];
$payload = $_POST['pload'];

// Content Patch -> Grades
if ($patchType == $type_content) {
	
	$instruction = $_POST['inst'];
	$payloadRaw = Cypher::payload($payload);
	$instructionSetRaw = Cypher::payload($instruction);
	
	// Handle patch instructions and insertions
	// Send payload to handle crown information.
	// Inst set for individual data insertions.
	$graded = Patcher::process($payloadRaw, $instructionSetRaw);
	
	if ($graded) {
		CacheUtils::writeCache($signature, $payload);
		echo "\n Graded successfully.";
		// Add cache metadata if it has none.
		CacheUtils::handleCacheMetadata($signature, $payloadRaw, false);
		echo "\n Created cache metadata successfully.";
	}
}

// Class Patch -> Student Registry
else if ($patchType == $type_class) {
	$payloadRaw = Cypher::payload($payload);
	
	$registered = Registrar::process($payloadRaw);
	
	if ($registered) {
		Registrar::writeCache($signature, $payload);
		echo "\n Registered successfully.";
		// Add cache metadata if it has none.
		CacheUtils::handleCacheMetadata($signature, $payloadRaw, true);
		echo "\n Created cache metadata successfully.";
	}
}
?>