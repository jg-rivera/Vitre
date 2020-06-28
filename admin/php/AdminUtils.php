<?php

require_once ('SqlUtils.php');

class AdminUtils {
	
	/**
		Counts registered students in the database.
	*/
	public static function countRegStudents() {
		$conn = SqlUtils::connect();
		$req = "SELECT count(*) as total from Students";
		$query = mysqli_query($conn, $req);
		$data= mysqli_fetch_assoc($query);
		$count = $data['total'];
		$conn -> close();
		return $count;
	}
	
	/**
		Counts registered teachers in the database.
	*/
	public static function countRegTeachers() {
		$conn = SqlUtils::connect();
		$req = "SELECT count(*) as total from Teachers";
		$query = mysqli_query($conn, $req);
		$data= mysqli_fetch_assoc($query);
		$count = $data['total'];
		$conn -> close();
		return $count;
	}
}

?>
