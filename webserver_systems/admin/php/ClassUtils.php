<?php

require_once ('SqlUtils.php');

class ClassUtils {
	
	/**
		Counts registered students in the database.
	*/
	public static function countRegStudents() {
		$conn = SqlUtils::connect();
		$req = "SELECT count(*) as total from Students";
		$query = mysqli_query($conn, $req);
		$data = mysqli_fetch_assoc($query);
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
		$data = mysqli_fetch_assoc($query);
		$count = $data['total'];
		$conn -> close();
		
		return $count;
	}
	
	/**
		Deletes all students and their associated grades.
	*/
	public static function deleteAllStudents() {
		$conn = SqlUtils::connect();
		$req1 = "DELETE FROM Analytics WHERE 1";
		$query1 = mysqli_query($conn, $req1);
		
		// Del analytics
		if ($query1) {
			echo "Deleted analytics. \n";
		} else {
			echo mysqli_error($conn);
			die();
		}
		
		// Del students
		$req2 = "DELETE FROM Students WHERE 1";
		$query2 = mysqli_query($conn, $req2);
		
		if ($query1) {
			echo "Deleted students. \n";
		} else {
			echo mysqli_error($conn);
			die();
		}
		
		// Del grades
		$req3 = "DELETE FROM Grades WHERE 1";
		$query3 = mysqli_query($conn, $req3);
		
		if ($query3) {
			echo "Deleted grades. \n";
		} else {
			echo mysqli_error($conn);
			die();
		}
		
		$conn -> close();
	}
	
	
}

?>
