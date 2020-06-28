<?php

require_once ('SqlUtils.php');

class SubjectUtils {
	
	public static function register($name, $track_id, $instructor_id, $semester, $icon_name) {
		$conn = SqlUtils::connect();
		
		$reg = preg_replace("/[^a-zA-Z0-9]+/", "", strtolower($name));
		$signature = hash('sha256', $reg);
		
		$req = "INSERT INTO Subjects(SubjectName, TrackID, InstructorID, Semester, IconName, SubjectChunkKey) ";
		$req .= "VALUES ('$name', $track_id, $instructor_id, '$semester', '$icon_name', '$signature')";
		
		$query = mysqli_query($conn, $req);
		
		$conn -> close();
		return $count;
	}
	
	public static function delete($subject_id) {
		$conn = SqlUtils::connect();
		$req = "DELETE FROM Subjects WHERE SubjectID=$subject_id;";
		$query = mysqli_query($conn, $req);
			
		if ($query) {
			echo "Subject deleted. \n";
		} else {
			echo mysqli_error();
		}
		$conn -> close();
	}
	
	public static function getInstructors() {
		$conn = SqlUtils::connect();
		$req = "SELECT TeacherName FROM Teachers";
		
		$teachers = array();
		$query = mysqli_query($conn, $req);
		
		while ($row = $query->fetch_assoc()) {
			array_push($teachers, $row['TeacherName']);
		}
		
		$conn -> close();
		return $teachers;
	}
	
	public static function getTracks() {
		$conn = SqlUtils::connect();
		$req = "SELECT TrackName FROM Tracks";
		
		$tracks = array();
		$query = mysqli_query($conn, $req);
		
		while ($row = $query->fetch_assoc()) { 
			array_push($tracks, $row['TrackName']);
		}
		
		$conn -> close();
		return $tracks;
	}
	
	public static function getTeacherID($name) {
		$conn = SqlUtils::connect();
		$req = "SELECT TeacherID FROM Teachers WHERE TeacherName='$name'";
		
		$query = mysqli_query($conn, $req);
		$row = $query -> fetch_assoc();
		
		$conn -> close();
		return $row['TeacherID'];
	}
	
	public static function getTrackID($name) {
		$conn = SqlUtils::connect();
		$req = "SELECT TrackID FROM Tracks WHERE TrackName='$name'";
		
		$query = mysqli_query($conn, $req);
		$row = $query -> fetch_assoc();
		
		$conn -> close();
		return $row['TrackID'];
	}
}

?>
