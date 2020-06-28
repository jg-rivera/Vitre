<?php

require_once ('SqlUtils.php');

class CacheUtils {
	
	public static function printCache() {
		$conn = SqlUtils::connect();
		$req = "SELECT CacheHash, IFNULL(SubjectName, 'Not applicable') AS SubjectName, ClassName, CacheType FROM CacheMetadata ";
		$req .= "INNER JOIN Classes on CacheMetadata.ClassID = Classes.ClassID ";
		$req .= "LEFT JOIN Subjects on CacheMetadata.SubjectID = Subjects.SubjectID";
		
		$query = mysqli_query($conn, $req);
		$cache = array();
		
		while ($row = $query->fetch_assoc()) {
			$c = array();
			$c['hash'] = $row['CacheHash'];
			$c['subject'] = $row['SubjectName'];
			$c['class'] = $row['ClassName'];
			$c['type'] = $row['CacheType'] < 1 ? 'Grades Cache' : 'Class Cache';
			array_push($cache, $c);
		}
		
		foreach($cache as $c) {
			echo '<tr>';
			echo '<td class="hash"><code>' . $c['hash'] . '</code></td>';
			echo '<td>' . $c['subject'] . '</td>';
			echo '<td>' . $c['class'] . '</td>';
			echo '<td>' . $c['type'] . '</td>';
			echo '<td> <button class="btn btn-danger act-delete"> <i class="far fa-trash-alt"></i> </button> </td>';
			echo '</tr>';
		}
		
		$conn -> close();
	}
	
	public static function printEmpty() {
		$conn = SqlUtils::connect();
		$req = "SELECT COUNT(*) as total FROM CacheMetadata;";
		$query = mysqli_query($conn, $req);
		
		$data = mysqli_fetch_assoc($query);
		$total = $data['total'];
		
		if ($total < 1) {
			echo '<p class="small text-center text-muted my-5"><em>No caches found.</em></p>';
		}
		
		$conn -> close();
	}
	
	public static function deleteCache($hash) {
		// Delete entry from the database.
		$conn = SqlUtils::connect();
		$req = "DELETE FROM CacheMetadata WHERE CacheHash='$hash'";
		$query = mysqli_query($conn, $req);
		
		if ($query) {
			$fileRoot = $_SERVER['DOCUMENT_ROOT'] . '/files/grade11/';
			$filePath = $fileRoot . $hash . '.cache';
			
			if (file_exists($filePath)) {
				// Deleted.
				unlink($filePath) or die ("Cannot delete: $hash");
			}
		}
		$conn -> close();
	}
	
	public static function deleteAllCache() {
		
	}
}

?>
