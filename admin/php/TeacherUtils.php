<?php

require_once ('SqlUtils.php');

class TeacherUtils {
	
	public static function register($name, $honorific) {
		$conn = SqlUtils::connect();
		
		$reg = preg_replace("/[^a-zA-Z0-9]+/", "", strtolower($name));
		$signature = hash('sha256', $reg);
		
		$req = "INSERT INTO Teachers(TeacherName, TeacherChunkKey, Honorific) ";
		$req .= "VALUES ('$name', '$signature', '$honorific')";
		
		$query = mysqli_query($conn, $req);
		
		$conn -> close();
		return $count;
	}
	
	public static function delete($teacher_id) {
		$conn = SqlUtils::connect();
		
		$req1 = "UPDATE Subjects SET InstructorID=NULL WHERE InstructorID=$teacher_id;";
		$query1 = mysqli_query($conn, $req1);
			
		if ($query1) {
			echo "Instructor deleted from subject. \n";
		} else {
			echo mysqli_error($conn);
		}
		
		$req2 = "DELETE FROM Teachers WHERE TeacherID=$teacher_id;";
		$query2 = mysqli_query($conn, $req2);
			
		if ($query2) {
			echo "Teacher deleted. \n";
		} else {
			echo mysqli_error($conn);
		}
		$conn -> close();
	}
	
	public static function getAdvisoryClass($teacher_id) {
		$conn = SqlUtils::connect();
		$req = "SELECT ClassID, ClassName FROM Classes WHERE AdviserID=$teacher_id";
		
		$advisories = array();
		$query = mysqli_query($conn, $req);
		
		while ($row = $query->fetch_assoc()) {
			$newdata = array (
				'id' => $row['ClassID'],
				'name' => $row['ClassName']
			);
			
			$advisories[] = $newdata;
		}
		$conn -> close();
		return $advisories;
	}
	
	public static function getSubjects($teacher_id) {
		$conn = SqlUtils::connect();
		$req = "SELECT SubjectID, SubjectName FROM Subjects WHERE InstructorID=$teacher_id";
		
		$advisories = array();
		$query = mysqli_query($conn, $req);
		
		while ($row = $query->fetch_assoc()) {
			$newdata = array (
				'id' => $row['SubjectID'],
				'name' => $row['SubjectName']
			);
			
			$advisories[] = $newdata;
		}
		$conn -> close();
		return $advisories;
	}
}

?>
