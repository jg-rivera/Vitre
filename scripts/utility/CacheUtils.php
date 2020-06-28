<?php

require_once ('SqlUtils.php');

class CacheUtils {
	
	public static function readCacheFiles() {
		$path = $_SERVER['DOCUMENT_ROOT'] . '/files/grade11/';
		$files = array_diff(scandir($path), array('.', '..'));
		return $files;
	}
	
	public static function printCache() {
		$cache = CacheUtils::readCacheFiles();
		foreach($cache as $fn) {
			echo '<b>' . $fn . '</b>';
		}
	}
	
	public static function hasCache($signature) {
		$fileRoot = $_SERVER['DOCUMENT_ROOT'] . '/files/grade11/';
		$filePath = $fileRoot . $signature . '.cache';
		return file_exists($filePath);
	}
		
	public static function writeCache($signature, $payload) {
		$fileRoot = $_SERVER['DOCUMENT_ROOT'] . '/files/grade11/';
		$filePath = $fileRoot . $signature . '.cache';
		$fp = fopen($filePath, 'w');
		fwrite($fp, $payload);
		fclose($fp);
	}
		
	private static function addCacheMetadata($conn, $signature, $payloadRaw, $isClassCache) {
		$payloadJson = json_decode ($payloadRaw, true);
		
		$crowns = null;
		$subjectID = null;
		$classID = null;
		$cacheType = $isClassCache ? 1 : 0;
		
		// If grade cache
		if (!$isClassCache) {
			// Get id data
			$crowns = $payloadJson['crown']['id'];
			$subjectID = $crowns['subject'];
			$classID = $crowns['class'];
		} 
		// If class cache
		else {
			// Get simple crown data
			$crowns = $payloadJson['crown'];
			$classID = $crowns['id'];
			$subjectID = 'NULL';
		}
		
		$req = "INSERT INTO CacheMetadata(CacheHash, SubjectID, ClassID, CacheType) VALUES ('$signature', $subjectID, $classID, $cacheType);";
		echo $req;
		$query = mysqli_query($conn, $req);
		
		if ($query) {
			echo 'Added cache metadata.';
		} else {
			echo "Error: " . mysqli_error($conn);
		}
	}
	
	public static function handleCacheMetadata($signature, $payloadRaw, $isClassCache) {
		$conn = SqlUtils::connect();
		$req = "SELECT 1 FROM CacheMetadata WHERE CacheHash='$signature';";
		$query = mysqli_query($conn, $req);
		
		// No registered cache.
		if (mysqli_num_rows($query) < 1) {
			// Add to database
			CacheUtils::addCacheMetadata($conn, $signature, $payloadRaw, $isClassCache);
		}
		$conn -> close();
		
	}
}

?>
